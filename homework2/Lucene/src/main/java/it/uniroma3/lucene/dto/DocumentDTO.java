package it.uniroma3.lucene.dto;

public class DocumentDTO {

    private String title;

    private String authors;

    private String content;

    public DocumentDTO(String title, String authors, String content) {
        this.title = title;
        this.authors = authors;
        this.content = content;
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
}
