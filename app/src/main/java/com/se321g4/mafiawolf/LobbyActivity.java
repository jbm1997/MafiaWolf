package com.se321g4.mafiawolf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LobbyActivity extends AppCompatActivity {
    DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("/Players");//allows the app to access the FireBase database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        TextView player1 = findViewById(R.id.player1);
        player1.setVisibility(View.VISIBLE);
    }
}
