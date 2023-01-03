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

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.jsoup.Jsoup;
import social.bigbone.api.entity.Attachment;
import social.bigbone.api.entity.Status;

/**
 * @author hrupp
 */
public class MainListItemCell extends ListCell<Status> {

    @FXML
    AnchorPane listItem;

    @FXML
    TextField fromField;

    @FXML TextField reblogBy;

    @FXML
    TextArea content;

    @FXML
    ImageView imageView;

    private FXMLLoader mLLoader;

    public MainListItemCell() {
    }


    @Override
    protected void updateItem(Status item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
            setText(null);

            return;
        }

        if (mLLoader == null) {
            mLLoader = new FXMLLoader(getClass().getResource("mainListItem.fxml"));
            mLLoader.setController(this);

            try {
                mLLoader.load();
                System.out.println("List cell loaded");
            } catch (IOException e){
                e.printStackTrace();  // TODO: Customise this generated block
            }
        }
        content.setText(deHtml(item.getContent()));

        Status contentItem;

        if (item.getReblog() != null) { // isReblogged() is not reliable
            var reblogged = item.getReblog();
            content.setText(deHtml(reblogged.getContent()));
            fromField.setText(reblogged.getAccount().getDisplayName()+ " (" + reblogged.getAccount().getAcct() + ")" );
            reblogBy.setText(item.getAccount().getDisplayName() + " (" + item.getAccount().getAcct() + ")");
            contentItem = reblogged;
        }
        else {
            reblogBy.setText(null);
            fromField.setText(item.getAccount().getDisplayName() + " (" + item.getAccount().getAcct() + ")");
            contentItem = item;
        }

        if (!contentItem.getMediaAttachments().isEmpty()) {
            Attachment attachment = contentItem.getMediaAttachments().get(0);
            if (attachment.getType().equals("image")) {
                Image image = new Image(attachment.getPreviewUrl(), true);
                imageView.setImage(image);
            } else {
                // TODO more types?
                imageView.setImage(null);
            }
        }
        else {
            imageView.setImage(null);
        }

        setText(null);
        setGraphic(listItem);
    }

    private String deHtml(String content) {

        return Jsoup.parse(content).text();
      }
}
