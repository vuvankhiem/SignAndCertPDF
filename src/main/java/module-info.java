module com.team7.signandcertpdf {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.kordamp.bootstrapfx.core;
    requires buildvu.html;
    requires javafx.base;
    requires com.gluonhq.charm.glisten;
    requires kernel;
    requires sign;
    requires org.bouncycastle.provider;
    requires java.desktop;
    requires java.base;
    requires jdk.unsupported;
    opens com.team7.signandcertpdf.object;
    opens com.team7.signandcertpdf.controller;
    opens com.team7.signandcertpdf to javafx.fxml;
    exports com.team7.signandcertpdf to javafx.graphics;
    exports com.team7.signandcertpdf.controller;
}