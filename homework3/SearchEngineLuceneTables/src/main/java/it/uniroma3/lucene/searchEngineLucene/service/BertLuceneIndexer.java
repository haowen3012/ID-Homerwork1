package it.uniroma3.lucene.searchEngineLucene.service;

import ai.djl.Application;
import ai.djl.MalformedModelException;
import ai.djl.Model;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import it.uniroma3.lucene.searchEngineLucene.model.BertTranslator;
import it.uniroma3.lucene.searchEngineLucene.service.utils.AnalyzerUtil;
import it.uniroma3.lucene.searchEngineLucene.service.utils.JsonValidatorUtil;
import it.uniroma3.lucene.searchEngineLucene.service.utils.PropUtil;
import it.uniroma3.lucene.searchEngineLucene.service.utils.TableExtractorUtil;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.*;
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

public class BertLuceneIndexer {

    private static Model bertModel;

    public static void index() throws Exception {
        // Start time
        long startTime = System.nanoTime();

        // Load the model
          loadModel();

//        // Define where to save Lucene index
//        Directory directory = FSDirectory.open(Paths.get(PropUtil.getProperty("bert.lucene.index.path")));
//
//        Map<String, Analyzer> perFieldAnalyzers = new HashMap<>();
//        perFieldAnalyzers.put("caption", AnalyzerUtil.getStandardAnalyzer());
//        perFieldAnalyzers.put("table", AnalyzerUtil.getStandardAnalyzer());
//        perFieldAnalyzers.put("footnotes", AnalyzerUtil.getStandardAnalyzer());
//        perFieldAnalyzers.put("references", AnalyzerUtil.getStandardAnalyzer());
//        Analyzer perFieldAnalyzer = new PerFieldAnalyzerWrapper(new EnglishAnalyzer(), perFieldAnalyzers);
//
//        IndexWriterConfig config = new IndexWriterConfig(perFieldAnalyzer);
//        IndexWriter writer = new IndexWriter(directory, config);
//
//        // Initialize BERT model
//        Translator<String, float[]> translator = new BertTranslator();
//        try (Predictor<String, float[]> predictor = bertModel.newPredictor(translator)) {
//            // Directory containing JSON files
//            String jsonDirPath = PropUtil.getProperty("json.directory.path");
//            AtomicInteger indexedHtmlCounter = new AtomicInteger();
//
//            // List and process all JSON files in the directory
//            try (Stream<Path> paths = Files.walk(Paths.get(jsonDirPath))) {
//                paths.filter(Files::isRegularFile)
//                        .filter(path -> path.toString().endsWith(".json"))
//                        .forEach(path -> {
//                            // Validate and process each JSON file
//                            String filePath = path.toString();
//                            if (JsonValidatorUtil.isValidJson(filePath)) {
//                                int numTablesPerJson = 0;
//                                for (JSONObject tableObject : TableExtractorUtil.getTableObjects(filePath)) {
//                                    numTablesPerJson++;
//                                    try {
//                                        // Index each table as a document
//                                        indexTable(writer, predictor, tableObject, filePath, numTablesPerJson);
//                                        indexedHtmlCounter.incrementAndGet();
//                                    } catch (IOException | TranslateException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            } else {
//                                System.out.println("Invalid JSON: " + filePath);
//                            }
//                        });
//            }
//
//            writer.commit(); // Persist changes to disk
//            writer.close();
//
//            System.out.println("Indexed " + indexedHtmlCounter.get() + " tables");
//        }
//
//        // End time
//        long endTime = System.nanoTime();
//        double elapsedTimeInMinutes = (endTime - startTime) / 1_000_000_000.0 / 60.0;
//        System.out.println("Elapsed time: " + elapsedTimeInMinutes + " minutes");
    }

    private static void indexTable(IndexWriter writer, Predictor<String, float[]> predictor, JSONObject tableObject, String filePath, int tableNumber) throws IOException, TranslateException {
        String caption = tableObject.optString("caption");
        Object table = tableObject.opt("table");
        JSONArray footnotes = tableObject.optJSONArray("footnotes");
        JSONArray references = tableObject.optJSONArray("references");

        Document doc = new Document();
        doc.add(new TextField("caption", caption, Field.Store.YES));

        // Generate embedding for caption
        float[] captionEmbedding = predictor.predict(caption);
        addVectorField(doc, "caption_embedding", captionEmbedding);

        if (table instanceof JSONArray tableArray) {
            for (int i = 0; i < tableArray.length(); i++) {
                String tableRow = tableArray.optString(i);
                doc.add(new TextField("table", tableRow, Field.Store.YES));

                // Generate embedding for each table row
                float[] tableRowEmbedding = predictor.predict(tableRow);
                addVectorField(doc, "table_embedding_", tableRowEmbedding);
            }
        } else {
            String tableText = tableObject.optString("table");
            doc.add(new TextField("table", tableText, Field.Store.YES));

            // Generate embedding for table text
            float[] tableEmbedding = predictor.predict(tableText);
            addVectorField(doc, "table_embedding", tableEmbedding);
        }

        if (footnotes != null) {
            for (int i = 0; i < footnotes.length(); i++) {
                String footnote = footnotes.optString(i);
                doc.add(new TextField("footnotes", footnote, Field.Store.YES));

                // Generate embedding for each footnote
                float[] footnoteEmbedding = predictor.predict(footnote);
                addVectorField(doc, "footnotes_embedding_", footnoteEmbedding);
            }
        }

        if (references != null) {
            for (int i = 0; i < references.length(); i++) {
                Object reference = references.get(i);
                if (reference instanceof JSONArray nestedReferences) {
                    for (int j = 0; j < nestedReferences.length(); j++) {
                        String nestedReference = nestedReferences.optString(j);
                        doc.add(new TextField("references", nestedReference, Field.Store.YES));

                        // Generate embedding for each nested reference
                        float[] nestedReferenceEmbedding = predictor.predict(nestedReference);
                        addVectorField(doc, "references_embedding_", nestedReferenceEmbedding);
                    }
                } else {
                    String referenceText = references.optString(i);
                    doc.add(new TextField("references", referenceText, Field.Store.YES));

                    // Generate embedding for reference text
                    float[] referenceEmbedding = predictor.predict(referenceText);
                    addVectorField(doc, "references_embedding_", referenceEmbedding);
                }
            }
        }

        System.out.println("Indexing Table number: " + tableNumber + " from file: " + filePath);
        writer.addDocument(doc);
    }

    private static void addVectorField(Document doc, String fieldName, float[] vector) {
        for (int i = 0; i < vector.length; i++) {
            doc.add(new FloatPoint(fieldName, vector[i]));
            doc.add(new StoredField(fieldName, vector[i]));
        }
    }

    private static void loadModel() {
        System.out.println("Loading BERT model...");

        // Percorso al modello locale
        Path modelDir = Paths.get("C:/Users/h.zheng/Documents/Ingegneria dei Dati/ID-Homerworks/homework3/SearchEngineLuceneTables/src/main/model/model.pt");

        try {
            // Crea i criteri per il caricamento del modello
            Criteria<String, float[]> criteria = Criteria.builder()
                    .optApplication(Application.NLP.TEXT_EMBEDDING) // Specifica che è un'app NLP
                    .setTypes(String.class, float[].class)           // Input e output del modello
                    .optModelPath(modelDir)
                    .optTranslator(new BertTranslator())// Percorso al modello locale
                    .build();

            // Carica il modello con ZooModel
            bertModel = criteria.loadModel();
            System.out.println("Model loaded successfully!");

        } catch (IOException | ModelNotFoundException | MalformedModelException e) {
            System.err.println("Error loading model: " + e.getMessage());
            e.printStackTrace();
        }
    }

}