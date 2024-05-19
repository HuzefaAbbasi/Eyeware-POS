package com.example.pos.BillModule;

import com.example.pos.UtilityClasses.Function;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import static com.example.pos.UtilityClasses.Function.checkForDecimal;
import static com.example.pos.UtilityClasses.Function.checkForInt;

public class OrderBaseProductController implements Initializable {

    @FXML
    private TextField nameField;
    @FXML
    private TextField quantityField;
    @FXML
    private TextField purchasePriceField;
    @FXML
    private TextField retailPriceField;
    @FXML
    private TextField discountPercentageField;
    @FXML
    private TextField discountField;
    @FXML
    private Button add_UpdateButton;
    public static CustomerBillTable obProduct;
    DecimalFormat decimalFormat = new DecimalFormat("#.##");

    static int obProductCode = 1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        discountField.setText("0.00");
        if (!Function.isOBProdAdd) {
            CustomerBillTable product = UpdateBillProductController.billTableProduct;

            nameField.setText(product.getName());
            quantityField.setText(String.valueOf(product.getQuantity()));
            purchasePriceField.setText(String.valueOf(product.getPurchasePrice()));
            retailPriceField.setText(String.valueOf(product.getRetailPrice()));
            discountPercentageField.setText(String.valueOf(product.getDiscountPercentage()));
            discountField.setText(String.valueOf(product.getDiscount()));

            nameField.requestFocus();
        }
        else{
            try{
                //Getting the max prodCode form table
                Connection connection = DriverManager.getConnection(Function.databasePath);
                Statement statement = connection.createStatement();
                String query = "Select MAX(productCode) FROM OrderBasedProducts";
                ResultSet max = statement.executeQuery(query);
                if (max.next()){
                    obProductCode = max.getInt(1); //1 base index
                    //productCode to be used for adding
                    obProductCode++;
                }
            }catch (SQLException e){
                System.out.println(e.getMessage());
            }
        }

    }


    @FXML
    void onNameField(ActionEvent event) {
        quantityField.requestFocus();
    }
    @FXML
    void onQuantityField(ActionEvent event) {
        purchasePriceField.requestFocus();
    }
    @FXML
    void onPurchasePriceField(ActionEvent event) {
        retailPriceField.requestFocus();
    }
    @FXML
    void onRetailPriceField(ActionEvent event) {
        discountPercentageField.requestFocus();
    }

    @FXML
    void onDiscountPercentageField(ActionEvent event) {
        if (discountPercentageField.getText().isEmpty()){
            discountField.requestFocus();
        }
        else {
            if(Function.checkForDecimal(discountPercentageField.getText().trim())){
                double discountPercentage = Double.parseDouble(discountPercentageField.getText().trim());
                if ( retailPriceField.getText().isEmpty()){
                    Function.alert(Alert.AlertType.ERROR, "Error", "Please enter retail price");
                }else {
                    if(discountPercentage > 100){
                        Function.alert(Alert.AlertType.ERROR, "Error", "Discount Percentage not Allowed");
                        discountPercentageField.clear();
                        discountPercentageField.requestFocus();
                    }
                    else{
                        if(checkForDecimal(retailPriceField.getText().trim())){
                            double price = Double.parseDouble(retailPriceField.getText().trim());
                            double discount = price*(discountPercentage/100);
//                            // Format the double value
//                            discount = Double.parseDouble(decimalFormat.format(discount));
                            discountField.setText(String.valueOf(discount));
                            discountField.requestFocus();
                        } else {
                            Function.alert(Alert.AlertType.ERROR, "Error", "Retail Price is not in correct format");
                        }
                    }
                }
            } else {
                Function.alert(Alert.AlertType.ERROR, "Error", "Discount Percentage is not in correct format");
            }
        }
    }

    @FXML
    void onDiscountField(ActionEvent event) {
        if(checkForDecimal(discountField.getText().trim())){
            if(checkForDecimal(retailPriceField.getText().trim())){
                double discount = Double.parseDouble(discountField.getText().trim());
                double price = Double.parseDouble(retailPriceField.getText().trim());
                if (retailPriceField.getText().isEmpty() || discount > price ){
                    Function.alert(Alert.AlertType.ERROR, "Error", "Discount not Allowed");
                    discountField.clear();
                }
                else {
                    double discountPercentage = 100 - (((price - discount)/price)*100);
//                    // Format the double value
//                    discountPercentage = Double.parseDouble(decimalFormat.format(discountPercentage));
                    discountPercentageField.setText(String.valueOf(discountPercentage));
                }

                add_UpdateButton.requestFocus();
            } else {
                Function.alert(Alert.AlertType.ERROR, "Error", "Retail Price is not in correct format");
            }
        }else {
            Function.alert(Alert.AlertType.ERROR, "Error", "Discount is not in correct format");
        }
    }

    @FXML
    void onAdd_UpdateClicked(ActionEvent event)  {
        try {

            boolean fieldsNotEmpty = true;
            if ( nameField.getText().trim().isEmpty() || purchasePriceField.getText().trim().isEmpty() || quantityField.getText().trim().isEmpty()
                    || retailPriceField.getText().trim().isEmpty() || (discountField.getText().trim().isEmpty() && discountPercentageField.getText().trim().isEmpty() )){
                fieldsNotEmpty = false;
            }

            if(fieldsNotEmpty){
                if( checkForInt(quantityField.getText().trim()) ){
                    if( checkForDecimal(purchasePriceField.getText().trim()) ){
                        if( checkForDecimal(retailPriceField.getText().trim()) ) {
                            if (checkForDecimal(discountPercentageField.getText().trim())){
                                if (checkForDecimal(discountField.getText().trim())) {
                                    double purchasePrice = Double.parseDouble(purchasePriceField.getText().trim());
                                    double retailPrice = Double.parseDouble(retailPriceField.getText().trim());
                                    double discountPercentage = 0;
                                    double discount = 0;

                                    if (!discountPercentageField.getText().isEmpty()){
                                        discountPercentage = Double.parseDouble(discountPercentageField.getText().trim());
                                    }

                                    //Giving priority to discountAmount Field, if user enters in it then percentage should be updated accordingly
                                    if (!discountField.getText().isEmpty()){
                                        discount = Double.parseDouble(discountField.getText().trim());
                                        discountPercentage = 100 - (((retailPrice - discount)/retailPrice)*100);
                                        discountPercentageField.setText(String.valueOf(discountPercentage));
                                    }

                                    if ((retailPrice-discount) >= purchasePrice) {

                                        if(discountPercentage<100.00) {
//                                            // Format the double value
//                                            discountPercentage = Double.parseDouble(decimalFormat.format(discountPercentage));
//                                            discount = Double.parseDouble(decimalFormat.format(discount));


                                            if (Function.isOBProdAdd){
                                                //making object for CustomerBill
                                                String code = String.valueOf(obProductCode);
                                                obProduct = new CustomerBillTable(code,nameField.getText().trim(),Integer.parseInt(quantityField.getText().trim()),
                                                        retailPrice, purchasePrice, discountPercentage, discount,true,
                                                        Double.parseDouble(decimalFormat.format(retailPrice*Integer.parseInt(quantityField.getText().trim())
                                                                - discount *Integer.parseInt(quantityField.getText().trim())))
                                                        , new Button("✎"),new Button("🗑"));

                                            }
                                            else {
                                                UpdateBillProductController.billTableProduct.setName(nameField.getText().trim());
                                                UpdateBillProductController.billTableProduct.setQuantity(Integer.parseInt(quantityField.getText().trim()));
                                                UpdateBillProductController.billTableProduct.setRetailPrice(retailPrice);
                                                UpdateBillProductController.billTableProduct.setPurchasePrice(purchasePrice);
                                                UpdateBillProductController.billTableProduct.setTotalPrice(Double.parseDouble(decimalFormat.format(
                                                        retailPrice*Integer.parseInt(quantityField.getText().trim()) - discount*Integer.parseInt(quantityField.getText().trim()))));
                                                UpdateBillProductController.billTableProduct.setDiscountPercentage(discountPercentage);
                                                UpdateBillProductController.billTableProduct.setDiscount(discount);
                                                Function.isOBProdAdd = true;
                                            }
                                            //Closing the OBProduct User Interface
                                            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                                            stage.close();
                                        } else {
                                            Function.alert(Alert.AlertType.ERROR, "Error", "Discount not Allowed");
                                            discountField.clear();
                                            discountField.requestFocus();
                                        }
                                    } else {
                                        Function.alert(Alert.AlertType.ERROR, "Error", "Retail price is less than purchase price ");
                                        retailPriceField.clear();
                                        discountField.clear();
                                        discountPercentageField.clear();
                                        retailPriceField.requestFocus();
                                    }
                                } else {
                                    Function.alert(Alert.AlertType.ERROR, "Error", "Special characters or Alphabets are not allowed" +
                                            " for Discount");
                                    discountField.clear();
                                    discountField.requestFocus();
                                }
                            }
                            else {
                                Function.alert(Alert.AlertType.ERROR, "Error", "Special characters or Alphabets are not allowed" +
                                        " for Discount Percentage");
                                discountPercentageField.clear();
                                discountPercentageField.requestFocus();
                            }

                        } else{
                            Function.alert(Alert.AlertType.ERROR,"Error","Special characters or Alphabets are not allowed" +
                                    " for Retail Price");
                            retailPriceField.clear();
                            retailPriceField.requestFocus();
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
                    quantityField.clear();
                    quantityField.requestFocus();
                }
            }else{
                Function.alert(Alert.AlertType.ERROR,"Error","Fill all the information");
            }
        }
        catch (Exception ex){
            Function.alert(Alert.AlertType.ERROR,"Error",ex.getMessage());
        }
    }

    @FXML
    void onCancelClicked(ActionEvent event) {
        UpdateBillProductController.billTableProduct = null;
        Function.isOBProdAdd = true;
        Stage stage= (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }



}
