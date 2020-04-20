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
    private int[] playerIcons = new int[3];//stores the icon value for each player in the game other than the local clients player


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

        players.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(int i = 1; i <= lobbyCount; i++ ) {
                    if(i == lobbyPosition){
                        continue;
                    }
                    iconValue = dataSnapshot.child("Player" + i).child("icon").getValue(Integer.class);
                    playerIcons[i-1] = iconValue;
                }
                assignIcons();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        if(MainActivity.thisUser.getRole() == 0 /*include or gamestate check if daytime voting so that every other role can also nominate a player*/){

        }


    }

    private void assignIcons(){
        final int[] images = new int[7];//stores all references to the player icons in an array so that they can be accessed contextually
        images[1] = R.drawable.playericon1;
        images[2] = R.drawable.playericon2;
        images[3] = R.drawable.playericon3;
        images[4] = R.drawable.playericon4;
        images[5] = R.drawable.playericon5;
        images[6] = R.drawable.playericon6;

//        for(int i = 1; i <= lobbyCount; i++ ){
//            if(i == lobbyPosition){
//                continue;
//            }
//            playerIcons.add(getIconInfo(i));
//        }

        for(int i = 1; i < 4; i++){//assigns icons contextually based on who's playing the game.
            for(int j = 1; j < 7; j++ ){
                if(i == 1 /*&& i != lobbyCount*/){
                    if(j == playerIcons[0]){
                        firstPlayer.setImageResource(images[j]);
                        break;
                    }
                    continue;
                }
                else if(i == 2 /*&& i != lobbyCount*/){
                    if(j == playerIcons[1]){
                        secondPlayer.setImageResource(images[j]);
                        break;
                    }
                    continue;
                }
                else if(i == 3 /*&& i != lobbyCount*/){
                    if(j == playerIcons[2]){
                        thirdPlayer.setImageResource(images[j]);
                        break;
                    }
                    continue;
                }
            }
        }//for
    }//assignIcons

    private int getIconInfo(int i){
        final int index = i;
        return iconValue;
    }//gets icon information from players in the database

}
