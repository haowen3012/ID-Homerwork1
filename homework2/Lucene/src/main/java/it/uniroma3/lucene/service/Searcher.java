package it.uniroma3.lucene.service;

import it.uniroma3.lucene.dto.DocumentDTO;
import it.uniroma3.lucene.service.utils.AnalyzerUtil;
import it.uniroma3.lucene.service.utils.PropUtil;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class Searcher {


    public static List<DocumentDTO> search(String searchQuery, String field, IndexSearcher searcher) throws Exception {

        QueryParser parser;
        Query query;

        if (field.equalsIgnoreCase("title")) {
            PhraseQuery.Builder builder = new PhraseQuery.Builder();
            String[] terms = searchQuery.split(" ");
            for (String term : terms) {
                builder.add(new Term(field, term));
            }
            builder.setSlop(0);
            query = builder.build();
        } else if (field.equalsIgnoreCase("authors")) {
            query = new QueryParser(field, AnalyzerUtil.getCustomAnalyzer()).parse(searchQuery);
        } else {
            query = new QueryParser(field, AnalyzerUtil.getStandardAnalyzer()).parse(searchQuery);
        }


        List<DocumentDTO> documents = new ArrayList<>();

        TopDocs hits = searcher.search(query, parseInt(PropUtil.getProperty("top.results")));


        for (int i = 0; i < hits.scoreDocs.length; i++) {
            ScoreDoc scoreDoc = hits.scoreDocs[i];
            int docId = scoreDoc.doc;
            Document doc = searcher.doc(docId);
            DocumentDTO documentDTO = new DocumentDTO(doc.get("title"), doc.get("authors"), doc.get("content"));
            documents.add(documentDTO);
        }

        return documents;
    }

}