package com.amarposondo.windows10.customeramarposondo.Model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class phonenumber {
    //private String phone, password;
    private String name, phone, password, image, address;
    private DatabaseReference databaseReference;
    public phonenumber()
    {

    }
    public phonenumber(String phone, String password) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        this.phone = phone;
        this.password = password;
    }



    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public phonenumber(String name, String phone, String password, String image, String address) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.image = image;
        this.address = address;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
