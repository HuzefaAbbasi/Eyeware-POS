package com.example.pos.BillModule;

import com.example.pos.CustomerModule.CustomerDB;
import com.example.pos.LoginModule.LoginController;
import com.example.pos.UtilityClasses.Function;
import com.example.pos.SceneController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.ResourceBundle;

public class BillsController implements Initializable {

    public static Integer billId;

    @FXML
    private Button billButton;
    @FXML
    private Button customerButton;
    @FXML
    private Button inventoryButton;
    @FXML
    private Button vendorButton;
    @FXML
    private Button employeeButton;
    @FXML
    private Button reportButton;
    @FXML
    private Button signOutButton;
    @FXML
    private Button addPreviousBillButton;
    @FXML
    private Button localBackupButton;
    @FXML
    private Button profileButton;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Bill> table;
    @FXML
    private TableColumn<Bill, Integer> billIdTVColumn;
    @FXML
    private TableColumn<Bill, String> nameTVColumn;
    @FXML
    private TableColumn<Bill, String> phoneNumberTVColumn;
    @FXML
    private TableColumn<Bill, String> billDateTVColumn;
    @FXML
    private TableColumn<Bill, String> deliveryDateTVColumn;
    @FXML
    private TableColumn<Bill, Double> totalBillTVColumn;
    @FXML
    private TableColumn<Bill, Double> advanceTVColumn;
    @FXML
    private TableColumn<Bill, Double> balanceTVColumn;
    @FXML
    private TableColumn<Bill, Button> viewButtonTVColumn;
    @FXML
    private ComboBox<String> billTypeComboBox;
    ObservableList<String> billTypeList = FXCollections.observableArrayList("All", "Returned", "Balance");


    //showing data in table view
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        billButton.setStyle("-fx-background-color: #2c2483;");
        billTypeComboBox.setItems(billTypeList);
        billTypeComboBox.getSelectionModel().select(0);
        billIdTVColumn.setCellValueFactory(new PropertyValueFactory<>("billId"));
        nameTVColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        phoneNumberTVColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        billDateTVColumn.setCellValueFactory(new PropertyValueFactory<>("billDate"));
        deliveryDateTVColumn.setCellValueFactory(new PropertyValueFactory<>("deliveryDate"));
        totalBillTVColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        advanceTVColumn.setCellValueFactory(new PropertyValueFactory<>("advance"));
        balanceTVColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));
        viewButtonTVColumn.setCellValueFactory(new PropertyValueFactory<>("viewButton"));

        String query = "SELECT b."+ CustomerBillsDB.idColumn+", b."+CustomerBillsDB.dateColumn+", b."+CustomerBillsDB.deliveryDateColumn
                + ", b."+CustomerBillsDB.advanceColumn+", b."+ CustomerBillsDB.balanceColumn+",  b.customerNameId, b.billTotal AS totalPrice, cn.customerName, c."+ CustomerDB.phoneNumberColumn+ " FROM "+CustomerBillsDB.customerBillTableName+
                " b LEFT JOIN CustomerName cn ON cn.customerNameId = b.customerNameId LEFT JOIN Customer c on c.customerId = cn.customerId GROUP BY b."+
                CustomerBillsDB.idColumn+ ", b."+ CustomerBillsDB.dateColumn +", b."+CustomerBillsDB.deliveryDateColumn +", b."+
                CustomerBillsDB.advanceColumn+", b."+CustomerBillsDB.balanceColumn +", cn.customerName, c."+
                CustomerDB.phoneNumberColumn+ ", b.customerNameId";



        ObservableList billsList = showBills(query);
        table.setItems(billsList);

        viewButtonTVColumn.setCellFactory(param -> new TableCell<Bill, Button>() {
            @Override
            protected void updateItem(Button button, boolean empty) {
                super.updateItem(button, empty);

                if (empty || button == null) {
                    setGraphic(null);
                } else {
                    setGraphic(button);
                    button.setOnAction(event -> {
                        Bill bill = getTableView().getItems().get(getIndex());
                        billId = bill.getBillId();
                        Function.fxmlFileName="BillModule/Bills.fxml";
                        Function.title="Bills";
                        try {
                            SceneController.switchScene("BillModule/ViewCustomerBill.fxml",event,"View Customer Bill");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
        });

        if(!LoginController.currentUserName.equals(LoginController.adminUserName)){
            employeeButton.visibleProperty().set(false);
            vendorButton.visibleProperty().set(false);
            inventoryButton.visibleProperty().set(false);
            reportButton.visibleProperty().set(false);
            localBackupButton.visibleProperty().set(false);
            profileButton.visibleProperty().set(true);
        }

    }

    @FXML
    void dropDownChoiceAction(ActionEvent event) {
        String query = "";
        if(billTypeComboBox.getValue().equals("All")){
             query = "SELECT b."+CustomerBillsDB.idColumn+", b."+CustomerBillsDB.dateColumn+", b."+CustomerBillsDB.deliveryDateColumn
                    + ", b."+CustomerBillsDB.advanceColumn+", b."+ CustomerBillsDB.balanceColumn+",  b.customerNameId, b.billTotal AS totalPrice, cn.customerName, c."+CustomerDB.phoneNumberColumn+ " FROM "+CustomerBillsDB.customerBillTableName+
                    " b LEFT JOIN CustomerName cn ON cn.customerNameId = b.customerNameId LEFT JOIN Customer c on c.customerId = cn.customerId GROUP BY b."+
                    CustomerBillsDB.idColumn+ ", b."+ CustomerBillsDB.dateColumn +", b."+CustomerBillsDB.deliveryDateColumn +", b."+
                    CustomerBillsDB.advanceColumn+", b."+CustomerBillsDB.balanceColumn +", cn.customerName, c."+
                    CustomerDB.phoneNumberColumn+ ", b.customerNameId";
            }
        else if(billTypeComboBox.getValue().equals("Returned")){
            query = "SELECT b."+CustomerBillsDB.idColumn+", b."+CustomerBillsDB.dateColumn+", b."+CustomerBillsDB.deliveryDateColumn
                    + ", b."+CustomerBillsDB.advanceColumn+", b."+ CustomerBillsDB.balanceColumn+",  b.customerNameId, b.billTotal AS totalPrice, cn.customerName, c."+CustomerDB.phoneNumberColumn+ " FROM "+CustomerBillsDB.customerBillTableName+
                    " b LEFT JOIN CustomerName cn ON cn.customerNameId = b.customerNameId LEFT JOIN Customer c on c.customerId = cn.customerId WHERE" +
                    " b.hasReturnedItems = "+ 1 +"  GROUP BY b."+
                    CustomerBillsDB.idColumn+ ", b."+ CustomerBillsDB.dateColumn +", b."+CustomerBillsDB.deliveryDateColumn +", b."+
                    CustomerBillsDB.advanceColumn+", b."+CustomerBillsDB.balanceColumn +", cn.customerName, c."+
                    CustomerDB.phoneNumberColumn+ ", b.customerNameId";

        }
        else if(billTypeComboBox.getValue().equals("Balance")){
            query = "SELECT b."+CustomerBillsDB.idColumn+", b."+CustomerBillsDB.dateColumn+", b."+CustomerBillsDB.deliveryDateColumn
                    + ", b."+CustomerBillsDB.advanceColumn+", b."+ CustomerBillsDB.balanceColumn+",  b.customerNameId, b.billTotal AS totalPrice, cn.customerName, c."+CustomerDB.phoneNumberColumn+ " FROM "+CustomerBillsDB.customerBillTableName+
                    " b LEFT JOIN CustomerName cn ON cn.customerNameId = b.customerNameId LEFT JOIN Customer c on c.customerId = cn.customerId WHERE b."+
                     CustomerBillsDB.balanceColumn +" > "+ 0 +" GROUP BY b."+
                    CustomerBillsDB.idColumn+ ", b."+ CustomerBillsDB.dateColumn +", b."+CustomerBillsDB.deliveryDateColumn +", b."+
                    CustomerBillsDB.advanceColumn+", b."+CustomerBillsDB.balanceColumn +", cn.customerName, c."+
                    CustomerDB.phoneNumberColumn+ ", b.customerNameId";

        }
        ObservableList customerList = showBills(query);
        table.setItems(customerList);
    }


    public ObservableList showBills(String query) {

        ObservableList<Bill> billsList = FXCollections.observableArrayList();
        String phoneNumber = "", customerName = "";
        try {
            Connection connection = DriverManager.getConnection(Function.databasePath);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {

                phoneNumber = rs.getString("customerNo");
                customerName = rs.getString("customerName");

                if (phoneNumber==null){
                    phoneNumber = "";
                }
                if (customerName==null){
                    customerName = "";
                }
                billsList.add(new Bill(rs.getInt("cBillId"), customerName, phoneNumber
                        ,rs.getString("billDate"), rs.getString("deliveryDate"), rs.getDouble("totalPrice"),
                        rs.getDouble("billAdvance"), rs.getDouble("billBalance"),new Button("👁")));
            }

            connection.close();
            statement.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            Function.alert(Alert.AlertType.ERROR,"Error",e.getMessage());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return billsList;
    }

    @FXML
    void onSearchKeyDown(KeyEvent event) {

//        String query = "SELECT b."+CustomerBillsDB.idColumn+", b."+CustomerBillsDB.dateColumn+", b."+CustomerBillsDB.deliveryDateColumn
//                + ", b."+CustomerBillsDB.advanceColumn+", b."+ CustomerBillsDB.balanceColumn+",  b.customerNameId, sum(p."+BillProductsDB.retailPriceColumn
//                +") AS totalPrice, cn.customerName, c."+CustomerDB.phoneNumberColumn+ " FROM "+CustomerBillsDB.customerBillTableName+
//                " b JOIN "+ BillProductsDB.billProductsTableName+ " p ON b."+ CustomerBillsDB.idColumn +" = p."+ BillProductsDB.billIdColumn+
//                " LEFT JOIN CustomerName cn ON cn.customerNameId = b.customerNameId JOIN Customer c on c.customerId = cn.customerId WHERE CAST("+CustomerBillsDB.idColumn
//                +" AS VARCHAR(10)) LIKE '%"+ searchField.getText()+"%' GROUP BY b."+ CustomerBillsDB.idColumn+ ", b."+ CustomerBillsDB.dateColumn +
//                ", b."+CustomerBillsDB.deliveryDateColumn +", b."+
//                CustomerBillsDB.advanceColumn+", b."+CustomerBillsDB.balanceColumn +", cn.customerName, c."+
//                CustomerDB.phoneNumberColumn+ ", b.customerNameId";
        String query = "";
        if(billTypeComboBox.getValue().equals("All")){
            query = "SELECT b."+CustomerBillsDB.idColumn+", b."+CustomerBillsDB.dateColumn+", b."+CustomerBillsDB.deliveryDateColumn
                    + ", b."+CustomerBillsDB.advanceColumn+", b."+ CustomerBillsDB.balanceColumn+",  b.customerNameId, b.billTotal AS totalPrice, cn.customerName, c."+CustomerDB.phoneNumberColumn+ " FROM "+CustomerBillsDB.customerBillTableName+
                    " b LEFT JOIN CustomerName cn ON cn.customerNameId = b.customerNameId LEFT JOIN Customer c on c.customerId = cn.customerId WHERE CAST("
                    +CustomerBillsDB.idColumn +  " AS VARCHAR(10)) LIKE '%"+ searchField.getText()+"%' GROUP BY b."+
                    CustomerBillsDB.idColumn+ ", b."+ CustomerBillsDB.dateColumn +", b."+CustomerBillsDB.deliveryDateColumn +", b."+
                    CustomerBillsDB.advanceColumn+", b."+CustomerBillsDB.balanceColumn +", cn.customerName, c."+
                    CustomerDB.phoneNumberColumn+ ", b.customerNameId";
        }
        else if(billTypeComboBox.getValue().equals("Returned")){
            query = "SELECT b."+CustomerBillsDB.idColumn+", b."+CustomerBillsDB.dateColumn+", b."+CustomerBillsDB.deliveryDateColumn
                    + ", b."+CustomerBillsDB.advanceColumn+", b."+ CustomerBillsDB.balanceColumn+",  b.customerNameId, b.billTotal AS totalPrice, cn.customerName, c."+CustomerDB.phoneNumberColumn+ " FROM "+CustomerBillsDB.customerBillTableName+
                    " b LEFT JOIN CustomerName cn ON cn.customerNameId = b.customerNameId LEFT JOIN Customer c on c.customerId = cn.customerId WHERE CAST("
                    +CustomerBillsDB.idColumn +  " AS VARCHAR(10)) LIKE '%"+ searchField.getText()+"%' AND p.returnedQuantity > "+ 0 +"  GROUP BY b."+
                    CustomerBillsDB.idColumn+ ", b."+ CustomerBillsDB.dateColumn +", b."+CustomerBillsDB.deliveryDateColumn +", b."+
                    CustomerBillsDB.advanceColumn+", b."+CustomerBillsDB.balanceColumn +", cn.customerName, c."+
                    CustomerDB.phoneNumberColumn+ ", b.customerNameId";

        }
        else if(billTypeComboBox.getValue().equals("Balance")){
            query = "SELECT b."+CustomerBillsDB.idColumn+", b."+CustomerBillsDB.dateColumn+", b."+CustomerBillsDB.deliveryDateColumn
                    + ", b."+CustomerBillsDB.advanceColumn+", b."+ CustomerBillsDB.balanceColumn+",  b.customerNameId, b.billTotal AS totalPrice, cn.customerName, c."+CustomerDB.phoneNumberColumn+ " FROM "+CustomerBillsDB.customerBillTableName+
                    " b LEFT JOIN CustomerName cn ON cn.customerNameId = b.customerNameId LEFT JOIN Customer c on c.customerId = cn.customerId WHERE  CAST(" +
                    CustomerBillsDB.idColumn + " AS VARCHAR(10)) LIKE '%"+ searchField.getText()+"%' AND b."+
                    CustomerBillsDB.balanceColumn +" > "+ 0 +" GROUP BY b."+
                    CustomerBillsDB.idColumn+ ", b."+ CustomerBillsDB.dateColumn +", b."+CustomerBillsDB.deliveryDateColumn +", b."+
                    CustomerBillsDB.advanceColumn+", b."+CustomerBillsDB.balanceColumn +", cn.customerName, c."+
                    CustomerDB.phoneNumberColumn+ ", b.customerNameId";

        }

        ObservableList billsList = showBills(query);
        table.setItems(billsList);
    }


    @FXML
    void onAddPreviousBillClicked(ActionEvent event) throws IOException {
        try {
            SceneController.switchScene("BillModule/CustomerBill.fxml",event,"Customer Previous Bill");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    void onCustomersClicked(ActionEvent event) {
        try {
            SceneController.switchScene("CustomerModule/Customer.fxml",event,"Customers");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void onInventoryClicked(ActionEvent event) {
        try {
            SceneController.switchScene("InventoryModule/Inventory.fxml",event,"Inventory");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void onVendorsClicked(ActionEvent event) {
        try {
            SceneController.switchScene("VendorModule/Vendor.fxml",event,"Vendors");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void onEmployeesClicked(ActionEvent event) {
        try {
            SceneController.switchScene("EmployeeModule/Employee.fxml",event,"Employees");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void onReportsClicked(ActionEvent event) {
        try {
            SceneController.switchScene("ReportModule/Report.fxml",event,"Reports");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void onSignOutClicked(ActionEvent event) {
        try {
            SceneController.switchScene("LoginModule/Login.fxml",event,"Login");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void onLocalBackupClicked(ActionEvent event) {

        File backupFile = new File(Function.localBackUpPath);
        File databaseFile = new File("config/POS.accdb");

        try {
            Files.copy(databaseFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Function.alert(Alert.AlertType.INFORMATION,"Backup","Backup Created Successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onProfileClicked(ActionEvent event) {
        try {
            SceneController.switchScene("EmployeeModule/EmployeeProfile.fxml",event,"Employee Profile");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}

