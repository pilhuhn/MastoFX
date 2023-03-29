
package de.bsd.mastofx;

import static java.lang.System.exit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;
import social.bigbone.MastodonClient;

/**
 * @author hrupp
 */
public class MastoMain extends Application {

  static Scene scene;

  private static final Type SERVER_TYPE = new TypeToken<List<Server>>() {
  }.getType();

  private static List<Server> serverList = new ArrayList<>();
  private static Server defaultServer;

  public static void main(String[] args) {

    List<Server> servers = loadServersFromFile();

    for (Server server: servers) {
      MastodonClient.Builder client = new MastodonClient.Builder(server.getName());
      if (server.token!=null) {
        client.accessToken(server.token);
      }
      server.client = client.build();
      serverList.add(server);
    }

    launch(args);
  }

  private static List<Server> loadServersFromFile() {

    try {
      String fileName = System.getProperty("user.home") + "/.mastoFX.json";
      FileReader fr = new FileReader(fileName);
      Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
      JsonReader jsonReader = new JsonReader(fr);
      List<Server> list = gson.fromJson(jsonReader, SERVER_TYPE);
      int defaults = 0;
      for (Server s: list) {
        if (s.defaultServer) {
          defaultServer = s;
          defaults++;
        }
      }
      if (defaults!=1) {
        System.err.println("You may only have 1 default server");
        System.exit(1);
      }
      return list;
    } catch (IOException e) {
      e.printStackTrace();  // TODO: Customise this generated block
      exit(1);
    }
    return new ArrayList<>();
  }

  @Override
  public void start(Stage globalStage) throws Exception {
    int i = 0;
    for (Server server : serverList) {
      ParentAndController pac = loadFxmlFile("main");

      var root = pac.parent;

      scene = new Scene(root, 650, 375);
      scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());

      Stage stage = new Stage();

      stage.setTitle(server.name);
      stage.setScene(scene);

      MainView controller = (MainView) pac.controller;
      controller.setServer(server);
      controller.showList();
      stage.show();
      position(stage, i++); // Must happen after stage.show()
    }
    // TODO in background thread load DetailsView fxml
  }


  public static List<Server> getNonAnonServers() {
    List ret = new ArrayList(serverList.size());
    for (Server server : serverList) {
      if (!server.isAnonymous()) {
        ret.add(server);
      }
    }
    return ret;
  }

  public static Server getDefaultServer() {
    return defaultServer;
  }

  /*
   * Place Stage i at an offset of (i*25, i*25)
   * of the default position in the center of the screen
   */
  private void position(Stage stage, int i) {
    stage.centerOnScreen(); // Default location
    stage.setX(stage.getX()+i*25);
    stage.setY(stage.getY()+i*25);
  }

  static ParentAndController loadFxmlFile(String baseName) throws IOException {
    if (!baseName.endsWith(".fxml")) {
      baseName = baseName + ".fxml";
    }
    System.out.print("Trying to load " + baseName );
    URL resource = MastoMain.class.getResource(baseName);
    System.out.println(" with resource " + (resource != null ? resource.toExternalForm() : " - not found -"));
    var loader = new FXMLLoader(resource);
    Parent root = loader.load();
    if (root == null) {
      throw new IllegalStateException("Fxml file " + baseName + ".fxml can't be loaded");
    }
    if (loader.getController() == null) {
      throw new IllegalStateException("Controller is null for " + baseName + ".fxml");
    }
    ParentAndController pac = new ParentAndController();
    pac.controller = loader.getController();
    pac.parent = root;

    return pac;
  }
}
