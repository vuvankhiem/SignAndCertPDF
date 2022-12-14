package com.team7.signandcertpdf.controller;

import com.team7.signandcertpdf.util.Constant;
import com.team7.signandcertpdf.util.Util;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class KeyStoreController implements Initializable {
    public TextField alias;
    public TextField dname_1;
    public TextField dname_2;
    public TextField dname_3;
    public TextField dname_4;
    public TextField dname_5;
    public TextField dname_6;
    public PasswordField retypePassword;
    public PasswordField password;
    public TextArea script;
    public Label notification;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        onchangeEvent(alias);
        onchangeEvent(dname_1);
        onchangeEvent(dname_2);
        onchangeEvent(dname_3);
        onchangeEvent(dname_4);
        onchangeEvent(dname_5);
        onchangeEvent(dname_6);
        onchangeEvent(password);
        onchangeEvent(retypePassword);

    }
    @FXML
    public void copyScript(ActionEvent actionEvent) {
        if (script.getText().isEmpty() && !validate()) {
            Util.displayNotification(notification,"Copy fail !", "danger");
        } else {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(script.getText());
            clipboard.setContent(content);
            Util.displayNotification(notification,"Copied !", "success");
        }
    }
    @FXML
    public void executeScript(ActionEvent actionEvent) {
        String command = script.getText();
        if (command.isEmpty()) {
            Util.displayNotification(notification,"Execute fail !", "danger");
        } else {
            ProcessBuilder processBuilder = new ProcessBuilder( "cmd.exe" ,"/c" ,command);
            processBuilder.redirectErrorStream(true);
            try {
                processBuilder.start();
                Util.displayNotification(notification,"Execute successfully !", "success");
            } catch (IOException e) {
                Util.displayNotification(notification,"Fail : " + e.getMessage(), "danger");
            }
        }
    }
    @FXML
    public void clearAllField(ActionEvent actionEvent) {
        alias.setText("");
        dname_1.setText("");
        dname_2.setText("");
        dname_3.setText("");
        dname_4.setText("");
        dname_5.setText("");
        dname_6.setText("");
        password.setText("");
        retypePassword.setText("");
        script.setText("");
        notification.setText("");
    }
    private void onchangeEvent (TextField field) {
        field.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if (validate()) {
                    script.setText(generateScript());
                } else {
                    script.setText("");
                }
            }
        });
    }
    private boolean validate () {
        if (alias.getText().isEmpty()) {
            Util.displayNotification(notification,"- alias can't be empty !", "danger");
            return false;
        }
        if (alias.getText().contains(" ")) {
            Util.displayNotification(notification,"- alias cannot contain space characters !", "danger");
            return false;
        }
        if (dname_1.getText().isEmpty() || dname_2.getText().isEmpty() || dname_3.getText().isEmpty() || dname_4.getText().isEmpty() || dname_5.getText().isEmpty() || dname_6.getText().isEmpty()) {
            Util.displayNotification(notification,"-dname can't be empty !", "danger");
            return false;
        }
        if (password.getText().isEmpty() || retypePassword.getText().isEmpty()) {
            Util.displayNotification(notification,"Password field or Retype password field can't be empty !","danger");
            return false;
        }
        if (!password.getText().equals(retypePassword.getText())) {
            Util.displayNotification(notification,"Password and Retype password don't match !","danger");
            return false;
        }
        notification.setText("");
        return true;
    }
    private String generateScript () {
        String storePath = Constant.keyStorePath + File.separator + alias.getText() + ".pfx";
        return "keytool -genkeypair -alias " + alias.getText()
                + " -keyalg RSA -keystore " + storePath
                + " -storetype PKCS12 -storepass " + password.getText()
                + " -validity 730 -keysize 2048 -dname \"CN=" + dname_1.getText()
                +", OU=" + dname_2.getText()
                +", O=" + dname_3.getText()
                +", L=" + dname_4.getText()
                +", ST=" + dname_5.getText()
                +", C=" + dname_6.getText()
                +"\"";
    }



}
