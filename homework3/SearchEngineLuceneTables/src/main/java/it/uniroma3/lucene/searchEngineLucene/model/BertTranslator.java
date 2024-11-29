package it.uniroma3.lucene.searchEngineLucene.model;

import ai.djl.modality.nlp.preprocess.SimpleTokenizer;
import ai.djl.modality.nlp.preprocess.Tokenizer;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.translate.Batchifier;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class BertTranslator implements Translator<String, float[]> {

    private static final Tokenizer tokenizer = new SimpleTokenizer();
    private static final Map<String, Integer> vocabulary = new HashMap<>();

    static {
        try {
            // Load the BERT vocabulary from the vocab.txt file
            BufferedReader reader = Files.newBufferedReader(Paths.get("C:/Users/h.zheng/Documents/Ingegneria dei Dati/ID-Homerworks/homework3/SearchEngineLuceneTables/src/main/model/vocab.txt"));
            String line;
            int index = 0;
            while ((line = reader.readLine()) != null) {
                vocabulary.put(line.trim(), index++);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load BERT vocabulary", e);
        }
    }

    @Override
    public NDList processInput(TranslatorContext ctx, String input) throws Exception {
        // Tokenize the input text
        List<String> tokens = tokenizer.tokenize(input);

        // Convert tokens to indices
        int[] tokenIndices = tokens.stream()
                .mapToInt(token -> vocabulary.getOrDefault(token, vocabulary.get("[UNK]")))
                .toArray();

        // Create an NDArray from the token indices
        NDArray ndArray = ctx.getNDManager().create(tokenIndices);

        // Return the NDList containing the tokenized input
        return new NDList(ndArray);
    }

    @Override
    public float[] processOutput(TranslatorContext ctx, NDList list) throws Exception {
        // Extract the embedding array from the NDList (model output)
        NDArray embeddingArray = list.singletonOrThrow();

        // Return the embedding array as a float array
        return embeddingArray.toFloatArray();
    }

    @Override
    public Batchifier getBatchifier() {
        return Batchifier.STACK;
    }
}