module com.team7.signandcertpdf {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.kordamp.bootstrapfx.core;
    requires buildvu.html;
    requires javafx.base;
    requires com.gluonhq.charm.glisten;

    opens com.team7.signandcertpdf to javafx.fxml;
    exports com.team7.signandcertpdf to javafx.graphics;
    exports com.team7.signandcertpdf.controller;
}