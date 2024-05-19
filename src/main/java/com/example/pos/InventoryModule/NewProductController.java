package com.example.pos.InventoryModule;

import com.example.pos.UtilityClasses.Function;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import static com.example.pos.UtilityClasses.Function.*;

public class NewProductController implements Initializable {

    // Declaration of InventoryController Class static Object
    // so that we can call the method (refreshTableData) of the InventoryController Class on runtime
    private static InventoryController inventoryControllerInNewProduct;

    // This Method is setting the value of the static object created above
    public static void setInventoryControllerInNewProduct(InventoryController controller) {
        inventoryControllerInNewProduct = controller;
    }

    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    private TextField productCodeField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField stockField;
    @FXML
    private TextField purchasePriceField;
    @FXML
    private TextField minimumRetailPriceField;
    @FXML
    private TextField discountField;
    @FXML
    private Button addButton;

    ObservableList<String> categoryList = FXCollections.observableArrayList("Contact Lens", "Opthalmic Lens", "Frame", "Lens Solution", "Sun Glasses", "Batteries", "Other");


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        categoryComboBox.setItems(categoryList);
    }


    @FXML
    void onProductCodeField(ActionEvent event) { nameField.requestFocus(); }
    @FXML
    void onNameField(ActionEvent event) {
        stockField.requestFocus();
    }
    @FXML
    void onStockField(ActionEvent event) {
        purchasePriceField.requestFocus();
    }
    @FXML
    void onPurchasePriceField(ActionEvent event) {
        minimumRetailPriceField.requestFocus();
    }
    @FXML
    void onMinimumRetailPriceField(ActionEvent event) {
        discountField.requestFocus();
    }
    @FXML
    void onDiscountField(ActionEvent event) { addButton.requestFocus(); }


    @FXML
    void onAddClicked(ActionEvent event) throws SQLException {

        try {
            Connection connection = DriverManager.getConnection(Function.databasePath);

            boolean fieldsNotEmpty = true;
            if ( categoryComboBox.getValue().trim()==null || productCodeField.getText().trim().isEmpty() || nameField.getText().trim().isEmpty() || purchasePriceField.getText().trim().isEmpty()
                    || stockField.getText().trim().isEmpty() || minimumRetailPriceField.getText().trim().isEmpty() || discountField.getText().trim().isEmpty()){
                fieldsNotEmpty = false;
            }

            if(fieldsNotEmpty){
                if( checkForInt(stockField.getText().trim()) ){
                    if( checkForDecimal(purchasePriceField.getText().trim()) ){
                        if( checkForDecimal(minimumRetailPriceField.getText().trim()) ) {
                            if (checkForDecimal(discountField.getText().trim())) {

                                double purchasePrice = Double.parseDouble(purchasePriceField.getText().trim());
                                double minimumRetailPrice = Double.parseDouble(minimumRetailPriceField.getText().trim());
                                double discount = Double.parseDouble(discountField.getText().trim());

                                if (minimumRetailPrice >= purchasePrice) {
                                    if(discount<100) {

                                        String query = " Insert into Inventory ( category, productCode, productName, stock, purchasePrice, minimumRetailPrice, discount) " +
                                                "Values('" + categoryComboBox.getValue().trim() + "' , '" + productCodeField.getText().trim() + "' , '" + nameField.getText().trim() + "' ," +
                                                Integer.parseInt(stockField.getText().trim()) + " , " + purchasePrice + " , " + minimumRetailPrice + " , " + discount + " ) ";

                                        Statement statement = connection.createStatement();
                                        statement.execute(query);
                                        Function.alert(Alert.AlertType.INFORMATION, "Congratulations!!!", "Product added into inventory successfully");
                                        statement.close();
                                        connection.close();

                                        //Refreshing the Data in Inventory Table View
                                        if (inventoryControllerInNewProduct != null) {
                                            inventoryControllerInNewProduct.refreshTableData();
                                        }

                                        //Closing the New Product User Interface
                                        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                                        stage.close();
                                    } else {
                                        Function.alert(Alert.AlertType.ERROR, "Error", "Discount not Allowed");
                                        discountField.clear();
                                        discountField.requestFocus();
                                    }
                                } else {
                                    Function.alert(Alert.AlertType.ERROR, "Error", "Retail price is less than purchase price ");
                                    minimumRetailPriceField.clear();
                                    minimumRetailPriceField.requestFocus();
                                }
                            } else {
                                Function.alert(Alert.AlertType.ERROR, "Error", "Special characters or Alphabets are not allowed" +
                                        " for Discount");
                                discountField.clear();
                                discountField.requestFocus();
                            }
                        } else{
                            Function.alert(Alert.AlertType.ERROR,"Error","Special characters or Alphabets are not allowed" +
                                    " for Retail Price");
                            minimumRetailPriceField.clear();
                            minimumRetailPriceField.requestFocus();
                        }
                    }else{
                        Function.alert(Alert.AlertType.ERROR,"Error","Special characters or Alphabets are not allowed" +
                                " for Purchase Price");
                        purchasePriceField.clear();
                        purchasePriceField.requestFocus();
                    }
                }else{
                    Function.alert(Alert.AlertType.ERROR,"Error","Special characters or Alphabets are not allowed" +
                            " for Stock");
                    stockField.clear();
                    stockField.requestFocus();
                }
            }else{
               Function.alert(Alert.AlertType.ERROR,"Error","Fill all the information");
            }
           }
        catch(SQLException e){
            System.out.println(e.getMessage());
            Function.alert(Alert.AlertType.ERROR,"Error",e.getMessage());
        }
        catch (Exception ex){
            Function.alert(Alert.AlertType.ERROR,"Error",ex.getMessage());
        }
    }

    @FXML
    void onCancelClicked(ActionEvent event) {
        Stage stage= (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }

}
