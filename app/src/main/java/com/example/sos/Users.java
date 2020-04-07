package com.example.sos;
public class Users {

    public String name;
    public String image;
    public String status;
    public String thumb_image;


    public Users(String name, String image, String status, String thumb) {
        this.name = name;
        this.thumb_image = thumb;
        this.image = image;
        this.status = status;
    }

    public Users() {

    }

    public String getThumbImage() {
        return thumb_image;
    }

    public void setThumbImage(String thumb) {
        this.thumb_image = thumb;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
