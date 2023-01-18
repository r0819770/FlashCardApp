package com.example.flashcardapp;

import static java.util.UUID.randomUUID;

import com.google.firebase.firestore.Exclude;

public class Deck {
    public String name;
    public String id;

    public Deck(){
        //empty
    }

    public String getId(){
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public Deck(String name){
        this.name= name;
    }

    public String getName(){
        return name;
    }
}
