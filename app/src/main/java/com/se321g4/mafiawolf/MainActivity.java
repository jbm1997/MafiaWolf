package com.se321g4.mafiawolf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("/Players");//allows the app to access the FireBase database
    DatabaseReference playerCount = FirebaseDatabase.getInstance().getReference().child("/Count");//allows the app to access the FireBase database
//possible idea, set all listeners in the main activity and then access the data from here? might help with the async nature of firebase

    private Button playGame;//Play Game button
    private ImageButton changePic;
    private EditText userName;//user name text box
    public static User thisUser = new User(null);//user object
    private int lobbyCount = 0;
    private int currentPic = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        playGame = findViewById(R.id.playGameButton);//initializes the Play Game button
        changePic = findViewById(R.id.playerIcon);


        changePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(currentPic == 1){
                    changePic.setImageResource(R.drawable.playericon2);
                    currentPic++;
                    thisUser.setIcon(currentPic);//passes the icon information as an int
                }
                else if(currentPic == 2){
                    changePic.setImageResource(R.drawable.playericon3);
                    currentPic++;
                    thisUser.setIcon(currentPic);//passes the icon information as an int
                }
                else if(currentPic == 3){
                    changePic.setImageResource(R.drawable.playericon4);
                    currentPic++;
                    thisUser.setIcon(currentPic);//passes the icon information as an int
                }
                else if(currentPic == 4){
                    changePic.setImageResource(R.drawable.playericon5);
                    currentPic++;
                    thisUser.setIcon(currentPic);//passes the icon information as an int
                }
                else if(currentPic == 5){
                    changePic.setImageResource(R.drawable.playericon6);
                    currentPic++;
                    thisUser.setIcon(currentPic);//passes the icon information as an int
                }
                else{
                    changePic.setImageResource(R.drawable.playericon1);
                    currentPic = 1;
                    thisUser.setIcon(currentPic);//passes the icon information as an int
                }

            }//executes code on click
        });//listens for clicks on the button

        playGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = findViewById(R.id.userName);//takes the text input by the user and assigns it to the user object as its name
                thisUser.setName(userName.getText().toString());

                playerCount.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {//Allows the program to check for the player count
                        lobbyCount = dataSnapshot.getValue(Integer.class);

                        if(lobbyCount == 0){
                            thisUser.setPoll(2);//User is VIP
                            if(thisUser.getPoll() == 2){
                                Toast.makeText(getApplicationContext(),"You're the VIP", Toast.LENGTH_LONG).show();
                            }
                        }//makes the user the VIP (game host) if they're the first to log in
                        else{
                            thisUser.setPoll(0);//user is NOT ready
                        }

                        playerCount.setValue(++lobbyCount);
                        database.child("Player" + (lobbyCount)).setValue(thisUser);//pushes data to the FireBase database

                        Intent toWait = new Intent(MainActivity.this, ReadyActivity.class);//creates the intent to switch to the wait activity
                        toWait.putExtra("lobbyPosition", lobbyCount);//stores the lobby position for the local instance of the mobile app and passes it to the next activity
                        startActivity(toWait);//switches to the wait activity for the game
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });//Event Listener for database. This updates the count every time a client of the app adds an user to FireBase
            }

        });//runs the code inside the block once the button is pressed

    }
}

