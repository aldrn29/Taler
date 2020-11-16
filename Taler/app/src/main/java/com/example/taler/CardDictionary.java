package com.example.taler;

public class CardDictionary {
    //Todo 이미지 정보 타입
    private String id;
    private String title;
//    private im


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public CardDictionary(String id, String title) {
        this.id = id;
        this.title = title;
    }
}
