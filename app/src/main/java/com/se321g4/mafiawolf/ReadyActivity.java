package com.se321g4.mafiawolf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReadyActivity extends AppCompatActivity {
    private int lobbyPosition;
    private Button startGame;
    private  Button ready;
    private TextView vip;
    public int checkR;        // this checks that readyplayers is only icr once in DB
    private int count;                 // provides count of players from db
    private  int gameState =1;
    private int readyAmount = 0;   //provide how many players are ready in the DB
    private int roleNum = 0;
    DatabaseReference database;
    DatabaseReference databaseCount;
    DatabaseReference readyC;
    DatabaseReference gameS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        lobbyPosition = getIntent().getIntExtra("lobbyPosition", 1);
        database = FirebaseDatabase.getInstance().getReference().child("/Players").child("Player" + lobbyPosition);
        databaseCount = FirebaseDatabase.getInstance().getReference().child("Count");     // total # of players
        readyC = FirebaseDatabase.getInstance().getReference().child("/ReadyPlayers");  //references how many players are ready
        gameS = FirebaseDatabase.getInstance().getReference().child("/GameState");
        Toast.makeText(getApplicationContext(), Integer.toString(lobbyPosition), Toast.LENGTH_LONG).show();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ready);

        startGame = findViewById(R.id.StartButton);     //intialize buttons
        startGame.setEnabled(false);
        ready = findViewById(R.id.playerReady);
        vip = findViewById(R.id.VIP_role_box);

        if(lobbyPosition == 1){
            ready.setVisibility(View.INVISIBLE);
        }

        // setting roles for 4 players for demo purposes
        if (lobbyPosition == 1) {
            MainActivity.thisUser.setRole(0);
            roleNum = 0; //civ
            database.child("role").setValue(roleNum);//updates player role in database
        } else if (lobbyPosition == 2) {
            MainActivity.thisUser.setRole(1);
            roleNum = 1; //wolf
            database.child("role").setValue(roleNum);//updates player role in database
        } else if (lobbyPosition == 3) {
            MainActivity.thisUser.setRole(3);
            roleNum = 3; //sheriff
            database.child("role").setValue(roleNum);//updates player role in database
        } else {
            MainActivity.thisUser.setRole(2);
            roleNum = 2; // medic
            database.child("role").setValue(roleNum);//updates player role in database
        }


        gameS.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(Integer.class) == 1){
                    Intent toDiscussion = new Intent(ReadyActivity.this, DiscussionActivity.class);//creates the intent to switch to the wait activity
                    toDiscussion.putExtra("lobbyPosition", lobbyPosition);//stores the lobby position for the local instance of the mobile app and passes it to the next activity
                    toDiscussion.putExtra("playerCount", count);//stores the lobby position for the local instance of the mobile app and passes it to the next activity
                    gameS.setValue(gameState);
                    MainActivity.thisUser.setPoll(0);
                    database.child("poll").setValue(0);
                    startActivity(toDiscussion);//switches to the wait activity for the game
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        readyC.addValueEventListener(new ValueEventListener() {                //when a player is ready, the lobby checks if all players are ready
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                readyAmount = dataSnapshot.getValue(Integer.class);
                if(readyAmount == 3 && lobbyPosition == 1)
                {
                    startGame.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseCount.addValueEventListener(new ValueEventListener() {     //gets count of players
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                count =  Integer.parseInt(dataSnapshot.getValue().toString());
                //Toast.makeText(getApplicationContext(), Integer.toString(rc) , Toast.LENGTH_LONG).show();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });



        ready.setOnClickListener(new View.OnClickListener() {           // button sets non-vip pol val to 1
            @Override
            public void onClick(View v) {
                int val = 1;
                int x = readyAmount + 1;

                readyC.setValue(x);               //adds ready user
                MainActivity.thisUser.setPoll(1);    //changes specific user status
                database.child("poll").setValue(val);     // updates db
                ready.setVisibility(View.INVISIBLE);

            }//executes code on click
        });//listens for clicks on the button

        if(MainActivity.thisUser.getPoll() == 2)       //if the user is the VIP it will show the Start button
        {
            startGame.setVisibility(View.VISIBLE);
        }

        if(lobbyPosition != 1)       //if the user is the VIP it will show the Start button
        {
            startGame.setVisibility(View.INVISIBLE);
        }

        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toDiscussion = new Intent(ReadyActivity.this, DiscussionActivity.class);//creates the intent to switch to the wait activity
                toDiscussion.putExtra("lobbyPosition", lobbyPosition);//stores the lobby position for the local instance of the mobile app and passes it to the next activity
                toDiscussion.putExtra("playerCount", count);//stores the lobby position for the local instance of the mobile app and passes it to the next activity
                gameS.setValue(gameState);
                startActivity(toDiscussion);//switches to the wait activity for the game
            }//executes code on click
        });//listens for clicks on the button

    }
}
