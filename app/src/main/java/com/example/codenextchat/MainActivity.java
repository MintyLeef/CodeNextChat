package com.example.codenextchat;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    ArrayList<String> messages = new ArrayList<>();
    ArrayAdapter<String> messagesAdapter;

    //TODO: Define a DatabaseReference reference to the messages object here
    DatabaseReference databaseMessages;

    public static final int RC_SIGN_IN = 1;

    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.edit_text);

        //TODO: Place a FirebaseDatabase reference to the database here
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();


        //TODO: Instantiate the DatabaseReference to the messages object
        databaseMessages = database.getReference().child("messages");


        ListView listView = findViewById(R.id.list_view);
        messagesAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, messages);
        listView.setAdapter(messagesAdapter);

        //TODO: Add a listener for when a child message is added to the messages object in the db here
        //when a child message has been added to the db:
        //get the message String from the Snapshot and add it to the ArrayList
        //notify the adapter to update the listview with a new message

        databaseMessages.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                messages.add(dataSnapshot.getValue(String.class));
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {



                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                //user is signed in
                    Toast.makeText(MainActivity.this, "Signed in.", Toast.LENGTH_LONG).show();
                } else {
                 //user is signed out

                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }





            }
        };


    }

    public void sendMessage(View view) {
        //get the string from the edittext field
        //push the message as a child to the messages object in the db
        String message = editText.getText().toString();
        editText.setText("");
        messages.add(message); //TODO: remove later
        messagesAdapter.notifyDataSetChanged(); //TODO: remove later

        //TODO: push the message as a child to the messages object in the db
        databaseMessages.push().setValue(message);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(authStateListener != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}
