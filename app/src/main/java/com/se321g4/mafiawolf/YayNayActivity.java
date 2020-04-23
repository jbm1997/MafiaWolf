package com.se321g4.mafiawolf;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

public class YayNayActivity extends AppCompatActivity {

    private ImageView accusedPlayer;
    private Button yayButton;
    private Button nayButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yay_nay);

        yayButton = findViewById(R.id.yayButton);
        nayButton = findViewById(R.id.nayButton);
        accusedPlayer = findViewById(R.id.accusedImage);
    }
}
