package com.example.taler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {

    private MutableLiveData<String> liveText = new MutableLiveData<>();

    public LiveData<String> getText(){
        return liveText;
    }

    public void setText(String text){
        liveText.setValue(text);
    }

}