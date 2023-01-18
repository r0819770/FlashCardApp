package com.example.flashcardapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditCardActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference deckRef = db.collection("decks");

    private String cardId;
    private String deckId;
    private EditText editFrontTextInput;
    private EditText editBackTextInput;
    private Button editCardButton;
    private Button deleteCardButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_card);

        editCardButton = (Button) findViewById(R.id.editCardButton);
        deleteCardButton = (Button) findViewById(R.id.deleteCardButton);
        editBackTextInput = (EditText) findViewById(R.id.editBackTextInput);
        editFrontTextInput = (EditText) findViewById(R.id.editFrontTextInput);

        Intent intent = this.getIntent();

        if (intent != null) {
            editFrontTextInput.setText(intent.getStringExtra("front"));
            editBackTextInput.setText(intent.getStringExtra("back"));
            cardId = intent.getStringExtra("cardId");
            deckId = intent.getStringExtra("deckId");
        }

        editCardButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        updateCard();
                    }
                });

        deleteCardButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        deckRef.document(deckId).collection("extra collection").document(cardId).delete();
                        finish();
                    }
                });
    }

    public void updateCard(){
        if ((editFrontTextInput.getText().length()==0) || (editBackTextInput.getText().length()==0)){
            Context context = getApplicationContext();
            CharSequence text = "Both fields must be filled! Try again!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        else {
            ItemCard updatedCard = new ItemCard(editFrontTextInput.getText().toString(), editBackTextInput.getText().toString());
            deckRef.document(deckId).collection("extra collection").document(cardId).set(updatedCard);
            finish();
        }
    }
}