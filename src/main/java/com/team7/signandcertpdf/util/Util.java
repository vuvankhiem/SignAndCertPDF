package com.team7.signandcertpdf.util;

import com.team7.signandcertpdf.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jpedal.examples.html.PDFtoHTML5Converter;
import org.jpedal.exception.PdfException;
import org.jpedal.render.output.IDRViewerOptions;
import org.jpedal.render.output.html.HTMLConversionOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class Util {

    public static void convertPDFToHtml (String pathIn) {
        HTMLConversionOptions conversionOptions = new HTMLConversionOptions();
        IDRViewerOptions viewerOptions = new IDRViewerOptions();
        File pdfFile = new File(pathIn);
        File outputDir = new File(Constant.PDFToHtmlPathOut);
        PDFtoHTML5Converter converter = new PDFtoHTML5Converter(pdfFile, outputDir, conversionOptions, viewerOptions);
        try {
            converter.convert();
        } catch (PdfException e) {
            throw new RuntimeException(e);
        }
    }

    public static Stage getStageByActionEvent (ActionEvent actionEvent) {
        return (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
    }

    public static void configuringFileChooser (FileChooser fileChooser) {
        fileChooser.setInitialDirectory(Paths.get(System.getProperty("user.home")).toFile());
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf", "*.PDF"));
    }

    public static String getIndexFileInFolder (String folder) {
        String masterPath = System.getProperty("user.dir") + File.separator + "store" + File.separator + folder;
        AtomicReference<String> resultPath = new AtomicReference<>("");
        try {
            Stream<Path> pathStream = Files.walk(Paths.get(masterPath));
            pathStream.filter(Files::isRegularFile)
                    .filter(path -> "index.html".equals(path.getFileName().toString()))
                    .forEach(path -> {
                        resultPath.set(path.toString());
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return resultPath.get();
    }

    public static void openScene(Stage window, Stage newWindow, String fxmlResource, String style) {
        try {
            Parent parent = FXMLLoader.load(Objects.requireNonNull(Application.class.getResource(fxmlResource)));
            Scene scene = new Scene(parent);
            newWindow.setScene(scene);
            if (!style.isEmpty() || !style.isBlank()) {
                scene.getStylesheets().add(Objects.requireNonNull(Application.class.getResource(style)).toExternalForm());
            }
            newWindow.initModality(Modality.WINDOW_MODAL);
            newWindow.initOwner(window);
            newWindow.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
