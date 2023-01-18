package com.example.flashcardapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class DeckActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference decksRef = db.collection("decks");
    ArrayAdapter<String> arrayAdapter;
    private List<String> your_array_list;
    private String currentDeckId;
    private Button addCardButton;
    private Button reviewDeckButton;
    private Button editCurrentDeckButton;
    private String deckName;
    private TextView deckTitle;
    private ListView cardListView;
    private long docCount;

    private List<ItemCard> cardList;

    private TextView deckNameText;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck);

        cardListView = (ListView) findViewById(R.id.cardListView);
        addCardButton =(Button) findViewById(R.id.addCardButton);
        reviewDeckButton = (Button) findViewById(R.id.reviewDeckButton);
        editCurrentDeckButton = (Button) findViewById(R.id.editCurrentDeckButton);
        deckTitle = (TextView) findViewById(R.id.deckTitle);
        Intent intent = this.getIntent();

        if (intent != null){
            deckName = intent.getStringExtra("name");
            currentDeckId = intent.getStringExtra("id");
            deckTitle.setText(deckName);
        }
        addCardButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        addCard();
                    }
                });

        editCurrentDeckButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        editDeck();
                    }
                });

        reviewDeckButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        AggregateQuery count = decksRef.document(currentDeckId).collection("extra collection").count();
                        count.get(AggregateSource.SERVER).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                AggregateQuerySnapshot snapshot = task.getResult();
                                docCount = snapshot.getCount();
                            }
                            if (docCount != 0) {
                                Intent i = new Intent(DeckActivity.this, ReviewActivity.class);
                                i.putExtra("id", currentDeckId);
                                i.putExtra("name", deckName);
                                startActivity(i);
                            }
                            else{
                                Context context = getApplicationContext();
                                CharSequence text = "There is nothing to review! Add a card!";
                                int duration = Toast.LENGTH_SHORT;

                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                            }
                        });
                    }
                });


        cardListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent i = new Intent(DeckActivity.this, EditCardActivity.class);
                ItemCard itemCard = cardList.get(position);
                i.putExtra("front", itemCard.getFront());
                i.putExtra("back", itemCard.getBack());
                i.putExtra("cardId", itemCard.getId());
                i.putExtra("deckId",currentDeckId);

                startActivity(i);
            }
        });
    }

      @Override
    protected void onStart(){
        cardList = new ArrayList<>();

        super.onStart();

        showCards();
    }


    public void showCards(){
        decksRef.document(currentDeckId).collection("extra collection").get()
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
                        UpdateCardListView(cardList);
                    }
                });
    }


    public void addCard(){
        Intent i = new Intent(DeckActivity.this, AddCardActivity.class);
        i.putExtra("id", currentDeckId);
        i.putExtra("name", deckName);

        startActivity(i);
    }

    public void editDeck(){
        Intent i = new Intent(DeckActivity.this, EditDeckActivity.class);
        i.putExtra("id", currentDeckId);
        i.putExtra("name", deckName);

        startActivity(i);

        finish();
    }

    public void UpdateCardListView(List<ItemCard> itemCardList){

        cardListView = (ListView) findViewById(R.id.cardListView);

        your_array_list = new ArrayList<String>();

        arrayAdapter = new ArrayAdapter<String>(
                this,
                R.layout.text_color_layout,
                your_array_list);

        for (ItemCard itemCard : itemCardList)
        {
            your_array_list.add(itemCard.front);
        }

        cardListView.setAdapter(arrayAdapter);
    }
}