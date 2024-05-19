package com.example.pos.ReportModule;

import com.example.pos.BillModule.Bill;
import com.example.pos.BillModule.BillProductsDB;
import com.example.pos.BillModule.CustomerBillsDB;
import com.example.pos.CustomerModule.CustomerDB;
import com.example.pos.UtilityClasses.Function;
import com.example.pos.SceneController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ReportController implements Initializable{

    public static Integer billId;
    double totalSales ;
    double totalCostOfGoodsSold ;
    int deliveriesCount;


    @FXML
    private Pane ordersPane;
    @FXML
    private Pane deliveryReportPane;

    @FXML
    private TableView<Bill> table;
    @FXML
    private TableView<Bill> table1;

    @FXML
    private TableColumn<Bill, Integer> billIdTVColumn1;
    @FXML
    private TableColumn<Bill, String> billDateTVColumn1;
    @FXML
    private TableColumn<Bill, String> deliveryDateTVColumn1;
    @FXML
    private TableColumn<Bill, Double> totalBillTVColumn1;
    @FXML
    private TableColumn<Bill, Double> advanceTVColumn1;
    @FXML
    private TableColumn<Bill, Double> balanceTVColumn1;

    @FXML
    private TableColumn<Bill, Integer> billIdTVColumn;
    @FXML
    private TableColumn<Bill, String> nameTVColumn;
    @FXML
    private TableColumn<Bill, Integer> phoneNumberTVColumn;
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
    private ComboBox<String> reportsChoiceComboBox;
    @FXML
    private ComboBox<String> deliveryFilterComboBox;
    @FXML
    private ComboBox<String> timeFilterComboBox;

    @FXML
    private DatePicker fromDateDatePicker;
    @FXML
    private DatePicker toDateDatePicker;

    @FXML
    private TextField totalSalesField;
    @FXML
    private TextField costOfGoodsSoldField;
    @FXML
    private TextField grossProfitField;
    @FXML
    private TextField totalOrdersField;

    ObservableList<String> reportsOptionList = FXCollections.observableArrayList( "Deliveries", "Sales");
    ObservableList<String> timeFilterList = FXCollections.observableArrayList("All", "Today", "Yesterday", "This Week", "This Month", "Last Month");
    ObservableList<String> deliveryFilterList = FXCollections.observableArrayList("All", "Today", "Tomorrow", "This Week", "This Month");


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        reportButton.setStyle("-fx-background-color: #2c2483;");

        reportsChoiceComboBox.setItems(reportsOptionList);
        reportsChoiceComboBox.getSelectionModel().select(0);

        timeFilterComboBox.setItems(timeFilterList);
        timeFilterComboBox.getSelectionModel().select(0);

        deliveryFilterComboBox.setItems(deliveryFilterList);
        deliveryFilterComboBox.getSelectionModel().select(0);

        billIdTVColumn.setCellValueFactory(new PropertyValueFactory<>("billId"));
        nameTVColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        phoneNumberTVColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        billDateTVColumn.setCellValueFactory(new PropertyValueFactory<>("billDate"));
        deliveryDateTVColumn.setCellValueFactory(new PropertyValueFactory<>("deliveryDate"));
        totalBillTVColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        advanceTVColumn.setCellValueFactory(new PropertyValueFactory<>("advance"));
        balanceTVColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));
        viewButtonTVColumn.setCellValueFactory(new PropertyValueFactory<>("viewButton"));

        String query = "SELECT b." + CustomerBillsDB.idColumn + ", b." + CustomerBillsDB.dateColumn + ", b." + CustomerBillsDB.deliveryDateColumn
                + ", b." + CustomerBillsDB.advanceColumn + ", b." + CustomerBillsDB.balanceColumn + ", b.customerNameId, b.billTotal AS totalPrice, cn.customerName, c." + CustomerDB.phoneNumberColumn + " FROM " + CustomerBillsDB.customerBillTableName +
                " b LEFT JOIN CustomerName cn ON cn.customerNameId = b.customerNameId LEFT JOIN Customer c ON c.customerId = cn.customerId WHERE CDate(b." +
                CustomerBillsDB.deliveryDateColumn + ") >= Date() GROUP BY b." +
                CustomerBillsDB.idColumn + ", b." + CustomerBillsDB.dateColumn + ", b." + CustomerBillsDB.deliveryDateColumn + ", b." +
                CustomerBillsDB.advanceColumn + ", b." + CustomerBillsDB.balanceColumn + ", cn.customerName, c." +
                CustomerDB.phoneNumberColumn + ", b.customerNameId";

        ObservableList billsList = showBills(query);
        table1.setItems(billsList);

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
                        Function.fxmlFileName="ReportModule/Report.fxml";
                        Function.title="Reports";
                        try {
                            SceneController.switchScene("BillModule/ViewCustomerBill.fxml",event,"View Customer Bill");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
        });

    }

    public ObservableList showBills(String query) {

        ObservableList<Bill> billsList = FXCollections.observableArrayList();
        deliveriesCount = 0;
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
                if (customerName==null) {
                    customerName = "";
                }
                billsList.add(new Bill(rs.getInt("cBillId"), customerName, phoneNumber
                        ,rs.getString("billDate"), rs.getString("deliveryDate"), rs.getDouble("totalPrice"),
                        rs.getDouble("billAdvance"), rs.getDouble("billBalance"),new Button("👁")));
                deliveriesCount++;
            }

            connection.close();
            statement.close();

            totalOrdersField.setText(String.valueOf(deliveriesCount));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            Function.alert(Alert.AlertType.ERROR,"Error",e.getMessage());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return billsList;
    }

    public ObservableList showOnlyBills(String query) {

        ObservableList<Bill> billsList = FXCollections.observableArrayList();
        try {
            Connection connection = DriverManager.getConnection(Function.databasePath);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            totalSales = 0;
            totalCostOfGoodsSold = 0;
            while (rs.next()) {
                totalSales += rs.getDouble("totalPrice");
                totalCostOfGoodsSold += rs.getDouble("costOfGoodsSold");
                billsList.add(new Bill(rs.getInt("cBillId")
                        ,rs.getString("billDate"), rs.getString("deliveryDate"), rs.getDouble("totalPrice"),
                        rs.getDouble("billAdvance"), rs.getDouble("billBalance")));
            }
            connection.close();
            statement.close();
            totalSalesField.setText(String.valueOf(totalSales));
            costOfGoodsSoldField.setText(String.valueOf(totalCostOfGoodsSold));
            grossProfitField.setText(String.valueOf(totalSales - totalCostOfGoodsSold));

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            Function.alert(Alert.AlertType.ERROR,"Error",e.getMessage());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return billsList;
    }


    @FXML
    void deliveryFilterAction(ActionEvent event) {
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.of(9999, 1, 1);
        if (deliveryFilterComboBox.getValue().equals("Today")){
            toDate = fromDate;
        } else if (deliveryFilterComboBox.getValue().equals("Tomorrow")) {
            toDate =  fromDate.plusDays(1);
            fromDate = toDate;
        }
        else if (deliveryFilterComboBox.getValue().equals("This Week")) {
            toDate = fromDate.with(DayOfWeek.SUNDAY);
        }
        else if (deliveryFilterComboBox.getValue().equals("This Month")) {
            toDate = fromDate.withDayOfMonth(LocalDate.now().lengthOfMonth());
        }
        String query = "SELECT b." + CustomerBillsDB.idColumn + ", b." + CustomerBillsDB.dateColumn + ", b." + CustomerBillsDB.deliveryDateColumn
                + ", b." + CustomerBillsDB.advanceColumn + ", b." + CustomerBillsDB.balanceColumn + ", b.customerNameId, b.billTotal AS totalPrice, cn.customerName, c." + CustomerDB.phoneNumberColumn + " FROM " + CustomerBillsDB.customerBillTableName +
                " b LEFT JOIN CustomerName cn ON cn.customerNameId = b.customerNameId LEFT JOIN Customer c ON c.customerId = cn.customerId WHERE CDate(b." +
                CustomerBillsDB.deliveryDateColumn + ") >= CDate('" + fromDate.toString() + "') AND CDate(b." + CustomerBillsDB.deliveryDateColumn + ") <= CDate('" + toDate.toString() + "') GROUP BY b." +
                CustomerBillsDB.idColumn + ", b." + CustomerBillsDB.dateColumn + ", b." + CustomerBillsDB.deliveryDateColumn + ", b." +
                CustomerBillsDB.advanceColumn + ", b." + CustomerBillsDB.balanceColumn + ", cn.customerName, c." +
                CustomerDB.phoneNumberColumn + ", b.customerNameId";

        ObservableList customerList = showBills(query);
        table1.setItems(customerList);
    }

    @FXML
    void deliverySaleChoiceAction(ActionEvent event) {
        if (reportsChoiceComboBox.getValue().equals("Deliveries")) {

            deliveryReportPane.setVisible(true);
            ordersPane.setVisible(false);

        }
        else if(reportsChoiceComboBox.getValue().equals("Sales")){

            deliveryReportPane.setVisible(false);
            ordersPane.setVisible(true);

            toDateDatePicker.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    LocalDate today = LocalDate.now();
                    setDisable(empty || date.compareTo(today) > 0);
                }
            });

            billIdTVColumn1.setCellValueFactory(new PropertyValueFactory<>("billId"));
            billDateTVColumn1.setCellValueFactory(new PropertyValueFactory<>("billDate"));
            deliveryDateTVColumn1.setCellValueFactory(new PropertyValueFactory<>("deliveryDate"));
            totalBillTVColumn1.setCellValueFactory(new PropertyValueFactory<>("total"));
            advanceTVColumn1.setCellValueFactory(new PropertyValueFactory<>("advance"));
            balanceTVColumn1.setCellValueFactory(new PropertyValueFactory<>("balance"));


            String query = "SELECT " +
                    "b." + CustomerBillsDB.idColumn + ", " +
                    "b." + CustomerBillsDB.dateColumn + ", " +
                    "b." + CustomerBillsDB.deliveryDateColumn + ", " +
                    "b." + CustomerBillsDB.advanceColumn + ", " +
                    "b." + CustomerBillsDB.balanceColumn + ", " +
                    "b.billTotal AS totalPrice, " +
                    "NZ(sum(p."+BillProductsDB.purchasePriceColumn+" * p.quantity), 0) + NZ(sum(obp.purchasePrice * obp.productQuantity), 0) AS costOfGoodsSold " +
                    "FROM " +
                    CustomerBillsDB.customerBillTableName + " b " +
                    "LEFT JOIN " +
                    BillProductsDB.billProductsTableName + " p ON b." + CustomerBillsDB.idColumn + " = p." + BillProductsDB.billIdColumn +
                    " LEFT JOIN " +
                    "OrderBasedProducts obp ON b." + CustomerBillsDB.idColumn + " = obp.cBillId" +
                    " GROUP BY " +
                    "b." + CustomerBillsDB.idColumn + ", " +
                    "b." + CustomerBillsDB.dateColumn + ", " +
                    "b." + CustomerBillsDB.deliveryDateColumn + ", " +
                    "b." + CustomerBillsDB.advanceColumn + ", " +
                    "b." + CustomerBillsDB.balanceColumn;

            ObservableList billsList = showOnlyBills(query);
            table.setItems(billsList);

        }
    }

    @FXML
    void timeFilterAction(ActionEvent event) {

        LocalDate toDate = LocalDate.now();
        LocalDate fromDate = LocalDate.of(2000, 1, 1);
        if (timeFilterComboBox.getValue().equals("Today")){
            fromDate =  LocalDate.now();
        } else if (timeFilterComboBox.getValue().equals("Yesterday")) {
            fromDate =  toDate.minusDays(1);
            toDate = fromDate;
        }
        else if (timeFilterComboBox.getValue().equals("This Week")) {
            fromDate = toDate.with(DayOfWeek.MONDAY);
        }
        else if (timeFilterComboBox.getValue().equals("This Month")) {
            fromDate = toDate.withDayOfMonth(1);
        }
        else if (timeFilterComboBox.getValue().equals("Last Month")) {
            fromDate = toDate.minusMonths(1).withDayOfMonth(1);
            toDate = fromDate.withDayOfMonth(fromDate.lengthOfMonth());
        }
//        String query = "SELECT b." + CustomerBillsDB.idColumn + ", b." + CustomerBillsDB.dateColumn + ", b." + CustomerBillsDB.deliveryDateColumn
//                + ", b." + CustomerBillsDB.advanceColumn + ", b." + CustomerBillsDB.balanceColumn + ", b.billTotal AS totalPrice, sum(p."+BillProductsDB.purchasePriceColumn+") AS costOfGoodsSold FROM " + CustomerBillsDB.customerBillTableName +
//                " b JOIN " + BillProductsDB.billProductsTableName + " p ON b." + CustomerBillsDB.idColumn + " = p." + BillProductsDB.billIdColumn +
//                " WHERE CDate(b." + CustomerBillsDB.dateColumn + ") >= CDate('" + fromDate.toString() + "') AND CDate(b." + CustomerBillsDB.dateColumn + ")" +
//                " <= CDate('" + toDate.toString() + "') GROUP BY b." + CustomerBillsDB.idColumn + ", b." + CustomerBillsDB.dateColumn + ", b." +
//                CustomerBillsDB.deliveryDateColumn + ", b." + CustomerBillsDB.advanceColumn + ", b." + CustomerBillsDB.balanceColumn;
        String query = "SELECT " +
                "b." + CustomerBillsDB.idColumn + ", " +
                "b." + CustomerBillsDB.dateColumn + ", " +
                "b." + CustomerBillsDB.deliveryDateColumn + ", " +
                "b." + CustomerBillsDB.advanceColumn + ", " +
                "b." + CustomerBillsDB.balanceColumn + ", " +
                "b.billTotal AS totalPrice, " +
                "NZ(sum(p."+BillProductsDB.purchasePriceColumn+" * p.quantity), 0) + NZ(sum(obp.purchasePrice * obp.productQuantity), 0) AS costOfGoodsSold " +
                "FROM " +
                CustomerBillsDB.customerBillTableName + " b " +
                "LEFT JOIN " +
                BillProductsDB.billProductsTableName + " p ON b." + CustomerBillsDB.idColumn + " = p." + BillProductsDB.billIdColumn +
                " LEFT JOIN " +
                "OrderBasedProducts obp ON b." + CustomerBillsDB.idColumn + " = obp.cBillId" +
                " WHERE " +
                "CDate(b." + CustomerBillsDB.dateColumn + ") >= CDate('" + fromDate.toString() + "') AND " +
                "CDate(b." + CustomerBillsDB.dateColumn + ") <= CDate('" + toDate.toString() + "') OR " +
                "CDate(IIf(IsNull(b.finalAmountDate), '01/01/1900', b.finalAmountDate)) >= CDate('" + fromDate.toString() + "') AND " +
                "CDate(IIf(IsNull(b.finalAmountDate), '01/01/1900', b.finalAmountDate)) <= CDate('" + toDate.toString() + "') " +
                "GROUP BY " +
                "b." + CustomerBillsDB.idColumn + ", " +
                "b." + CustomerBillsDB.dateColumn + ", " +
                "b." + CustomerBillsDB.deliveryDateColumn + ", " +
                "b." + CustomerBillsDB.advanceColumn + ", " +
                "b." + CustomerBillsDB.balanceColumn;



        ObservableList customerList = showOnlyBills(query);
        table.setItems(customerList);

    }

    @FXML
    void toDateAction(ActionEvent event) {
        datesAction();
    }

    @FXML
    void fromDateAction(ActionEvent event) {
       datesAction();
    }

    void datesAction(){

        LocalDate toDate = toDateDatePicker.getValue();
        LocalDate fromDate = fromDateDatePicker.getValue();
        if(toDate.isBefore(fromDate)){
            Function.alert(Alert.AlertType.ERROR, "Error", "From Date is before To date.");
        }
        else {
            String query = "SELECT " +
                    "b." + CustomerBillsDB.idColumn + ", " +
                    "b." + CustomerBillsDB.dateColumn + ", " +
                    "b." + CustomerBillsDB.deliveryDateColumn + ", " +
                    "b." + CustomerBillsDB.advanceColumn + ", " +
                    "b." + CustomerBillsDB.balanceColumn + ", " +
                    "b.billTotal AS totalPrice, " +
                    "NZ(sum(p."+BillProductsDB.purchasePriceColumn+" * p.quantity), 0) + NZ(sum(obp.purchasePrice * obp.productQuantity), 0) AS costOfGoodsSold " +
                    "FROM " +
                    CustomerBillsDB.customerBillTableName + " b " +
                    "LEFT JOIN " +
                    BillProductsDB.billProductsTableName + " p ON b." + CustomerBillsDB.idColumn + " = p." + BillProductsDB.billIdColumn +
                    " LEFT JOIN " +
                    "OrderBasedProducts obp ON b." + CustomerBillsDB.idColumn + " = obp.cBillId" +
                    " WHERE " +
                    "CDate(b." + CustomerBillsDB.dateColumn + ") >= CDate('" + fromDate.toString() + "') AND " +
                    "CDate(b." + CustomerBillsDB.dateColumn + ") <= CDate('" + toDate.toString() + "') OR " +
                    "CDate(IIf(IsNull(b.finalAmountDate), '01/01/1900', b.finalAmountDate)) >= CDate('" + fromDate.toString() + "') AND " +
                    "CDate(IIf(IsNull(b.finalAmountDate), '01/01/1900', b.finalAmountDate)) <= CDate('" + toDate.toString() + "') " +
                    "GROUP BY " +
                    "b." + CustomerBillsDB.idColumn + ", " +
                    "b." + CustomerBillsDB.dateColumn + ", " +
                    "b." + CustomerBillsDB.deliveryDateColumn + ", " +
                    "b." + CustomerBillsDB.advanceColumn + ", " +
                    "b." + CustomerBillsDB.balanceColumn;

            ObservableList customerList = showOnlyBills(query);
        table.setItems(customerList);
    }
    }

    @FXML
    void onBillsClicked(ActionEvent event) {
        try {
            SceneController.switchScene("BillModule/Bills.fxml",event,"Bills");
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
    void onSignOutClicked(ActionEvent event) {
        try {
            SceneController.switchScene("LoginModule/Login.fxml",event,"Login");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
