package com.example.flashcardapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class EditDeckActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference deckRef = db.collection("decks");


    private String deckId;
    private String deckName;
    private EditText editDeckTextInput;
    private Button editDeckButton;
    private Button deleteDeckButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_deck);

        editDeckButton = (Button) findViewById(R.id.editDeckButton);
        deleteDeckButton = (Button) findViewById(R.id.deleteDeckButton);
        editDeckTextInput = (EditText) findViewById(R.id.editDeckTextInput);

        Intent intent = this.getIntent();

        if (intent != null) {
            deckName = intent.getStringExtra("name");
            editDeckTextInput.setText(deckName);
            deckId = intent.getStringExtra("id");
        }

        editDeckButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        updateDeck();
                    }
                });

        deleteDeckButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        AlertDialog dialog = new AlertDialog.Builder(EditDeckActivity.this)
                                .setTitle("Are you sure?")
                                .setMessage("Deleting this deck will remove all your cards in it!")
                                .setNegativeButton("no",null)
                                .setPositiveButton("yes", null)
                                .show();
                        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        positiveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deckRef.document(deckId).delete();
                                finish();
                            }
                        });
                    }
                });
        }



    public void updateDeck(){
        if (editDeckTextInput.getText().length()==0){
            Context context = getApplicationContext();
            CharSequence text = "This cannot be empty! Try again!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        else {
            Deck updatedDeck = new Deck(editDeckTextInput.getText().toString());
            deckRef.document(deckId).set(updatedDeck);

            Intent i = new Intent(EditDeckActivity.this, DeckActivity.class);

            i.putExtra("id", deckId);
            i.putExtra("name", updatedDeck.getName());

            startActivity(i);
            finish();
        }
    }
}