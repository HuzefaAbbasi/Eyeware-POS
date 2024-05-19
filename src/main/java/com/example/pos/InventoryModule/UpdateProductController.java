package com.example.pos.InventoryModule;

import com.example.pos.UtilityClasses.Function;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;

import java.util.ResourceBundle;

import static com.example.pos.UtilityClasses.Function.checkForDecimal;
import static com.example.pos.UtilityClasses.Function.checkForInt;

public class UpdateProductController implements Initializable{

    // Declaration of InventoryController Class static Object
    // so that we can call the method (refreshTableData) of the InventoryController Class on runtime
    private static InventoryController inventoryControllerInUpdateProduct;

    // This Method is setting the value of the static object created above
    public static void setInventoryControllerInUpdateProduct(InventoryController controller) {
        inventoryControllerInUpdateProduct = controller;
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
    private Button updateButton;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        String productCode = InventoryController.productCode;
        if (productCode != null){
            try{
                Connection connection = DriverManager.getConnection(Function.databasePath);
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery("Select * From Inventory Where productCode = '"+productCode+"' ");
                rs.next();
                categoryComboBox.setValue(rs.getString(ProductDB.productCategoryColumn));
                productCodeField.setText(rs.getString(ProductDB.productCodeColumn));
                nameField.setText(rs.getString(ProductDB.productNameColumn));
                stockField.setText(Integer.toString(rs.getInt(ProductDB.productStockColumn)));
                purchasePriceField.setText(Double.toString(rs.getDouble(ProductDB.productPurchasePriceColumn)));
                minimumRetailPriceField.setText(Double.toString(rs.getDouble(ProductDB.productMinimumRetailPriceColumn)));
                discountField.setText(Double.toString(rs.getDouble("discount")));
                connection.close();
                statement.close();
                SearchProductController.productCode="";
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }


    @FXML
    void onNameField(ActionEvent event) {
        stockField.requestFocus();
    }
    @FXML
    void onStockField(ActionEvent event) { purchasePriceField.requestFocus(); }
    @FXML
    void onPurchasePriceField(ActionEvent event) { minimumRetailPriceField.requestFocus(); }
    @FXML
    void onMinimumRetailPriceField(ActionEvent event) { discountField.requestFocus(); }
    @FXML
    void onDiscountField(ActionEvent event) { updateButton.requestFocus(); }
    
    @FXML
    void onUpdateClicked(ActionEvent event) {
        try {
            Connection connection = DriverManager.getConnection(Function.databasePath);

            boolean fieldsNotEmpty = true;


            if ( categoryComboBox.getValue().trim()==null ||productCodeField.getText().trim().isEmpty() || nameField.getText().trim().isEmpty() || purchasePriceField.getText().trim().isEmpty()
                    || stockField.getText().trim().isEmpty() || minimumRetailPriceField.getText().trim().isEmpty() || discountField.getText().trim().isEmpty() ) {
                fieldsNotEmpty = false;
            }


                if (fieldsNotEmpty) {
                    if ( checkForInt(stockField.getText().trim()) ) {
                        if ( checkForDecimal(purchasePriceField.getText().trim()) ) {
                            if ( checkForDecimal(minimumRetailPriceField.getText().trim()) ) {
                                if (checkForDecimal(discountField.getText().trim())) {

                                double purchasePrice= Double.parseDouble(purchasePriceField.getText().trim());
                                double minimumRetailPrice= Double.parseDouble(minimumRetailPriceField.getText().trim());
                                double discount = Double.parseDouble(discountField.getText().trim());

                                if(minimumRetailPrice>=purchasePrice) {
                                    if(discount<100) {

                                    String query = "Update Inventory set productName = '"+nameField.getText().trim()+"', stock = " + Integer.parseInt(stockField.getText().trim()) +
                                            " , purchasePrice = " + purchasePrice + " , minimumRetailPrice = " + minimumRetailPrice +" , discount = "+ discount +
                                            " where productCode = '"+productCodeField.getText().trim()+"' ";
                                    Statement statement = connection.createStatement();
                                    statement.execute(query);
                                    Function.alert(Alert.AlertType.INFORMATION, "Congratulations!!!", "Product Updated Successfully");
                                    statement.close();
                                    connection.close();


                                    if(inventoryControllerInUpdateProduct!= null){
                                        inventoryControllerInUpdateProduct.refreshTableData();
                                    }

                                    Stage stage= (Stage) ((Node)event.getSource()).getScene().getWindow();
                                    stage.close();
                                } else {
                                    Function.alert(Alert.AlertType.ERROR, "Error", "Discount not Allowed");
                                    discountField.clear();
                                    discountField.requestFocus();
                                }

                                }else {
                                    Function.alert(Alert.AlertType.ERROR,"Error","Retail price is less than purchase price");
                                    minimumRetailPriceField.clear();
                                    minimumRetailPriceField.requestFocus();
                                }
                                } else {
                                    Function.alert(Alert.AlertType.ERROR, "Error", "Special characters or Alphabets are not allowed for Discount");
                                    discountField.clear();
                                    discountField.requestFocus();
                                }
                            } else {
                                Function.alert(Alert.AlertType.ERROR, "Error", "Special characters or Alphabets are not allowed for Retail Price");
                                minimumRetailPriceField.clear();
                                minimumRetailPriceField.requestFocus();
                            }
                        } else {
                            Function.alert(Alert.AlertType.ERROR, "Error", "Special characters or Alphabets are not allowed for Purchase Price");
                            purchasePriceField.clear();
                            purchasePriceField.requestFocus();
                        }
                    } else {
                        Function.alert(Alert.AlertType.ERROR, "Error", "Special characters or Alphabets are not allowed for Stock");
                        stockField.clear();
                        stockField.requestFocus();
                    }
                } else {
                    Function.alert(Alert.AlertType.ERROR, "Error", "Fill all the information");
                }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            Function.alert(Alert.AlertType.ERROR,"Error",e.getMessage());
        }
    }

    @FXML
    void onCancelClicked(ActionEvent event) {
        Stage stage= (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }

}




