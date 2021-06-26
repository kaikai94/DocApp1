package com.example.docapp.classes;

import android.content.Context;

import com.google.firebase.firestore.Exclude;

public interface UserInterface {

    void setAge(int age);
    int getAge();

    void setSpecialityId(String no);
    String getSpecialityId();

    void setSpecialityName(String no);
    String getSpecialityName();

    void setBadgeNo(String badgeNo);
    String getBadgeNo();

    void setDocumentId(String no);
    String getDocumentId();

    void setName(String name);
    String getName();

    void setIc_no(String Ic_no);
    String getIc_no();

}
