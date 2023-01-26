
package de.bsd.mastofx;

import java.io.IOException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;
import social.bigbone.api.Pageable;
import social.bigbone.api.entity.Notification;
import social.bigbone.api.entity.Status;

/**
 * @author hrupp
 */
public class MainView {

  @FXML
  public ListView tootListView;


  public void showList() {
    List<Status> itemList = getStatusesForTimeline();

    ObservableList<Status> items = FXCollections.observableList(itemList);

    if (tootListView != null) {
      tootListView.setItems(items);
      tootListView.setCellFactory(param -> {
        var cell =  new MainListItemCell();
        cell.setOnMouseClicked((evt) -> {
          Status item = cell.getItem();
          System.out.println(item.getId());
          showStatusDetail(item);

        });

        return cell;
      });

    }

  }

  private void showStatusDetail(Status item) {
    ParentAndController pac;
    try {
      pac = MastoMain.loadFxmlFile("tootDetails");
    } catch (IOException e) {
      e.printStackTrace();  // TODO: Customise this generated block
      return;
    }

    Stage stage = new Stage();
    Scene sc = new Scene(pac.parent, 500,300);

    TootDetailView controller = (TootDetailView) pac.controller;
    controller.setStatus(item);
    controller.display();

    stage.setScene(sc);
    stage.show();

  }

  public void showNotifications(ActionEvent actionEvent) {
    List<Notification> itemList = getNotifications();

    ObservableList<Notification> items = FXCollections.observableList(itemList);

    if (tootListView != null) {
      tootListView.getItems().clear();
      tootListView.setItems(items);
      tootListView.setCellFactory(param -> {
        var cell =  new NotificationTextFieldListCell();
        cell.setOnMouseClicked((evt) -> {
          Notification item = cell.getItem();
          System.out.println(item.getId());
          // showStatusDetail(item);

        });

        return cell;
      });

    }

  }

  public void exit(ActionEvent actionEvent) {
    System.exit(0);
  }


  class NotificationTextFieldListCell extends TextFieldListCell<Notification> {
    public NotificationTextFieldListCell() {
      super(new NotificationStringConverter());
    }
  }

  @FXML
  public void newToot(Event event) throws IOException {
    Parent tootBox = MastoMain.loadFxmlFile("newToot").parent;

    Stage stage = new Stage();
    Scene sc = new Scene(tootBox, 500,300);
    sc.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());

    stage.setScene(sc);
    stage.show();
  }

  List<Status> getStatusesForTimeline() {

    var timelines = MastoMain.getMastodonClient().timelines();

    try {

      Pageable<Status> statuses = timelines.getHomeTimeline().execute();

      return statuses.getPart();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  List<Notification> getNotifications() {

    var notifications = MastoMain.getMastodonClient().notifications();

    try {
      Pageable<Notification> notificationPageable = notifications.getNotifications().execute();
      return notificationPageable.getPart();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

}
