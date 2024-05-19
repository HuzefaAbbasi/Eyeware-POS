package com.example.pos.BillModule;

import com.example.pos.UtilityClasses.Function;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ReturnItemQuantityController {

    public static CustomerBillTable product;
    public static Integer returnedQuantity;

    private static ViewCustomerBillController viewCustomerBillController;

    public static void setViewCustomerBillController(ViewCustomerBillController controller) {
        viewCustomerBillController = controller;
    }

    @FXML
    private TextField returnItemQuantityField;
    @FXML
    private Button addButton;

    @FXML
    void onReturnItemQuantityField(ActionEvent event) {addButton.requestFocus();}

    @FXML
    void onAddClicked(ActionEvent event) {
        int  maximumQuantity = product.getQuantity()-returnedQuantity;
        if(Function.checkForInt(returnItemQuantityField.getText().trim())){
            int quantityEntered = Integer.parseInt(returnItemQuantityField.getText().trim());
            if(quantityEntered <= maximumQuantity){
                if (viewCustomerBillController != null) {
                 viewCustomerBillController.returnItem(product,quantityEntered,returnedQuantity+quantityEntered);
                 Function.alert(Alert.AlertType.INFORMATION, "Congratulations", "Item Returned Successfully!!");
                 Stage stage= (Stage) ((Node)event.getSource()).getScene().getWindow();
                 stage.close();
                }
            } else if(quantityEntered > maximumQuantity){
                Function.alert(Alert.AlertType.ERROR, "Error", "Maximum Quantity is "+maximumQuantity);
                returnItemQuantityField.requestFocus();
            }
        }else {
            Function.alert(Alert.AlertType.ERROR, "Error", "Enter only digits!!!");
            returnItemQuantityField.requestFocus();
        }

    }

    @FXML
    void onCancelClicked(ActionEvent event) {
        Stage stage= (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }

}
