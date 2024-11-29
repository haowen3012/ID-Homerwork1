package it.uniroma3.lucene.searchEngineLucene.service;

import it.uniroma3.lucene.searchEngineLucene.dto.TableDTO;
import it.uniroma3.lucene.searchEngineLucene.service.utils.AnalyzerUtil;
import it.uniroma3.lucene.searchEngineLucene.service.utils.PropUtil;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class Searcher {

    public static List<TableDTO> search(String searchQuery, IndexSearcher searcher) throws Exception {
//
//        BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
//
//        queryBuilder.add( new QueryParser("caption", AnalyzerUtil.getStandardAnalyzer()).parse(searchQuery), BooleanClause.Occur.SHOULD);
//        queryBuilder.add( new QueryParser("table", AnalyzerUtil.getStandardAnalyzer()).parse(searchQuery), BooleanClause.Occur.SHOULD);
//        queryBuilder.add( new QueryParser("footnotes", AnalyzerUtil.getStandardAnalyzer()).parse(searchQuery), BooleanClause.Occur.SHOULD);
//        queryBuilder.add( new QueryParser("references", AnalyzerUtil.getStandardAnalyzer()).parse(searchQuery), BooleanClause.Occur.SHOULD);
//

        String[] fields = {"caption", "table", "footnotes", "references"};
        MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, AnalyzerUtil.getStandardAnalyzer());
        Query query = parser.parse(searchQuery);

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