package com.example.taler;

public class MediaData {

    private String url;
    private String title;
    private String genre;

    public MediaData(String url, String title, String genre) {
        this.url = url;
        this.title = title;
        this.genre = genre;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }
}
