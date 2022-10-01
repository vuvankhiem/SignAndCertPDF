package com.team7.signandcertpdf.controller;

import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.PdfSigner;
import com.team7.signandcertpdf.util.Signature;
import com.team7.signandcertpdf.util.Util;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TreeMap;

public class SignatureController implements Initializable {
    public ComboBox<String> keyStore;
    public PasswordField password;
    public TextField reason;
    public TextField location;
    public Label notify;
    public Button btnFileChooser;

    private final DirectoryChooser directoryChooser = new DirectoryChooser();
    public TextField dest;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TreeMap<String, String> keyStores = Util.getAllKeyStore();
        for (Map.Entry<String, String> key : keyStores.entrySet()
             ) {
            keyStore.getItems().add(key.getKey());
        }
    }
    public void onSigning(ActionEvent actionEvent) throws IOException, GeneralSecurityException{
        String _password = password.getText();
        String _reason = reason.getText();
        String _location = location.getText();
        String _keystore = Util.getAllKeyStore().get(keyStore.getSelectionModel().getSelectedItem());
        String _dest = dest.getText();
        String _src = MainController.pathFile2.getText();
        if (_password.isEmpty() || _reason.isEmpty() || _location.isEmpty() || _keystore.isEmpty() || _dest.isEmpty()) {
            Util.displayNotification(notify, "Require : fill all fileds !", "danger");
            return;
        }
        File fsrc = new File(_src);
        _dest = _dest + File.separator + fsrc.getName().replaceAll(".pdf","-signature.pdf");
        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
        try {
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(new FileInputStream(_keystore), _password.toCharArray());
            String alias = ks.aliases().nextElement();
            PrivateKey pk = (PrivateKey) ks.getKey(alias, _password.toCharArray());
            Certificate[] chain = ks.getCertificateChain(alias);
            Signature.sign(_src,_dest,chain,pk, DigestAlgorithms.SHA256, provider.getName(), PdfSigner.CryptoStandard.CMS, _reason,_location);
            Util.displayNotification(notify, "Signed successfully !", "success");
            Util.loadingFileOnWebview(new File(_dest), MainController.pathFile2, MainController.webView2);
            //showing confirm dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Open with Adobe Reader");
            alert.setHeaderText("Do you want to open the file with Adobe Acrobat DC?");
            Optional<ButtonType> optional = alert.showAndWait();
            if (optional.isPresent() && optional.get() == ButtonType.OK) {
                Desktop.getDesktop().open(new File(_dest));
            }
        } catch (Exception e) {
            if (e instanceof FileNotFoundException) {
                Util.displayNotification(notify, "File output path invalid !", "danger");
            } else {
                Util.displayNotification(notify, e.getMessage(), "danger");
            }
        }
    }

    public void choosePathOut(ActionEvent actionEvent) {
        Stage stage = Util.getStageByActionEvent(actionEvent);
        File file = directoryChooser.showDialog(stage);
        if (file != null) {
            dest.setText(file.getAbsolutePath());
        }
    }
}
