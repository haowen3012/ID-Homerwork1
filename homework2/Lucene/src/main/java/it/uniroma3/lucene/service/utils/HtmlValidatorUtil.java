package it.uniroma3.lucene.service.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.safety.Safelist;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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