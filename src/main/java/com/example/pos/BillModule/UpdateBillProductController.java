package com.example.pos.BillModule;

import com.example.pos.InventoryModule.ProductDB;
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

public class UpdateBillProductController implements Initializable{

    @FXML
    private TextField productCodeField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField quantityField;
    @FXML
    private TextField retailPriceField;
    @FXML
    private TextField discountPercentageField;
    @FXML
    private TextField discountField;
    @FXML
    private Button updateButton;

    private int initialQuantity;

    public static CustomerBillTable billTableProduct;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        productCodeField.setText(billTableProduct.getCode());
        nameField.setText(billTableProduct.getName());
        quantityField.setText(String.valueOf(billTableProduct.getQuantity()));
        retailPriceField.setText(String.valueOf(billTableProduct.getRetailPrice()));
        discountPercentageField.setText(String.valueOf(billTableProduct.getDiscountPercentage()));
        discountField.setText(String.valueOf(billTableProduct.getDiscount()));
        nameField.setEditable(false); //change in AMO
        productCodeField.setEditable(false);
        initialQuantity = Integer.parseInt(quantityField.getText().trim());
        productCodeField.requestFocus();
    }

    @FXML
    void onProductCodeField(ActionEvent event) { nameField.requestFocus(); }
    @FXML
    void onNameField(ActionEvent event) { quantityField.requestFocus(); }
    @FXML
    void onQuantityField(ActionEvent event) {
        retailPriceField.requestFocus();
    }
    @FXML
    void onRetailPriceField(ActionEvent event) {
        discountPercentageField.requestFocus();
    }
    @FXML
    void onDiscountPercentageField(ActionEvent event) {
        double discountPercentage = Double.parseDouble(discountPercentageField.getText().trim());
        if(discountPercentage > 100 || retailPriceField.getText().isEmpty()){
            Function.alert(Alert.AlertType.ERROR, "Error", "Discount Percentage not Allowed");
            discountPercentageField.clear();
        }
        else{
            double price = Double.parseDouble(retailPriceField.getText().trim());
            double discount = price*(discountPercentage/100);
//            // Format the double value
//            discount = Double.parseDouble(decimalFormat.format(discount));
            discountField.setText(String.valueOf(discount));
            discountField.requestFocus();
        }
    }
    @FXML
    void onDiscountField(ActionEvent event) {
        double discount = Double.parseDouble(discountField.getText().trim());
        double price = Double.parseDouble(retailPriceField.getText().trim());
        if (retailPriceField.getText().isEmpty() || discount > price ){
            Function.alert(Alert.AlertType.ERROR, "Error", "Discount not Allowed");
            discountField.clear();
        }
        else {
            double discountPercentage = 100 - (((price - discount)/price)*100);
            // Format the double value
//            discountPercentage = Double.parseDouble(decimalFormat.format(discountPercentage));
            discountPercentageField.setText(String.valueOf(discountPercentage));
        }
        updateButton.requestFocus();
    }

    @FXML
    void onUpdateClicked(ActionEvent event) {
        if (productCodeField.getText().isEmpty()){
            Function.alert(Alert.AlertType.ERROR, "Error", "Please enter product code");
            return;
        }
        try{
            ResultSet rs;
            Connection connection = DriverManager.getConnection(Function.databasePath);
            String query = "Select * From "+ ProductDB.productTableName+" Where "+ProductDB.productCodeColumn+" = '" + productCodeField.getText().trim() + "'";
            Statement statement = connection.createStatement();
            rs = statement.executeQuery(query);
            statement.close();
            connection.close();

            if(!rs.next()){
                Function.alert(Alert.AlertType.ERROR,"Error","No Product Exists for this Code.");
                productCodeField.clear();
                return;
            }

            nameField.setText(rs.getString(ProductDB.productNameColumn));
            if (quantityField.getText().isEmpty()){
                Function.alert(Alert.AlertType.ERROR, "Error", "Please enter product quantity");
                return;
            }
            if (!Function.checkForInt(quantityField.getText().trim())){
                Function.alert(Alert.AlertType.ERROR,"Error","Please enter only numbers in quantity field");
                quantityField.clear();
                return;
            }
            //Checking Stock
            int quantity = Integer.parseInt(quantityField.getText().trim());
            int difference = initialQuantity -  quantity;
            int remainingStock = CustomerBillController.stockMap.get(productCodeField.getText().trim()) + difference;
//            if (difference > 0){
//                remainingStock =
//                System.out.println(remainingStock);
//            }
//            else if (difference < 0){
//                remainingStock = CustomerBillController.stockMap.get(productCodeField.getText().trim()) + difference;
//                System.out.println(remainingStock);
//            }
//            else {
//                remainingStock = CustomerBillController.stockMap.get(productCodeField.getText().trim());
//            }
            if( remainingStock < 0 ){
                Function.alert(Alert.AlertType.ERROR,"Error","Not Enough Stock");
                quantityField.clear();
                nameField.clear();
                productCodeField.clear();
                productCodeField.requestFocus();
                return;
            }
            else if (remainingStock <=10 ) {
                Function.alert(Alert.AlertType.ERROR,"Error","Stock Low, "+remainingStock+
                        " items left");
            }
            //Price field checks
            if (retailPriceField.getText().isEmpty()){
                Function.alert(Alert.AlertType.ERROR, "Error", "Please enter product price");
                return;
            }
            if (!Function.checkForDecimal(retailPriceField.getText().trim())){
                Function.alert(Alert.AlertType.ERROR,"Error","Please enter only numbers in price field");
                retailPriceField.clear();
                return;
            }
            if(rs.getDouble(ProductDB.productMinimumRetailPriceColumn) > Double.parseDouble(retailPriceField.getText().trim())){
                Function.alert(Alert.AlertType.ERROR,"Error","Price Not Allowed");
                retailPriceField.clear();
                retailPriceField.requestFocus();
                return;
            }
            if(!Function.checkForDecimal(discountPercentageField.getText().trim())){
                Function.alert(Alert.AlertType.ERROR,"Error","Please Enter correct discount percentage.");
                discountPercentageField.clear();
                return;
            }
            if (!Function.checkForDecimal(discountField.getText().trim())){
                Function.alert(Alert.AlertType.ERROR,"Error","Please Enter correct discount.");
                return;
            }
            // Logic

            double maxDiscount = rs.getDouble("discount");
            double priceAmount = Double.parseDouble(retailPriceField.getText().trim());
            double discountPercentage = 0;
            double discount = 0;

            if (!discountPercentageField.getText().isEmpty()){
                discountPercentage = Double.parseDouble(discountPercentageField.getText().trim());
            }

            //Giving priority to discountAmount Field, if user enters in it then percentage should be updated accordingly
            if (!discountField.getText().isEmpty()){
                discount = Double.parseDouble(discountField.getText().trim());
                discountPercentage = 100 - (((priceAmount - discount)/priceAmount)*100);
                discountPercentageField.setText(String.valueOf(discountPercentage));
            }
            if (maxDiscount < Double.parseDouble(discountPercentageField.getText().trim())){
                Function.alert(Alert.AlertType.ERROR,"Error","Please Enter correct discount.");
                discountPercentageField.clear();
                discountField.clear();
                discountPercentageField.requestFocus();
                return;
            }

            double discountedPrice = priceAmount - discount;
//            // Format the double value
//            discountPercentage = Double.parseDouble(decimalFormat.format(discountPercentage));
//            discount = Double.parseDouble(decimalFormat.format(discount));

            billTableProduct.setCode(productCodeField.getText().trim());
            billTableProduct.setName(nameField.getText().trim());
            billTableProduct.setQuantity(Integer.parseInt(quantityField.getText().trim()));
            billTableProduct.setRetailPrice(priceAmount);
            billTableProduct.setTotalPrice(
                   discountedPrice*Integer.parseInt(quantityField.getText().trim()));
            billTableProduct.setDiscountPercentage(discountPercentage);
            billTableProduct.setDiscount(discount);


            Stage stage= (Stage) ((Node)event.getSource()).getScene().getWindow();
            stage.close();

        }
        catch (SQLException e){
            Function.alert(Alert.AlertType.ERROR,"Error",e.getMessage());
        }
    }

    @FXML
    void onCancelClicked(ActionEvent event) {
        billTableProduct = null;
        Stage stage= (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }


}
