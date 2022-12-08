/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.bsd.mastofx;

import com.sys1yagi.mastodon4j.api.Pageable;
import com.sys1yagi.mastodon4j.api.entity.Status;
import com.sys1yagi.mastodon4j.api.method.Timelines;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * @author hrupp
 */
public class MainView {

  @FXML
  public ListView<Status> tootListView;

  public MainView() {

  }

  public void showList() {
    List<Status> itemList = getStatusesForTimeline();

    ObservableList<Status> items = FXCollections.observableList(itemList);

    if (tootListView != null) {
      tootListView.setItems(items);
      tootListView.setCellFactory(param -> {
        var cell =  new StatusTextFieldListCell();
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

  class StatusTextFieldListCell extends TextFieldListCell<Status> {
    public StatusTextFieldListCell() {
      super(new StatusStringConverter());
    }
  }

  @FXML
  public void newToot(Event event) throws IOException {
    Parent tootBox = MastoMain.loadFxmlFile("toot-box").parent;

    Stage stage = new Stage();
    Scene sc = new Scene(tootBox, 500,300);

    stage.setScene(sc);
    stage.show();
  }

  List<Status> getStatusesForTimeline() {

    Timelines timelines = new Timelines(MastoMain.getMastodonClient());

    try {

      Pageable<Status> statuses = timelines.getHome().execute();

      return statuses.getPart();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

}