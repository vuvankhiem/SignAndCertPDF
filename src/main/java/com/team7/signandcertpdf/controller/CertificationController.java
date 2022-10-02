package com.team7.signandcertpdf.controller;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.SignatureUtil;
import com.team7.signandcertpdf.object.Detail;
import com.team7.signandcertpdf.util.Util;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.util.encoders.Hex;

import javax.security.auth.x500.X500Principal;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Set;

public class CertificationController implements Initializable {
    public Label label_1;
    public Label label_2;
    public Label label_3;
    public Label label_4;
    public Label label_5;
    public Label label_6;
    public TableView<Detail> table;
    public TableColumn<Object, Object> nameColumn;
    public TableColumn<Object, Object> valueColumn;
    public TextArea txtarea;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            //get info certification in pdf-signature
            File f = new File(SignatureController._dest2);
            PdfReader pdfReader = new PdfReader(f);
            PdfDocument pdfDocument = new PdfDocument(pdfReader);
            SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);
            PdfPKCS7 pdfPKCS7 = signatureUtil.readSignatureData("sig");
            if (pdfPKCS7.verifySignatureIntegrityAndAuthenticity()) {
                //get info certification in keystore
                String author = getDName(pdfPKCS7, 0);
                X509Certificate x509Certificate = pdfPKCS7.getSigningCertificate();
                //display certificate information on screen
                label_1.setText("Signature is VALID, signed by " + author);
                label_2.setText("Issued by : " + author + " - " + getDName(pdfPKCS7, 1));
                label_3.setText("Signing time : " + getDate(pdfPKCS7.getSignDate().getTime()));
                label_4.setText("Valid time : " + getDate(x509Certificate.getNotBefore()) + " - " + getDate(x509Certificate.getNotAfter()));
                label_5.setText("Reason : " + pdfPKCS7.getReason());
                label_6.setText("Location : " + pdfPKCS7.getLocation());
                //get public key
                String publicKey = new String(Hex.toHexString(x509Certificate.getPublicKey().getEncoded()));
                String x509Data = new String(Hex.toHexString(x509Certificate.getEncoded()));
                String sha1 = new String(Hex.toHexString(MessageDigest.getInstance("SHA-1").digest(x509Certificate.getEncoded())));
                String md5 = new String(Hex.toHexString(MessageDigest.getInstance("MD5").digest(x509Certificate.getEncoded())));
                ObservableList<Detail> details = FXCollections.observableArrayList(
                        new Detail("Version", String.valueOf(x509Certificate.getVersion())),
                        new Detail("Signature algorithm", x509Certificate.getSigAlgName()),
                        new Detail("Subject", x509Certificate.getSubjectDN().toString()),
                        new Detail("Issuer", x509Certificate.getIssuerDN().toString()),
                        new Detail("Serial number", formatHex(x509Certificate.getSerialNumber().longValue())),
                        new Detail("Validity start", getDate(x509Certificate.getNotBefore())),
                        new Detail("Validity end", getDate(x509Certificate.getNotAfter())),
                        new Detail("Subject key identifier", getSubKeyIdentify(x509Certificate)),
                        new Detail("Public key", formatHexByString(publicKey)),
                        new Detail("SHA1 digest of public key", getSubKeyIdentify(x509Certificate).replaceAll(" ","")),
                        new Detail("X.509 data", formatHexByString(x509Data)),
                        new Detail("SHA1 digest", formatHexByString(sha1)),
                        new Detail("MD5 digest", formatHexByString(md5))
                );
                table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
                nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
                valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
                table.setItems(details);
                table.setRowFactory(table -> {
                    TableRow<Detail> row = new TableRow<>();
                    row.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            Detail detail = row.getItem();
                            txtarea.setText(detail.getValue());
                        }
                    });
                    return row;
                });
            } else {
                label_1.setText("Signature is INVALID");
            }


        } catch (IOException | GeneralSecurityException ignored) {
            throw new RuntimeException(ignored);
        }
    }
    private String getDName (PdfPKCS7 pdfPKCS7, int index) {
        X500Principal principal = pdfPKCS7.getSigningCertificate().getSubjectX500Principal();
        X500Name x500name = new X500Name(principal.getName());
        return IETFUtils.valueToString(x500name.getRDNs()[index].getFirst().getValue());
    }
    private String getSubKeyIdentify (X509Certificate x509Certificate) {
        Set<String> oids = x509Certificate.getNonCriticalExtensionOIDs();
        byte[] extensionValue = x509Certificate.getExtensionValue(oids.iterator().next());
        byte[] octets = ASN1OctetString.getInstance(extensionValue).getOctets();
        SubjectKeyIdentifier subjectKeyIdentifier = new SubjectKeyIdentifier(octets);
        byte[] keyIdentifier = subjectKeyIdentifier.getKeyIdentifier();
        String keyIdentifierHex = new String(Hex.encode(keyIdentifier));
        return  formatHexByString(keyIdentifierHex.substring(4, keyIdentifierHex.length()));
    }
    private String getDate (Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss XXX");
        return dateFormat.format(date);
    }
    private String formatHex (long val) {
        String valStr = String.format("%X",val);
        valStr = (valStr.length() % 2 == 0 ? "" : "0") + valStr;
        return formatHexByString(valStr);
    }
    private String formatHexByString (String valStr) {
        return String.join(" ", valStr.split("(?<=\\G(..))")).toUpperCase();
    }

    @FXML
    public void onHide(ActionEvent actionEvent) {
        Stage stage = Util.getStageByActionEvent(actionEvent);
        stage.hide();
    }
}
