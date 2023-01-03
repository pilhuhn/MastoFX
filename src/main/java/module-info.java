/**
 * @author hrupp
 */
module MastoFX {
  requires javafx.controls;
  requires javafx.fxml;

  requires com.google.gson;
  requires okhttp3;
  requires bigbone;
  requires org.jsoup;
  requires org.kordamp.bootstrapfx.core;
  requires javafx.web;

  // opens org.openjfx to javafx.fxml;
  opens de.bsd.mastofx;
  exports de.bsd.mastofx;
}
