package com.example.taler.Story;

public class CardDictionary {
    //Todo 이미지 정보 타입
    private int id;
    private String title;
    //    private im

    final String[] title_data = {"Alice", "Harry_potter"};


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public CardDictionary(int num) {
        this.id = num;
        this.title = title_data[num];
    }
}
