package it.uniroma3.searchEngineLucene.service;

import it.uniroma3.searchEngineLucene.service.utils.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.codecs.simpletext.SimpleTextCodec;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Indexer {


    public static void index() throws Exception {

        // Start time
        long startTime = System.nanoTime();

        // Define where to save Lucene index
        Directory directory = FSDirectory.open(Paths.get(PropUtil.getProperty("lucene.index.path")));

        Map<String, Analyzer> perFieldAnalyzers = new HashMap<>();


        perFieldAnalyzers.put("title", AnalyzerUtil.getCustomAnalyzer());
        perFieldAnalyzers.put("authors", AnalyzerUtil.getCustomAnalyzer());
        perFieldAnalyzers.put("content", AnalyzerUtil.getStandardAnalyzer());
        perFieldAnalyzers.put("abstract", AnalyzerUtil.getEnglishAnalyzer());

        Analyzer perFieldAnalyzer = new PerFieldAnalyzerWrapper(new EnglishAnalyzer(), perFieldAnalyzers);

        // Definiamo la configurazione dell' IndexWriter
        IndexWriterConfig config = new IndexWriterConfig(perFieldAnalyzer);
        config.setCodec(new SimpleTextCodec());
        IndexWriter writer = new IndexWriter(directory, config);

        // Directory containing HTML files
        String htmlDirPath = PropUtil.getProperty("html.directory.path");

        AtomicInteger indexedHtmlCounter = new AtomicInteger();


        // List and process all HTML files in the directory
        try (Stream<Path> paths = Files.walk(Paths.get(htmlDirPath))) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".html"))
                    .forEach(path -> {
                        // Validate and process each HTML file
                        String filePath = path.toString();
                        if (HtmlValidatorUtil.isValidHtml(filePath)) {
                            String title = FieldExtractorUtil.extractTitle(filePath);
                            String authors = FieldExtractorUtil.extractAuthor(filePath);
                            String content = FieldExtractorUtil.extractContent(filePath);
                            String paperAbstract = FieldExtractorUtil.extractAbstract(filePath);


                            // Create a new Lucene document
                            Document doc = new Document();
                            doc.add(new TextField("title", title, Field.Store.YES));
                            doc.add(new TextField("authors", authors, Field.Store.YES));
                            doc.add(new TextField("content", content, Field.Store.YES));
                            doc.add(new TextField("abstract", paperAbstract, Field.Store.YES));

                            // Add the document to the index
                            try {
                                System.out.println("Indexing: " + filePath);
                                writer.addDocument(doc);
                                indexedHtmlCounter.set(indexedHtmlCounter.get() + 1);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("Invalid HTML: " + filePath);
                        }
                    });
        }

        writer.commit(); // Persist changes to the disk
        writer.close();
        System.out.println("Indexed " + indexedHtmlCounter.get() + " HTML files");
        // End time
        long endTime = System.nanoTime();

        // Calculate elapsed time in nanoseconds
        long elapsedTime = endTime - startTime;

        // Convert to minutes
        double elapsedTimeInMinutes = elapsedTime / 1_000_000_000.0 / 60.0;

        System.out.println("Elapsed time: " + elapsedTimeInMinutes + " minutes");

    }
}
