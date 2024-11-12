package it.uniroma3.searchEngineLucene.dto;

public class DocumentDTO {

    private String title;

    private String authors;

    private String content;
    private String paperAbstract;

    private float score;

    private int rankingPosition;


    public DocumentDTO(String title, String authors, String content, String paperAbstract, float score, int rankingPosiion) {
        this.title = title;
        this.authors = authors;
        this.content = content;
        this.paperAbstract = paperAbstract;
        this.score = score;
        this.rankingPosition = rankingPosiion;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPaperAbstract() {
        return paperAbstract;
    }

    public void setPaperAbstract(String paperAbstract) {
        this.paperAbstract = paperAbstract;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public float getScore() {
        return score;
    }

    public void setRankingPosition(int rankingPosition) {
        this.rankingPosition = rankingPosition;
    }

    public int getRankingPosition() {
        return rankingPosition;
    }

}
