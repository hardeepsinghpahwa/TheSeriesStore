package com.example.login;

public class cartitemdetails {
    String size,sizename,color,colorname;
    String id,image;

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSizename() {
        return sizename;
    }

    public void setSizename(String sizename) {
        this.sizename = sizename;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColorname() {
        return colorname;
    }

    public void setColorname(String colorname) {
        this.colorname = colorname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public cartitemdetails(String size, String sizename, String color, String colorname, String id, String image) {
        this.size = size;
        this.sizename = sizename;
        this.color = color;
        this.colorname = colorname;
        this.id = id;
        this.image = image;
    }

    public cartitemdetails() {
    }
}
