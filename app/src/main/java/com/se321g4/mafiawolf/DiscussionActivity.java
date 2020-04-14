package com.se321g4.mafiawolf;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Random;

public class DiscussionActivity extends AppCompatActivity {

    private int lobbyPosition;
    private Button ReadyButton;//Ready button
    private ImageButton roleIcon;
    private TextView roleName;
    private int roleNum; //stores player role
    private ArrayList<Integer> Roles = new ArrayList<>(); //list of numbers representing roles
    private int check = 0; //used to ensure we initialize Roles list only once
    private String playerName;
    DatabaseReference database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        lobbyPosition = getIntent().getIntExtra("lobbyPosition", 0);
        database = FirebaseDatabase.getInstance().getReference().child("/Players").child("Player" + lobbyPosition);;//allows the app to access the FireBase database*/

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);
        ReadyButton = findViewById(R.id.ReadyButton);//initializes the Play Game button
        roleIcon = findViewById(R.id.imageView3);
        roleName = findViewById(R.id.RoleName);

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

        MainActivity.thisUser.setRole(roleNum); //set the player role
        database.child("role").setValue(roleNum);//updates player role in database
        Roles.remove(roleNum); //remove role from list, cannot be assigned to another player


        //Used for setting Role icon in layout, based on assigned player role
        if(roleNum == 0){
           // roleIcon.setImageResource(R.drawable.roleciv);
            roleName.setText("Civilian");

            //currentPic = 4;
            //MainActivity.thisUser.setIcon(4);

            //Display brief Role info on image click
            roleIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Vote on and Accuse Other Players", Toast.LENGTH_LONG).show();
                }
            });
        }
        else if(roleNum == 1){
            //roleIcon.setImageResource(R.drawable.rolewolf);
            roleName.setText("Werewolf");
            //currentPic = 2;
            //MainActivity.thisUser.setIcon(2);

            roleIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Attack or Kill Players at Night", Toast.LENGTH_LONG).show();
                }
            });
        }
        else if(roleNum == 2){
            //roleIcon.setImageResource(R.drawable.rolecop);
            roleName.setText("Sheriff");
            //currentPic = 2;
            //MainActivity.thisUser.setIcon(2);

            roleIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Investigate Player Alignment", Toast.LENGTH_LONG).show();
                }
            });
        }
        else{
            //roleIcon.setImageResource(R.drawable.rolemed);
            roleName.setText("Doctor");
            //currentPic = 2;
            //MainActivity.thisUser.setIcon(2);

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
                MainActivity.thisUser.setPoll(1); //User is Ready
                Toast.makeText(getApplicationContext(),MainActivity.thisUser.getName()+" Is Ready!", Toast.LENGTH_SHORT).show();
                if(check == 4){
                    //Go to Next Activity
                }
            }
        });
    }

/*    private void createUser(DataSnapshot dataSnapshot){
        MainActivity.thisUser = new User(dataSnapshot.child("name").getValue().toString());
        MainActivity.thisUser.setIcon((Integer) dataSnapshot.child("icon").getValue());
        MainActivity.thisUser.setPoll((Integer) dataSnapshot.child("poll").getValue());
        MainActivity.thisUser.setRole((Integer) dataSnapshot.child("role").getValue());
    }*/
}
