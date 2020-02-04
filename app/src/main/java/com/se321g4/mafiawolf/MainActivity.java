package com.se321g4.mafiawolf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
    int lobbyCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        playGame = findViewById(R.id.playGameButton);//initializes the Play Game button

        playGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = findViewById(R.id.userName);//takes the text input by the user and assigns it to the user object as its name
                thisUser = new User(userName.getText().toString());

                playerCount.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        lobbyCount = Integer.parseInt(dataSnapshot.getValue().toString());

                        if(lobbyCount == 0){
                            thisUser.setVIP(true);
                            if(thisUser.getVIP()){
                                Toast.makeText(getApplicationContext(),"im a vip", Toast.LENGTH_LONG).show();
                            }
                        }//makes the user the VIP (game host) if they're the first to log in

                        playerCount.setValue(lobbyCount + 1);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });//Event Listener for database. This updates the count every time a client of the app adds an user to FireBase

                database.push().setValue(thisUser);//pushes data to the FireBase database
                Intent toLobby = new Intent(MainActivity.this, LobbyActivity.class);//creates the intent to switch to the lobby activity
                toLobby.putExtra("lobbyPosition", lobbyCount);
                startActivity(toLobby);//switches to the lobby activity
            }
        });//runs the code inside the block once the button is pressed

    }
}
