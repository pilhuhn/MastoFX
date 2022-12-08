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

import com.sys1yagi.mastodon4j.MastodonClient;
import com.sys1yagi.mastodon4j.MastodonRequest;
import com.sys1yagi.mastodon4j.api.entity.Status;
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException;
import com.sys1yagi.mastodon4j.api.method.Media;
import com.sys1yagi.mastodon4j.api.method.Statuses;
import java.io.IOException;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Parent;
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
      System.out.println(res.getId());
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
