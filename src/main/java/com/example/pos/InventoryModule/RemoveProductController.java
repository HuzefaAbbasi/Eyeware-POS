package com.example.pos.InventoryModule;

import com.example.pos.UtilityClasses.Function;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.*;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class RemoveProductController implements Initializable {

    // Declaration of InventoryController Class static Object
    // so that we can call the method (refreshTableData) of the InventoryController Class on runtime
    private static InventoryController inventoryControllerInRemoveProduct;

    // This Method is setting the value of the static object created above
    public static void setInventoryControllerInRemoveProduct(InventoryController controller) {
        inventoryControllerInRemoveProduct = controller;
    }

    @FXML
    private TextField categoryField;
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
    private Button deleteButton;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        String productCode = InventoryController.productCode;
        if (productCode != null){
            try{
                Connection connection = DriverManager.getConnection(Function.databasePath);
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery("Select * From Inventory Where productCode = '"+productCode+"' ");
                rs.next();
                categoryField.setText(rs.getString(ProductDB.productCategoryColumn));
                this.productCodeField.setText(rs.getString(ProductDB.productCodeColumn));
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
                Function.alert(Alert.AlertType.ERROR,"Error",e.getMessage());
            }
        }
    }


    @FXML
    void onDeleteClicked(ActionEvent event) {

        try {

            Connection connection = DriverManager.getConnection(Function.databasePath);

                    String query = "Delete * from Inventory where productCode = '"+productCodeField.getText().trim()+"' ";

                    Statement statement = connection.createStatement();
                    JOptionPane JOptionPane = null;
                    int choice = JOptionPane.showConfirmDialog(null, "Do you really want to delete this Product?", "Confirmation", JOptionPane.OK_CANCEL_OPTION);
                      if(choice == JOptionPane.OK_OPTION) {
                         statement.execute(query);
                         Function.alert(Alert.AlertType.INFORMATION,"Congratulations!!!","Product has been deleted from your Inventory");
                         statement.close();
                         connection.close();

                         if(inventoryControllerInRemoveProduct != null){
                             inventoryControllerInRemoveProduct.refreshTableData();
                         }

                         Stage stage= (Stage) ((Node)event.getSource()).getScene().getWindow();
                         stage.close();

                      }
                       else{
                          Stage stage= (Stage) ((Node)event.getSource()).getScene().getWindow();
                          stage.close();
                       }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            Function.alert(Alert.AlertType.ERROR,"Error",e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void onCancelClicked(ActionEvent event) {
        Stage stage= (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }

}