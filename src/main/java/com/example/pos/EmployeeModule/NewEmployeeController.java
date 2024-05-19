package com.example.pos.EmployeeModule;

import com.example.pos.UtilityClasses.Function;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import javafx.scene.control.TextField;
import javafx.stage.Stage;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


import static com.example.pos.UtilityClasses.Function.checkAlphabets;
import static com.example.pos.UtilityClasses.Function.checkPhoneNo;

public class NewEmployeeController {

    // Declaration of EmployeeController Class static Object
    // so that we can call the method (refreshTableData) of the EmployeeController Class on runtime
    private static EmployeeController employeeControllerInNewEmployee;

    // This Method is setting the value of the static object created above
    public static void setEmployeeControllerInNewEmployee(EmployeeController controller) {
        employeeControllerInNewEmployee = controller;
    }

    @FXML
    private TextField employeeUserNameField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField phoneNumber1Field;

    @FXML
    private TextField phoneNumber2Field;

    @FXML
    private Button addButton;

    @FXML
    void onEmployeeUserNameField(ActionEvent event) {
nameField.requestFocus();
    }
    @FXML
    void onNameField(ActionEvent event) {
passwordField.requestFocus();
    }
    @FXML
    void onPasswordField(ActionEvent event) {
phoneNumber1Field.requestFocus();
    }
    @FXML
    void onPhoneNumber1Field(ActionEvent event) {
phoneNumber2Field.requestFocus();
    }
    @FXML
    void onPhoneNumber2Field(ActionEvent event) {
addButton.requestFocus();
    }


    @FXML
    void onAddClicked(ActionEvent event) throws SQLException {
        try {

            Connection connection = DriverManager.getConnection(Function.databasePath);

            boolean fieldsNotEmpty = true;
            if (employeeUserNameField.getText().trim().isEmpty() || nameField.getText().trim().isEmpty() || passwordField.getText().trim().isEmpty()
                    || phoneNumber1Field.getText().trim().isEmpty()) {
                fieldsNotEmpty = false;
            }

            if (fieldsNotEmpty) {
                if ( checkAlphabets(nameField.getText().trim())) {
                    if (checkPhoneNo(phoneNumber1Field.getText().trim())) {
                        if (checkPhoneNo(phoneNumber2Field.getText().trim())|| phoneNumber2Field.getText().trim().isEmpty()) {

                            String query = "Insert into Employee( Employee_UserName,Employee_Name,Employee_Password,Employee_Ph_1,Employee_Ph_2) "+
                                    "Values ('"+employeeUserNameField.getText().trim()+"', '"+nameField.getText().trim()+"', '"+
                                    passwordField.getText().trim()+"', '"+phoneNumber1Field.getText().trim()+"', '"+
                                    phoneNumber2Field.getText().trim()+"' ) ";

                            Statement statement = connection.createStatement();
                            statement.execute(query);
                            Function.alert(Alert.AlertType.INFORMATION, "Congratulations!!!", "Employee Added Successfully");
                            statement.close();
                            connection.close();
                            employeeUserNameField.clear();
                            nameField.clear();
                            passwordField.clear();
                            phoneNumber1Field.clear();
                            phoneNumber2Field.clear();

                            //Refreshing the Data in Employee Table View
                            if (employeeControllerInNewEmployee != null) {
                                employeeControllerInNewEmployee.refreshTableData();
                            }

                            //Closing the New Employee User Interface
                            Stage stage= (Stage) ((Node)event.getSource()).getScene().getWindow();
                            stage.close();
                        } else {
                            Function.alert(Alert.AlertType.ERROR, "Error", "Less or more than 11 digits & Special characters are not allowed for Phone Number 2");
                            phoneNumber2Field.clear();
                            phoneNumber2Field.requestFocus();
                        }
                    } else {
                        Function.alert(Alert.AlertType.ERROR, "Error", "Less or more than 11 digits & Special characters  are not allowed for Phone Number 1");
                        phoneNumber1Field.clear();
                        phoneNumber1Field.requestFocus();
                    }
                } else {
                    Function.alert(Alert.AlertType.ERROR, "Error", "Numbers or Special characters are not allowed in Name");
                    nameField.clear();
                    nameField.requestFocus();
                }
            } else {
                Function.alert(Alert.AlertType.ERROR, "Error", "Fill all the information");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            Function.alert(Alert.AlertType.ERROR,"Error",e.getMessage());
            employeeUserNameField.clear();
            employeeUserNameField.requestFocus();
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    void onCancelClicked(ActionEvent event) {
        Stage stage= (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }

}

