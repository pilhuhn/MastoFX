
package de.bsd.mastofx;

import com.sys1yagi.mastodon4j.MastodonClient;
import com.sys1yagi.mastodon4j.api.entity.Status;
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException;
import com.sys1yagi.mastodon4j.api.method.Statuses;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;

/**
 * @author hrupp
 */
public class NewTootView {

  private Scene scene;
  private long originalId;

  public NewTootView() {

  }

  NewTootView(Scene scene) {

    this.scene = scene;
  }


  /*
   * Callback from masto-box.fxml Toot-button
   */
  @FXML
  public void toot(javafx.event.ActionEvent event) {
    System.out.println(event);
    System.out.println(textarea.getParagraphs());

    String text = textarea.getText();

    MastodonClient client = MastoMain.getMastodonClient();

    Statuses statuses = new Statuses(client);
    try {
      Long inReplyToId = null;
      if (originalId > 0) {
        inReplyToId = originalId;
      }
      var req = statuses.postStatus(text,
                                    inReplyToId, // inReply
                                    null, // mediaIds
                                    false, // sensitive
                                    null, // Spoiler Text
                                    Status.Visibility.Public
         );

      var res = req.execute();
      System.out.println("Posted with id " + res.getId());
    } catch (Mastodon4jRequestException e) {
      e.printStackTrace();  // TODO: Customise this generated block
    }

  }

  @FXML
  public TextArea textarea;


  public void setInReplyTo(long originalId) {
    this.originalId = originalId;
  }
}
