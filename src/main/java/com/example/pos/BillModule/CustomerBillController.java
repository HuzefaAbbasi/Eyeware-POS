package com.example.pos.BillModule;

import com.example.pos.CustomerModule.CustomerDB;
import com.example.pos.CustomerModule.LensDB;
import com.example.pos.EmployeeModule.EmployeeDB;
import com.example.pos.InventoryModule.ProductDB;
import com.example.pos.InventoryModule.SearchProductController;
import com.example.pos.LoginModule.LoginController;
import com.example.pos.UtilityClasses.Function;
import com.example.pos.SceneController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.transform.Scale;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CustomerBillController implements Initializable {

    ResultSet rs;
    ResultSet crs;
    double total = 0;
    double prevCredit = 0;
    boolean newCustomer = true;
    boolean customerBox1 = false;

    public static Map<String, Integer> stockMap = new HashMap<>();
    boolean search = false;
    boolean customerPhoneVerify = true;
    boolean customerPhoneKeyDownCheck = false;
    DecimalFormat decimalFormat = new DecimalFormat("#.##");
    int invoiceId = 1;
    boolean hasOBProduct = false;
    @FXML
    private TextField invoiceNoField;
    @FXML
    private DatePicker deliveryDate;
    @FXML
    private DatePicker billDate;

    @FXML
    private Label customerStatusLabel;
    @FXML
    private ComboBox<String> customerNamesComboBox;
    @FXML
    private TextField customerNameField;
    @FXML
    private TextField customerPhoneField;
    @FXML
    private TextArea customerAddress;

    @FXML
    private TextField sphLeft1;
    @FXML
    private TextField sphRight1;
    @FXML
    private TextField addLeft1;
    @FXML
    private TextField addRight1;
    @FXML
    private TextField axisLeft1;
    @FXML
    private TextField axisRight1;
    @FXML
    private TextField cylLeft1;
    @FXML
    private TextField cylRight1;
    @FXML
    private TextArea ipd1;

    @FXML
    private TextField productCodeField;
    @FXML
    private TextField productNameField;
    @FXML
    private TextField quantityField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField discountPercentageField;
    @FXML
    private TextField discountField;

    @FXML
    private TableView<CustomerBillTable> table;
    @FXML
    private TableColumn<CustomerBillTable, String> pCode;
    @FXML
    private TableColumn<CustomerBillTable, String> pName;
    @FXML
    private TableColumn<CustomerBillTable, Double> pPrice;
    @FXML
    private TableColumn<CustomerBillTable, Integer> pQuantity;
    @FXML
    private TableColumn<CustomerBillTable, Double> pTotal;
    @FXML
    private TableColumn<CustomerBillTable, Double> discountPercentageTVColumn;
    @FXML
    private TableColumn<CustomerBillTable, Double> discountTVColumn;
    @FXML
    private TableColumn<CustomerBillTable, Button> editButtonTVColumn;
    @FXML
    private TableColumn<CustomerBillTable, Button> removeButtonTVColumn;

    @FXML
    private TextField totalBillField;
    @FXML
    private TextField advanceAmountField;
    @FXML
    private TextField balanceAmountField;

    @FXML
    private TextArea billNote;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        pCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        pName.setCellValueFactory(new PropertyValueFactory<>("name"));
        pQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        pPrice.setCellValueFactory(new PropertyValueFactory<>("retailPrice"));
        discountPercentageTVColumn.setCellValueFactory(new PropertyValueFactory<>("discountPercentage"));
        discountTVColumn.setCellValueFactory(new PropertyValueFactory<>("discount"));
        pTotal.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        editButtonTVColumn.setCellValueFactory(new PropertyValueFactory<>("editButton"));
        removeButtonTVColumn.setCellValueFactory(new PropertyValueFactory<>("removeButton"));

        removeButtonTVColumn.setCellFactory(param -> new TableCell<CustomerBillTable, Button>() {
            @Override
            protected void updateItem(Button button, boolean empty) {
                super.updateItem(button, empty);

                if (empty || button == null) {
                    setGraphic(null);
                } else {
                    setGraphic(button);
                    button.setOnAction(event -> {
                        CustomerBillTable product = getTableView().getItems().get(getIndex());
                        total = total - product.getTotalPrice();
                        total = Double.parseDouble(decimalFormat.format(total));
                        totalBillField.setText(String.valueOf(total));
                        table.getItems().remove(product);

                        // adding stock again
                        Integer stock = stockMap.get(product.getCode());
                        stock += product.getQuantity();
                        stockMap.put(product.getCode(), stock);

                        if (table.getItems().isEmpty()) {
                            advanceAmountField.clear();
                            advanceAmountField.setEditable(false);
                            balanceAmountField.clear();
                        }
                    });
                }
            }
        });

        editButtonTVColumn.setCellFactory(param -> new TableCell<CustomerBillTable, Button>() {
            @Override
            protected void updateItem(Button button, boolean empty) {
                super.updateItem(button, empty);

                if (empty || button == null) {
                    setGraphic(null);
                } else {
                    setGraphic(button);
                    button.setOnAction(event -> {

                        //Logic
                        // initiating static variable of UpdateBillProduct
                        int tableIndex = getTableRow().getIndex();


                        CustomerBillTable product = getTableView().getItems().get(getIndex());
                        Double tPrice = product.getTotalPrice();
                        UpdateBillProductController.billTableProduct = product;

                        if (product.isOBProduct()){
                            Function.isOBProdAdd = false;
                            Stage dialog = new Stage();
                            Scene scene;
                            Parent root;
                            try {
                                root = FXMLLoader.load(SceneController.class.getResource("BillModule/OrderBaseProduct.fxml"));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            dialog.initModality(Modality.APPLICATION_MODAL);
                            scene = new Scene(root);
                            dialog.setScene(scene);
                            dialog.initOwner(SceneController.stage);
                            dialog.initStyle(StageStyle.UNDECORATED);
                            dialog.resizableProperty().set(false);
                            dialog.setTitle("Order Base Product");
                            dialog.showAndWait();

//                            if(!tPrice.equals(UpdateBillProductController.billTableProduct.getTotalPrice()))
//                            {
//                                double totalOFProd = tPrice;
//                                total-=totalOFProd;
//                                totalOFProd = UpdateBillProductController.billTableProduct.getTotalPrice();
//                                total+=totalOFProd;
//                                total = Double.parseDouble(decimalFormat.format(total));
//                                totalBillField.setText(String.valueOf(total));
//
//                            }
                        }
                        else {
                            Stage dialog = new Stage();
                            Scene scene;
                            Parent root;
                            try {
                                root = FXMLLoader.load(SceneController.class.getResource("BillModule/UpdateBillProduct.fxml"));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            dialog.initModality(Modality.APPLICATION_MODAL);
                            scene = new Scene(root);
                            dialog.setScene(scene);
                            dialog.initOwner(SceneController.stage);
                            dialog.initStyle(StageStyle.UNDECORATED);
                            dialog.resizableProperty().set(false);
                            dialog.setTitle("Update Bill Product");
                            dialog.showAndWait();

                        }

                        // To make sure cancel not clicked
                        if (UpdateBillProductController.billTableProduct != null){

                            CustomerBillTable billItem = null;
                            CustomerBillTable newBillItem = UpdateBillProductController.billTableProduct;

                            int tableIndexToUpdate = -1;
                            int lastIndexToUpdate = -1;


                            // gets first occurrence
                            for (int i = 0; i < table.getItems().size(); i++) {
                                if (table.getItems().get(i).getName().equals(newBillItem.getName()) &&
                                        table.getItems().get(i).getPurchasePrice().equals(newBillItem.getPurchasePrice()) &&
                                        table.getItems().get(i).getRetailPrice().equals(newBillItem.getRetailPrice())
                                        && table.getItems().get(i).getDiscountPercentage().equals(newBillItem.getDiscountPercentage())){
//                                    billItem = table.getItems().get(i);
                                    tableIndexToUpdate = i;
                                    break;
                                }
                            }
                            // gets second(last) occurrence
                            for (int i = 0; i < table.getItems().size(); i++) {
                                if (table.getItems().get(i).getName().equals(newBillItem.getName()) &&
                                        table.getItems().get(i).getPurchasePrice().equals(newBillItem.getPurchasePrice()) &&
                                        table.getItems().get(i).getRetailPrice().equals(newBillItem.getRetailPrice())
                                        && table.getItems().get(i).getDiscountPercentage().equals(newBillItem.getDiscountPercentage())){
//                                    billItem = table.getItems().get(i);
                                    lastIndexToUpdate = i;
                                }
                            }
                            if (tableIndex == tableIndexToUpdate){
                                billItem = table.getItems().get(lastIndexToUpdate);
                            }
                            else{
                                billItem = table.getItems().get(tableIndexToUpdate);
                            }

                            if (billItem != null && tableIndexToUpdate >= 0 && lastIndexToUpdate >= 0){

                                if (tableIndexToUpdate != lastIndexToUpdate){
                                    // Update the relevant value in the object
                                    billItem.setQuantity(billItem.getQuantity() + newBillItem.getQuantity());
                                    billItem.setTotalPrice(billItem.getTotalPrice() + newBillItem.getTotalPrice());

                                    // Update the TableView to reflect the changes
                                    table.getItems().set(tableIndexToUpdate, billItem);
                                    table.getItems().remove(lastIndexToUpdate);
                                }
                                else {
                                    //set tableview, and list on specific index
                                    table.getItems().set(tableIndex, UpdateBillProductController.billTableProduct);
                                }
                            }
                            else{
                                //set tableview, and list on specific index
                                table.getItems().set(tableIndex, UpdateBillProductController.billTableProduct);
                            }

                            if(!tPrice.equals(UpdateBillProductController.billTableProduct.getTotalPrice())){

                                double totalOFProd = tPrice;
                                total-=totalOFProd;
                                totalOFProd = UpdateBillProductController.billTableProduct.getTotalPrice();
                                total+=totalOFProd;
                                total = Double.parseDouble(decimalFormat.format(total));
                                totalBillField.setText(String.valueOf(total));
                            }


                            table.refresh();
                        }



                    });
                }
            }
        });



        advanceAmountField.setText("0");
        discountField.setText("0.00");
//        billDate.setValue(LocalDate.now());
//        billDate.setDisable(true);
//        try{
//
//        Connection connection = DriverManager.getConnection(Function.databasePath);
//        Statement statement1 = connection.createStatement();
//        ResultSet set = statement1.executeQuery("SELECT MAX("+CustomerBillsDB.idColumn+") FROM "+CustomerBillsDB.customerBillTableName);
//        if(!set.next()){
//            invoiceNoField.setText(String.valueOf(invoiceId));
//        }
//        else {
//            invoiceId  = set.getInt(1)+1;
//            invoiceNoField.setText(String.valueOf(invoiceId));
//        }
//    }
//        catch (Exception e){
//            System.out.println(e.getMessage());
//        }

        billDate.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) > 0);
            }
        });

//        deliveryDate.setDayCellFactory(picker -> new DateCell() {
//            @Override
//            public void updateItem(LocalDate date, boolean empty) {
//                super.updateItem(date, empty);
//                LocalDate today = LocalDate.now();
//                setDisable(empty || date.compareTo(today) < 0);
//            }
//        });
        customerStatusLabel.setVisible(false);
    }

    @FXML
    void onInvoiceKeyDown(ActionEvent event)  {
        if (!invoiceNoField.getText().trim().isEmpty()) {
            boolean flag;
            flag = Function.checkForInt(invoiceNoField.getText().trim());
            if (flag) {
                try{
                    Connection connection = DriverManager.getConnection(Function.databasePath);
                    Statement statement = connection.createStatement();
                    ResultSet set = statement.executeQuery("Select * from  CustomerBills Where cBillId =  " + Integer.parseInt(invoiceNoField.getText()));
                    if (set.next()) {
                        Function.alert(Alert.AlertType.ERROR, "Error", "Invoice Number Already exists.");
                        invoiceNoField.clear();
                    }
                    else {
                        invoiceId = Integer.parseInt(invoiceNoField.getText().trim());
                        billDate.requestFocus();
                    }
                }
                catch (SQLException e){
                    billDate.requestFocus();
                    Function.alert(Alert.AlertType.ERROR,"Error",e.getMessage());
                }
            } else {
                Function.alert(Alert.AlertType.ERROR, "Error", "Invoice Number not in correct format.");
                invoiceNoField.clear();
                invoiceNoField.requestFocus();
            }
        } else {
            Function.alert(Alert.AlertType.ERROR, "Error", "Invoice No field Empty");
            invoiceNoField.requestFocus();
        }
    }

    @FXML
    void customerDropDownAction(ActionEvent event) {
        String selectedValue = customerNamesComboBox.getValue();
        if (selectedValue.equals("New Name")){
            clearForNewCustomer();
            customerNameField.requestFocus();
            customerNameField.setEditable(true);
        } else if (selectedValue.equals("New Customer")) {
            customerNameField.setEditable(true);
        } else {
            customerNameField.setEditable(false);
            int selectedIndex = customerNamesComboBox.getSelectionModel().getSelectedIndex();
            try{
                if (crs.absolute(selectedIndex+1)){
                    customerNameField.setText(crs.getString("customerName"));
                    // not sure about this one.... so print
                    if (crs.getInt("lensId") != 0){
                        ViewCustomerBillController.initializeLensBoxes(crs, sphRight1, cylRight1, axisRight1, addRight1,
                                sphLeft1, cylLeft1, axisLeft1, addLeft1, ipd1);
                    }
                    else {
                        clearBox();
                    }
                }
            }catch (Exception e){
                System.out.println("some error"+ e.getMessage());
            }
        }

    }

    @FXML
    void onCustomerPhKeyDown(ActionEvent event){


        boolean flag = true;
        customerPhoneKeyDownCheck = true;
        int counter = 0;
        try {
            //Checks if the number is in right format.
            String phone = customerPhoneField.getText().trim();
            for (int i = 0; i < phone.length(); i++) {
                counter++;
                if (phone.charAt(i) >= '0' && phone.charAt(i) <= '9') ;
                else {
                    flag = false;
                }
            }
            if (flag && counter == 11) {
                Connection connection = DriverManager.getConnection(Function.databasePath);
                Statement statement =  connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                crs = statement.executeQuery("SELECT * FROM " + CustomerDB.customerTableName + " c " +
                        "JOIN CustomerName cn ON c.customerId = cn.customerId " +
                        "Left JOIN Lens l ON l.lensId = cn.lensId " +
                        "WHERE c." + CustomerDB.phoneNumberColumn + " = '" + customerPhoneField.getText().trim() + "'");

                //if customer does exist.
                if (crs.next()) {
                    customerNamesComboBox.getItems().clear();
                    customerStatusLabel.setVisible(true);
                    customerNameField.setText(crs.getString("customerName"));
                    customerAddress.setText(crs.getString(CustomerDB.addressColumn));
                    customerNameField.setEditable(false);
                    do {
                        customerNamesComboBox.getItems().add(crs.getString("customerName"));
                    }while (crs.next());
                    customerNamesComboBox.getItems().add("New Name");
                    customerNamesComboBox.getSelectionModel().select(0);
//                    customerDropDownAction(event);

                    //To get pointer to first value
                    crs.first();


                    connection.close();
                    statement.close();
                    prevCredit = crs.getDouble(CustomerDB.creditColumn);
                    newCustomer = false;
                    productCodeField.requestFocus();
                } else {
//                    customerStatusLabel.setText("New Customer");
                    customerNameField.setEditable(true);
                    customerStatusLabel.setVisible(false);
                    customerNamesComboBox.getItems().clear();
                    customerAddress.clear();
                    clearForNewCustomer();
                    customerNamesComboBox.getItems().add("New Customer");
                    customerNamesComboBox.getSelectionModel().select(0);
                    customerNameField.requestFocus();
                }
            } else if (!flag) {
                Function.alert(Alert.AlertType.ERROR, "Error", "Enter Numbers Only.");
                customerPhoneField.clear();
                customerPhoneField.requestFocus();
            } else if (counter < 11 || counter > 11) {
                Function.alert(Alert.AlertType.ERROR, "Error", "Enter 11 digit number.");
                customerPhoneField.clear();
                customerPhoneField.requestFocus();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }

    @FXML
    void onCustomerNameKeyDown(ActionEvent event) {
        //Checks Customer name format
        boolean flag = Function.checkAlphabets(customerNameField.getText().trim());
        if(customerPhoneField.getText()!=null){
            if(flag){
                sphRight1.requestFocus();
            }
            else{
                Function.alert(Alert.AlertType.ERROR,"Error","Enter only alphabets in Name");
                customerNameField.clear();
            }
        }
        else{
            Function.alert(Alert.AlertType.ERROR,"Error","You Must Enter Customer Number");
        }
    }

    @FXML
    void onLAdd1(ActionEvent event) {
        ipd1.requestFocus();
    }

    @FXML
    void onLAxis1(ActionEvent event) {
        addLeft1.requestFocus();
    }

    @FXML
    void onLCyl1(ActionEvent event) {
        axisLeft1.requestFocus();
    }

    @FXML
    void onLSph1(ActionEvent event) {
        cylLeft1.requestFocus();
    }

    @FXML
    void onRAdd1(ActionEvent event) {
        sphLeft1.requestFocus();
    }

    @FXML
    void onRAxis1(ActionEvent event) {
        addRight1.requestFocus();
    }

    @FXML
    void onRCyl1(ActionEvent event) {
        axisRight1.requestFocus();
    }

    @FXML
    void onRSph1(ActionEvent event) {
        cylRight1.requestFocus();
    }

    @FXML
    void onIpd1(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            productCodeField.requestFocus();
        }
    }

    @FXML
    void productCodeKeyDown(ActionEvent event) throws IOException, SQLException {
            if (!productCodeField.getText().isEmpty()) {
                if (productCodeField.getText().equals("*")) {

                    Stage dialog = new Stage();
                    Scene scene2;
                    Parent root2;
                    Function.fxmlFileName = "BillModule/CustomerBill.fxml";
                    Function.title = "Customer Previous Bill";
                    productCodeField.clear();
                    search = true;

                    root2 = FXMLLoader.load(SceneController.class.getResource("InventoryModule/SearchProduct.fxml"));
                    dialog.initModality(Modality.APPLICATION_MODAL);
                    scene2 = new Scene(root2);
                    dialog.setScene(scene2);
                    dialog.initOwner(SceneController.stage);
                    dialog.initStyle(StageStyle.UNDECORATED);
                    dialog.resizableProperty().set(false);
                    dialog.setTitle("Customer Bill");
                    dialog.showAndWait();

                    String productCode = SearchProductController.productCode;
                    if (productCode != null && search) {
                        this.productCodeField.setText(productCode);
                    }
                } else {
                    try{
                        quantityField.setEditable(true);
                        Connection connection = DriverManager.getConnection(Function.databasePath);
                        String query = "Select * From "+ ProductDB.productTableName+" Where "+ProductDB.productCodeColumn+" = '" + productCodeField.getText().trim() + "'";
                        Statement statement = connection.createStatement();
                        rs = statement.executeQuery(query);
                        statement.close();
                        connection.close();

                        if(!rs.next()){
                            Function.alert(Alert.AlertType.ERROR,"Error","No Product Exists for this Code.");
                            productCodeField.clear();
                        }
                        else{
                            productNameField.setText(rs.getString(ProductDB.productNameColumn));
                            productCodeField.setEditable(false);
                            quantityField.setEditable(true);
                            quantityField.requestFocus();
                        }
                    }catch (SQLException e){
                        Function.alert(Alert.AlertType.ERROR,"Error","No Product Exists for this Code");
                    }
                }
            } else {
                if (!table.getItems().isEmpty()) {
                    advanceAmountField.setEditable(true);
                    advanceAmountField.requestFocus();
                }  else {
                    Function.alert(Alert.AlertType.ERROR, "Error", "NO Product Added in Bill");
                }
            }


    }

    @FXML
    void onQuantityKeyDown(ActionEvent event){

            if(!quantityField.getText().trim().isEmpty()){
                boolean flag;
                flag = Function.checkForInt(quantityField.getText().trim());
                try{
                    if (!flag){
                        Function.alert(Alert.AlertType.ERROR,"Error","Please Enter Only Numbers in Quantity Box");
                        quantityField.clear();
                    }
                    else {
                        String prodCode = productCodeField.getText().trim();
                        Integer stock; // to get from map
                        Integer quantity = Integer.parseInt(quantityField.getText().trim());
                        int remainingStock;
                        if (stockMap.containsKey(prodCode)){
                            stock = stockMap.get(prodCode);
                            remainingStock = stock - quantity;
                        }
                        else {
                            remainingStock =rs.getInt(ProductDB.productStockColumn)-quantity ;
                        }
                        if( remainingStock < 0 ){
                            Function.alert(Alert.AlertType.ERROR,"Error","Not Enough Stock");
                            quantityField.clear();
                            productNameField.clear();
                            productCodeField.clear();
                            quantityField.setEditable(false);
                            productCodeField.setEditable(true);
                            productCodeField.requestFocus();
                        }
                        else if (remainingStock <=10 ) {
                            Function.alert(Alert.AlertType.ERROR,"Error","Stock Low, "+remainingStock+
                                    " items left");
                            //quantityField.setEditable(false);
                            priceField.setEditable(true);
                            priceField.requestFocus();
                        }
                        else {
                           // quantityField.setEditable(false);
                            priceField.setEditable(true);
                            priceField.requestFocus();
                        }
                    }
                }
                catch (Exception e){
                    Function.alert(Alert.AlertType.ERROR,"Error","No data for product code");
                }
            }
            else {
                Function.alert(Alert.AlertType.ERROR,"Error","Quantity Field Empty");
            }

    }

    @FXML
    void onPriceKeyDown(ActionEvent event) {
        try{
                if(!priceField.getText().trim().isEmpty()){
                    boolean flag;
                    flag = Function.checkForDecimal(priceField.getText().trim());
                    if(!flag){
                        Function.alert(Alert.AlertType.ERROR,"Error","Please enter correct price.");
                        priceField.clear();
                        priceField.requestFocus();
                    }
                    else{
                        if(rs.getDouble(ProductDB.productMinimumRetailPriceColumn) > Double.parseDouble(priceField.getText().trim())){
                            Function.alert(Alert.AlertType.ERROR,"Error","Price Not Allowed");
                            priceField.clear();
                            priceField.requestFocus();
                        }
                        else {
                            discountPercentageField.setEditable(true);
                            //priceField.setEditable(false);
                            discountPercentageField.requestFocus();
                        }
                    }
                }
                else{
                    Function.alert(Alert.AlertType.ERROR,"Error","Please Enter Price");
                }

        }
        catch (Exception e){
            Function.alert(Alert.AlertType.ERROR,"Error","No data for product code");
        }
    }

    @FXML
    void onDiscountPercentageField(ActionEvent event) {
        if (discountPercentageField.getText().isEmpty()){
            discountField.setEditable(true);
            discountField.requestFocus();
        }
        else {
            try{
                boolean flag;
                flag = Function.checkForDecimal(discountPercentageField.getText().trim());
                if(!flag){
                    Function.alert(Alert.AlertType.ERROR,"Error","Please Enter correct discount percentage.");
                    discountPercentageField.clear();
                }
                else{

                    double maxDiscount = rs.getDouble("discount");
                    if (maxDiscount >= Double.parseDouble(discountPercentageField.getText().trim())){
                        //Populating discount field.
                        double priceAmount = Double.parseDouble(priceField.getText().trim());
                        double discountAmount = priceAmount * (Double.parseDouble(discountPercentageField.getText().trim())/100);
                        double discountedPrice = priceAmount - discountAmount;
//                    price.setText(String.valueOf(priceAmount - discountAmount));
                        if (discountedPrice >= rs.getDouble(ProductDB.productMinimumRetailPriceColumn)){
                            discountField.setText(String.valueOf(discountAmount));

                            discountField.setEditable(true);
                            // discountPercentageField.setEditable(false);
                            discountField.requestFocus();
                        }
                        else{
                            //price less than minimum
                            Function.alert(Alert.AlertType.ERROR,"Error", "Discount percentage not allowed");
                            discountPercentageField.clear();
                        }
                    }
                    else{
                        Function.alert(Alert.AlertType.ERROR,"Error", "Discount percentage not allowed");
                        discountPercentageField.clear();
                    }
                }
            } catch (SQLException e){
//                System.out.println(e.getMessage());
                Function.alert(Alert.AlertType.ERROR,"Error","No data for product code");
            }
        }

    }

    @FXML
    void onDiscountField(ActionEvent event) {
        try{
            //Checking All fields
            productCodeKeyDown(event);
            onQuantityKeyDown(event);
            onPriceKeyDown(event);
            boolean flag;
            flag = Function.checkForDecimal(discountPercentageField.getText().trim());
            if(!flag){
                Function.alert(Alert.AlertType.ERROR,"Error","Please Enter correct discount amount.");
                discountField.clear();
                return;
            }
//            onDiscountPercentageField(event);
            if (quantityField.getText().isEmpty()){
                quantityField.requestFocus();
            }
            else if (priceField.getText().isEmpty()){
                priceField.requestFocus();
            }

            if (!quantityField.getText().isEmpty() && !priceField.getText().isEmpty() ){
                if(!discountField.getText().isEmpty() || !discountPercentageField.getText().isEmpty()){
                    flag = Function.checkForDecimal(discountField.getText().trim());
                    if(!flag){
                        Function.alert(Alert.AlertType.ERROR,"Error","Please Enter correct discount amount.");
                        discountField.clear();
                    }
                    else{
                        double maxDiscount = rs.getDouble("discount");
                        double priceAmount = Double.parseDouble(priceField.getText().trim());
                        double discountPercentage = 0;
                        double purchasePrice = rs.getDouble("purchasePrice");
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


                        if (maxDiscount >= Double.parseDouble(discountPercentageField.getText().trim())){
                            double discountAmount = priceAmount * (Double.parseDouble(discountPercentageField.getText().trim())/100);
                            double discountedPrice = priceAmount - discountAmount;
//                    price.setText(String.valueOf(priceAmount - discountAmount));
                            if (discountedPrice >= rs.getDouble(ProductDB.productMinimumRetailPriceColumn)){
//                      For Checking if same product already exists with same price.
                                CustomerBillTable billItem = null;
                                int tableIndexToUpdate = -1;

                                for (int i = 0; i < table.getItems().size(); i++) {
                                    if (table.getItems().get(i).getCode().equals(productCodeField.getText()) &&
                                            table.getItems().get(i).getRetailPrice() == priceAmount
                                            && table.getItems().get(i).getDiscount() == Double.parseDouble(discountField.getText()) &&
                                            !table.getItems().get(i).isOBProduct()){
                                        billItem = table.getItems().get(i);
                                        tableIndexToUpdate = i;
                                        break;
                                        }
                                }



                                if (billItem != null && tableIndexToUpdate >= 0) {
                                    // Update the relevant value in the person object
                                    billItem.setQuantity(billItem.getQuantity() + Integer.parseInt(quantityField.getText()));
                                    billItem.setTotalPrice(Double.parseDouble(decimalFormat.format(billItem.getTotalPrice() +
                                            Integer.parseInt(quantityField.getText()) * discountedPrice)) );

                                    // Update the TableView to reflect the changes
                                    table.getItems().set(tableIndexToUpdate, billItem);
                                }
                                else {
                                    // Create a DecimalFormat object with the desired format
//                                    discountPercentage = Double.parseDouble(decimalFormat.format(discountPercentage));
//                                    discountAmount = Double.parseDouble(decimalFormat.format(discountAmount));
                                    table.getItems().add(new CustomerBillTable(productCodeField.getText(), productNameField.getText(),
                                            Integer.parseInt(quantityField.getText()),
                                            priceAmount, purchasePrice, discountPercentage,discountAmount,false,
                                            Integer.parseInt(quantityField.getText())
                                            * discountedPrice, new Button("✎"),new Button("🗑")));

                                }


                                Integer stock ;
                                String prodCode = productCodeField.getText().trim();
                                Integer quantity = Integer.parseInt(quantityField.getText());
                                if (stockMap.containsKey(prodCode)){
                                    stock = stockMap.get(prodCode);
                                }
                                else {
                                    stock = rs.getInt(ProductDB.productStockColumn);
                                }

                                Integer remainingStock = stock - quantity;

                                stockMap.put(prodCode, remainingStock);

                                total += discountedPrice * Integer.parseInt(quantityField.getText());
                                total = Double.parseDouble(decimalFormat.format(total));
                                totalBillField.setText(String.valueOf(total));

                                productCodeField.clear();
                                productNameField.clear();
                                quantityField.clear();
                                priceField.clear();
                                discountPercentageField.clear();
                                discountField.clear();

                                quantityField.setEditable(false);
                                priceField.setEditable(false);
                                discountField.setEditable(false);
                                discountPercentageField.setEditable(false);

                                productCodeField.setEditable(true);
                                advanceAmountField.setEditable(true);
                                discountField.setText("0");
                                productCodeField.requestFocus();
                            }
                            else{
                                //price less than minimum
                                Function.alert(Alert.AlertType.ERROR,"Error", "Discount not allowed");
                                discountField.clear();
                            }
                        }
                        else{
                            Function.alert(Alert.AlertType.ERROR,"Error", "Discount not allowed");
                            discountField.clear();
                        }
                    }
                }
                else{
                    Function.alert(Alert.AlertType.ERROR,"Error","Please Enter Discount");
                }
            }
        } catch (SQLException e){
            Function.alert(Alert.AlertType.ERROR,"Error","No data for product code");
            System.out.println(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void onAdvanceKeyDown(ActionEvent event) {
        if (!advanceAmountField.getText().isEmpty()) {
            boolean checkAdvance;
            checkAdvance = Function.checkForDecimal(advanceAmountField.getText().trim());
            if (checkAdvance) {
                if (Double.parseDouble(totalBillField.getText().trim()) < Double.parseDouble(advanceAmountField.getText().trim())) {
                    Function.alert(Alert.AlertType.ERROR, "Error", "Advance amount more than total amount");
                    advanceAmountField.clear();
                } else {
                    balanceAmountField.setText(String.valueOf(Double.parseDouble(totalBillField.getText().trim()) - Double.parseDouble(advanceAmountField.getText().trim())));
                }
            } else {
                Function.alert(Alert.AlertType.ERROR, "Error", "Advance amount not in correct format");
                advanceAmountField.clear();
            }
        } else {
            Function.alert(Alert.AlertType.ERROR, "Error", "Advance field Empty");
        }

    }

    @FXML
    void onOBProductClicked(ActionEvent event) {
        UpdateBillProductController.billTableProduct = null;
        Function.isOBProdAdd = true;
        Stage dialog = new Stage();
        Scene scene;
        Parent root;
        try {
            root = FXMLLoader.load(SceneController.class.getResource("BillModule/OrderBaseProduct.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        dialog.initModality(Modality.APPLICATION_MODAL);
        scene = new Scene(root);
        dialog.setScene(scene);
        dialog.initOwner(SceneController.stage);
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.resizableProperty().set(false);
        dialog.setTitle("Order Base Product");
        dialog.showAndWait();

        if (OrderBaseProductController.obProduct != null){

            CustomerBillTable billItem = null;
            CustomerBillTable newBillItem = OrderBaseProductController.obProduct;

            int tableIndexToUpdate = -1;

            for (int i = 0; i < table.getItems().size(); i++) {
                if (table.getItems().get(i).getName().equals(newBillItem.getName()) &&
                        table.getItems().get(i).getPurchasePrice().equals(newBillItem.getPurchasePrice()) &&
                        table.getItems().get(i).getRetailPrice().equals(newBillItem.getRetailPrice())
                        && table.getItems().get(i).getDiscount().equals(newBillItem.getDiscount()) &&
                        table.getItems().get(i).isOBProduct()){
                    billItem = table.getItems().get(i);
                    tableIndexToUpdate = i;
                    break;
                }
            }

            if (billItem != null && tableIndexToUpdate >= 0) {
                // Update the relevant value in the object
                billItem.setQuantity(billItem.getQuantity() + newBillItem.getQuantity());
                billItem.setTotalPrice(billItem.getTotalPrice() + newBillItem.getTotalPrice());

                // Update the TableView to reflect the changes
                table.getItems().set(tableIndexToUpdate, billItem);
            }
            else{
                table.getItems().add(OrderBaseProductController.obProduct);
            }
            total += OrderBaseProductController.obProduct.getTotalPrice();
            total = Double.parseDouble(decimalFormat.format(total));
            totalBillField.setText(String.valueOf(total));
            hasOBProduct = true;
            OrderBaseProductController.obProduct = null;
            advanceAmountField.setEditable(true);

        }

    }

    @FXML
    void onSaveClicked(ActionEvent event) {

//        //For checking invoice number format
        boolean invoiceCheck = Function.checkForInt(invoiceNoField.getText().trim());
        //For Checking Customers Phone Number
        boolean phoneflag = Function.checkPhoneNo(customerPhoneField.getText().trim());
        //For Checking Customer Name.
        boolean nameflag = Function.checkAlphabets(customerNameField.getText().trim());
        //For Checking Advance amount format.
        boolean checkAdvance;
        checkAdvance = Function.checkForDecimal(advanceAmountField.getText().trim());
        try {
            Connection connection = DriverManager.getConnection(Function.databasePath);
            //For verifying invoice no field is not empty
            if (!invoiceNoField.getText().trim().isEmpty()) {
                //For verifying invoice no is in right format
                if (invoiceCheck) {
//                    Connection connection = DriverManager.getConnection(Function.databasePath);
                    Statement statement = connection.createStatement();
                    ResultSet set = statement.executeQuery("Select * from  CustomerBills Where cBillId =  " + Integer.parseInt(invoiceNoField.getText()));
                    if (!set.next()) {
                        //For verifying user has selected bill date
                        if (billDate.getValue() != null) {
                            //For Verifying User has selected delivery date
                            if (deliveryDate.getValue() != null) {
                                //For verifying that bill date must be before delivery date
                                boolean checkDeliveryDate = true;
                                if (deliveryDate.getValue().isBefore(billDate.getValue())) {
                                    checkDeliveryDate = false;
                                }

                                if (checkDeliveryDate) {
                                    //To verify phone number
                                    if (phoneflag) {
                                        //To verify customer name.
                                        if (nameflag) {
                                            //For checking list is not empty
                                            if (!table.getItems().isEmpty()) {
                                                if ((customerPhoneField.getText().trim().isEmpty() && customerNameField.getText().trim().isEmpty())
                                                        || (!customerPhoneField.getText().trim().isEmpty() && !customerNameField.getText().trim().isEmpty())) {
                                                    //For checking whole boxes format, will also return true if box is empty.
                                                    if (checkTheBox1()) {
                                                        //For verifying that if user enter anything in boxes, user must enter customer phone number.
                                                        if (((!isBox1Empty() && !customerPhoneField.getText().isEmpty()) && !customerNameField.getText().isEmpty()) || isBox1Empty()) {
                                                            //To verify advance field is not empty
                                                            if (!advanceAmountField.getText().isEmpty()) {
                                                                //To verify advance amount format
                                                                if (checkAdvance) {
                                                                    //For verifying that advance amount is less than total amount
                                                                    if (Double.parseDouble(totalBillField.getText().trim()) >= Double.parseDouble(advanceAmountField.getText().trim())) {

                                                                        //check if the balance is non-zero then phone number is necessary
                                                                        if(customerPhoneField.getText().trim().isEmpty() && (balanceAmountField.getText().trim().isEmpty() || Double.parseDouble(balanceAmountField.getText().trim())>0.0) ){
                                                                            Function.alert(Alert.AlertType.ERROR, "Alert", "Customer's Phone Number is necessary, if the balance is empty or non-zero");
                                                                        } else if (customerPhoneField.getText().trim().isEmpty() && customerPhoneVerify) {
                                                                            customerPhoneVerify = false;
                                                                            Function.alert(Alert.AlertType.INFORMATION, "Confirmation", "Customer's Phone Number is empty, do you want to proceed?");
                                                                        } else if (!customerPhoneField.getText().trim().isEmpty() || !customerPhoneVerify) {
                                                                            //Logic
                                                                            //Strings for queries.
                                                                            String queryCustomer;
                                                                            String queryCustomerName;
                                                                            String queryLens;
                                                                            balanceAmountField.setText(String.valueOf(Double.parseDouble(totalBillField.getText().trim())
                                                                                    - Double.parseDouble(advanceAmountField.getText().trim())));
                                                                            Double credit = Double.parseDouble(totalBillField.getText().trim()) - Double.parseDouble(advanceAmountField.getText().trim());
                                                                            try {
                                                                                int customerId = -1;
                                                                                int lensId = -1;
                                                                                int customerNameId = -1;
                                                                                Statement statement2 = connection.createStatement();
                                                                                ResultSet cSet;

                                                                                //Checking wether customer already exists, in case user doesn't press enter after writing phone number.
                                                                                //If customer is new, inserting data, if old then updating
                                                                                if (!customerPhoneField.getText().trim().isEmpty()) {
                                                                                    //If Enter has been pressed on customer Phone
                                                                                    if (customerPhoneKeyDownCheck) {
                                                                                        if (customerNamesComboBox.getValue().equals("New Customer")) {
                                                                                            queryCustomer = "Insert Into " + CustomerDB.customerTableName + " ( " + CustomerDB.customerIdColumn + ", " + CustomerDB.phoneNumberColumn +
                                                                                                    ", " + CustomerDB.addressColumn + " ) Values " + " ( default,'" + customerPhoneField.getText().trim() + "', '" +
                                                                                                    customerAddress.getText().trim() + "' )";
                                                                                            statement2.executeUpdate(queryCustomer, Statement.RETURN_GENERATED_KEYS);
                                                                                            ResultSet autoKey = statement2.getGeneratedKeys();
                                                                                            autoKey.next();
                                                                                            //Newly Generated customerId
                                                                                            customerId = autoKey.getInt(1);
                                                                                            //closing resultSet

                                                                                            queryCustomerName = "Insert Into CustomerName ( customerNameId, customerName, customerCredit, lensId, customerId ) Values" +
                                                                                                    " ( default, '" + customerNameField.getText().trim() + "', " + credit + ", default, " + customerId + " )";
                                                                                            statement2.executeUpdate(queryCustomerName, Statement.RETURN_GENERATED_KEYS);
                                                                                            autoKey = statement2.getGeneratedKeys();
                                                                                            autoKey.next();
                                                                                            //Newly Generated customerNameId
                                                                                            customerNameId = autoKey.getInt(1);
                                                                                            autoKey.close();
                                                                                        } else {
                                                                                            //If selected option in combo box is New Name
                                                                                            if (customerNamesComboBox.getValue().equals("New Name")) {
                                                                                                customerId = crs.getInt("customerId");
                                                                                                //Inserting into customerName
                                                                                                queryCustomerName = "Insert Into CustomerName ( customerNameId, customerName, customerCredit, lensId, customerId ) Values" +
                                                                                                        " ( default, '" + customerNameField.getText().trim() + "', " + credit + ", default, " + customerId + " )";

                                                                                                statement2.executeUpdate(queryCustomerName, Statement.RETURN_GENERATED_KEYS);
                                                                                                ResultSet autoKey = statement2.getGeneratedKeys();
                                                                                                autoKey.next();
                                                                                                //Newly Generated customerNameId
                                                                                                customerNameId = autoKey.getInt(1);
                                                                                                autoKey.close();
                                                                                            } else {
                                                                                                //Updating Customer Name
                                                                                                customerId = crs.getInt("customerId");
                                                                                                cSet = statement2.executeQuery("Select customerCredit, customerNameId, lensId From CustomerName Where customerId = '" + customerId
                                                                                                        + "' And customerName = '" + customerNameField.getText().trim() + "'");
                                                                                                if (cSet.next()) {
                                                                                                    prevCredit = cSet.getDouble("customerCredit");
                                                                                                    customerNameId = cSet.getInt("customerNameId");
                                                                                                    queryCustomerName = "Update CustomerName Set customerCredit = " + (prevCredit + credit) + " Where customerNameId = " + cSet.getInt("customerNameId");
                                                                                                    statement2.execute(queryCustomerName);

                                                                                                    // Checking if lens Data Already exists.
                                                                                                    if (cSet.getInt("lensId") != 0) {
                                                                                                        customerBox1 = true;
                                                                                                        lensId = cSet.getInt("lensId");
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                            //Updating Customer
                                                                                            queryCustomer = "Update Customer Set customerAddress = '" + customerAddress.getText().trim() + "' Where customerId = " + customerId;
                                                                                            statement2.execute(queryCustomer);
                                                                                            statement2.close();
                                                                                        }
                                                                                    } else {
                                                                                        onCustomerPhKeyDown(event);
                                                                                        Function.alert(Alert.AlertType.INFORMATION, "Customer", "Enter was not pressed after adding Phone Number");
                                                                                        return;
                                                                                    }
                                                                                }


                                                                                //If Box1 is not empty
                                                                                if (!isBox1Empty()) {
                                                                                    Statement stLens = connection.createStatement();
                                                                                    //If Customer Lens Data Already Exists
                                                                                    if (customerBox1) {
                                                                                        queryLens = "Update " + LensDB.lensTableName + "  Set " + LensDB.rSphColumn + " = '" + sphRight1.getText().trim() + "', " + LensDB.rCylColumn + " = '" + cylRight1.getText().trim() +
                                                                                                "', " + LensDB.rAxisColumn + " = '" + axisRight1.getText().trim() + "', " + LensDB.rAddColumn + " = '" + addRight1.getText().trim() + "'," +
                                                                                                " " + LensDB.lSphColumn + " = '" + sphLeft1.getText().trim() + "', " + LensDB.lCylColumn + " = '" + cylLeft1.getText().trim() + "'," +
                                                                                                " " + LensDB.lAxisColumn + " = '" + axisLeft1.getText().trim() + "', " + LensDB.lAddColumn + " = '" + addLeft1.getText().trim() + "'," +
                                                                                                " " + LensDB.ipdColumn + " = '" + ipd1.getText().trim() + "' Where " + LensDB.lensIdColumn + " = " + lensId;

                                                                                        stLens.execute(queryLens);
                                                                                    } else {
                                                                                        queryLens = "Insert Into " + LensDB.lensTableName + " (  " + LensDB.lensIdColumn + ", " + LensDB.rSphColumn + ", " + LensDB.rCylColumn + ", " + LensDB.rAxisColumn + ", " + LensDB.rAddColumn +
                                                                                                ", " + LensDB.lSphColumn + ", " + LensDB.lCylColumn + ", " + LensDB.lAxisColumn + ", " + LensDB.lAddColumn + ", " + LensDB.ipdColumn + ")" +
                                                                                                "Values ( default, '" + sphRight1.getText().trim() + "', '" + cylRight1.getText().trim() + "', '" + axisRight1.getText().trim() +
                                                                                                "', '" + addRight1.getText().trim() + "'," + "'" + sphLeft1.getText().trim() + "', '" + cylLeft1.getText().trim() + "', '"
                                                                                                + axisLeft1.getText().trim() + "', '" + addLeft1.getText().trim() + "', '" + ipd1.getText().trim() + "')";

                                                                                        stLens.executeUpdate(queryLens, Statement.RETURN_GENERATED_KEYS);
                                                                                        ResultSet autoKey = stLens.getGeneratedKeys();
                                                                                        autoKey.next();
                                                                                        //Newly Generated LensId
                                                                                        lensId = autoKey.getInt(1);
                                                                                        //closing resultSet
                                                                                        autoKey.close();

                                                                                        // updating value in CustomerName of lensId.
                                                                                        String query = "Update CustomerName Set lensId = " + lensId + " Where customerNameId = " + customerNameId;

                                                                                        stLens.execute(query);
                                                                                        stLens.close();

                                                                                    }
                                                                                }

                                                                                Statement stBill = connection.createStatement();

//                                                                for (int i = 0; i < productList.size(); i++) {
//                                                                    System.out.println(productList.size());
//                                                                    CustomerBillDB obj = productList.get(i);
//                                                                    String queryBillProducts = " Insert Into " + BillProductsDB.billProductsTableName + "(" + BillProductsDB.productIdColumn + ", " + BillProductsDB.billIdColumn
//                                                                            + ", " + BillProductsDB.codeColumn + ", " + BillProductsDB.nameColumn + ", " + BillProductsDB.quantityColumn + ", " + BillProductsDB.purchasePriceColumn + "," +
//                                                                            BillProductsDB.retailPriceColumn + ", discount ) Values ( " + (i + 1) + ", " + Integer.parseInt(invoiceNoField.getText()) + " ,'" + obj.getCode() + "', '" + obj.getName()
//                                                                            + "', " + obj.getQuantity() + "," + obj.getPurchasePrice() + ", " + obj.getRetailPrice() + ", " + obj.getDiscount() + ")";
//
//                                                                    stBill.execute(queryBillProducts);
//                                                                }
                                                                                int employeeId = 0;
                                                                                if (!LoginController.currentUserName.equals(LoginController.adminUserName)) {
                                                                                    ResultSet empSet = stBill.executeQuery("Select employeeId From Employee Where Employee_UserName = '" + LoginController.currentUserName + "'");
                                                                                    empSet.next();
                                                                                    employeeId = empSet.getInt("employeeId");
                                                                                }
                                                                                // 1 means has ob product
                                                                                int obProd = (hasOBProduct) ? 1 : 0;

                                                                                String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")).toString();
                                                                                String queryBill = "Insert Into " + CustomerBillsDB.customerBillTableName + "(" + CustomerBillsDB.idColumn + ", " + CustomerBillsDB.timeColumn + ", "
                                                                                        + CustomerBillsDB.dateColumn + ", " + CustomerBillsDB.deliveryDateColumn + ", " + CustomerBillsDB.advanceColumn + ", " + CustomerBillsDB.balanceColumn + ", "
                                                                                        + CustomerBillsDB.noteColumn + ", customerNameId, employeeId, hasOBProd, billTotal ) Values ("
                                                                                        + Integer.parseInt(invoiceNoField.getText()) + ", '" + currentTime + "', '" + billDate.getValue().toString() + "','" + deliveryDate.getValue().toString() +
                                                                                        "', " + Double.parseDouble(advanceAmountField.getText()) + "," +
                                                                                        Double.parseDouble(balanceAmountField.getText()) + ",'" + billNote.getText() + "', " + customerNameId + ", " + employeeId + ", "+obProd+", "+ total +" )";
                                                                                stBill.execute(queryBill);

                                                                                String queryBillProducts;
                                                                                CustomerBillTable obj;
                                                                                for (int i = 0; i < table.getItems().size(); i++) {
                                                                                    obj = table.getItems().get(i);
                                                                                    if (obj.isOBProduct()){
                                                                                        queryBillProducts = " Insert into OrderBasedProducts ( productCode, productName, productQuantity, purchasePrice, retailPrice," +
                                                                                                " discountPercentage, cBillId) Values ( "+ Integer.parseInt(obj.getCode()) +" , '" + obj.getName() + "' , " + obj.getQuantity()
                                                                                                + " ," + obj.getPurchasePrice() + " , " + obj.getRetailPrice() + " , " + obj.getDiscountPercentage() + " , " + Integer.parseInt(invoiceNoField.getText())  + " ) ";
                                                                                    }
                                                                                    else{
                                                                                        queryBillProducts = " Insert Into " + BillProductsDB.billProductsTableName + "(" + BillProductsDB.productIdColumn + ", " + BillProductsDB.billIdColumn
                                                                                                + ", " + BillProductsDB.codeColumn + ", " + BillProductsDB.nameColumn + ", " + BillProductsDB.quantityColumn + ", " + BillProductsDB.purchasePriceColumn + "," +
                                                                                                BillProductsDB.retailPriceColumn + ", discount ) Values ( " + (i + 1) + ", " + Integer.parseInt(invoiceNoField.getText()) + " ,'" + obj.getCode() + "', '" + obj.getName()
                                                                                                + "', " + obj.getQuantity() + "," + obj.getPurchasePrice() + ", " + obj.getRetailPrice() + ", " + obj.getDiscountPercentage() + ")";
                                                                                    }

                                                                                    stBill.execute(queryBillProducts);

                                                                                }

                                                                                // Subtracting Stock
                                                                                String stockQuery;
                                                                                for (String prodCode : stockMap.keySet()){
                                                                                    Integer remainingStock = stockMap.get(prodCode);
                                                                                    stockQuery = "UPDATE Inventory SET stock = " + remainingStock + " WHERE productCode = '" + prodCode + "'";
                                                                                    stBill.execute(stockQuery);
                                                                                }

                                                                                stBill.close();

                                                                                Function.alert(Alert.AlertType.INFORMATION, "Success", "Bill saved successfully.");


                                                                                int choice = JOptionPane.showConfirmDialog(null, "Bill is going to be printed. Click 'OK' to print or 'Cancel' to not print.", "Confirmation", JOptionPane.OK_CANCEL_OPTION);
                                                                                if (choice == JOptionPane.OK_OPTION) {
                                                                                    Statement statementCashierName= connection.createStatement();
                                                                                    String cashierName = "";
                                                                                    if(employeeId==0){
                                                                                        // Cashier Name
                                                                                        cashierName = "M.Muzammil";
                                                                                    }
                                                                                    else if(employeeId>0) {
                                                                                        // Cashier Name
                                                                                        ResultSet em = statementCashierName.executeQuery("Select Employee_Name from Employee where employeeId = " + employeeId);
                                                                                        if (em.next()) {
                                                                                            cashierName = em.getString(EmployeeDB.employeeNameColumn);
                                                                                        }
                                                                                        em.close();
                                                                                        statementCashierName.close();
                                                                                    }
                                                                                    printBill(event,invoiceNoField.getText().trim(),billDate.getValue().toString(),currentTime, deliveryDate.getValue().toString(),
                                                                                            customerPhoneField.getText().trim(), customerNameField.getText().trim(),cashierName, totalBillField.getText().trim(),
                                                                                            advanceAmountField.getText().trim(),"0.0",balanceAmountField.getText().trim(),sphRight1.getText().trim(),cylRight1.getText().trim(),axisRight1.getText().trim(),
                                                                                            addRight1.getText().trim(),sphLeft1.getText().trim(),cylLeft1.getText().trim(),axisLeft1.getText().trim(),addLeft1.getText().trim(), ipd1.getText().trim(),table,1);

                                                                                }else {
                                                                                    try {
                                                                                        SceneController.switchScene("BillModule/Bills.fxml", event, "Bills");
                                                                                    } catch (IOException e) {
                                                                                        throw new RuntimeException(e);
                                                                                    }
                                                                                }

                                                                                connection.close();


                                                                            } catch (Exception e) {
                                                                                e.printStackTrace();
                                                                                System.out.println(e.getMessage());
                                                                            }

                                                                        }
                                                                    } else {
                                                                        Function.alert(Alert.AlertType.ERROR, "Error", "Advance amount more than total amount");
                                                                        advanceAmountField.clear();
                                                                        advanceAmountField.requestFocus();
                                                                    }
                                                                } else {
                                                                    Function.alert(Alert.AlertType.ERROR, "Error", "Enter right Amount in Advance Field.");
                                                                    advanceAmountField.clear();
                                                                    advanceAmountField.requestFocus();
                                                                }
                                                            } else {
                                                                Function.alert(Alert.AlertType.ERROR, "Error", "Advance amount field is empty");
                                                                advanceAmountField.requestFocus();
                                                            }

                                                        } else {
                                                            Function.alert(Alert.AlertType.ERROR, "Error", "Customer Phone number and name are must if any lens detail exists");
                                                            customerPhoneField.requestFocus();
                                                        }
                                                    } else {
                                                        Function.alert(Alert.AlertType.ERROR, "Error", "Enter only Numbers in SPH, CYL, AXIS, ADD, IPD ");
                                                        sphRight1.requestFocus();
                                                    }
                                                } else {
                                                    Function.alert(Alert.AlertType.ERROR, "Error", "Customer Name and Phone number both are necessary");
                                                    customerNameField.requestFocus();
                                                }
                                            } else {
                                                Function.alert(Alert.AlertType.ERROR, "Error", "NO Product Added in Bill");
                                                productCodeField.requestFocus();
                                            }
                                        } else {
                                            Function.alert(Alert.AlertType.ERROR, "Error", "Customer Name not in Correct Format!!");
                                            customerNameField.clear();
                                            customerNameField.requestFocus();
                                        }
                                    } else {
                                        Function.alert(Alert.AlertType.ERROR, "Error", "Phone Number not in Correct Format!!");
                                        customerPhoneField.clear();
                                        customerPhoneField.requestFocus();
                                    }
                                } else {
                                    Function.alert(Alert.AlertType.ERROR, "Error", "Delivery date is before bill date");
                                }
                            } else {
                                Function.alert(Alert.AlertType.ERROR, "Error", "Please Select Delivery Date.");
                                deliveryDate.requestFocus();
                            }
                        } else{
                            Function.alert(Alert.AlertType.ERROR, "Error", "Please Select Bill Date.");
                            billDate.requestFocus();
                        }

                    } else {
                        Function.alert(Alert.AlertType.ERROR, "Error", "Invoice Number Already exists.");
                        invoiceNoField.clear();
                        invoiceNoField.requestFocus();
                    }
                } else {
                    Function.alert(Alert.AlertType.ERROR, "Error", "Invoice number not in a correct format");
                    invoiceNoField.clear();
                    invoiceNoField.requestFocus();
                }
            } else {
                Function.alert(Alert.AlertType.ERROR, "Error", "Invoice number field empty");
                invoiceNoField.requestFocus();
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
            Function.alert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    void onCancelClicked(ActionEvent event) {
        int choice = JOptionPane.showConfirmDialog(null, "Do you really want to cancel Bill?", "Confirmation", JOptionPane.OK_CANCEL_OPTION);
        if (choice == JOptionPane.OK_OPTION) {
            try {
                table.getItems().clear();
                stockMap.clear();
                SceneController.switchScene("BillModule/Bills.fxml",event,"Bills");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void clearForNewCustomer(){
        customerNameField.clear();
        clearBox();
    }

    public boolean isBox1Empty() {
        // Checks if all fields are empty
        if (sphRight1.getText().trim().isEmpty() && cylRight1.getText().trim().isEmpty() && axisRight1.getText().trim().isEmpty() &&
                addRight1.getText().trim().isEmpty() && sphLeft1.getText().trim().isEmpty() && cylLeft1.getText().trim().isEmpty() &&
                axisLeft1.getText().trim().isEmpty() && addLeft1.getText().trim().isEmpty() && ipd1.getText().trim().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkTheBox1() {
        boolean[] flag = new boolean[9];
        boolean check = true;
        Arrays.fill(flag, true);
        String values = sphRight1.getText().trim();
        flag[0] = Function.checkForDecimalPlusMinus(values);
        values = cylRight1.getText().trim();
        flag[1] = Function.checkForDecimalPlusMinus(values);
        values = axisRight1.getText().trim();
        flag[2] = Function.checkForDecimalPlusMinus(values);
        values = addRight1.getText().trim();
        flag[3] = Function.checkForDecimalPlusMinus(values);
        values = sphLeft1.getText().trim();
        flag[4] = Function.checkForDecimalPlusMinus(values);
        values = cylLeft1.getText().trim();
        flag[5] = Function.checkForDecimalPlusMinus(values);
        values = axisLeft1.getText().trim();
        flag[6] = Function.checkForDecimalPlusMinus(values);
        values = addLeft1.getText().trim();
        flag[7] = Function.checkForDecimalPlusMinus(values);
        for (int i = 0; i < flag.length; i++) {
            if (!flag[i]) {
                check = false;
            }
        }
        return check;
    }

    public void clearBox(){
        sphLeft1.clear();
        sphRight1.clear();
        ipd1.clear();
        cylLeft1.clear();
        cylRight1.clear();
        addLeft1.clear();
        addRight1.clear();
        axisLeft1.clear();
        axisRight1.clear();
    }

    void printBill(ActionEvent event, String invoiceNo,String billDate, String billTime, String dDate, String cPhone, String cName, String cshName,
                   String totalBillAmount, String advanceAmount, String finalPaymentAmount,String balanceAmount, String sphRight, String cylRight, String axisRight,
                   String addRight, String sphLeft, String cylLeft, String axisLeft, String addLeft, String ipdVal, TableView<CustomerBillTable> productsTable , int sceneSwitch){

        try {

            AnchorPane pane = new AnchorPane();

            // Declaring Labels and setting their values

            //Heading Labels

            Label billId= new Label("Bill No");
            Label orderDate= new Label("Order Date & Time");
            Label deliveryDate= new Label("Delivery Date");
            Label customerPhone= new Label("Customer Phone");
            Label customerName= new Label("Name");
            Label cashierName= new Label("Cashier Name");
            Label total= new Label("Total:");
            Label advance= new Label("Advance:");
            Label finalAmount= new Label("Final Amount:");
            Label balance= new Label("Balance:");

            Label billId1= new Label("Bill No");
            Label orderDate1= new Label("Order Date & Time");
            Label deliveryDate1= new Label("Delivery Date");
            Label customerName1= new Label("Name");
            Label customerPhone1= new Label("Customer Phone");
            Label cashierName1= new Label("Cashier Name");
            Label total1= new Label("Total:");
            Label advance1= new Label("Advance:");
            Label finalAmount1= new Label("Final Amount:");
            Label balance1= new Label("Balance:");
            Label right= new Label("Right");
            Label left= new Label("Left");
            Label sph= new Label("SPH");
            Label cyl= new Label("CYL");
            Label axis= new Label("AXIS");
            Label add= new Label("ADD");
            Label ipd= new Label("IPD");

            //Value Labels

            Label billIdValue= new Label(invoiceNo);
            Label orderDateValue= new Label(billDate);
            Label orderTimeValue= new Label(billTime);
            Label deliveryDateValue= new Label(dDate);
            Label customerPhoneValue= new Label(cPhone);
            Label customerNameValue= new Label(cName);
            Label cashierNameValue= new Label(cshName);
            Label totalValue= new Label(totalBillAmount);
            Label advanceValue= new Label(advanceAmount);
            Label finalAmountValue= new Label(finalPaymentAmount);
            Label balanceValue= new Label(balanceAmount);

            Label billIdValue1= new Label(invoiceNo);
            Label orderDateValue1= new Label(billDate);
            Label orderTimeValue1= new Label(billTime);
            Label deliveryDateValue1= new Label(dDate);
            Label customerNameValue1= new Label(cName);
            Label customerPhoneValue1= new Label(cPhone);
            Label cashierNameValue1= new Label(cshName);
            Label totalValue1= new Label(totalBillAmount);
            Label advanceValue1= new Label(advanceAmount);
            Label finalAmountValue1= new Label(finalPaymentAmount);
            Label balanceValue1= new Label(balanceAmount);

            Label rSphValue= new Label(sphRight);
            Label rCylValue= new Label(cylRight);
            Label rAxisValue= new Label(axisRight);
            Label rAddValue= new Label(addRight);

            Label lSphValue= new Label(sphLeft);
            Label lCylValue= new Label(cylLeft);
            Label lAxisValue= new Label(axisLeft);
            Label lAddValue= new Label(addLeft);

            Label ipdValue= new Label(ipdVal);

            //Dummy Labels
            Label dummy1= new Label("");
            Label dummy9= new Label("");
            Label dummy10= new Label("");
            Label dummy11= new Label("");
            Label dummy12= new Label("");

            // Declaring Table Views and their Columns and setting their values

            TableView<CustomerBillTable> table1 = new TableView<>();

            TableColumn<CustomerBillTable, String> productNamePTVColumn = new TableColumn<>("Name");
            TableColumn<CustomerBillTable, Integer> productQuantityPTVColumn = new TableColumn<>("Qty");
            TableColumn<CustomerBillTable, Double> productPricePTVColumn = new TableColumn<>("Price");
            TableColumn<CustomerBillTable, Double> discountPTVColumn = new TableColumn<>("DC");
            TableColumn<CustomerBillTable, Double> productTotalPTVColumn = new TableColumn<>("Total");


            productNamePTVColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            productQuantityPTVColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            productPricePTVColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
            discountPTVColumn.setCellValueFactory(new PropertyValueFactory<>("discount"));
            productTotalPTVColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));


            table1.getColumns().setAll(productNamePTVColumn,productQuantityPTVColumn,productPricePTVColumn,discountPTVColumn,productTotalPTVColumn);

            ObservableList<CustomerBillTable> observableList = FXCollections.observableArrayList();

            for (CustomerBillTable data : productsTable.getItems()) {
                String name = data.getName();
                Integer quantity = data.getQuantity();
                Double retailPrice = data.getRetailPrice();
                Double purchasePrice = data.getPurchasePrice();
                Double discount = data.getDiscountPercentage();
                Double totalPrice = data.getTotalPrice();

                // Create a new CustomerBillTable object with the data you want to keep
                CustomerBillTable newData = new CustomerBillTable(name, quantity, retailPrice, purchasePrice,discount,false, totalPrice);

                // Add it to the observableList
                observableList.add(newData);
            }

            table1.setItems(observableList);

            TableView<CustomerBillTable> table2 = new TableView<>();

            TableColumn<CustomerBillTable, String> productNamePTVColumn1 = new TableColumn<>("Name");
            TableColumn<CustomerBillTable, Integer> productQuantityPTVColumn1 = new TableColumn<>("Qty");
            TableColumn<CustomerBillTable, Double> productPricePTVColumn1 = new TableColumn<>("Price");
            TableColumn<CustomerBillTable, Double> discountPTVColumn1 = new TableColumn<>("DC");
            TableColumn<CustomerBillTable, Double> productTotalPTVColumn1 = new TableColumn<>("Total");

            productNamePTVColumn1.setCellValueFactory(new PropertyValueFactory<>("name"));
            productQuantityPTVColumn1.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            productPricePTVColumn1.setCellValueFactory(new PropertyValueFactory<>("price"));
            discountPTVColumn1.setCellValueFactory(new PropertyValueFactory<>("discount"));
            productTotalPTVColumn1.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

            table2.getColumns().setAll(productNamePTVColumn1,productQuantityPTVColumn1,productPricePTVColumn1,discountPTVColumn1,productTotalPTVColumn1);

            table2.setItems(observableList);

            // Declaring Grid-pane and setting the row and column index of labels

            GridPane gridPane= new GridPane();

            // Create ColumnConstraints
            ColumnConstraints column0 = new ColumnConstraints(76);
            ColumnConstraints column1 = new ColumnConstraints(76);
            ColumnConstraints column2 = new ColumnConstraints(76);
            ColumnConstraints column3 = new ColumnConstraints(76);
            ColumnConstraints column4 = new ColumnConstraints(76);
            ColumnConstraints column5 = new ColumnConstraints(76);

            // Apply the constraints to the GridPane
            gridPane.getColumnConstraints().addAll(column0, column1,column2, column3,column4, column5);

            GridPane.setConstraints(right, 0, 1);
            GridPane.setConstraints(left, 0, 2);
            GridPane.setConstraints(sph, 1, 0);
            GridPane.setConstraints(rSphValue, 1, 1);
            GridPane.setConstraints(lSphValue, 1, 2);
            GridPane.setConstraints(cyl, 2, 0);
            GridPane.setConstraints(rCylValue, 2, 1);
            GridPane.setConstraints(lCylValue, 2, 2);
            GridPane.setConstraints(axis, 3, 0);
            GridPane.setConstraints(rAxisValue, 3, 1);
            GridPane.setConstraints(lAxisValue, 3, 2);
            GridPane.setConstraints(add, 4, 0);
            GridPane.setConstraints(rAddValue, 4, 1);
            GridPane.setConstraints(lAddValue, 4, 2);
            GridPane.setConstraints(ipd, 5, 0);
            GridPane.setConstraints(ipdValue, 5, 1);

            gridPane.getChildren().addAll(right,left,sph,rSphValue,lSphValue,cyl,rCylValue,lCylValue,axis,rAxisValue,lAxisValue,
                    add,rAddValue,lAddValue,ipd,ipdValue);

            // Dummy Grid Pane
            GridPane dummyGridPane= new GridPane();
            ColumnConstraints column0d = new ColumnConstraints(76);
            ColumnConstraints column1d = new ColumnConstraints(76);
            ColumnConstraints column2d = new ColumnConstraints(76);
            ColumnConstraints column3d = new ColumnConstraints(76);
            ColumnConstraints column4d = new ColumnConstraints(76);
            ColumnConstraints column5d = new ColumnConstraints(76);
            dummyGridPane.getColumnConstraints().addAll(column0d, column1d,column2d, column3d,column4d, column5d);

            GridPane.setConstraints(dummy9, 0, 0);
            GridPane.setConstraints(dummy10, 5, 0);
            GridPane.setConstraints(dummy11, 0, 2);
            GridPane.setConstraints(dummy12, 5, 2);

            dummyGridPane.getChildren().addAll(dummy9,dummy10,dummy11,dummy12);

            dummyGridPane.setLayoutX(5); dummyGridPane.setLayoutY(585);
            dummyGridPane.setPrefWidth(460);dummyGridPane.setPrefHeight(55);

            // Width:480 is Page Limit
            // Anchor-pane Width & Height

            pane.setPrefWidth(520);
            pane.setPrefHeight(755);
            pane.getChildren().addAll(billId,billIdValue,orderDate,orderDateValue,orderTimeValue,cashierName,cashierNameValue,deliveryDate,deliveryDateValue,
                    customerPhone,customerPhoneValue,customerName,customerNameValue,table1,total,totalValue,advance,advanceValue,finalAmount,finalAmountValue,
                    balance,balanceValue,billId1,billIdValue1,orderDate1,orderDateValue1,orderTimeValue1,cashierName1,cashierNameValue1,deliveryDate1,deliveryDateValue1,
                    customerPhone1,customerPhoneValue1,customerName1,customerNameValue1,table2,total1,totalValue1,advance1,advanceValue1,finalAmount1,finalAmountValue1,balance1,balanceValue1,gridPane,
                    dummy1,dummyGridPane);

            int startX=0,row1Y=0,row2Y=20,row3Y=40,row4Y=235,row5Y=265,row6Y=285,row7Y=305,row8Y=500;
            int tableWidth=470,tableHeight=170,priceWidth=55;
            double height15=15.2;

            //Row 1 X&Y Co-ordinates
            billId.setLayoutX(startX); billId.setLayoutY(row1Y); billIdValue.setLayoutX(110); billIdValue.setLayoutY(row1Y);
            orderDate.setLayoutX(230); orderDate.setLayoutY(row1Y); orderDateValue.setLayoutX(350); orderDateValue.setLayoutY(row1Y); orderTimeValue.setLayoutX(420); orderTimeValue.setLayoutY(row1Y);
            //Row 1 Width & Height
            billId.setPrefWidth(100); billId.setPrefHeight(height15);  billIdValue.setPrefWidth(110); billIdValue.setPrefHeight(height15);
            orderDate.setPrefWidth(115); orderDate.setPrefHeight(height15); orderDateValue.setPrefWidth(65); orderDate.setPrefHeight(height15); orderTimeValue.setPrefWidth(50); orderTimeValue.setPrefHeight(height15);

            //Row 2 X&Y Co-ordinates
            cashierName.setLayoutX(startX); cashierName.setLayoutY(row2Y); cashierNameValue.setLayoutX(110); cashierNameValue.setLayoutY(row2Y);
            deliveryDate.setLayoutX(230); deliveryDate.setLayoutY(row2Y); deliveryDateValue.setLayoutX(350); deliveryDateValue.setLayoutY(row2Y);
            //Row 2 Width & Height
            cashierName.setPrefWidth(100); cashierName.setPrefHeight(height15); cashierNameValue.setPrefWidth(110); cashierNameValue.setPrefHeight(height15);
            deliveryDate.setPrefWidth(80); deliveryDate.setPrefHeight(height15); deliveryDateValue.setPrefWidth(65); deliveryDateValue.setPrefHeight(height15);

            //Row 3 X&Y Co-ordinate
            customerPhone.setLayoutX(startX); customerPhone.setLayoutY(row3Y); customerPhoneValue.setLayoutX(110); customerPhoneValue.setLayoutY(row3Y);
            customerName.setLayoutX(230); customerName.setLayoutY(row3Y); customerNameValue.setLayoutX(300); customerNameValue.setLayoutY(row3Y);
            //Row 3 Width & Height
            customerPhone.setPrefWidth(100); customerPhone.setPrefHeight(height15); customerPhoneValue.setPrefWidth(110); customerPhoneValue.setPrefHeight(height15);
            customerName.setPrefWidth(50); customerName.setPrefHeight(height15); customerNameValue.setPrefWidth(180); customerNameValue.setPrefHeight(height15);

            //Table View 1 X&Y Co-ordinates
            table1.setLayoutX(0); table1.setLayoutY(60);
            //Table View 1 Width & Height
            table1.setPrefWidth(tableWidth); table1.setPrefHeight(tableHeight);
            productNamePTVColumn.setPrefWidth(260); productQuantityPTVColumn.setPrefWidth(40);
            productPricePTVColumn.setPrefWidth(60); discountPTVColumn.setPrefWidth(40); productTotalPTVColumn.setPrefWidth(70);

            //Row 4 X&Y Co-ordinates
            total.setLayoutX(startX); total.setLayoutY(row4Y); totalValue.setLayoutX(40); totalValue.setLayoutY(row4Y);
            advance.setLayoutX(100); advance.setLayoutY(row4Y); advanceValue.setLayoutX(160); advanceValue.setLayoutY(row4Y);
            finalAmount.setLayoutX(220); finalAmount.setLayoutY(row4Y);  finalAmountValue.setLayoutX(310); finalAmountValue.setLayoutY(row4Y);
            balance.setLayoutX(370); balance.setLayoutY(row4Y);  balanceValue.setLayoutX(425); balanceValue.setLayoutY(row4Y);
            //Row 4 Width & Height
            total.setPrefWidth(35); total.setPrefHeight(height15); totalValue.setPrefWidth(priceWidth); totalValue.setPrefHeight(height15);
            advance.setPrefWidth(55); advance.setPrefHeight(height15); advanceValue.setPrefWidth(priceWidth); advanceValue.setPrefHeight(height15);
            finalAmount.setPrefWidth(85); finalAmount.setPrefHeight(height15); finalAmountValue.setPrefWidth(priceWidth); finalAmountValue.setPrefHeight(height15);
            balance.setPrefWidth(50); balance.setPrefHeight(height15);  balanceValue.setPrefWidth(priceWidth); balanceValue.setPrefHeight(height15);

            //Row 5 X&Y Co-ordinates
            billId1.setLayoutX(startX); billId1.setLayoutY(row5Y); billIdValue1.setLayoutX(110); billIdValue1.setLayoutY(row5Y);
            orderDate1.setLayoutX(230); orderDate1.setLayoutY(row5Y); orderDateValue1.setLayoutX(350); orderDateValue1.setLayoutY(row5Y); orderTimeValue1.setLayoutX(420); orderTimeValue1.setLayoutY(row5Y);
            //Row 5 Width & Height
            billId1.setPrefWidth(100); billId1.setPrefHeight(height15);  billIdValue1.setPrefWidth(110); billIdValue1.setPrefHeight(height15);
            orderDate1.setPrefWidth(115); orderDate1.setPrefHeight(height15); orderDateValue1.setPrefWidth(65); orderDate1.setPrefHeight(height15); orderTimeValue1.setPrefWidth(50); orderTimeValue1.setPrefHeight(height15);

            //Row 6 X&Y Co-ordinates
            cashierName1.setLayoutX(startX); cashierName1.setLayoutY(row6Y); cashierNameValue1.setLayoutX(110); cashierNameValue1.setLayoutY(row6Y);
            deliveryDate1.setLayoutX(230); deliveryDate1.setLayoutY(row6Y); deliveryDateValue1.setLayoutX(350); deliveryDateValue1.setLayoutY(row6Y);
            //Row 6 Width & Height
            cashierName1.setPrefWidth(100); cashierName1.setPrefHeight(height15); cashierNameValue1.setPrefWidth(110); cashierNameValue1.setPrefHeight(height15);
            deliveryDate1.setPrefWidth(80); deliveryDate1.setPrefHeight(height15); deliveryDateValue1.setPrefWidth(65); deliveryDateValue1.setPrefHeight(height15);

            //Row 7 X&Y Co-ordinates
            customerPhone1.setLayoutX(startX); customerPhone1.setLayoutY(row7Y); customerPhoneValue1.setLayoutX(110); customerPhoneValue1.setLayoutY(row7Y);
            customerName1.setLayoutX(230); customerName1.setLayoutY(row7Y); customerNameValue1.setLayoutX(300); customerNameValue1.setLayoutY(row7Y);
            //Row 7 Width & Height
            customerPhone1.setPrefWidth(100); customerPhone1.setPrefHeight(height15); customerPhoneValue1.setPrefWidth(110); customerPhoneValue1.setPrefHeight(height15);
            customerName1.setPrefWidth(50); customerName1.setPrefHeight(height15); customerNameValue1.setPrefWidth(180); customerNameValue1.setPrefHeight(height15);

            //Table View 2 X&Y Co-ordinates
            table2.setLayoutX(0); table2.setLayoutY(325);
            //Table View 2 Width & Height
            table2.setPrefWidth(tableWidth); table2.setPrefHeight(tableHeight);
            productNamePTVColumn1.setPrefWidth(260); productQuantityPTVColumn1.setPrefWidth(40);
            productPricePTVColumn1.setPrefWidth(60); discountPTVColumn1.setPrefWidth(40); productTotalPTVColumn1.setPrefWidth(70);

            //Row 8 X&Y Co-ordinates
            total1.setLayoutX(startX); total1.setLayoutY(row8Y); totalValue1.setLayoutX(40); totalValue1.setLayoutY(row8Y);
            advance1.setLayoutX(100); advance1.setLayoutY(row8Y); advanceValue1.setLayoutX(160); advanceValue1.setLayoutY(row8Y);
            finalAmount1.setLayoutX(220); finalAmount1.setLayoutY(row8Y);  finalAmountValue1.setLayoutX(310); finalAmountValue1.setLayoutY(row8Y);
            balance1.setLayoutX(370); balance1.setLayoutY(row8Y);  balanceValue1.setLayoutX(425); balanceValue1.setLayoutY(row8Y);
            //Row 8 Width & Height
            total1.setPrefWidth(35); total1.setPrefHeight(height15); totalValue1.setPrefWidth(priceWidth); totalValue1.setPrefHeight(height15);
            advance1.setPrefWidth(55); advance1.setPrefHeight(height15); advanceValue1.setPrefWidth(priceWidth); advanceValue1.setPrefHeight(height15);
            finalAmount1.setPrefWidth(85); finalAmount1.setPrefHeight(height15); finalAmountValue1.setPrefWidth(priceWidth); finalAmountValue1.setPrefHeight(height15);
            balance1.setPrefWidth(50); balance1.setPrefHeight(height15);  balanceValue1.setPrefWidth(priceWidth); balanceValue1.setPrefHeight(height15);

            // Grid Pane X&Y Co-ordinates & (Height & Width)
            gridPane.setLayoutX(5); gridPane.setLayoutY(520);
            gridPane.setPrefWidth(460); gridPane.setPrefHeight(60);

            // Set alignment for individual nodes within the GridPane
            GridPane.setHalignment(right, HPos.CENTER);
            GridPane.setValignment(right, VPos.CENTER);
            GridPane.setHalignment(left, HPos.CENTER);
            GridPane.setValignment(left, VPos.CENTER);
            GridPane.setHalignment(sph, HPos.CENTER);
            GridPane.setValignment(sph, VPos.CENTER);
            GridPane.setHalignment(rSphValue, HPos.CENTER);
            GridPane.setValignment(rSphValue, VPos.CENTER);
            GridPane.setHalignment(lSphValue, HPos.CENTER);
            GridPane.setValignment(lSphValue, VPos.CENTER);
            GridPane.setHalignment(cyl, HPos.CENTER);
            GridPane.setValignment(cyl, VPos.CENTER);
            GridPane.setHalignment(rCylValue, HPos.CENTER);
            GridPane.setValignment(rCylValue, VPos.CENTER);
            GridPane.setHalignment(lCylValue, HPos.CENTER);
            GridPane.setValignment(lCylValue, VPos.CENTER);
            GridPane.setHalignment(axis, HPos.CENTER);
            GridPane.setValignment(axis, VPos.CENTER);
            GridPane.setHalignment(rAxisValue, HPos.CENTER);
            GridPane.setValignment(rAxisValue, VPos.CENTER);
            GridPane.setHalignment(lAxisValue, HPos.CENTER);
            GridPane.setValignment(lAxisValue, VPos.CENTER);
            GridPane.setHalignment(add, HPos.CENTER);
            GridPane.setValignment(add, VPos.CENTER);
            GridPane.setHalignment(rAddValue, HPos.CENTER);
            GridPane.setValignment(rAddValue, VPos.CENTER);
            GridPane.setHalignment(lAddValue, HPos.CENTER);
            GridPane.setValignment(lAddValue, VPos.CENTER);
            GridPane.setHalignment(ipd, HPos.CENTER);
            GridPane.setValignment(ipd, VPos.CENTER);
            GridPane.setHalignment(ipdValue, HPos.CENTER);
            GridPane.setValignment(ipdValue, VPos.CENTER);

            right.setPrefWidth(35); right.setPrefHeight(height15);
            left.setPrefWidth(35); left.setPrefHeight(height15);
            sph.setPrefWidth(35); sph.setPrefHeight(height15);
            rSphValue.setPrefWidth(45); rSphValue.setPrefHeight(height15);
            lSphValue.setPrefWidth(45); lSphValue.setPrefHeight(height15);
            cyl.setPrefWidth(35); cyl.setPrefHeight(height15);
            rCylValue.setPrefWidth(45); rCylValue.setPrefHeight(height15);
            lCylValue.setPrefWidth(45); lCylValue.setPrefHeight(height15);
            axis.setPrefWidth(35); axis.setPrefHeight(height15);
            rAxisValue.setPrefWidth(45); rAxisValue.setPrefHeight(height15);
            lAxisValue.setPrefWidth(45); lAxisValue.setPrefHeight(height15);
            add.setPrefWidth(35); add.setPrefHeight(height15);
            rAddValue.setPrefWidth(45); rAddValue.setPrefHeight(height15);
            lAddValue.setPrefWidth(45); lAddValue.setPrefHeight(height15);
            ipd.setPrefWidth(35); ipd.setPrefHeight(height15);
            ipdValue.setPrefWidth(70); rAddValue.setPrefHeight(height15);

            //Dummy Items
            int dummyX=485,dummyWidth=55;
            dummy1.setLayoutX(dummyX); dummy1.setLayoutY(row1Y);
            dummy1.setPrefWidth(dummyWidth); dummy1.setPrefHeight(height15);

            dummyGridPane.setLayoutX(5); dummyGridPane.setLayoutY(590);


            //  Adding CSS to Labels

            String fontFamily="-fx-font-family: 'Times New Roman'; ",fontSize=" -fx-font-size: 13px; ",fontWeight=" -fx-font-weight: bold; ";
            String padding="-fx-padding: 0px; ",backgroundColor="-fx-control-inner-background-alt: white, white; ";

            gridPane.setStyle("-fx-grid-lines-visible: true;");

            table1.setStyle(fontFamily+fontSize+padding+backgroundColor);
            table2.setStyle(fontFamily+fontSize+padding+backgroundColor);

            billId.setStyle(fontFamily+fontSize+fontWeight);
            billIdValue.setStyle(fontFamily+fontSize);
            orderDate.setStyle(fontFamily+fontSize+fontWeight);
            orderDateValue.setStyle(fontFamily+fontSize);
            orderTimeValue.setStyle(fontFamily+fontSize);
            deliveryDate.setStyle(fontFamily+fontSize+fontWeight);
            deliveryDateValue.setStyle(fontFamily+fontSize);
            customerName.setStyle(fontFamily+fontSize+fontWeight);
            customerNameValue.setStyle(fontFamily+fontSize);
            customerPhone.setStyle(fontFamily+fontSize+fontWeight);
            customerPhoneValue.setStyle(fontFamily+fontSize);
            cashierName.setStyle(fontFamily+fontSize+fontWeight);
            cashierNameValue.setStyle(fontFamily+fontSize);
            total.setStyle(fontFamily+fontSize+fontWeight);
            totalValue.setStyle(fontFamily+fontSize);
            advance.setStyle(fontFamily+fontSize+fontWeight);
            advanceValue.setStyle(fontFamily+fontSize);
            finalAmount.setStyle(fontFamily+fontSize+fontWeight);
            finalAmountValue.setStyle(fontFamily+fontSize);
            balance.setStyle(fontFamily+fontSize+fontWeight);
            balanceValue.setStyle(fontFamily+fontSize);

            cashierName1.setStyle(fontFamily+fontSize+fontWeight);
            cashierNameValue1.setStyle(fontFamily+fontSize);
            billId1.setStyle(fontFamily+fontSize+fontWeight);
            billIdValue1.setStyle(fontFamily+fontSize);
            orderDate1.setStyle(fontFamily+fontSize+fontWeight);
            orderDateValue1.setStyle(fontFamily+fontSize);
            orderTimeValue1.setStyle(fontFamily+fontSize);
            deliveryDate1.setStyle(fontFamily+fontSize+fontWeight);
            deliveryDateValue1.setStyle(fontFamily+fontSize);
            customerName1.setStyle(fontFamily+fontSize+fontWeight);
            customerNameValue1.setStyle(fontFamily+fontSize);
            customerPhone1.setStyle(fontFamily+fontSize+fontWeight);
            customerPhoneValue1.setStyle(fontFamily+fontSize);
            total1.setStyle(fontFamily+fontSize+fontWeight);
            totalValue1.setStyle(fontFamily+fontSize);
            advance1.setStyle(fontFamily+fontSize+fontWeight);
            advanceValue1.setStyle(fontFamily+fontSize);
            finalAmount1.setStyle(fontFamily+fontSize+fontWeight);
            finalAmountValue1.setStyle(fontFamily+fontSize);
            balance1.setStyle(fontFamily+fontSize+fontWeight);
            balanceValue1.setStyle(fontFamily+fontSize);

            right.setStyle(fontFamily+fontSize+fontWeight);
            left.setStyle(fontFamily+fontSize+fontWeight);
            sph.setStyle(fontFamily+fontSize+fontWeight);
            rSphValue.setStyle(fontFamily+fontSize);
            lSphValue.setStyle(fontFamily+fontSize);
            cyl.setStyle(fontFamily+fontSize+fontWeight);
            rCylValue.setStyle(fontFamily+fontSize);
            lCylValue.setStyle(fontFamily+fontSize);
            axis.setStyle(fontFamily+fontSize+fontWeight);
            rAxisValue.setStyle(fontFamily+fontSize);
            lAxisValue.setStyle(fontFamily+fontSize);
            add.setStyle(fontFamily+fontSize+fontWeight);
            rAddValue.setStyle(fontFamily+fontSize);
            lAddValue.setStyle(fontFamily+fontSize);
            ipd.setStyle(fontFamily+fontSize+fontWeight);
            ipdValue.setStyle(fontFamily+fontSize);


            // Create a PrinterJob
            PrinterJob printerJob = PrinterJob.createPrinterJob();

            if (printerJob != null && printerJob.showPrintDialog(null)) {
                // Set up the printing layout
                Node root = pane;
                double scaleX = printerJob.getJobSettings().getPageLayout().getPrintableWidth() / root.getBoundsInParent().getWidth();
                double scaleY = printerJob.getJobSettings().getPageLayout().getPrintableHeight() / root.getBoundsInParent().getHeight();
                Scale scale = new Scale(scaleX, scaleY);
                root.getTransforms().add(scale);

                // Print the content
                boolean success = printerJob.printPage(root);

                if (success) {
                    printerJob.endJob();
                    Function.alert(Alert.AlertType.INFORMATION, "Success", "Bill printed successfully.");
                    if(sceneSwitch==1){
                        try {
                        SceneController.switchScene("BillModule/Bills.fxml", event, "Bills");
                        } catch (IOException e) {
                        throw new RuntimeException(e);
                        }
                    }
                } else {
                    // Print job failed
                    Function.alert(Alert.AlertType.ERROR, "Print Error", "An error occurred while printing.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Function.alert(Alert.AlertType.ERROR, "Print Error", "An error occurred while printing.");
        }

    }

}


