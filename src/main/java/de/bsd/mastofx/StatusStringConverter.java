
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
