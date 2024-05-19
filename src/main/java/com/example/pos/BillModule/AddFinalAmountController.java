package com.example.pos.BillModule;

import com.example.pos.UtilityClasses.Function;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddFinalAmountController implements Initializable {

    private static ViewCustomerBillController viewCustomerBillController;

    public static void setViewCustomerBillController(ViewCustomerBillController controller) {
        viewCustomerBillController = controller;
    }
    public static Double balanceAmount;
    public static Integer customerBillId;
    public static String customerName;
    public static String customerPhoneNo;

    @FXML
    private TextField finalAmountField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        finalAmountField.setText(String.valueOf(balanceAmount));
    }

    @FXML
    void onAddClicked(ActionEvent event) {

        //Setting the balance & final field in ViewCustomerBillController
        if (viewCustomerBillController != null) {
            viewCustomerBillController.setBalanceField("0.0");
            viewCustomerBillController.setFinalAmountField(finalAmountField.getText());
            viewCustomerBillController.setFinalAmountButtonInvisible();
        }

        try {
            Connection connection = DriverManager.getConnection(Function.databasePath);
            Statement statement = connection.createStatement();
            statement.execute("Update CustomerBills set finalAmount = "+Double.parseDouble(finalAmountField.getText().trim())+
                    " , finalAmountDate = '"+ LocalDate.now().toString() +"' , billBalance = 0.0 where cBillId = "+ customerBillId);

            ResultSet rs = statement.executeQuery("SELECT * from Customer where customerNo = '"+customerPhoneNo+"'");
            Integer customerID = 0;
            if(rs.next()) {
                customerID = rs.getInt("customerId");
                ResultSet rsCN = statement.executeQuery("Select * from CustomerName where customerId = " + customerID + " AND customerName = '" + customerName + "'");
                Double customerCredit = 0.0;
                if (rsCN.next()) {
                    customerCredit = rsCN.getDouble("customerCredit");
                    customerCredit = customerCredit - Double.parseDouble(finalAmountField.getText());
                    statement.execute("Update CustomerName set customerCredit = " + customerCredit + " where customerId = " + customerID + " AND customerName = '" + customerName + "'");
                }
            }

            Function.alert(Alert.AlertType.INFORMATION, "Congratulations", "Final Amount Added Successfully!!");
            statement.close();
            connection.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Stage stage= (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.close();

    }

    @FXML
    void onCancelClicked(ActionEvent event) {
        Stage stage= (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }

}

