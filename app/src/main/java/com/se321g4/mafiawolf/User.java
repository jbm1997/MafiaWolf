package com.se321g4.mafiawolf;
/*
*Developer: Jaime Botero Martinez
*Date:01/28/20
*
* This class represents a user object. The user should have their name declared upon initialization,
* their role (Civilian = 0, Seer = 1, Doctor = 2, Wolf = 3) is declared post-initialization.
*/

/*import android.os.Parcel;
import android.os.Parcelable;*/

public class User{ //implements Parcelable { //Parcelable is an implementation that allows a custom object to be passed between intents in Android Studio
    private String name;
    private int role;
    private boolean isVIP = false;

    User(String name){
        this.name = name;
    }//constructor for User class, contains the users name

    public String getName(){
        return name;
    }//getName


    public int getRole(){
        return role;
    }//getRole

    public boolean getVIP(){
        return isVIP;
    }//getVIP

    public void setRole(int role){
        this.role = role;
    }//setRole

    public void setVIP(boolean vip){
        isVIP = vip;
    }//setVIP

/*    public void writeToParcel(Parcel out, int flags){
        out.writeString(name);
        out.writeInt(role);
        out.writeBoolean(isVIP);
    }*/
}
