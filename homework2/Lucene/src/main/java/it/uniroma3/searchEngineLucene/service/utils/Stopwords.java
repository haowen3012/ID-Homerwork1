package it.uniroma3.searchEngineLucene.service.utils;

import org.apache.lucene.analysis.CharArraySet;

import java.util.Arrays;

public class Stopwords {
    private final CharArraySet STOPWORDS = new CharArraySet(Arrays.asList(
            "a", "an", "the", "in", "on", "and", "or", "of", "for", "with", "to", "from",
            "by", "at", "about", "as", "is", "are", "were", "be", "been", "being", "which",
            "that", "those", "these", "such", "has", "have", "having", "into", "under",
            "between", "over", "more", "less", "one", "two", "three", "new", "study",
            "using", "based", "method", "methods", "toward", "towards", "this",
            "through", "via", "it", "its", "their", "our",
            "we", "can", "could", "would", "will", "may", "might", "do", "does", "did",
            "find", "findings", "show", "shows", "effect", "effects", "model", "models",
            "use", "uses", "evidence", "case", "cases", "some", "many", "several",
            "related", "associated", "impact", "impacts", "implications", "role", "roles",
            "perspective", "perspectives", "review", "reviews",
            // Additional scientific and technical terms
            "study", "research", "experiment", "analysis", "method", "approach", "result",
            "data", "model", "effect", "process", "technique", "system", "example",
            "paper", "report", "figure", "table", "found", "observed", "performed", "conducted"
    ), true);


    public CharArraySet getStopWords() {
        return STOPWORDS;
    }

}
