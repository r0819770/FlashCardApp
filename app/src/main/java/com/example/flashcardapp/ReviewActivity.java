package com.example.flashcardapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReviewActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference decksRef = db.collection("decks");

    private Button showBackButton;
    private Button stopReviewButton;
    private Button reviewNextButton;
    private TextView reviewFrontTextView;
    private TextView reviewBackTextView;
    private String deckName;
    private String deckId;
    private ItemCard chosenCard;
    private List<ItemCard> cardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        showBackButton = (Button) findViewById(R.id.showBackButton);
        reviewFrontTextView = (TextView) findViewById(R.id.reviewFrontTextView);
        reviewBackTextView = (TextView) findViewById(R.id.reviewBackTextView);
        stopReviewButton = (Button) findViewById(R.id.stopReviewButton);
        reviewNextButton = (Button) findViewById(R.id.reviewNextButton);

        Intent intent = this.getIntent();

        if (intent != null) {
            deckName = intent.getStringExtra("name");
            deckId = intent.getStringExtra("id");
        }

        getRandomCard();

        showBackButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        showBack(chosenCard);
                    }
                });

        stopReviewButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        finish();
                    }
                });

        reviewNextButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        getRandomCard();
                    }
                });
    }

    public void getRandomCard(){
        reviewBackTextView.setText("");
        decksRef.document(deckId).collection("extra collection").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            cardList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ItemCard itemCard = document.toObject(ItemCard.class);
                                itemCard.setId(document.getId());
                                cardList.add(itemCard);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        Collections.shuffle(cardList);
                        chosenCard = cardList.get(0);
                        showFront(chosenCard);
                    }
                });
    }

    public void showBack(ItemCard itemCard){
        reviewNextButton.setVisibility(View.VISIBLE);
        showBackButton.setVisibility(View.GONE);
        reviewBackTextView.setText(itemCard.back);
    }

    public void showFront(ItemCard itemcard){
        reviewNextButton.setVisibility(View.GONE);
        showBackButton.setVisibility(View.VISIBLE);
        reviewFrontTextView.setText(itemcard.front);
    }
}