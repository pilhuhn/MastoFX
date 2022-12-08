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

import com.sys1yagi.mastodon4j.api.entity.Status;

/**
 * @author hrupp
 */
public class StatusStringConverter extends javafx.util.StringConverter<Status> {
  @Override
  public String toString(Status status) {
    StringBuilder sb = new StringBuilder();
    sb.append(status.getAccount().getDisplayName());

    if (status.getReblog() != null) { // isReblogged() is not reliable
      var reblogged = status.getReblog();

      sb.append("=> from ").append(reblogged.getAccount().getDisplayName());
      sb.append(" -> ");
      sb.append(reblogged.getContent());

    }
    else {
      sb.append("->");
      sb.append(status.getContent());
    }

    return sb.toString();
  }

  @Override
  public Status fromString(String string) {
    return null;  // TODO: Customise this generated block
  }
}
