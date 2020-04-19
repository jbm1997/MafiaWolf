package com.se321g4.mafiawolf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Random;

public class DiscussionActivity extends AppCompatActivity {

    private int lobbyPosition;
    private Button ReadyButton;//Ready button
    private ImageButton roleIcon;
    private TextView roleName;
    private int check = 0; //
    private  int gameState =2;
    DatabaseReference database;
    DatabaseReference gameS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        lobbyPosition = getIntent().getIntExtra("lobbyPosition", 1);
        database = FirebaseDatabase.getInstance().getReference().child("/Players").child("Player" + lobbyPosition);
        gameS = FirebaseDatabase.getInstance().getReference().child("/GameState");


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);
        ReadyButton = findViewById(R.id.ReadyButton);//initializes the Play Game button
        roleIcon = findViewById(R.id.imageView3);
        roleName = findViewById(R.id.RoleName);


        //Used for setting Role icon in layout, based on assigned player role
        if(check == 0){
            check++;
            roleIcon.setImageResource(R.drawable.roleciv);
            MainActivity.thisUser.setRole(check); //set the player role
            database.child("role").setValue(check);//updates player role in database
            roleName.setText("Civilian");

            //currentPic = 4;
            //MainActivity.thisUser.setIcon(4);

            //Display brief Role info on image click
            roleIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Vote on and Accuse Other Players", Toast.LENGTH_LONG).show();
                }
            });
        }
        else if(check == 1){
            check++;
            roleIcon.setImageResource(R.drawable.rolewolf);
            MainActivity.thisUser.setRole(check); //set the player role
            database.child("role").setValue(check);//updates player role in database
            roleName.setText("Werewolf");
            //currentPic = 2;
            //MainActivity.thisUser.setIcon(2);

            roleIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Attack or Kill Players at Night", Toast.LENGTH_LONG).show();
                }
            });
        }
        else if(check == 2){
            check++;
            roleIcon.setImageResource(R.drawable.rolecop);
            MainActivity.thisUser.setRole(check); //set the player role
            database.child("role").setValue(check);//updates player role in database
            roleName.setText("Sheriff");
            //currentPic = 2;
            //MainActivity.thisUser.setIcon(2);

            roleIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Investigate Player Alignment", Toast.LENGTH_LONG).show();
                }
            });
        }
        else{
            check++;
            roleIcon.setImageResource(R.drawable.rolemed);
            MainActivity.thisUser.setRole(check); //set the player role
            database.child("role").setValue(check);//updates player role in database
            roleName.setText("Doctor");
            //currentPic = 2;
            //MainActivity.thisUser.setIcon(2);

            roleIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Protect other Players from Wolf attacks", Toast.LENGTH_LONG).show();
                }
            });
        }

        //sets player ReadyState to "Ready" on 'ReadyButton' click
        ReadyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.thisUser.setPoll(1); //User is Ready
                database.child("poll").setValue(1);
                Toast.makeText(getApplicationContext(),MainActivity.thisUser.getName()+" Is Ready!", Toast.LENGTH_SHORT).show();
                if(check == 4){
                    Intent toVote = new Intent(DiscussionActivity.this, VoteActivity.class);//creates the intent to switch to the vote activity
                    toVote.putExtra("lobbyPosition", lobbyPosition);//stores the lobby position for the local instance of the mobile app and passes it to the next activity
                    startActivity(toVote);//switches to the vote activity for the game
                    gameS.setValue(gameState);
                }
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
