
package de.bsd.mastofx;

import org.jsoup.Jsoup;
import social.bigbone.api.entity.Notification;

/**
 * @author hrupp
 */
public class NotificationStringConverter extends javafx.util.StringConverter<Notification> {
  @Override
  public String toString(Notification notification) {
    StringBuilder sb = new StringBuilder();
    sb.append(notification.getAccount().getDisplayName());

///*
//    if (notification.getReblog() != null) { // isReblogged() is not reliable
//      var reblogged = notification.getReblog();
//
//      sb.append("( RB from ").append(reblogged.getAccount().getDisplayName()).append(")");
//      sb.append(" -> ");
//      String content = reblogged.getContent();
//      sb.append(deHtml(content));
//
//    }
//    else {
//      sb.append("->");
//      sb.append(deHtml(notification.getContent()));
//    }
//*/
    sb.append(" -> ").append(notification.getType());
    if (notification.getStatus() != null) {

      sb.append(" -> ");
      String content = notification.getStatus().getContent();
      sb.append(deHtml(content));
    }

    return sb.toString();
  }

  private String deHtml(String content) {

    return Jsoup.parse(content).text();
  }

  @Override
  public Notification fromString(String string) {
    return null;  // TODO: Customise this generated block
  }
}
