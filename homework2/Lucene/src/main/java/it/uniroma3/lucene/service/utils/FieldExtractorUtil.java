package it.uniroma3.lucene.service.utils;

import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FieldExtractorUtil {

    public static NodeList extractNodeList(String filePath, String expression) {
        try {
            // Read the file content
            String content = new String(Files.readAllBytes(Paths.get(filePath)));

            // Parse the content with Jsoup
            org.jsoup.nodes.Document jsoupDoc = Jsoup.parse(content, "", Parser.xmlParser());

            // Convert Jsoup document to W3C document
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(jsoupDoc.outerHtml())));

            // Create XPath object
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();



            XPathExpression xPathExpression = xPath.compile(expression);
            NodeList nodeList = (NodeList) xPathExpression.evaluate(doc, XPathConstants.NODESET);

            return nodeList;

        } catch (Exception e) {
            System.out.println("Error while parsing file: " + filePath);
            return null;
        }
    }

    public static String extractTitle(String filePath) {

        NodeList nodeList = extractNodeList(filePath, "//h1[@class='ltx_title ltx_title_document']");

        String title = "";
        // Return the title content
        if (nodeList != null && nodeList.getLength() > 0) {
            title = nodeList.item(0).getTextContent().replaceAll("/r?/n", "").trim();
        }

        return title;
    }

    public static List<String> extractAuthor(String filePath) {

        NodeList nodeList = extractNodeList(filePath,
                "//span[@class='ltx_personname']/text()[not(ancestor::sup)] | //span[@class='ltx_personname']//span/text()[not(ancestor::sup)]");

        List<String> authors = new ArrayList<>();

        if (nodeList != null) {
            // Return the title content
            for (int i = 0; i < nodeList.getLength(); i++) {
                String author = nodeList.item(i).getTextContent()
                        .replaceAll("[,/r?/n]+", "")  // Remove commas, newlines, and carriage returns// Replace multiple spaces with a single space
                        .trim();
                if (!author.isEmpty()) {
                    authors.add(author);
                }
            }
        }
        return authors;
    }

    public static String extractContent(String filePath) {

        NodeList nodeList = extractNodeList(filePath, "//body/text()");

        String content = "";
        // Return the title content
        if (nodeList != null && nodeList.getLength() > 0) {
            content = nodeList.item(0).getTextContent().replaceAll("/r?/n", "").trim();
        }

        return content;
    }

}
