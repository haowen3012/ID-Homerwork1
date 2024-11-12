package it.uniroma3.searchEngineLucene.service;

import it.uniroma3.searchEngineLucene.dto.DocumentDTO;
import it.uniroma3.searchEngineLucene.service.utils.AnalyzerUtil;
import it.uniroma3.searchEngineLucene.service.utils.PropUtil;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.Integer.parseInt;

public class Searcher {

    public static BooleanQuery.Builder createQuery(String searchQuery, String field) throws Exception {


        String[] terms = searchQuery.split("\\s+");
        BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();


        //PhraseQuery
        PhraseQuery.Builder phraseQueryBuilder = new PhraseQuery.Builder();
        for (String term : terms) {
            phraseQueryBuilder.add(new Term(field, term.toLowerCase()));
        }
        phraseQueryBuilder.setSlop(0);
        queryBuilder.add(phraseQueryBuilder.build(), BooleanClause.Occur.SHOULD);

        //FuzzyQuery
        for (String term : terms) {
            queryBuilder.add(new FuzzyQuery(new Term(field, term.toLowerCase()), 1), BooleanClause.Occur.SHOULD);
        }
        //WildCardQuery
        for (String term : terms) {
            if (term.contains("*") || term.contains("?")) {
                queryBuilder.add(new WildcardQuery(new Term(field, term.toLowerCase())), BooleanClause.Occur.SHOULD);
            }
        }

        if (field.equalsIgnoreCase("title")) {
            queryBuilder.add(new QueryParser(field, AnalyzerUtil.getCustomAnalyzer()).parse(searchQuery), BooleanClause.Occur.SHOULD);
        } else if (field.equalsIgnoreCase("author")) {
            queryBuilder.add(new QueryParser("authors", AnalyzerUtil.getCustomAnalyzer()).parse(searchQuery), BooleanClause.Occur.SHOULD);
        } else if (field.equalsIgnoreCase("content")) {
            queryBuilder.add(new QueryParser(field, AnalyzerUtil.getStandardAnalyzer()).parse(searchQuery), BooleanClause.Occur.SHOULD);
        } else if (field.equalsIgnoreCase("abstract")) {
            queryBuilder.add(new QueryParser(field, AnalyzerUtil.getEnglishAnalyzer()).parse(searchQuery), BooleanClause.Occur.SHOULD);
        } else {
            throw new IllegalArgumentException("Field not supported");
        }

        return queryBuilder;
    }


    public static List<DocumentDTO> search(String searchQuery, String searchQuery1, String searchQuery2, String field, IndexSearcher searcher) throws Exception {

        BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();

        if (field.equalsIgnoreCase("titleAndAuthor") && searchQuery1 != null && searchQuery2 != null) { //search in title and author
            queryBuilder.add(createQuery(searchQuery1, "title").build(), BooleanClause.Occur.SHOULD);
            queryBuilder.add(createQuery(searchQuery2, "author").build(), BooleanClause.Occur.MUST);
        } else {
            if (searchQuery != null) {
                queryBuilder = createQuery(searchQuery, field);
            }
        }

        List<DocumentDTO> documents = new ArrayList<>();

        TopDocs hits = searcher.search(queryBuilder.build(), parseInt(PropUtil.getProperty("top.results")));


        for (int i = 0; i < hits.scoreDocs.length; i++) {
            ScoreDoc scoreDoc = hits.scoreDocs[i];
            int docId = scoreDoc.doc;
            float score = scoreDoc.score; // Get the score of the document
            Document doc = searcher.doc(docId);
            DocumentDTO documentDTO = new DocumentDTO(doc.get("title"), doc.get("authors"), doc.get("content"), doc.get("abstract"), score, i + 1);
            documents.add(documentDTO);
        }

        return documents;
    }

}