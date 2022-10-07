package com.example.login;

public class colordetails {

    String colorcode,name;

    public colordetails(String colorcode, String name) {
        this.colorcode = colorcode;
        this.name = name;
    }

    public String getColorcode() {
        return colorcode;
    }

    public void setColorcode(String colorcode) {
        this.colorcode = colorcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public colordetails() {
    }
}
