package com.example.pos.LoginModule;

import com.example.pos.UtilityClasses.Function;
import com.example.pos.SceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

import static com.example.pos.UtilityClasses.Function.databasePath;

public class LoginController {

    public static String adminUserName="Admin@001";
    private String adminPassword="10203040";


    public static String currentUserName = "";

    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField userIdField;
    @FXML
    private Button loginButton;


    @FXML
    void onUserIdField(ActionEvent event) {
        passwordField.requestFocus();
    }

    @FXML
    void onPasswordField(ActionEvent event) throws SQLException {
        onLoginClicked(event);
    }

    @FXML
    void onLoginClicked(ActionEvent event) throws SQLException {

        Connection connection = DriverManager.getConnection(databasePath);
        Statement statement = connection.createStatement();
        String query = "SELECT Employee_UserName FROM Employee WHERE Employee_UserName = '"+userIdField.getText().trim()+
                "' AND Employee_Password = '"+passwordField.getText().trim()+"' ";
        ResultSet set = statement.executeQuery(query);
        if (set.next()){
            currentUserName = set.getString("Employee_UserName");
//            System.out.println(currentUserName);
            try {
                SceneController.switchScene("BillModule/Bills.fxml",event,"Bills");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else if(userIdField.getText().trim().equals(adminUserName)&&passwordField.getText().trim().equals(adminPassword)){
            currentUserName=adminUserName;
            try {
                SceneController.switchScene("BillModule/Bills.fxml",event,"Bills");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            Function.alert(Alert.AlertType.ERROR,"Error","Wrong User Id or password ");
            userIdField.clear();
            passwordField.clear();
            userIdField.requestFocus();
        }
    }

    @FXML
    void onExitClicked(ActionEvent event) {
        Stage stage= (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }


}
