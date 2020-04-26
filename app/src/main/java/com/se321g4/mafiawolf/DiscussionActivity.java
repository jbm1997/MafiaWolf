package com.se321g4.mafiawolf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DiscussionActivity extends AppCompatActivity {

    private int lobbyPosition;
    private int readyPlayers;
    private Button PassButton; // Button for pass time vote
    private ImageButton roleIcon;
    private TextView roleName;
    private TextView rolePrompt;
    private int roleNum; //stores player role
    private int playerCount;
    private ArrayList<Integer> Roles = new ArrayList<>(); //list of numbers representing roles
    private int check = 0; //used to ensure we initialize Roles list only once
    private DatabaseReference database, poll, checkReady, gameState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        lobbyPosition = getIntent().getIntExtra("lobbyPosition", 0);
        playerCount  = getIntent().getIntExtra("playerCount", 0);
        database = FirebaseDatabase.getInstance().getReference().child("/Players").child("Player" + lobbyPosition);//allows the app to access the FireBase database*/
        checkReady = FirebaseDatabase.getInstance().getReference().child("/ReadyPlayers");
        gameState = FirebaseDatabase.getInstance().getReference().child("/GameState");
        checkReady.setValue(0); //reset readyPlayers to zero (and never use it again)
        poll = database.child("poll");
        poll.setValue(0); // reset poll value to zero (please do this for every activity)

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);
        PassButton = findViewById(R.id.PassButton);//initializes the Play Game button
        roleIcon = findViewById(R.id.imageView3);
        roleName = findViewById(R.id.RoleName);

        gameState.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(Integer.class) == 2){
                    Intent toVoting = new Intent(DiscussionActivity.this, VotingActivity.class);//creates the intent to switch to the wait activity
                    toVoting.putExtra("lobbyPosition", lobbyPosition);//stores the number of players and passes it to the next activity
                    toVoting.putExtra("lobbyCount", playerCount);//stores the number of players and passes it to the next activity
                    gameState.setValue(2);
                    startActivity(toVoting);//switches to the wait activity for the game
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        /*checkReady.addValueEventListener(new ValueEventListener() { //old transition method, now unneeded
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                readyPlayers = dataSnapshot.getValue(Integer.class);
                if(readyPlayers == playerCount ){
                    Intent toVoting = new Intent(DiscussionActivity.this, VotingActivity.class);//creates the intent to switch to the wait activity
                    toVoting.putExtra("lobbyPosition", lobbyPosition);//stores the number of players and passes it to the next activity
                    toVoting.putExtra("lobbyCount", playerCount);//stores the number of players and passes it to the next activity
                    startActivity(toVoting);//switches to the wait activity for the game
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });*/

//<======THIS CODE IS RESPONSIBLE FOR RNG ROLE ASSIGNMENT, IT WORKS BUT WONT BE USED FOR DEMO PURPOSES, REMOVE COMMENT BEFORE SUBMISSION OF GITHUB LINK TO GTA=======>
//        if(check == 0) { //add our role numbers to list
//            Roles.add(0);
//            Roles.add(1);
//            Roles.add(2);
//            Roles.add(3);
//        }
//        check++;
//
//        Random rNumber = new Random();
//        int i;
//        i = rNumber.nextInt(Roles.size());  //Receive Random Index based on size of list
//        roleNum = Roles.get(i);  //get value at index
//
//        MainActivity.thisUser.setRole(roleNum); //set the player role
//        database.child("role").setValue(roleNum);//updates player role in database
//        Roles.remove(roleNum); //remove role from list, cannot be assigned to another player
//<=====================================================================================================================================================================>

//<===============================THIS CODE IS RESPONSIBLE FOR FORCE ASSIGNING A ROLE TO PLAYERS 1-4 FOR DEMO PURPOSES. REMOVE BEFORE SUBMISSION OF GITHUB LINK TO GTA============>
        if(readyPlayers == 0) {
            if (lobbyPosition == 1) {
                MainActivity.thisUser.setRole(0);
                roleNum = 0;
                database.child("role").setValue(roleNum);//updates player role in database
            } else if (lobbyPosition == 2) {
                MainActivity.thisUser.setRole(1);
                roleNum = 1;
                database.child("role").setValue(roleNum);//updates player role in database
            } else if (lobbyPosition == 3) {
                MainActivity.thisUser.setRole(3);
                roleNum = 3;
                database.child("role").setValue(roleNum);//updates player role in database
            } else {
                MainActivity.thisUser.setRole(2);
                roleNum = 2;
                database.child("role").setValue(roleNum);//updates player role in database
            }
        }
//<================================================================================================================================================================================>

        //Used for setting Role icon in layout, based on assigned player role
        switch(roleNum){
            case 0:
                roleIcon.setImageResource(R.drawable.roleciv);
                roleName.setText("Civilian");

                //Display brief Role info on image click
                roleIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), "Vote on and Accuse Other Players", Toast.LENGTH_LONG).show();
                    }
                });
                break;
            case 1:
                roleIcon.setImageResource(R.drawable.rolewolf);
                roleName.setText("Werewolf");

                roleIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), "Attack or Kill Players at Night", Toast.LENGTH_LONG).show();
                    }
                });
                break;
            case 2:
                roleIcon.setImageResource(R.drawable.rolemed);
                roleName.setText("Doctor");

                roleIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), "Protect other Players from Wolf attacks", Toast.LENGTH_LONG).show();
                    }
                });
                break;
            case 3:
                roleIcon.setImageResource(R.drawable.rolecop);
                roleName.setText("Sheriff");

                roleIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), "Investigate Player Alignment", Toast.LENGTH_LONG).show();
                    }
                });
            case 4:
                roleName.setText("You have been slain...");
                roleIcon.setVisibility(View.INVISIBLE);
                rolePrompt.setVisibility(View.INVISIBLE);
                PassButton.setEnabled(false);
                PassButton.setVisibility(View.INVISIBLE);
                break;
            case 5:
                roleName.setText("You have been slain by the Werewolves...");
                roleIcon.setVisibility(View.INVISIBLE);
                rolePrompt.setVisibility(View.INVISIBLE);
                PassButton.setEnabled(false);
                PassButton.setVisibility(View.INVISIBLE);
                break;
        }

        // simply sends pass signal for the lobby client
        PassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                poll.setValue(1); // user chose to pass
                PassButton.setVisibility(View.INVISIBLE); // remove the button once pressed
                Toast.makeText(getApplicationContext(),"You have voted to pass time", Toast.LENGTH_SHORT).show();
                //checkReady.setValue(readyPlayers + 1); // unneeded
            }
        });
    }

/*    private void createUser(DataSnapshot dataSnapshot){
        MainActivity.thisUser = new User(dataSnapshot.child("name").getValue().toString());
        MainActivity.thisUser.setIcon((Integer) dataSnapshot.child("icon").getValue());
        MainActivity.thisUser.setPoll((Integer) dataSnapshot.child("poll").getValue());
        MainActivity.thisUser.setRole((Integer) dataSnapshot.child("role").getValue());
    }*/
}
