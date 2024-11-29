package it.uniroma3.lucene.searchEngineLucene.evaluation;

import it.uniroma3.lucene.searchEngineLucene.dto.TableDTO;

import java.util.List;

public class MeanReciprocalRanking {

    // Calcola il Reciprocal Rank (RR) per una singola query
    public static double calculateReciprocalRank(List<TableDTO> results, String relevantDocument) {
        for (int i = 0; i < results.size(); i++) {
            if (results.get(i).getCaption().equals(relevantDocument)) {
                return 1.0 / (i + 1);
            }
        }
        return 0.0;
    }

    // Calcola il Mean Reciprocal Ranking (MRR) per un insieme di query
    public static double calculateMeanReciprocalRank(List<List<TableDTO>> allResults, List<String> relevantDocuments) {
        double sumReciprocalRanks = 0.0;
        for (int i = 0; i < allResults.size(); i++) {
            sumReciprocalRanks += calculateReciprocalRank(allResults.get(i), relevantDocuments.get(i));
        }
        return sumReciprocalRanks / allResults.size();
    }
}


