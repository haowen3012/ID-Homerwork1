package it.uniroma3.lucene.searchEngineLucene.evaluation;

public class NDCG {

    public static double calculateDCG(int[] input, int k) {
        double dcg = 0.0;
        for (int i = 0; i < k; i++) {
            int rel = input[i];
            dcg += rel / (Math.log(i + 2) / Math.log(2)); // log base 2
//            System.out.println("DCG: "+ (i+1) + " " + rel / (Math.log(i + 2) / Math.log(2)));
        }
        return dcg;
    }

    public static double calculateNDCG(int[] relevance, int[] idealRelevance, int k) {
        double dcg = calculateDCG(relevance, k);
        double idcg = calculateDCG(idealRelevance, k);
        return dcg / idcg;
    }

    public static void main(String[] args) {
        int[][] relevances = {
                {1, 1, 1, 1, 0, 0, 1, 1, 0, 1},
                {0, 1, 1, 0, 1, 1, 1, 1, 1, 1},
                {1, 0, 0, 0, 0, 0, 1, 0, 1, 0},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                {1, 1, 1, 1, 0, 1, 1, 0, 1, 1},
                {1, 0, 0, 1, 0, 1, 0, 0, 0, 0},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {0, 1, 0, 1, 0, 1, 1, 1, 1, 1},
                {1, 1, 1, 0, 0, 0, 0, 0, 1, 0}
        };

        int[][] idealRelevances = {
                {1, 1, 1, 1, 1, 1, 1, 0, 0, 0},
                {1, 1, 1, 1, 1, 1, 1, 0, 0, 0},
                {1, 1, 1, 1, 1, 1, 1, 0, 0, 0},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                {1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {1, 1, 1, 0, 0, 0, 0, 0, 0, 0},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 0, 0, 0},
                {1, 1, 1, 1, 0, 0, 0, 0, 0, 0}
        };

        int k = 10;
        double totalNDCG = 0.0;

        for (int i = 0; i < relevances.length; i++) {
            double ndcg = calculateNDCG(relevances[i], idealRelevances[i], k);
            totalNDCG += ndcg;
            System.out.println("NDCG for relevance set " + (i + 1) + ": " + ndcg);
        }

        double averageNDCG = totalNDCG / relevances.length;
        System.out.println("Average NDCG: " + averageNDCG);



        int[] relevance = {1,1,1,1,0,0,1,1,0,1};
        int[] idealRelevance = {1,1,1,1,1,1,1,0,0,0};
        System.out.println("DCG: " + calculateDCG(relevance, k));
        System .out.println("IDCG: " + calculateDCG(idealRelevance, k));
        System.out.println("NDCG1: " + calculateNDCG(relevance, idealRelevance, k));
    }
}
