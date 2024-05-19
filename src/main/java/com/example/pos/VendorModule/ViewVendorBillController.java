package com.example.pos.VendorModule;

import com.example.pos.BillModule.BillProductsDB;
import com.example.pos.BillModule.BillsController;
import com.example.pos.BillModule.CustomerBillTable;
import com.example.pos.BillModule.CustomerBillsDB;
import com.example.pos.UtilityClasses.Function;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ViewVendorBillController implements Initializable {

    @FXML
    private TextField balanceField;

    @FXML
    private TextField advanceField;

    @FXML
    private DatePicker billDate;

    @FXML
    private TextField invoiceNo;

    @FXML
    private TextArea billNoteTextArea;

    @FXML
    private TableColumn<CustomerBillTable, String> name;

    @FXML
    private TableColumn<CustomerBillTable, String> pCode;

    @FXML
    private TableColumn<CustomerBillTable, Double> price;

    @FXML
    private TableColumn<CustomerBillTable, Integer> quantity;

    @FXML
    private TableView<CustomerBillTable> table;

    @FXML
    private TableColumn<CustomerBillTable, Double> total;

    @FXML
    private TextField totalBillField;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        if(BillsController.billId != 0){
            try{
                Connection connection = DriverManager.getConnection(Function.databasePath);
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery("SELECT * FROM "+VendorBillDB.tableName+
                        " WHERE "+VendorBillDB.idColumn+" = "+ 2);

                ResultSet rsProducts = statement.executeQuery("SELECT * FROM "+ BillProductsDB.billProductsTableName+" WHERE "
                        +BillProductsDB.billIdColumn+ " = "+ BillsController.billId);

                if (rs.next()){
                    invoiceNo.setText(String.valueOf(rs.getInt(CustomerBillsDB.idColumn)));
                    billDate.setValue(LocalDate.parse(rs.getString(CustomerBillsDB.dateColumn)));
                    advanceField.setText(String.valueOf(rs.getDouble(CustomerBillsDB.advanceColumn)));
                    balanceField.setText(String.valueOf(rs.getDouble(CustomerBillsDB.balanceColumn)));
                    billNoteTextArea.setText(rs.getString(CustomerBillsDB.noteColumn));


                    pCode.setCellValueFactory(new PropertyValueFactory<>("code"));
                    name.setCellValueFactory(new PropertyValueFactory<>("name"));
                    quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
                    price.setCellValueFactory(new PropertyValueFactory<>("price"));
                    total.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

                    double totalBill = 0.00;

                    while (rsProducts.next()){
                        //Calculating total bill
                        totalBill = totalBill + (rsProducts.getDouble(BillProductsDB.retailPriceColumn)
                                * rsProducts.getInt(BillProductsDB.quantityColumn));
                        totalBillField.setText(String.valueOf(totalBill));
                        //initializing products in table
                        table.getItems().add(new CustomerBillTable(rsProducts.getString(BillProductsDB.codeColumn),
                                rsProducts.getString(BillProductsDB.nameColumn), rsProducts.getInt(BillProductsDB.quantityColumn),
                                rsProducts.getDouble(BillProductsDB.retailPriceColumn), rsProducts.getDouble("purchasePrice") , false
                                ,(rsProducts.getDouble(BillProductsDB.retailPriceColumn) * rsProducts.getInt(BillProductsDB.quantityColumn))));
                    }

                }
            }
            catch (Exception e){
                System.out.println(e.getMessage());
                Function.alert(Alert.AlertType.ERROR,"Error","An Error Occurred");
            }

        }
    }

//}
