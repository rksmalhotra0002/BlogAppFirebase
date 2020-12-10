package com.shubham.blogappfirebase.model;

public class Product {


    private String title,Description,image;

    public Product()
    {

    }

    public Product(String title, String Description, String image) {
        this.title = title;
        this.Description = Description;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return Description;
    }

    public String getImage() {
        return image;
    }
}
