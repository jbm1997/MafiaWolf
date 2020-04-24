package com.se321g4.mafiawolf;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

public class YayNayActivity extends AppCompatActivity {

    private ImageView accusedPlayer;
    private Button yayButton;
    private Button nayButton;

    /*DESIGN: poll value starts at 0 (you dont have to set it). "Yea" changes it to 1. "Nay" changes it to 2.
        Pressing either will disable (hide? idk how it works) both buttons.
        This activity listens for gamestate to change to 4, upon which we enter night time voting.
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yay_nay);

        yayButton = findViewById(R.id.yayButton);
        nayButton = findViewById(R.id.nayButton);
        accusedPlayer = findViewById(R.id.accusedImage);
    }
}
