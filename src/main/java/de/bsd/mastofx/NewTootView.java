
package de.bsd.mastofx;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import okhttp3.MediaType;
import social.bigbone.MastodonClient;
import social.bigbone.api.entity.MediaAttachment;
import social.bigbone.api.entity.Status;
import social.bigbone.api.exception.BigBoneRequestException;
import social.bigbone.api.method.MediaMethods;
import social.bigbone.api.method.StatusMethods;

/**
 * @author hrupp
 */
public class NewTootView extends TitledPane {

  @FXML
  public Label outcome;
  @FXML public MenuButton vMenu;
  @FXML public Button toot_button;
  @FXML public Button uploadButton;
  private String originalId;
  private String replyToAccount;

  private Status.Visibility visibility = Status.Visibility.Public;
  private File uploadFile;
  private Server server;


  @FXML
  public void initialize() {

    // TODO this is not working as expected
    //    see also tvCallback() below
    var contentBinding = textarea.textProperty().isEmpty();
    toot_button.disableProperty().isEqualTo(contentBinding);

    toot_button.getStyleClass().add("btn");
    toot_button.getStyleClass().add("btn-primary");

    uploadButton.getStyleClass().add("btn");
    uploadButton.getStyleClass().add("btn-default");

    vMenu.getStyleClass().add("btn-group-vertical");
    vMenu.getStyleClass().add("btn");


  }

  /*
   * Callback from newToot.fxml Toot-button
   */
  @FXML
  public void toot(ActionEvent event) {
    System.out.println(event);
    System.out.println(textarea.getParagraphs());

    String text = textarea.getText();
    if (text.isBlank()) {
      outcome.setText("Not tooted, text is empty");
      return;
    }


    MastodonClient client = getServerClient();

    StatusMethods statuses = client.statuses();
    try {
      String inReplyToId = null;
      if (originalId != null) {
        inReplyToId = originalId;
      }

      List<String> mediaIds = null;
      if (uploadFile != null) {
        MediaMethods media = client.media();

        var mediaType = getMediaTypeFromFile(uploadFile);
        var mReq = media.uploadMedia(uploadFile, mediaType.toString());

        MediaAttachment uploadedFile = mReq.execute();

        mediaIds = new ArrayList<>(1);
        mediaIds.add(uploadedFile.getId());
      }


      var req = statuses.postStatus(text,
                                    visibility,
                                    inReplyToId, // inReply
                                    mediaIds // mediaIds

         );



      var res = req.execute();
      System.out.println("Posted with id " + res.getId());
      outcome.setText("Posted with id " + res.getId());
    } catch (BigBoneRequestException e) {
      e.printStackTrace();  // TODO: Customise this generated block
      outcome.setText("Error: " + e.getMessage());
    }

  }

  private MediaType getMediaTypeFromFile(File uploadFile) {
    var i = uploadFile.getName().lastIndexOf('.');
    var ending = uploadFile.getName().substring(i+1);
    MediaType mediaType = switch (ending) {
      case "png" ->
        MediaType.get("image/png");
      case "jpg", "jpeg" ->
        MediaType.get("image/jpg");
      case "gif" ->
        MediaType.get("image/gif");
      default ->
        MediaType.get(ending);
    };
    return mediaType;
  }

  @FXML
  public TextArea textarea;


  public void setInReplyToId(String originalId) {
    this.originalId = originalId;
  }

  public void setInReplyToAccount(String replyToAccount) {
    this.replyToAccount = replyToAccount;
    textarea.setText(this.replyToAccount);
  }

  public void setVisibility(ActionEvent actionEvent) {

    vMenu.setText(((MenuItem)actionEvent.getTarget()).getText());

    switch ((String) (((MenuItem) actionEvent.getTarget()).getUserData())) {
      case "PR" -> visibility = Status.Visibility.Private;
      case "DI" -> visibility = Status.Visibility.Direct;
      case "UN" -> visibility = Status.Visibility.Unlisted;
      default -> visibility = Status.Visibility.Public;
    }
  }

  public void tvCallback(KeyEvent keyEvent) {
    toot_button.setDisable(textarea.getText().isEmpty());

  }

  public void uploadImage(ActionEvent actionEvent) {
    FileChooser fc = new FileChooser();
    File file = fc.showOpenDialog(uploadButton.getScene().getWindow());
    if (file!=null) {

      System.out.println(file.getAbsolutePath());
    }
    this.uploadFile = file;

  }

  public void setServer(Server server) {
    this.server = server;
  }

  MastodonClient getServerClient() {
    if (this.server != null && !this.server.isAnonymous()) {
      return this.server.client;
    }
    return MastoMain.getDefaultServer().client;
  }
}
