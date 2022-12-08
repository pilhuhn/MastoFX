package de.bsd.mastofx;

import static java.lang.System.exit;

import com.google.gson.Gson;
import com.sys1yagi.mastodon4j.MastodonClient;
import com.sys1yagi.mastodon4j.MastodonRequest;
import com.sys1yagi.mastodon4j.api.Pageable;
import com.sys1yagi.mastodon4j.api.Range;
import com.sys1yagi.mastodon4j.api.entity.Status;
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException;
import com.sys1yagi.mastodon4j.api.method.Public;
import com.sys1yagi.mastodon4j.api.method.Statuses;
import com.sys1yagi.mastodon4j.api.method.Timelines;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import okhttp3.OkHttpClient;

/**
 * @author ${USER}
 */
public class Main {
  public static void main(String[] args) {

    Properties props = null;
    try {
      String fileName = System.getProperty("user.home") + "/.mastoFX.properties";
      FileReader fr = new FileReader(fileName);
      props = new Properties();
      props.load(fr);
    } catch (IOException e) {
      e.printStackTrace();  // TODO: Customise this generated block
      exit(1);
    }

    String mastodonServer = props.getProperty("server");
    MastodonClient client =
      new MastodonClient.Builder(mastodonServer, new OkHttpClient.Builder(), new Gson())
        .accessToken(props.getProperty("token"))
        .build();
    Public publicMethod = new Public(client);
    Timelines timelines = new Timelines(client);

    try {
      Pageable<Status> statuses = timelines.getHome()
      //Pageable<Status> statuses = publicMethod.getLocalPublic(new Range(null,null,100))
        .doOnJson(System.out::println)
        .execute();
      statuses.getPart().forEach(status -> {
        System.out.println("=============");
        System.out.println(status.getAccount().getDisplayName() + " (" + status.getAccount().getAcct() + ")" );
        System.out.println(status.getId());
        if (status.getReblog() != null) { // isReblogged() is not reliable
          var reblogged = status.getReblog();

          System.out.println("Reblogged from " + reblogged.getAccount().getDisplayName());
          System.out.println("   " + reblogged.getContent());
        } else {
          System.out.println(status.getContent());
        }
        System.out.println(status.isReblogged() + " " + status.isFavourited());
      });
    } catch (Mastodon4jRequestException e) {
      e.printStackTrace();
    }

  }

}
