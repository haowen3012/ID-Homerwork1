package it.uniroma3.lucene.searchEngineLucene.service;

import ai.djl.Model;
import ai.djl.inference.Predictor;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import it.uniroma3.lucene.searchEngineLucene.dto.TableDTO;
import it.uniroma3.lucene.searchEngineLucene.model.BertTranslator;
import it.uniroma3.lucene.searchEngineLucene.service.utils.AnalyzerUtil;
import it.uniroma3.lucene.searchEngineLucene.service.utils.PropUtil;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FloatPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class BertLuceneSearcher {

    private final IndexSearcher searcher;
    private final Predictor<String, float[]> predictor;

    public BertLuceneSearcher(String indexPath, String modelPath) throws IOException {
        FSDirectory directory = FSDirectory.open(Paths.get(indexPath));
        DirectoryReader reader = DirectoryReader.open(directory);
        this.searcher = new IndexSearcher(reader);

        Model bertModel = Model.newInstance(modelPath);
        Translator<String, float[]> translator = new BertTranslator();
        this.predictor = bertModel.newPredictor(translator);
    }

    public List<TableDTO> search(String searchQuery) throws TranslateException, IOException {
        // Generate embedding for the search query
        float[] queryEmbedding = predictor.predict(searchQuery);

        // Create a BooleanQuery to search for similar embeddings
        BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
        for (int i = 0; i < queryEmbedding.length; i++) {
            Query dimensionQuery = FloatPoint.newRangeQuery("caption_embedding_", queryEmbedding[i] - 0.1f, queryEmbedding[i] + 0.1f);
            queryBuilder.add(dimensionQuery, BooleanClause.Occur.MUST);
        }
        Query query = queryBuilder.build();

        List<TableDTO> tables = new ArrayList<>();

        TopDocs hits = searcher.search(query, parseInt(PropUtil.getProperty("top.results")));

        for (int i = 0; i < hits.scoreDocs.length; i++) {
            ScoreDoc scoreDoc = hits.scoreDocs[i];
            int docId = scoreDoc.doc;
            float score = scoreDoc.score; // Get the score of the document
            Document doc = searcher.doc(docId);
            TableDTO documentDTO = new TableDTO(doc.get("caption"), doc.get("table"), doc.get("footnotes"), doc.get("references"), score, i + 1);
            tables.add(documentDTO);
        }

        return tables;
    }
}