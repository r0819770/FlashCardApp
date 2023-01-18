package com.example.flashcardapp;

public class User {
    public String email;
    public String password;

    public User(){
        //empty
    }

    public User(String email, String password){
        this.email= email;
        this.password = password;
    }
}
