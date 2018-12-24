package com.example.karimm7mad.vehicletracker;

public class Car {
    public String name;
    public String numOfCar;
    public String color;
    public String numToCall;
    public float currentLatitude;
    public float currentLongitude;
    public boolean isCarMoving;
    public Car() {}
    public Car(String name, String numOfCar, String color, String numToCall, float currentLatitude, float currentLongitude) {
        this.name = name;
        this.numOfCar = numOfCar;
        this.color = color;
        this.numToCall = numToCall;
        this.currentLatitude = currentLatitude;
        this.currentLongitude = currentLongitude;
        this.isCarMoving = false;
    }
    @Override
    public String toString() {
        return "name:" + name +
                "\nnumOfCar:" + numOfCar +
                "\ncolor:" + color +
                "\nnumToCall:" + numToCall +
                "\ncurrentLatitude:" + currentLatitude +
                "\ncurrentLongitude:" + currentLongitude;
    }
}
