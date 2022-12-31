
package de.bsd.mastofx;

import static java.lang.System.exit;

import com.google.gson.Gson;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import okhttp3.OkHttpClient;
import org.kordamp.bootstrapfx.BootstrapFX;
import social.bigbone.MastodonClient;

/**
 * @author hrupp
 */
public class MastoMain extends Application {

  private static MastodonClient client;
  static Scene scene;

  private static Properties properties;

  @FXML
  ListView <String> tootListView;

  public static void main(String[] args) {

    loadPropertiesFromFile();

    String mastodonServer = properties.getProperty("server");
    MastodonClient client = new MastodonClient.Builder(mastodonServer)
      .accessToken(properties.getProperty("token"))
      .build();
    MastoMain.client = client;

    launch(args);
  }

  private static void loadPropertiesFromFile() {
    try {
      String fileName = System.getProperty("user.home") + "/.mastoFX.properties";
      FileReader fr = new FileReader(fileName);
      properties = new Properties();
      properties.load(fr);
    } catch (IOException e) {
      e.printStackTrace();  // TODO: Customise this generated block
      exit(1);
    }
  }

  public static MastodonClient getMastodonClient() {
    return client;
  }

  @Override
  public void start(Stage stage) throws Exception {
    ParentAndController pac = loadFxmlFile("main");

    var root = pac.parent;

    scene = new Scene(root, 650, 375);
    scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());

    stage.setTitle("MastoFX");
    stage.setScene(scene);

   ((MainView)pac.controller).showList();
    stage.show();

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
