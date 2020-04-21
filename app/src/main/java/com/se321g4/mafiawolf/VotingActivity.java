package com.se321g4.mafiawolf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class VotingActivity extends AppCompatActivity {
    private Button submitButton;

    private ImageButton firstPlayer;
    private TextView firstPlayerName;

    private ImageButton secondPlayer;
    private TextView secondPlayerName;

    private ImageButton thirdPlayer;
    private TextView thirdPlayerName;

    private TextView titleText;

    private DatabaseReference currentPlayer;
    private DatabaseReference players;
    private int lobbyPosition;
    private int lobbyCount;
    private int iconValue = 0;
    private String playerName;
    private int[] playerIcons;
    private String[] playerNames;
    private int helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);

        firstPlayer = findViewById(R.id.player1Icon);
        firstPlayerName = findViewById(R.id.player1name);

        secondPlayer = findViewById(R.id.player2Icon);
        secondPlayerName = findViewById(R.id.player2name);

        thirdPlayer = findViewById(R.id.player3Icon);
        thirdPlayerName = findViewById(R.id.player3name);

        submitButton = findViewById(R.id.submitAccusation);
        titleText = findViewById(R.id.titleText);
        lobbyPosition = getIntent().getIntExtra("lobbyPosition", 0);
        lobbyCount = getIntent().getIntExtra("lobbyCount", 0);
        players = FirebaseDatabase.getInstance().getReference().child("/Players");
        currentPlayer = FirebaseDatabase.getInstance().getReference().child("/Players").child("Player" + lobbyPosition);//allows the app to access the FireBase database*/

        playerIcons = new int[lobbyCount-1];//stores the icon value for each player in the game other than the local clients player
        playerNames = new String[lobbyCount-1]; //stores all player names for each player in the game other than the local clients player

        players.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(int i = 0; i <= lobbyCount; i++) {
                    if(i + 1 == lobbyPosition && lobbyPosition == 1){//this prevents a NullPointerException from occurring
                        continue;
                    }

                    if(i + 1 == lobbyPosition || i == 0 || i == lobbyPosition){
                        helper = i;
                        i++;
                    }
                    iconValue = dataSnapshot.child("Player" + i).child("icon").getValue(Integer.class);
                    playerName = dataSnapshot.child("Player" + i).child("name").getValue().toString();

                    if(helper > 0){
                        playerIcons[helper-1] = iconValue;
                        playerNames[helper-1] = playerName;
                        helper++;
                    }
                    else{
                        playerIcons[i-1] = iconValue;
                        playerNames[i-1] = playerName;
                    }
                }
                assignIconsNames();//method assigns icons and names contextually for players on the voting screen
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        if(MainActivity.thisUser.getRole() == 0 /*include or gamestate check if daytime voting so that every other role can also nominate a player*/){

        }


    }

    private void assignIconsNames(){
        final int[] images = new int[7];//stores all references to the player icons in an array so that they can be accessed contextually

        images[1] = R.drawable.playericon1;
        images[2] = R.drawable.playericon2;
        images[3] = R.drawable.playericon3;
        images[4] = R.drawable.playericon4;
        images[5] = R.drawable.playericon5;
        images[6] = R.drawable.playericon6;

        for(int i = 0; i <= lobbyCount; i++){//assigns icons contextually based on who's playing the game.
            if(i +1 == lobbyPosition || i == 0 || i == lobbyPosition){ //this prevents a NullPointerException from occurring
                helper = i;
                i++;
            }
            for(int j = 1; j < 7; j++ ){
                if(helper > 0){//triggers helpers to maintain proper array iteration
                    if(helper == 1){
                        if(j == playerIcons[helper-1]){
                            firstPlayer.setImageResource(images[j]);
                            firstPlayerName.setText(playerNames[helper-1]);
                            helper++;
                            break;
                        }
                    }
                    else if(helper == 2){
                        if(j == playerIcons[helper-1]){
                            secondPlayer.setImageResource(images[j]);
                            secondPlayerName.setText(playerNames[helper-1]);
                            helper++;
                            break;
                        }
                    }
                    else if(helper == 3){
                        if(j == playerIcons[helper-1]){
                            thirdPlayer.setImageResource(images[j]);
                            thirdPlayerName.setText(playerNames[helper-1]);
                            helper++;
                            break;
                        }
                    }
                }
                else {
                    if (i == 1) {
                        if (j == playerIcons[i - 1]) {
                            firstPlayer.setImageResource(images[j]);
                            firstPlayerName.setText(playerNames[i - 1]);
                            break;
                        }
                    } else if (i == 2) {
                        if (j == playerIcons[i - 1]) {
                            secondPlayer.setImageResource(images[j]);
                            secondPlayerName.setText(playerNames[i - 1]);
                            break;
                        }
                    } else if (i == 3) {
                        if (j == playerIcons[i - 1]) {
                            thirdPlayer.setImageResource(images[j]);
                            thirdPlayerName.setText(playerNames[i - 1]);
                            break;
                        }
                    }
                }

            }
        }//for
    }//assignIcons

}
