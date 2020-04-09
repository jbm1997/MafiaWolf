package com.se321g4.mafiawolf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class WaitActivity extends AppCompatActivity {
    private int lobbyPosition;
    private Button startGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        lobbyPosition =  getIntent().getIntExtra("lobbyPosition", 1);
        Toast.makeText(getApplicationContext(),Integer.toString(lobbyPosition), Toast.LENGTH_LONG).show();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);

        startGame = findViewById(R.id.button2);

        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toReady = new Intent(WaitActivity.this, ReadyActivity.class);//creates the intent to switch to the wait activity
                toReady.putExtra("lobbyPosition", lobbyPosition);//stores the lobby position for the local instance of the mobile app and passes it to the next activity
                startActivity(toReady);//switches to the wait activity for the game
            }//executes code on click
        });//listens for clicks on the button
    }
}
