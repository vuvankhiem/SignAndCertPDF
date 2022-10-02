package com.team7.signandcertpdf.controller;

import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.PdfSigner;
import com.team7.signandcertpdf.util.Signature;
import com.team7.signandcertpdf.util.Util;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.ResourceBundle;
public class MainController implements Initializable {


    public Button btnCertificate;
    public Button btnSignature;
    private boolean isMaximized = false;
    private final FileChooser fileChooser = new FileChooser();
    @FXML
    public Button maximizeButton;
    @FXML
    public Button minimzeButton;
    @FXML
    public WebView webView;
    @FXML
    public Label pathFile;
    public static WebView webView2;
    public static Label pathFile2;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Util.configuringFileChooser(fileChooser);
        btnCertificate.setDisable(true);
        btnSignature.setDisable(true);
        webView2 = this.webView;
        pathFile2 = this.pathFile;
    }
    public void onCloseWindow(ActionEvent actionEvent) {
        Util.getStageByActionEvent(actionEvent).close();
    }
    public void onMaximizeWindow(ActionEvent actionEvent) {
        Stage stage = Util.getStageByActionEvent(actionEvent);
        if (isMaximized) {
            stage.setMaximized(false);
            isMaximized = false;
            maximizeButton.getStyleClass().remove(2);
        } else {
            stage.setMaximized(true);
            isMaximized = true;
            maximizeButton.getStyleClass().add("minimize-icon");
        }
    }
    public void onMinimizeWindow(ActionEvent actionEvent) {
        Util.getStageByActionEvent(actionEvent).setIconified(true);
    }
    public void onLoadingFile(ActionEvent actionEvent) {
        Stage stage = Util.getStageByActionEvent(actionEvent);
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) {
            return;
        }
        Util.loadingFileOnWebview(file, pathFile, webView);
        btnSignature.setDisable(false);
        btnCertificate.setDisable(false);
        if (pathFile.getText().endsWith("-signature.pdf")) {
            SignatureController._dest2 = pathFile.getText();
        } else {
            SignatureController._dest2 = "";
        }
    }
    public void showKeyStoreForm(ActionEvent actionEvent) {
        Stage oldWindow = Util.getStageByActionEvent(actionEvent);
        Stage newWindow = new Stage();
        newWindow.setTitle("Generate KeyStore");
        newWindow.setResizable(false);
        Util.openScene(oldWindow, newWindow,"views/keyStoreForm.fxml" ,"css/style.css");
    }
    public void showSignPane(ActionEvent actionEvent) {
        Stage oldWindow = Util.getStageByActionEvent(actionEvent);
        Stage newWindow = new Stage();
        newWindow.setTitle("Signature PDF");
        newWindow.setResizable(false);
        Util.openScene(oldWindow, newWindow, "views/signForm.fxml", "css/style.css");
    }

    public void showCertificationInfo(ActionEvent actionEvent) {
        try {
            Stage oldWindow = Util.getStageByActionEvent(actionEvent);
            Stage newWindow = new Stage();
            newWindow.setTitle("Certificate Viewer");
            newWindow.setResizable(false);
            Util.openScene(oldWindow, newWindow, "views/certification.fxml", "css/style.css");
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Document unsigned !");
            alert.showAndWait();
        }
    }
}
