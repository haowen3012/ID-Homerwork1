package it.uniroma3.lucene.searchEngineLucene.service.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;

public class HtmlValidatorUtil {

    public static boolean isValidHtml(String filePath) {
        try {
            File inputFile = new File(filePath);
            Document document = Jsoup.parse(inputFile,"UTF-8");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}