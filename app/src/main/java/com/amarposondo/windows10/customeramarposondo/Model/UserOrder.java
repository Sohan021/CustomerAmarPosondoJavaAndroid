package com.amarposondo.windows10.customeramarposondo.Model;

public class UserOrder
{
    private String name, phone, city, address, state, time, date, totalAmount;

    public UserOrder()
    {

    }

    public UserOrder(String name, String phone, String city, String address, String state, String time, String date, String totalAmount) {
        this.name = name;
        this.phone = phone;
        this.city = city;
        this.address = address;
        this.state = state;
        this.time = time;
        this.date = date;
        this.totalAmount = totalAmount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }
}
