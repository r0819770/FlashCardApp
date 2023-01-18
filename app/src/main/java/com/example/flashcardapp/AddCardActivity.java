package com.example.flashcardapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddCardActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference deckRef = db.collection("decks");

    private EditText addFrontTextInput;
    private EditText addBackTextInput;
    private Button saveCardButton;
    private String currentDeckId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        Intent intent = this.getIntent();

        if (intent != null){
            currentDeckId = intent.getStringExtra("id");

        addFrontTextInput = (EditText) findViewById(R.id.addFrontTextInput);
        addBackTextInput = (EditText) findViewById(R.id.addBackTextInput);
        saveCardButton = (Button) findViewById(R.id.saveCardButton);

        saveCardButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        saveCard();
                    }
                });
        }
    }

    public void saveCard(){
        if ((addFrontTextInput.getText().length()==0) || (addBackTextInput.getText().length()==0)){
            Context context = getApplicationContext();
            CharSequence text = "Both fields must be filled! Try again!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        else {
            String frontText = addFrontTextInput.getText().toString();
            String backText = addBackTextInput.getText().toString();

            ItemCard newCard = new ItemCard(frontText, backText);

            deckRef.document(currentDeckId).collection("extra collection").add(newCard);
            Intent i = new Intent(AddCardActivity.this, DeckActivity.class);

            startActivity(i);
        }
    }
}