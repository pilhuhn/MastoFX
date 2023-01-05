
package de.bsd.mastofx;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import social.bigbone.MastodonClient;
import social.bigbone.api.entity.Attachment;
import social.bigbone.api.entity.Status;
import social.bigbone.api.exception.BigboneRequestException;
import social.bigbone.api.method.Media;
import social.bigbone.api.method.Statuses;

/**
 * @author hrupp
 */
public class NewTootView extends TitledPane {

  @FXML
  public Label outcome;
  @FXML public MenuButton vMenu;
  @FXML public Button toot_button;
  @FXML public Button uploadButton;
  private Scene scene;
  private String originalId;
  private String replyToAccount;

  private Status.Visibility visibility = Status.Visibility.Public;
  private File uploadFile;


  @FXML
  public void initialize() {

    // TODO this is not working as expected
    //    see also tvCallback() below
    var contentBinding = textarea.textProperty().isEmpty();
    toot_button.disableProperty().isEqualTo(contentBinding);

  }

  /*
   * Callback from newToot.fxml Toot-button
   */
  @FXML
  public void toot(javafx.event.ActionEvent event) {
    System.out.println(event);
    System.out.println(textarea.getParagraphs());

    String text = textarea.getText();
    if (text.isBlank()) {
      outcome.setText("Not tooted, text is empty");
      return;
    }

    MastodonClient client = MastoMain.getMastodonClient();

    Statuses statuses = new Statuses(client);
    try {
      String inReplyToId = null;
      if (originalId != null) {
        inReplyToId = originalId;
      }

      List<String> mediaIds = null;
      if (uploadFile != null) {
        Media media = new Media(client);


        var mediaType = getMediaTypeFromFile(uploadFile);

        RequestBody body = RequestBody.create(mediaType, uploadFile);
        MultipartBody.Part pFile = MultipartBody.Part.createFormData("file", uploadFile.getName(), body);
        var mReq = media.postMedia(pFile);

        Attachment uploadedFile = mReq.execute();

        System.out.println(uploadedFile);
        mediaIds = new ArrayList<>(1);
        mediaIds.add(uploadedFile.getId());
      }


      var req = statuses.postStatus(text,
                                    inReplyToId, // inReply
                                    mediaIds, // mediaIds
                                    false, // sensitive
                                    null, // Spoiler Text
                                    visibility
         );



      var res = req.execute();
      System.out.println("Posted with id " + res.getId());
      outcome.setText("Posted with id " + res.getId());
    } catch (BigboneRequestException e) {
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
}
