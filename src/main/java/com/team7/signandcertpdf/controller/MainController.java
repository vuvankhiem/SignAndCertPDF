package com.team7.signandcertpdf.controller;

import com.team7.signandcertpdf.util.Util;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Util.configuringFileChooser(fileChooser);
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
        String folderName = file.getName().replaceAll("\\.pdf","");
        File checkFolder = new File(System.getProperty("user.dir") + File.separator + "store" +File.separator + folderName);
        if (!checkFolder.exists()) {
            Util.convertPDFToHtml(file.getAbsolutePath());
        }
        pathFile.setText(file.getAbsolutePath());
        WebEngine webEngine = webView.getEngine();
        File f = new File(Util.getIndexFileInFolder(folderName));
        URL url = null;
        try {
            url = f.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        webEngine.load(url.toString());
    }


    public void showKeyStoreForm(ActionEvent actionEvent) {
        Stage oldWindow = Util.getStageByActionEvent(actionEvent);
        Stage newWindow = new Stage();
        newWindow.setTitle("Generate KeyStore");
        newWindow.setResizable(false);
        Util.openScene(oldWindow, newWindow,"views/keyStoreForm.fxml" ,"css/style.css");
    }
}
