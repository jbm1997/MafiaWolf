package com.se321g4.mafiawolf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("/Players");//allows the app to access the FireBase database
    DatabaseReference playerCount = FirebaseDatabase.getInstance().getReference().child("/Count");//allows the app to access the FireBase database


    Button playGame;//Play Game button
    EditText userName;//user name text box
    User thisUser;//user object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        playGame = findViewById(R.id.playGameButton);//initializes the Play Game button

        playGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = findViewById(R.id.userName);//c
                thisUser = new User(userName.getText().toString());
                database.push().setValue(thisUser);//pushes data to the FireBase database

                playerCount.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String helothere = dataSnapshot.getValue().toString();
                        playerCount.setValue(Integer.parseInt(helothere) + 1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });

    }
}
