package com.example.pos.EmployeeModule;

import com.example.pos.UtilityClasses.Function;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Employee {

    private String employeeUserName;
    private String employeeName;
    private String password;
    private String phoneNumber1;
    private String phoneNumber2;
    private Button viewButton;

    public Employee(String employeeUserName, String employeeName, String password, String phoneNumber1, String phoneNumber2) {
        this.employeeUserName = employeeUserName;
        this.employeeName = employeeName;
        this.password = password;
        this.phoneNumber1 = phoneNumber1;
        this.phoneNumber2 = phoneNumber2;
    }


    public Employee(String employeeUserName, String employeeName, String password, String phoneNumber1, String phoneNumber2, Button viewButton) {
        this.employeeUserName = employeeUserName;
        this.employeeName = employeeName;
        this.password = password;
        this.phoneNumber1 = phoneNumber1;
        this.phoneNumber2 = phoneNumber2;
//        Image viewImage = new Image("D:\\Projects\\POS Software\\POS_V2.1 - Increment 3.1\\src\\main\\resources\\com\\example\\pos\\Images\\ViewBlack.png");
//        Image viewImage = new Image(Function.viewButtonImagePath);
//        ImageView imageView = new ImageView(viewImage);
//        imageView.setFitWidth(25);
//        imageView.setFitHeight(20);
//        viewButton.setGraphic ( imageView );
//        viewButton.setStyle("-fx-background-color: transparent; ");
        viewButton.setStyle("-fx-background-color: white;  -fx-background-radius: 15;-fx-border-radius: 15; -fx-pref-width: 25; -fx-pref-height: 20; " +
                "    -fx-padding: 5; -fx-font-family: Verdana; -fx-font-size: 22; -fx-text-fill: black; ");
        this.viewButton = viewButton;
    }


    public String getEmployeeUserName() {
        return employeeUserName;
    }

    public void setEmployeeUserName(String employeeUserName) {
        this.employeeUserName = employeeUserName;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber1() {
        return phoneNumber1;
    }

    public void setPhoneNumber1(String phoneNumber1) {
        this.phoneNumber1 = phoneNumber1;
    }

    public String getPhoneNumber2() {
        return phoneNumber2;
    }

    public void setPhoneNumber2(String phoneNumber2) {
        this.phoneNumber2 = phoneNumber2;
    }

    public Button getViewButton() {
        return viewButton;
    }

    public void setViewButton(Button viewButton) {
        this.viewButton = viewButton;
    }
}
