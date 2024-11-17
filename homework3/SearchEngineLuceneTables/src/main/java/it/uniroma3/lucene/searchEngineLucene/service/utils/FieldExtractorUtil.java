package it.uniroma3.lucene.searchEngineLucene.service.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FieldExtractorUtil {

    public static Elements extractElements(String filePath, String expression) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));

            // Parse the content with Jsoup
            org.jsoup.nodes.Document jsoupDoc = Jsoup.parse(content);

            Elements elements = jsoupDoc.select(expression);

            return elements;

        } catch (Exception e) {
            System.out.println("Error while parsing file: " + filePath);
            return null;
        }
    }

    public static String extractTitle(String filePath) {
        Elements elements = extractElements(filePath, "h1.ltx_title.ltx_title_document");

        String title = "";

        if (elements != null && !elements.isEmpty()) {
            title = elements.get(0).text().replaceAll("\r?\n", "").trim();
        }

        return title;
    }

    public static String extractAuthor(String filePath) {
        List<String> authors = new ArrayList<>();
        String filterOutList = "[^\\p{L}\\p{M}\\-'@ ]";


        Elements elements = extractElements(filePath, "span.ltx_personname");

        for (Element element : elements) {
            String html = element.html()
                    .replaceAll("<br[^>]*>", " ")      // Sostituisce i tag <br> con uno spazio
                    .replaceAll("<sup[^>]*>.*?</sup>", " ")  // Rimuove i tag <sup> e il loro contenuto
                    //.replaceAll("<span[^>]*>.*?</span>", " ") // Rimuove i tag <span> interni
                    .replaceAll("<[^>]+>", " "); // Rimuove eventuali tag html residui


            String[] authorsList = html.split("\\s*(,|;|\\s{2,}| |<br.*?>|\\n|\\\"|\\band\\b|&amp)\\s*");

            for (String author : authorsList) {

                String cleanedString = author.replaceAll(filterOutList, "").trim();

                // Decodes HTML entities
                cleanedString = org.jsoup.parser.Parser.unescapeEntities(cleanedString, true);

                if (!cleanedString.isEmpty()) {
                    authors.add(cleanedString);
                }
            }
        }
        // Join the list of author names into a single string
        return String.join(", ", authors);
    }

    public static String extractContent(String filePath) {

        Elements elements = extractElements(filePath, "body");

        String content = "";
        // Return the body content
        if (elements != null && !elements.isEmpty()) {
            content = elements.text().replaceAll("\r?\n", " ").trim();
        }

        return content;
    }

    public static String extractAbstract(String filePath) {

        Elements elements = extractElements(filePath, "div.ltx_abstract > p.ltx_p");

        String paperAbstract = "";
        // Return the title content
        if (elements != null && !elements.isEmpty()) {
            paperAbstract = elements.text().replaceAll("\r?\n", " ").trim();
        }

        return paperAbstract;
    }

}
