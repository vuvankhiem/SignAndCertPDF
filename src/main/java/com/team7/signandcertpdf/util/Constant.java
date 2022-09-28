package com.team7.signandcertpdf.util;

import java.io.File;

public class Constant {

    public final static String storeBasePath = System.getProperty("user.dir") + File.separator + "store";
    public final static String PDFToHtmlPathOut = storeBasePath + File.separator + "pdf";

    public final static String keyStorePath = storeBasePath + File.separator + "keyStore";
}
