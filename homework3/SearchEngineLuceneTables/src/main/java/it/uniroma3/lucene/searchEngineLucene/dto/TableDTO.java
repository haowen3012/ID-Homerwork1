package it.uniroma3.lucene.searchEngineLucene.dto;

public class TableDTO {

    private String caption;

    private String table;

    private String footnotes;
    private String references;

    private float score;

    private int rankingPosition;

    public TableDTO(String caption, String table, String footnotes, String references, float score, int rankingPosition) {
        this.caption = caption;
        this.table = table;
        this.footnotes = footnotes;
        this.references = references;
        this.score = score;
        this.rankingPosition = rankingPosition;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getFootnotes() {
        return footnotes;
    }

    public void setFootnotes(String footnotes) {
        this.footnotes = footnotes;
    }

    public String getReferences() {
        return references;
    }

    public void setReferences(String references) {
        this.references = references;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public int getRankingPosition() {
        return rankingPosition;
    }

    public void setRankingPosition(int rankingPosition) {
        this.rankingPosition = rankingPosition;
    }
}
