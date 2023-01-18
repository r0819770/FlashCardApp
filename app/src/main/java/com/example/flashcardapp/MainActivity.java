package com.example.flashcardapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference deckRef = db.collection("decks");

    private String deckName;

    private Button addDeckButton;
    private List<String> your_array_list;
    private TextView addCardText;
    private List<Deck> deckList;
    private ListView deckListView;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addCardText = (TextView) findViewById(R.id.addDeckInputText);
        addDeckButton = (Button) findViewById(R.id.addDeckButton);
        deckListView = (ListView) findViewById(R.id.deckListView);

        addDeckButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        addDeck();
                    }
                });

        //navigate to deck
        deckListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, DeckActivity.class);
                Deck currentDeck = deckList.get(position);
                i.putExtra("id", currentDeck.getId());
                i.putExtra("name", currentDeck.getName());

                startActivity(i);
            }
        });
    }

    @Override
    protected void onStart(){
        deckList = new ArrayList<>();

        super.onStart();

        showDecks();
    }

    public void addDeck(){
        if (addCardText.getText().length()==0){
            Context context = getApplicationContext();
            CharSequence text = "This cannot be empty! Try again!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        else{
        deckName = addCardText.getText().toString();
        addCardText.setText("");

        Deck deck = new Deck(deckName);

        // Add a new document with a generated ID
        deckRef.add(deck)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
        showDecks();
        }
    }

    public void showDecks(){
        deckRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            deckList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Deck deck = document.toObject(Deck.class);
                                deck.setId(document.getId());
                                deckList.add(deck);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        UpdateDeckListView(deckList);
                    }
                });
    }

    public void UpdateDeckListView(List<Deck> itemDeckList){

        deckListView = (ListView) findViewById(R.id.deckListView);

        your_array_list = new ArrayList<String>();

        arrayAdapter = new ArrayAdapter<String>(
                this,
                R.layout.text_color_layout,
                your_array_list);

        for (Deck deck : itemDeckList)
        {
            your_array_list.add(deck.name);
        }
        deckListView.setAdapter(arrayAdapter);
    }
}