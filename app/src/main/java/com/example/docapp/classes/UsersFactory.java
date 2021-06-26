package com.example.docapp.classes;

public class UsersFactory {

    public UserInterface getUsers(String type){
        if(type == null){
            return null;
        }
        if(type.equalsIgnoreCase("PATIENT")){
            return new Patient();

        } else if(type.equalsIgnoreCase("DOCTOR")){
            return new Doctor();

        }

        return null;
    }
}
