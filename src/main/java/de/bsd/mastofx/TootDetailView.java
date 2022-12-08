package de.bsd.mastofx;

import com.sys1yagi.mastodon4j.MastodonRequest;
import com.sys1yagi.mastodon4j.api.entity.Status;
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException;
import com.sys1yagi.mastodon4j.api.method.Statuses;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * @author hrupp
 */
public class TootDetailView {


  @FXML
  public WebView webView;
  @FXML
  GridPane theGrid;


  private Status status;

  public void reply(ActionEvent actionEvent) {
    System.out.println(actionEvent);
    ParentAndController tac  = null;
    try {
      tac = MastoMain.loadFxmlFile("toot-box");
    } catch (IOException e) {
      e.printStackTrace();  // TODO: Customise this generated block
      return;
    }

    Stage stage = new Stage();
    Scene sc = new Scene(tac.parent, 500, 300);

    stage.setScene(sc);

    ((NewTootView)tac.controller).setInReplyTo(status.getId());

    stage.show();


  }

  void setStatus(Status item) {
    this.status = item;
  }

  public void display() {

    Label label = new Label("Id");
    theGrid.add(label, 0, 0);
    Text text = new Text(String.valueOf(status.getId()));
    theGrid.add(text, 1, 0);

    label = new Label("From");
    theGrid.add(label, 0, 1);
    StringBuilder sb = new StringBuilder();
    sb.append(status.getAccount().getDisplayName());

    var reblogged = status.getReblog();
    if (reblogged != null) { // isReblogged() is not reliable

      sb.append("=> from ").append(reblogged.getAccount().getDisplayName());
    }
    text = new Text(sb.toString());
    theGrid.add(text, 1, 1);

    label = new Label("Text");
    theGrid.add(label, 0, 2);


///*
//    TextArea ta = new TextArea();
//    ta.setWrapText(true);
//    if (reblogged != null) {
//      ta.setText(reblogged.getContent());
//    } else {
//      ta.setText(status.getContent());
//    }
//    theGrid.add(ta, 1, 2);
//*/

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

  public void boost(ActionEvent actionEvent) {
    Statuses st = new Statuses(MastoMain.getMastodonClient());
    try {
      MastodonRequest<Status> req = st.postReblog(status.getId());
      req.execute();
    } catch (Mastodon4jRequestException e) {
      e.printStackTrace();  // TODO: Customise this generated block
    }
  }

  public void fav(ActionEvent actionEvent) {
    Statuses st = new Statuses(MastoMain.getMastodonClient());
    try {
      MastodonRequest<Status> req = st.postFavourite(status.getId());
      req.execute();
    } catch (Mastodon4jRequestException e) {
      e.printStackTrace();  // TODO: Customise this generated block
    }

  }
}
