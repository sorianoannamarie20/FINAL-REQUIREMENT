package com.example.afinal.Models;

public class ReadWriteUserDetails {
    public String fName, mName, lName;

    public ReadWriteUserDetails(){};

    public ReadWriteUserDetails(String textFName, String textMName, String textLName) {
        this.fName = textFName;
        this.mName = textMName;
        this.lName = textLName;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }
}
