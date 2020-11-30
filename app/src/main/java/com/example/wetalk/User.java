package com.example.wetalk;

public class User {

    private String name;
    private String status;
    private String image;
    private String phone;

    public User() {
        this.name = null;
        this.status = null;
        this.image = null;
        this.phone = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
