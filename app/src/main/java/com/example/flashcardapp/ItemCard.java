package com.example.flashcardapp;

import static java.util.UUID.randomUUID;

import com.google.firebase.firestore.Exclude;

public class ItemCard {

    public String front;
    public String back;
    public String id;

    public String getId(){
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public ItemCard(){
        //empty
    }

    public ItemCard(String front, String back){
        this.front= front;
        this.back = back;
    }

    public String getFront(){
        return front;
    }
    public String getBack(){
        return back;
    }
}
