package com.example.pos.VendorModule;

import com.example.pos.UtilityClasses.Function;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class NewVendorController {

    // Declaration of VendorController Class static Object
    // so that we can call the method (refreshTableData) of the VendorController Class on runtime
    private static VendorController vendorControllerInNewVendor;

    // This Method is setting the value of the static object created above
    public static void setVendorControllerInNewVendor(VendorController controller) {
        vendorControllerInNewVendor = controller;
    }

    @FXML
    private Button addButton;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private TextField nameField;
    @FXML
    private TextArea addressTextArea;

    @FXML
    void onPhoneNumberField(ActionEvent event) {
nameField.requestFocus();
    }
    @FXML
    void onNameField(ActionEvent event) {
        addressTextArea.requestFocus();
    }

    @FXML
    void onAddClicked(ActionEvent event) {

        try {
            Connection connection = DriverManager.getConnection(Function.databasePath);

            boolean fieldsNotEmpty = true;
            if (phoneNumberField.getText().trim().isEmpty() || nameField.getText().trim().isEmpty() || addressTextArea.getText().trim().isEmpty()) {
                fieldsNotEmpty = false;
            }

            if (fieldsNotEmpty) {
                if (Function.checkPhoneNo(phoneNumberField.getText().trim())) {
                    if (Function.checkAlphabets(nameField.getText().trim())) {

                            String query = "Insert into Vendor( vendorPhoneNO, vendorName, vendorCredit, vendorAddress ) Values ( "+
                                    "'"+phoneNumberField.getText().trim()+"' , '"+nameField.getText().trim()+"' , 0 , '"+addressTextArea.getText().trim()+"' ) ";

                            Statement statement = connection.createStatement();
                            statement.execute(query);
                            Function.alert(Alert.AlertType.INFORMATION, "Congratulations", "Vendor Added Successfully");
                            statement.close();
                            connection.close();

                            phoneNumberField.clear();
                            nameField.clear();
                            addressTextArea.clear();

                            //Refreshing the Data in Vendor Table View
                            if (vendorControllerInNewVendor != null) {
                            vendorControllerInNewVendor.refreshTableData();
                            }

                            //Closing the New Vendor User Interface
                            Stage stage= (Stage) ((Node)event.getSource()).getScene().getWindow();
                            stage.close();
                    } else {
                        Function.alert(Alert.AlertType.ERROR, "Error", "Numbers or special characters are not allowed in Vendor Name");
                        nameField.clear();
                        nameField.requestFocus();
                    }
                } else {
                    Function.alert(Alert.AlertType.ERROR, "Error", "Special Characters or  less than 11 digits are not allowed in Vendor Phone Number");
                    phoneNumberField.clear();
                    phoneNumberField.requestFocus();
                }
            } else {
                Function.alert(Alert.AlertType.ERROR, "Error", "Fill all the information");
            }
        }catch (SQLException ex){
            Function.alert(Alert.AlertType.ERROR,"Error","This Phone Number is already linked to a Vendor");
            phoneNumberField.clear();
            phoneNumberField.requestFocus();
        } catch (Exception e) {
            Function.alert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    void onCancelClicked(ActionEvent event) {
        Stage stage= (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }

}
