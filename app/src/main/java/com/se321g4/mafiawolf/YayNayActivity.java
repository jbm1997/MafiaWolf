package com.se321g4.mafiawolf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class YayNayActivity extends AppCompatActivity {

    private Button yayButton;
    private Button nayButton;
    private DatabaseReference gameStateRef;
    private DatabaseReference currentPlayer;
    private int gameState;
    private int lobbyPosition;

    /*DESIGN: poll value starts at 0 (you dont have to set it). "Yea" changes it to 1. "Nay" changes it to 2.
        Pressing either will disable (hide? idk how it works) both buttons.
        This activity listens for gamestate to change to 3, upon which we enter night time voting.
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yay_nay);

        yayButton = findViewById(R.id.yayButton);
        nayButton = findViewById(R.id.nayButton);
        gameStateRef = FirebaseDatabase.getInstance().getReference().child("/GameState");
        lobbyPosition = getIntent().getIntExtra("lobbyPosition", 0);
        currentPlayer = FirebaseDatabase.getInstance().getReference().child("/Players").child("Player" + lobbyPosition);//allows the app to access the FireBase database*/

//        gameStateRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                gameState = dataSnapshot.getValue(Integer.class);
//                if(gameState == 3){
//                    this.finish();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });

        yayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.thisUser.setPoll(1);
                currentPlayer.child("poll").setValue(MainActivity.thisUser.getPoll());
                yayButton.setEnabled(false);
                nayButton.setEnabled(false);
                YayNayActivity.super.onBackPressed();
            }
        });//updates current user poll value to the first player to indicate that is who they voted for
        nayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.thisUser.setPoll(0);
                currentPlayer.child("poll").setValue(MainActivity.thisUser.getPoll());
                yayButton.setEnabled(false);
                nayButton.setEnabled(false);
                YayNayActivity.super.onBackPressed();
            }
        });//updates current user poll value to the first player to indicate that is who they voted for
    }
}
