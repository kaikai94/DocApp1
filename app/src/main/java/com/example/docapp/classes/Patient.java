package com.example.docapp.classes;

import com.google.firebase.firestore.Exclude;

public class Patient implements UserInterface {

    private String badgeNo;
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
        return this.name;
    }

    @Override
    public void setIc_no(String Ic_no) {
        this.ic_no = Ic_no;
    }

    @Override
    public String getIc_no() {
        return this.ic_no;
    }


    @Exclude
    @Override
    public String getBadgeNo() {
        return badgeNo;
    }

    @Exclude
    @Override
    public void setBadgeNo(String badgeNo) {
        this.badgeNo = badgeNo;
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

    @Exclude
    @Override
    public void setSpecialityId(String no) {

    }

    @Exclude
    @Override
    public String getSpecialityId() {
        return null;
    }

    @Exclude
    @Override
    public void setSpecialityName(String no) {

    }

    @Exclude
    @Override
    public String getSpecialityName() {
        return null;
    }


}
