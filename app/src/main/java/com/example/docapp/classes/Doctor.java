package com.example.docapp.classes;

import com.google.firebase.firestore.Exclude;

public class Doctor implements UserInterface
{
    private String badgeNo;
    private String specialityId;
    private String specialityName;
    private String documentId;
    private String name;
    private int age;
    private String ic_no;

    @Exclude
    @Override
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    @Exclude
    @Override
    public String getDocumentId() {
        return documentId;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setIc_no(String Ic_no) {
        this.ic_no = Ic_no;
    }

    @Override
    public String getIc_no() {
        return ic_no;
    }
    
    @Override
    public String getBadgeNo() {
        return badgeNo;
    }

    @Override
    public void setBadgeNo(String badgeNo) {
        this.badgeNo = badgeNo;
    }

    @Override
    public String getSpecialityId() {
        return specialityId;
    }

    @Override
    public void setSpecialityId(String specialityId) {
        this.specialityId = specialityId;
    }

    @Override
    public void setAge(int age)
    {
        this.age = age;
    }

    @Override
    public int getAge()
    {
        return age;
    }

    @Override
    public void setSpecialityName(String no) {
        this.specialityName = no;
    }

    @Override
    public String getSpecialityName() {
        return this.specialityName;
    }
}
