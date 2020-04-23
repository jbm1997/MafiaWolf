package com.se321g4.mafiawolf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

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

    private ImageButton[] playerIconButton = new ImageButton[3];
    private TextView[] playerNamesFields = new TextView[3];

    private TextView titleText;

    private DatabaseReference currentPlayer;
    private DatabaseReference players;
    private DatabaseReference gameStateRef;

    private int gameState = 2;
    private int lobbyPosition;
    private int lobbyCount;
    private int iconValue = 0;
    private int helper;
    private int playerRole;

    private String playerName;
    private int[] playerIcons;
    private int[] playerRealPositions;
    private String[] playerNames;
    private String[] playerRoles;


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
        gameStateRef = FirebaseDatabase.getInstance().getReference().child("/GameState");


        playerIcons = new int[lobbyCount - 1];//stores the icon value for each player in the game other than the local clients player
        playerNames = new String[lobbyCount - 1]; //stores all player names for each player in the game other than the local clients player
        playerRoles = new String[lobbyCount-1];
        playerRealPositions = new int[lobbyCount - 1];

        //sets pointers to the buttons they can be accessed through a loop
        playerIconButton[0] = firstPlayer;
        playerIconButton[1] = secondPlayer;
        playerIconButton[2] = thirdPlayer;

        playerNamesFields[0] = firstPlayerName;
        playerNamesFields[1] = secondPlayerName;
        playerNamesFields[2] = thirdPlayerName;

        playersRef();
        gameStateRef();

    }

    private void assignIconsNames() {
        final int[] images = new int[7];//stores all references to the player icons in an array so that they can be accessed contextually

        images[1] = R.drawable.playericon1;
        images[2] = R.drawable.playericon2;
        images[3] = R.drawable.playericon3;
        images[4] = R.drawable.playericon4;
        images[5] = R.drawable.playericon5;
        images[6] = R.drawable.playericon6;
        
        ImageButton targetPlayer;
        TextView targetPlayerName;


        for (int i = 1; i <= lobbyCount; i++) {//assigns icons contextually based on who's playing the game.
            if (i == lobbyPosition) { //this prevents a NullPointerException from occurring
                helper = i;
                i++;
            }
            
            if (helper > 0) {//triggers helpers to maintain proper array iteration when the code reaches the position of the current player.
                if(helper != lobbyCount){
                    targetPlayer = playerIconButton[helper-1];
                    targetPlayer.setImageResource(images[playerIcons[helper-1]]);
                    targetPlayerName = playerNamesFields[helper-1];
                    targetPlayerName.setText(playerNames[helper-1]);
                    helper++;
                }
            }//Indices are - 1 to prevent null pointer exceptions and maintain proper parity with playerIcons and playerNames arrays which are assigned as i-1 on the players event listener
            else{
                targetPlayer = playerIconButton[i-1];
                targetPlayer.setImageResource(images[playerIcons[i-1]]);
                targetPlayerName = playerNamesFields[i-1];
                targetPlayerName.setText(playerNames[i-1]);
            }
        }//for loop
    }//assignIconsNames
    
    private void fillPlayerRolesArray(int playerRole, int index){
        switch(playerRole){//fills the playerRoles array with the appropriate roles transcribed to strings instead of 0-3
            case 1:
                playerRoles[index] = "Werewolf";
            case 2:
                playerRoles[index] = "Doctor";
            case 3:
                playerRoles[index] = "Sheriff";
            default:
                playerRoles[index] = "Civilian";
        }
    }//fillPlayerRolesArray

    private void gameStateRef(){
        gameStateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gameState = dataSnapshot.getValue(Integer.class);

                int currentPlayerRole = MainActivity.thisUser.getRole();

                switch(gameState){//differentiates between Day or Night phase of the game
                    case 2://everyone has the same voting powers
                        switch(currentPlayerRole) {
                            case 4:
                                titleText.setText("You have been slain...");
                                break;
                            case 5:
                                titleText.setText("You have been slain by the Werewolves...");
                                break;
                            default://re-enables the buttons / UI for those who haven't been slain
                                titleText.setText("Who do you accuse?");
                                toggleAllGUI(true);//turns on GUI

                                submitButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        gameStateRef.setValue(3);
                                    }
                                });//updates current users poll value to the third player to indicate that is who they voted for
                        }
                    case 3://civilians do nothing, all other roles have their unique voting abilities
                        switch(currentPlayerRole){//Roles: 0=civ not a case since they don't vote during the night; 1=wolf; 2=medic; 3=sheriff
                            case 1://Wolf - kills a player========================================================================================================
                                titleText.setText("Pick your victim...");
                                wolfRole();
                                break;

                            case 2://Medic - saves a player============================================================================================================
                                titleText.setText("Save someone!");
                                medicRole();
                                break;

                            case 3://Sheriff - Inspects a players role=====================================================================================================
                                titleText.setText("Pick someone to investigate...");
                                submitButton.setEnabled(false);
                                sheriffRole();
                                break;
                            default:
                                titleText.setText("Night falls... pray you survive");//all buttons / UI elements are disabled, civilians have to wait for daytime
                                toggleAllGUI(false);//turns off GUI
                        }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });//listener checks for the value of the gameState
    }

    private void playersRef(){
        players.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (int i = 0; i <= lobbyCount; i++) {
                    if (i + 1 == lobbyPosition && lobbyPosition == 1) {//this prevents a NullPointerException from occurring
                        continue;
                    }

                    if (i + 1 == lobbyPosition || i == 0 || i == lobbyPosition) {
                        helper = i;
                        i++;
                    }

                    iconValue = dataSnapshot.child("Player" + i).child("icon").getValue(Integer.class);
                    playerName = dataSnapshot.child("Player" + i).child("name").getValue().toString();
                    playerRole = dataSnapshot.child("Player" + i).child("role").getValue(Integer.class);

                    if (helper > 0) {
                        playerIcons[helper - 1] = iconValue;
                        playerNames[helper - 1] = playerName;
                        playerRealPositions[helper - 1] = i;
                        fillPlayerRolesArray(playerRole, helper-1);
                        helper++;
                    } else {
                        playerIcons[i - 1] = iconValue;
                        playerNames[i - 1] = playerName;
                        playerRealPositions[i - 1] = i;
                        fillPlayerRolesArray(playerRole, i-1);
                    }
                }
                helper = 0;
                assignIconsNames();//method assigns icons and names contextually for players on the voting screen
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });//this listener pulls all the player icons and names from the database, except for the current player
    }

    //have the local users poll set to the person they choose, update the database when they hit submit at disable the buttons
    private void medicRole(){//the way medic handles saving someone might change, could instead update a value on the saved player
        firstPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.thisUser.setPoll(playerRealPositions[0]);
            }
        });//updates current user poll value to the first player to indicate that is who they voted for

        secondPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.thisUser.setPoll(playerRealPositions[1]);
            }
        });//updates current users poll value to the second player to indicate that is who they voted for

        thirdPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.thisUser.setPoll(playerRealPositions[2]);
            }
        });//updates current users poll value to the third player to indicate that is who they voted for

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPlayer.child("poll").setValue(MainActivity.thisUser.getPoll());
                submitButton.setEnabled(false);
                submitButton.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void wolfRole(){
        firstPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.thisUser.setPoll(playerRealPositions[0]);
            }
        });//updates current user poll value to the first player to indicate that is who they voted for

        secondPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.thisUser.setPoll(playerRealPositions[1]);
            }
        });//updates current users poll value to the second player to indicate that is who they voted for

        thirdPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.thisUser.setPoll(playerRealPositions[2]);
            }
        });//updates current users poll value to the third player to indicate that is who they voted for

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPlayer.child("poll").setValue(MainActivity.thisUser.getPoll());
                submitButton.setEnabled(false);
                submitButton.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void sheriffRole(){
        submitButton.setVisibility(View.INVISIBLE);

        firstPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableAllButtons();
                Toast.makeText(getApplicationContext(), playerNames[0] + " is a " + playerRoles[0], Toast.LENGTH_LONG).show();
            }
        });//updates current user poll value to the first player to indicate that is who they voted for

        secondPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableAllButtons();
                Toast.makeText(getApplicationContext(), playerNames[1] + " is a " + playerRoles[1], Toast.LENGTH_LONG).show();
            }
        });//updates current users poll value to the second player to indicate that is who they voted for

        thirdPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableAllButtons();
                Toast.makeText(getApplicationContext(), playerNames[2] + " is a " + playerRoles[2], Toast.LENGTH_LONG).show();
            }
        });//updates current users poll value to the third player to indicate that is who they voted for
    }
    
    private void toggleAllGUI(boolean trigger){
        if(trigger){//turns on the GUI
            firstPlayer.setVisibility(View.VISIBLE);
            firstPlayer.setEnabled(true);
            firstPlayerName.setVisibility(View.VISIBLE);

            secondPlayer.setVisibility(View.VISIBLE);
            secondPlayer.setEnabled(true);
            secondPlayerName.setVisibility(View.VISIBLE);

            thirdPlayer.setVisibility(View.VISIBLE);
            thirdPlayer.setEnabled(true);
            thirdPlayerName.setVisibility(View.INVISIBLE);

            submitButton.setVisibility(View.VISIBLE);
            submitButton.setEnabled(true);
        }
        else{//turns off the GUI, except for the title text
            firstPlayer.setVisibility(View.INVISIBLE);
            firstPlayer.setEnabled(false);
            firstPlayerName.setVisibility(View.INVISIBLE);

            secondPlayer.setVisibility(View.INVISIBLE);
            secondPlayer.setEnabled(false);
            secondPlayerName.setVisibility(View.INVISIBLE);

            thirdPlayer.setVisibility(View.INVISIBLE);
            thirdPlayer.setEnabled(false);
            thirdPlayerName.setVisibility(View.INVISIBLE);

            submitButton.setVisibility(View.INVISIBLE);
            submitButton.setEnabled(false);
        }
    }//disables and makes all buttons invisible, makes all text other than title invisible

    private void disableAllButtons(){
        firstPlayer.setEnabled(false);

        secondPlayer.setEnabled(false);

        thirdPlayer.setEnabled(false);

        submitButton.setEnabled(false);
    }//disables all buttons



}
