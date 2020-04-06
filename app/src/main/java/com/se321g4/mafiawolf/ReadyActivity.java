package com.se321g4.mafiawolf;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Random;

public class ReadyActivity extends AppCompatActivity {

    private Button ReadyButton;//Ready button
    private ImageButton roleIcon;
    private int roleNum; //stores player role
    private ArrayList<Integer> Roles = new ArrayList<>(); //list of numbers representing roles
    private int check = 0; //used to ensure we initialize Roles list only once


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ready);
        ReadyButton = findViewById(R.id.ReadyButton);//initializes the Play Game button
        roleIcon = findViewById(R.id.imageView3);

        if(check == 0) { //add our role numbers to list
            Roles.add(0);
            Roles.add(1);
            Roles.add(2);
            Roles.add(3);
        }
        check++;

        Random rNumber = new Random();
        int i;
        i = rNumber.nextInt(Roles.size());  //Receive Random Index based on size of list
        roleNum = Roles.get(i);  //get value at index

        thisUser.setRole(roleNum); //set the player role
        Roles.remove(roleNum); //remove role from list, cannot be assigned to another player


        //Used for setting Role icon in layout, based on assigned player role
        if(roleNum == 0){
            roleIcon.setImageResource(R.drawable.Icon4);
            //currentPic = 4;
            //thisUser.setIcon(4);

            //Display brief Role info on image click
            roleIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Vote on and Accuse Other Players", Toast.LENGTH_LONG).show();
                }
            });
        }
        else if(roleNum == 1){
            roleIcon.setImageResource(R.drawable.Icon2);
            //currentPic = 2;
            //thisUser.setIcon(2);

            roleIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Attack or Kill Players at Night", Toast.LENGTH_LONG).show();
                }
            });
        }
        else if(roleNum == 2){
            roleIcon.setImageResource(R.drawable.Icon1);
            //currentPic = 2;
            //thisUser.setIcon(2);

            roleIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Investigate Player Alignment", Toast.LENGTH_LONG).show();
                }
            });
        }
        else{
            roleIcon.setImageResource(R.drawable.Icon3);
            //currentPic = 2;
            //thisUser.setIcon(2);

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
                thisUser.setPoll(1); //User is Ready
                Toast.makeText(getApplicationContext(),thisUser.getName()+" Is Ready!", Toast.LENGTH_SHORT).show();
                if(check == 4){
                    //Go to Next Activity
                }
            }
        });
    }
}
