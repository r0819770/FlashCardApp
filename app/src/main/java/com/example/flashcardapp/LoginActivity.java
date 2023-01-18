package com.example.flashcardapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference loginRef = db.collection("login");

    private Button loginButton;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private List<User> userList;
    private boolean success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.loginButton);
        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

    public void attemptLogin() {
        success = false;
        User attemptedUser = new User(usernameEditText.getText().toString(), passwordEditText.getText().toString());

        db.collection("login")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            success = true;
                            userList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String m = document.get("email").toString();
                                String p = document.get("password").toString();
                                User user = new User(m, p);
                                userList.add(user);
                            }
                            for (User user : userList) {
                                if ((attemptedUser.email.compareTo(user.email) == 0) && (attemptedUser.password.compareTo(user.password)) == 0) {
                                    Intent i = new Intent(LoginActivity.this, MainActivity.class);

                                    startActivity(i);
                                    finish();
                                    success= false;
                                }
                            }
                            if (success) {
                                Context context = getApplicationContext();
                                CharSequence text = "Your login wasn't correct, please try again.";
                                int duration = Toast.LENGTH_SHORT;

                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                            }
                        }
                    }
                });
    }
}
