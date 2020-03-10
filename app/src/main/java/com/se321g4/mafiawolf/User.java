package com.se321g4.mafiawolf;
/*
*Developer: Jaime Botero Martinez
*Date:01/28/20
*
* This class represents a user object. The user should have their name declared upon initialization,
* their role (Civilian = 0, Seer = 1, Doctor = 2, Wolf = 3) is declared post-initialization.
*/

public class User{
    private String name;
    private int role;
    private boolean isVIP;

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
}
