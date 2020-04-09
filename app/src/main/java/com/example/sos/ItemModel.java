package com.example.sos;

public class ItemModel {
    private int image;
    private String name, age, city;

    public ItemModel() {

    }

    public ItemModel(int image, String name, String age, String city) {
        this.image = image;
        this.name = name;
        this.age = age;
        this.city = city;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
