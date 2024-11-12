package it.uniroma3.searchEngineLucene.service.utils;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceTokenizerFactory;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilterFactory;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilterFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.io.IOException;

public class AnalyzerUtil {

    private static final Analyzer standardAnalyzer = new StandardAnalyzer(new Stopwords().getStopWords());

    private static final Analyzer customAnalyzer = createCustomAnalyzer();

    private static  final Analyzer englishAnalyzer = new EnglishAnalyzer(new Stopwords().getStopWords());

    private static Analyzer createCustomAnalyzer() {
        try {
            return CustomAnalyzer.builder()
                    .withTokenizer(WhitespaceTokenizerFactory.class)
                    .addTokenFilter(LowerCaseFilterFactory.class)
                    //.addTokenFilter(WordDelimiterGraphFilterFactory.class)
                    .addTokenFilter(ASCIIFoldingFilterFactory.class)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create custom analyzer", e);
        }
    }

    public static Analyzer getCustomAnalyzer() {
        return customAnalyzer;
    }

    public static Analyzer getStandardAnalyzer() {
        return standardAnalyzer;
    }

    public static Analyzer getEnglishAnalyzer() {
        return englishAnalyzer;
    }
}