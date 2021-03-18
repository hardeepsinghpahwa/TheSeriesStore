package com.example.login.Fragments;

public class itemdetails {

    String image;
    String name;
    String rating;
    String category,subcategory;
    String price,product,detail1,detail2,detail3,detail4;
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public itemdetails(String image, String name, String rating, String category, String price, String id) {
        this.image = image;
        this.name = name;
        this.rating = rating;
        this.category = category;
        this.price = price;
        this.id = id;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getDetail1() {
        return detail1;
    }

    public void setDetail1(String detail1) {
        this.detail1 = detail1;
    }

    public String getDetail2() {
        return detail2;
    }

    public void setDetail2(String detail2) {
        this.detail2 = detail2;
    }

    public String getDetail3() {
        return detail3;
    }

    public void setDetail3(String detail3) {
        this.detail3 = detail3;
    }

    public String getDetail4() {
        return detail4;
    }

    public void setDetail4(String detail4) {
        this.detail4 = detail4;
    }

    public itemdetails(String image, String name, String rating, String category, String subcategory, String price, String product, String detail1, String detail2, String detail3, String detail4, String id) {
        this.image = image;
        this.name = name;
        this.rating = rating;
        this.category = category;
        this.subcategory = subcategory;
        this.price = price;
        this.product = product;
        this.detail1 = detail1;
        this.detail2 = detail2;
        this.detail3 = detail3;
        this.detail4 = detail4;
        this.id = id;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }


    public itemdetails() {
    }
}
