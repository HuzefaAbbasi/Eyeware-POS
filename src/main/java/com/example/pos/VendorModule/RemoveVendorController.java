package com.example.pos.VendorModule;

import com.example.pos.UtilityClasses.Function;
import com.example.pos.SceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class RemoveVendorController implements Initializable {

    // Declaration of VendorController Class static Object
    // so that we can call the method (refreshTableData) of the VendorController Class on runtime
    private static VendorController vendorControllerInRemoveVendor;

    // This Method is setting the value of the static object created above
    public static void setVendorControllerInRemoveVendor(VendorController controller) {
        vendorControllerInRemoveVendor = controller;
    }

    @FXML
    private Button removeButton;
    @FXML
    private Button searchButton;
    @FXML
    private TextField searchField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField creditField;
    @FXML
    private TextArea addressTextArea;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        String phoneNumber = SearchVendorController.vendorPhoneNumber;
        if (phoneNumber != null){
            try{
                Connection connection = DriverManager.getConnection(Function.databasePath);
                Statement statement = connection.createStatement();
                String query= "Select * From Vendor Where vendorPhoneNo = '"+phoneNumber+"' ";
                ResultSet rs = statement.executeQuery(query);
                rs.next();
                phoneNumberField.setText(rs.getString("vendorPhoneNo"));
                nameField.setText(rs.getString("vendorName"));
                creditField.setText(rs.getString("vendorCredit"));
                addressTextArea.setText(rs.getString("vendorAddress"));
                connection.close();
                statement.close();
                SearchVendorController.vendorPhoneNumber="";
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }

    @FXML
    void onSearchField(ActionEvent event) {searchButton.requestFocus();}

    @FXML
    void onSearchClicked(ActionEvent event) throws IOException {

        if (searchField.getText().trim().equals("*")) {
            Function.fxmlFileName = "VendorModule/RemoveVendor.fxml";
            Function.title = "Remove Vendor";
            SceneController.switchScene("VendorModule/SearchVendor.fxml", event, "Search Vendor");
        }
        else if(searchField.getText().trim().isEmpty()){
            Function.alert(Alert.AlertType.ERROR,"Error","Search Field is Empty, Please enter the Phone Number");
            searchField.requestFocus();
        }
        else if (searchField.getText().trim() != null){
            try {
                Connection connection = DriverManager.getConnection(Function.databasePath);
                Statement statement = connection.createStatement();
                String querry = "Select * From Vendor Where vendorPhoneNo = '"+searchField.getText().trim()+"' ";
                ResultSet rs = statement.executeQuery(querry);
                if (!rs.next()) {
                    Function.alert(Alert.AlertType.ERROR, "Error", "No such Vendor exists in the System" +
                            "Please enter the correct Phone Number");
                    phoneNumberField.clear();
                    nameField.clear();
                    creditField.clear();
                    addressTextArea.clear();
                    searchField.clear();
                    searchField.requestFocus();
                } else {
                    phoneNumberField.setText(rs.getString("vendorPhoneNo"));
                    nameField.setText(rs.getString("vendorName"));
                    creditField.setText(rs.getString("vendorCredit"));
                    addressTextArea.setText(rs.getString("vendorAddress"));
                    connection.close();
                    statement.close();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        else{
            Function.alert(Alert.AlertType.ERROR,"Error","No such Vendor exists in the System" +
                    "Please enter the correct Phone Number");
        }
    }

    @FXML
    void onRemoveClicked(ActionEvent event) {
        try {
            boolean vendorSelected=true;

            if (phoneNumberField.getText().trim().isEmpty() && nameField.getText().trim().isEmpty()
                    && creditField.getText().trim().isEmpty() && addressTextArea.getText().trim().isEmpty()) {
                vendorSelected = false;
            }
            if(vendorSelected) {
                Connection connection = DriverManager.getConnection(Function.databasePath);
                String query = "Delete * from Vendor where vendorPhoneNo = '"+phoneNumberField.getText().trim()+"' ";
                Statement statement = connection.createStatement();
                JOptionPane JOptionPane = null;
                int choice = JOptionPane.showConfirmDialog(null, "Do you really want to Remove?", "Confirmation", JOptionPane.OK_CANCEL_OPTION);
                if (choice == JOptionPane.OK_OPTION) {
                    statement.execute(query);
                    Function.alert(Alert.AlertType.INFORMATION, "Congratulations", "Vendor has been removed Successfully");
                    statement.close();
                    connection.close();
                    phoneNumberField.clear();
                    nameField.clear();
                    creditField.clear();
                    addressTextArea.clear();

                    //Refreshing the Data in Vendor Table View
                    if (vendorControllerInRemoveVendor != null) {
                        vendorControllerInRemoveVendor.refreshTableData();
                    }

                    Stage stage= (Stage) ((Node)event.getSource()).getScene().getWindow();
                    stage.close();
                }
                else {
                    phoneNumberField.clear();
                    nameField.clear();
                    creditField.clear();
                    addressTextArea.clear();
                    searchField.clear();
                    searchField.requestFocus();
                }
            }
            else{
                Function.alert(Alert.AlertType.ERROR, "Error", "Please select the customer first");
                searchField.clear();
                searchField.requestFocus();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void onCancelClicked(ActionEvent event) {
        Stage stage= (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }


}
