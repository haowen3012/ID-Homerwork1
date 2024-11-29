package it.uniroma3.lucene.searchEngineLucene.service;


import it.uniroma3.lucene.searchEngineLucene.service.utils.AnalyzerUtil;
import it.uniroma3.lucene.searchEngineLucene.service.utils.TableExtractorUtil;
import it.uniroma3.lucene.searchEngineLucene.service.utils.JsonValidatorUtil;
import it.uniroma3.lucene.searchEngineLucene.service.utils.PropUtil;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
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


        perFieldAnalyzers.put("caption", AnalyzerUtil.getStandardAnalyzer());
        perFieldAnalyzers.put("table", AnalyzerUtil.getStandardAnalyzer());
        perFieldAnalyzers.put("footnotes", AnalyzerUtil.getStandardAnalyzer());
        perFieldAnalyzers.put("references", AnalyzerUtil.getStandardAnalyzer());

        Analyzer perFieldAnalyzer = new PerFieldAnalyzerWrapper(new EnglishAnalyzer(), perFieldAnalyzers);

        IndexWriterConfig config = new IndexWriterConfig(perFieldAnalyzer);
        config.setCodec(new SimpleTextCodec());
        IndexWriter writer = new IndexWriter(directory, config);

        // Directory containing HTML files
        String jsonDirPath = PropUtil.getProperty("json.directory.path");

        AtomicInteger totalNumTables = new AtomicInteger();


        // List and process all HTML files in the directory
        try (Stream<Path> paths = Files.walk(Paths.get(jsonDirPath))) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    .forEach(path -> {
                        // Validate and process each HTML file
                        String filePath = path.toString();

                        if (JsonValidatorUtil.isValidJson(filePath)) {
                            int numTablesPerJson = 0;
                            for (JSONObject tableObject : TableExtractorUtil.getTableObjects(filePath)) {
                                numTablesPerJson++;
                                totalNumTables.getAndIncrement();
                                String caption = tableObject.optString("caption");
                                Object table = tableObject.opt("table");
                                JSONArray footnotes = tableObject.optJSONArray("footnotes");
                                JSONArray references = tableObject.optJSONArray("references");

                                Document doc = new Document();
                                doc.add(new TextField("caption", caption, Field.Store.YES));

                                if (table instanceof JSONArray tableArray) {
                                    for (int i = 0; i < tableArray.length(); i++) {
                                        doc.add(new TextField("table", tableArray.optString(i), Field.Store.YES));
                                    }
                                } else {
                                    doc.add(new TextField("table", tableObject.optString("table"), Field.Store.YES));
                                }

                                if (footnotes != null) {
                                    for (int i = 0; i < footnotes.length(); i++) {
                                        doc.add(new TextField("footnotes", footnotes.optString(i), Field.Store.YES));
                                    }

                                    if (references != null) {
                                        for (int i = 0; i < references.length(); i++) {
                                            Object reference = references.get(i);
                                            if (reference instanceof JSONArray nestedReferences) {
                                                for (int j = 0; j < nestedReferences.length(); j++) {
                                                    doc.add(new TextField("references", nestedReferences.optString(j), Field.Store.YES));
                                                }
                                            } else {
                                                doc.add(new TextField("references", references.optString(i), Field.Store.YES));
                                            }
                                        }
                                    }
                                }

                                // Add the document to the index
                                try {
                                    System.out.println("Indexing Table number: " + numTablesPerJson + " from file: " + filePath);
                                    writer.addDocument(doc);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        } else {
                            System.out.println("Invalid JSON: " + filePath);
                        }
                    });
        }

        writer.commit(); // Persist changes to the disk
        writer.close();
        System.out.println("Indexed " + totalNumTables + " tables");
        // End time
        long endTime = System.nanoTime();

        // Calculate elapsed time in nanoseconds
        long elapsedTime = endTime - startTime;

        // Convert to minutes
        double elapsedTimeInMinutes = elapsedTime / 1_000_000_000.0 / 60.0;

        System.out.println("Elapsed time: " + elapsedTimeInMinutes + " minutes");

    }
}
