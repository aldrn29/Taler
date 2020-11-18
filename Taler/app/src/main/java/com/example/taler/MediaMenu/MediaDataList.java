package com.example.taler.MediaMenu;

import java.util.ArrayList;

public class MediaDataList {
    ArrayList<MediaData> items = new ArrayList<>();

    public ArrayList<MediaData> getItems() {

        // 일단 하드코딩 -> firebase storage url 자동으로 가져올 수 있도록 수정하기
        MediaData media_1 = new MediaData("https://firebasestorage.googleapis.com/v0/b/taler-db.appspot.com/o/Media.Menu.List.jpg%2FLoveYourself.JPG?alt=media&token=6fc10bca-e1f2-41f7-b3ef-d6650e5c873f",
                "Love Yourself", "pop music");
        MediaData media_2 = new MediaData("https://firebasestorage.googleapis.com/v0/b/taler-db.appspot.com/o/Media.Menu.List.jpg%2FModern.Family.JPG?alt=media&token=004db1d5-76d0-434c-b054-b619920d55f1",
                "Modern Family", "drama");
        MediaData media_3 = new MediaData("https://firebasestorage.googleapis.com/v0/b/taler-db.appspot.com/o/Media.Menu.List.jpg%2FFriends.jpg?alt=media&token=a91d620a-ffd5-4924-8fe9-ed9cedc5973c",
                "Friends", "drama");

        items.add(media_1);
        items.add(media_2);
        items.add(media_3);

        return items;
    }
}
