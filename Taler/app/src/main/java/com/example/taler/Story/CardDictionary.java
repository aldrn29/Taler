package com.example.taler.Story;

public class CardDictionary {
    //Todo 이미지 정보 타입
    private int id;
    private String title;
    private String genre;
    //    private im

    final String[] title_data = {"Alice", "Harry Potter", "Snow White"};
    final String[] genre_data = {"fairy tale", "fantasy novel", "fairy tale"};

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() { return this.genre; }

    public CardDictionary(int num) {
        this.id = num;
        this.title = title_data[num];
        this.genre = genre_data[num];
    }
}
