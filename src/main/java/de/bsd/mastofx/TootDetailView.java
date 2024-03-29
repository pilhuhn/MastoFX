package de.bsd.mastofx;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;
import social.bigbone.MastodonRequest;
import social.bigbone.api.entity.Status;
import social.bigbone.api.exception.BigBoneRequestException;

/**
 * @author hrupp
 */
public class TootDetailView {


  @FXML
  public WebView webView;
  @FXML
  public Button boostButton;
  @FXML
  public Button favButton;

  @FXML
  public Button replyButton;
  @FXML
  GridPane theGrid;


  private Status status;
  private Server server;

  public void reply(ActionEvent actionEvent) {
    System.out.println(actionEvent);
    ParentAndController tac  = null;
    try {
      tac = MastoMain.loadFxmlFile("newToot.fxml");
    } catch (IOException e) {
      e.printStackTrace();  // TODO: Customise this generated block
      return;
    }

    Stage stage = new Stage();
    Scene sc = new Scene(tac.parent, 500, 300);
    sc.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());

    stage.setScene(sc);

    NewTootView controller = (NewTootView) tac.controller;
    controller.setServer(server);
    controller.setInReplyToId(status.getId());
    String replyToAccount = "@" + status.getAccount().getAcct();
    if (status.getReblog() != null) {
      replyToAccount += " @" + status.getReblog().getAccount().getAcct();
    }
    if (!replyToAccount.endsWith(" ")) {
      replyToAccount += " ";
    }
    controller.setInReplyToAccount(replyToAccount);

    stage.show();


  }

  void setStatus(Status item) {
    this.status = item;
  }

  public void display() {

    Label label = new Label("Id");
    theGrid.add(label, 0, 0);
    Text text = new Text(status.getId());
    theGrid.add(text, 1, 0);

    label = new Label("From");
    theGrid.add(label, 0, 1);
    StringBuilder sb = new StringBuilder();
    sb.append(status.getAccount().getDisplayName());

    var reblogged = status.getReblog();
    if (reblogged != null) { // isReblogged() is true on the article that is reblogged only

      sb.append("=> from ").append(reblogged.getAccount().getDisplayName());
    }
    text = new Text(sb.toString());
    theGrid.add(text, 1, 1);

    label = new Label("Text");
    theGrid.add(label, 0, 2);

    setFavButtonState(status);


    var engine = webView.getEngine();
    String t ;
    if (reblogged != null) {
      t = reblogged.getContent();
    }
    else {
      t = status.getContent();
    }
    engine.loadContent(t);

  }

  private void setFavButtonState(Status status) {
    if (status.isFavourited()) {
      favButton.setText("Fav'd");
    } else {
      favButton.setText("Fav");
    }
  }

  public void boost(ActionEvent actionEvent) {
    var st = server.client.statuses();
    try {
      MastodonRequest<Status> req = st.reblogStatus(status.getId());
      req.execute();
    } catch (BigBoneRequestException e) {
      e.printStackTrace();  // TODO: Customise this generated block
    }
  }

  public void fav(ActionEvent actionEvent) {

    var ser = !server.isAnonymous() ? server : MastoMain.getDefaultServer();
    var st = ser.client.statuses();

    try {

      Status toBeFavD = status;
      if (status.getReblog() != null) {
        toBeFavD = status.getReblog();
      }

//      String query = "type=statuses&account_id=" + toBeFavD.getAccount().getId() +
//        "&max_id=" + toBeFavD.getId() +
//        "&min_id=" + (Long.parseLong(toBeFavD.getId()) - 1);
      String query = "type=statuses&url=" +toBeFavD.getUri();
      MastodonRequest r =
        ser.client.search().searchContent(
          query
        );
      Object result = r.execute();

      MastodonRequest<Status> req;
      if (toBeFavD.isFavourited()) {
        req = st.unfavouriteStatus(toBeFavD.getId());
      } else {
        req = st.favouriteStatus(toBeFavD.getId());
      }
      Status res = req.execute();
      setFavButtonState(res);

    } catch (BigBoneRequestException e) {
      e.printStackTrace();  // TODO: Customise this generated block
    }

  }

  public void raw(ActionEvent actionEvent) {
    System.out.print("=== raw for ");
    System.out.println(status.getId());
    System.out.printf("Account %s , ( %s ),  %s \n" , status.getAccount().getAcct(),
                      status.getAccount().getDisplayName(), status.getAccount().getId());
    System.out.println(status.getCreatedAt());
    if (status.getReblog() != null) {
      var re = status.getReblog();
      System.out.println("  --  reblogged from id " + re.getId());
      System.out.printf("      Account %s , ( %s ),  %s \n" , re.getAccount().getAcct(),
                        re.getAccount().getDisplayName(), re.getAccount().getId());
      System.out.println("      " + re.getUri());
      re.getEmojis().forEach(x2 -> System.out.println("      " + x2));
      re.getMediaAttachments().forEach(x1 -> System.out.printf("      %s %s %s %s %s \n", x1.getId(), x1.getType(),
                                                                       x1.getPreviewUrl(), x1.getTextUrl(), x1.getRemoteUrl()));
      re.getMentions().forEach(x -> System.out.println("      " + x.getUsername()));
      re.getTags().forEach(x -> System.out.println("      " + x.getName()));
      System.out.println("Reblogged flag: " + re.isReblogged());
      System.out.println(" --");
    }
    else {
      System.out.println("  not reblogged");
    }
    System.out.println("Reblogged flag: " + status.isReblogged());
    System.out.println(status.getUri());
    if (status.getInReplyToId() != null ) {
      System.out.println("  -- in reply to " + status.getInReplyToId());
      System.out.println(status.getInReplyToAccountId());
    }
    status.getEmojis().forEach(x2 -> System.out.println("      " + x2));
    status.getMediaAttachments().forEach(x1 -> System.out.printf("%s %s %s %s %s \n", x1.getId(), x1.getType(),
                                                                 x1.getPreviewUrl(), x1.getTextUrl(), x1.getRemoteUrl()));
    status.getMentions().forEach(x -> System.out.println(x.getUsername()));
    status.getTags().forEach(x -> System.out.println(x.getName()));
  }

  public void setServer(Server server) {

    this.server = server;
  }
}
