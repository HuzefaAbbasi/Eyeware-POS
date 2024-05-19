package com.example.pos.VendorModule;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class VendorBillController implements Initializable{

//    @FXML
//    private DatePicker billDate;
//
//    @FXML
//    private TextField advanceAmountField;
//
//    @FXML
//    private TextField balanceAmountField;
//
//    @FXML
//    private TextField invoiceNoField;
//
//    @FXML
//    private TableView<CustomerBillTable> table;
//
//    @FXML
//    private TableColumn<CustomerBillTable, String> pCode;
//
//    @FXML
//    private TableColumn<CustomerBillTable, String> pName;
//
//    @FXML
//    private TableColumn<CustomerBillTable, Double> pPrice;
//
//    @FXML
//    private TableColumn<CustomerBillTable, Integer> pQuantity;
//
//    @FXML
//    private TableColumn<CustomerBillTable, Double> pTotal;
//
//    @FXML
//    private TextField price;
//
//    @FXML
//    private TextField productName;
//
//    @FXML
//    private TextField productcode;
//
//    @FXML
//    private TextField quantity;
//
//    @FXML
//    private TextField totalBillField;
//
//    @FXML
//    private TextArea billNote;
//
//
//    ResultSet rs;
//    ResultSet crs;
//    double total = 0;
//    double prevCredit = 0;
//    boolean newCustomer = true;
//    boolean customerBox1 = false;
//    boolean customerBox2 = false;
//
//    int lensId1;
//    int lensId2;
//    boolean search = false;
//    boolean customerPhoneVerify = true;
//    int invoiceId = 1;
//    ArrayList<Supplies> supplies = new ArrayList<>();
//    ArrayList<CustomerBillDB> productList = new ArrayList<>();
//
    @FXML
    void onCancelClicked(ActionEvent event) {
//        int choice = JOptionPane.showConfirmDialog(null, "Do you really want to cancel Bill?", "Confirmation", JOptionPane.OK_CANCEL_OPTION);
//        if (choice == JOptionPane.OK_OPTION){
//            clearPage();
//        }
    }


    @FXML
    void onRemoveClicked(ActionEvent event) {
//        if(table.getSelectionModel().isEmpty()){
//            Function.alert(Alert.AlertType.ERROR,"Error","No Record Selected!!!");
//        }else{
//            CustomerBillTable product = table.getSelectionModel().getSelectedItem();
//            total = total - product.getTotalPrice();
//            totalBillField.setText(String.valueOf(total));
//            table.getItems().remove(product);
//
//
//            // The reason for separate remove methods is that each record is not entered in supplies list, so they
//            // may differ in size
//
//            //Removing from products list
//            for (int i = 0; i < productList.size(); i++) {
//                if (productList.get(i).getCode().equals(product.getCode())) {
//                    productList.remove(i);
//                }
//            }
//
//            // Removing from supplies list
//            for (int i = 0; i < supplies.size(); i++) {
//                if (supplies.get(i).getProductCode().equals(product.getCode())) {
//                    supplies.remove(i);
//                }
//            }
//
//            if(productList.isEmpty()){
//                advanceAmountField.clear();
//                advanceAmountField.setEditable(false);
//                balanceAmountField.clear();
//            }
//        }

    }
    @FXML
    void onSaveClicked(ActionEvent event) throws SQLException {
//        boolean checkAdvance;
//        checkAdvance = Function.checkForDecimal(advanceAmountField.getText());
//        //For Verifying User has selected date
//        if (billDate.getValue() != null) {
//            //For checking list is not empty
//            if (!productList.isEmpty()) {
//                //To verify advance amount format
//                if (checkAdvance) {
//                    //For verifying that advance amount is less than total amount
//                    if (Double.parseDouble(totalBillField.getText()) >= Double.parseDouble(advanceAmountField.getText())) {
//                        //Logic
//                        //Strings for queries.
//                        balanceAmountField.setText(String.valueOf(Double.parseDouble(totalBillField.getText())
//                                - Double.parseDouble(advanceAmountField.getText())));
//                        Double credit = Double.parseDouble(totalBillField.getText()) - Double.parseDouble(advanceAmountField.getText());
//                        try {
//                            Connection connection = DriverManager.getConnection(Function.databasePath);
//                            Statement stBill = connection.createStatement();
//
//                            for (int i = 0; i < supplies.size(); i++) {
//                                Supplies obj = supplies.get(i);
//                                String suppliesQuery = " Insert Into " + SuppliesDB.tableName + " ( " + SuppliesDB.vendorIdColumn
//                                        + ", " + SuppliesDB.productCodeColumn + ") Values ( " + 0 + ", " + obj.getProductCode() + ")";
//
//                                stBill.execute(suppliesQuery);
//                            }
//                            for (int i = 0; i < productList.size(); i++) {
//                                CustomerBillDB obj = productList.get(i);
//                                String queryBillProducts = " Insert Into " + VendorBillProductsDB.tableName + " ( " + VendorBillProductsDB.billProductIdColumn
//                                        + ", " + VendorBillProductsDB.billIdColumn + ", "+VendorBillProductsDB.codeColumn + ", "+VendorBillProductsDB.nameColumn
//                                        + ", "+VendorBillProductsDB.quantityColumn + ", "+VendorBillProductsDB.purchasePriceColumn +") Values ( "
//                                        + i+1 + ", " + invoiceId + ", '" + obj.getCode()+ "', '" +obj.getName()+ "', " +obj.getQuantity()+ ", " +obj.getPurchasePrice()+ ")";
//
//                                stBill.execute(queryBillProducts);
//                            }
//
//                            String queryBill = "Insert Into " + VendorBillDB.tableName + "(" + VendorBillDB.idColumn + ", "
//                                    + VendorBillDB.billDateColumn + ", " + VendorBillDB.billAdvanceColumn + ", " + VendorBillDB.billBalanceColumn + ", "
//                                    + VendorBillDB.billNoteColumn + ", " + VendorBillDB.vendorIdColumn + ") Values ("
//                                    + Integer.parseInt(invoiceNoField.getText()) + ", '" +
//                                    billDate.getValue().toString() +
//                                    "', " + Double.parseDouble(advanceAmountField.getText()) + "," +
//                                    Double.parseDouble(balanceAmountField.getText()) + ",'" + billNote.getText() + "', " + 0 + ")";
//                            stBill.execute(queryBill);
//
//                            Function.alert(Alert.AlertType.INFORMATION, "Success", "Bill saved successfully.");
//                            //To clear whole page
//                            clearPage();
//                            invoiceId++;
//                            invoiceNoField.setText(Integer.toString(invoiceId));
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    else {
//                        Function.alert(Alert.AlertType.ERROR, "Error", "Advance amount more than total amount");
//                        advanceAmountField.clear();
//                    }
//                }
//                else {
//                Function.alert(Alert.AlertType.ERROR, "Error", "Enter right Amount in Advance Field.");
//                advanceAmountField.clear();
//                }
//            } else {
//                Function.alert(Alert.AlertType.ERROR, "Error", "NO Product Added in Bill");
//            }
//        }
//        else{
//            Function.alert(Alert.AlertType.ERROR,"Error","Please Select Bill Date.");
//        }

    }
//
    @FXML
    void productCodeKeyDown(KeyEvent event) throws IOException, SQLException {
//        if(event.getCode().equals(KeyCode.ENTER)){
//            if(!productcode.getText().isEmpty()){
//                if(productcode.getText().equals("*")){
//
//                    Stage dialog = new Stage();
//                    Scene scene2;
//                    Parent root2;
//                    Function.fxmlFileName = "VendorBill.fxml";
//                    Function.title = "Vendor Bill";
//                    productcode.clear();
//                    search = true;
//
//                    root2 = FXMLLoader.load(SceneController.class.getResource("SearchProduct.fxml"));
//                    dialog.initModality(Modality.APPLICATION_MODAL);
//                    scene2 = new Scene(root2);
//                    dialog.setScene(scene2);
//                    dialog.initOwner(SceneController.stage);
//                    dialog.setTitle("Vendor Bill");
//                    dialog.showAndWait();
//
//                    String productCode = SearchProductController.productCode;
//                    if (productCode != null && search ){
//                        productcode.setText(productCode);
//                    }
//
////                    SceneController.switchScene("SearchProduct.fxml",event,"Search Product", Modality.APPLICATION_MODAL);
//                }
//                else{
//                    quantity.setEditable(true);
//                    Connection connection = DriverManager.getConnection(Function.databasePath);
//                    String query = "Select * From "+ProductDB.productTableName+" Where "+ProductDB.productCodeColumn+" = '" + productcode.getText() + "'";
//                    Statement statement = connection.createStatement();
//                    rs = statement.executeQuery(query);
//                    statement.close();
//                    connection.close();
//
//                    if(!rs.next()){
//                        Function.alert(Alert.AlertType.ERROR,"Error","No Product Exists for this Code.");
//                        productcode.clear();
//                    }
//                    else{
//                        productName.setText(rs.getString(ProductDB.productNameColumn));
//                        productcode.setEditable(false);
//                        quantity.setEditable(true);
//                        quantity.requestFocus();
//                    }
//                }
//            }
//            else {
//                if (!productList.isEmpty()){
//                    advanceAmountField.requestFocus();
//                }
//                else {
//                    Function.alert(Alert.AlertType.ERROR,"Error","NO Product Added in Bill");
//                }
//            }
//        }

    }
    @FXML
    void onQuantityKeyDown(KeyEvent event){
//        if(event.getCode().equals(KeyCode.ENTER)){
//            if(!quantity.getText().isEmpty()){
//                boolean flag;
//                flag = Function.checkForInt(quantity.getText());
//                try{
//                    if (!flag){
//                        Function.alert(Alert.AlertType.ERROR,"Error","Please Enter Only Numbers in Quantity Box");
//                        quantity.clear();
//                    }
//                    else {
//                        price.setText(String.valueOf(rs.getDouble(ProductDB.productPurchasePriceColumn)));
//                        quantity.setEditable(false);
//                        pCode.setCellValueFactory(new PropertyValueFactory<>("code"));
//                        pName.setCellValueFactory(new PropertyValueFactory<>("name"));
//                        pQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
//                        pPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
//                        pTotal.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
//
//
////                       For Checking if same product already exists with same price.
//                        CustomerBillDB billDB = null;
//                        CustomerBillTable billItem = null;
//                        int rowIndexToUpdate = -1;
//
//                        //looping through table to find if a product with same code already exists.
//                        for (int i = 0; i < table.getItems().size(); i++) {
//                            if (table.getItems().get(i).getCode().equals(productcode.getText())) {
//                                billItem = table.getItems().get(i);
//                                billDB = productList.get(i);
//                                rowIndexToUpdate = i;
//                                break;
//                            }
//                        }
//                        //if exists..
//                        if (billItem != null && rowIndexToUpdate >= 0) {
//                            // Update the relevant value in the item object
//                            billItem.setQuantity(billItem.getQuantity() + Integer.parseInt(quantity.getText()));
//                            billItem.setTotalPrice(billItem.getTotalPrice() +
//                                    Integer.parseInt(quantity.getText()) * Double.parseDouble(price.getText()));
//
//                            // Update the TableView to reflect the changes
//                            table.getItems().set(rowIndexToUpdate, billItem);
//
//                            // updating list respectively
//                            billDB.setQuantity(billDB.getQuantity() + Integer.parseInt(quantity.getText()));
//                            productList.set(rowIndexToUpdate, billDB);
//                        } else {
//                            table.getItems().add(new CustomerBillTable(productcode.getText(), productName.getText(),
//                                    Integer.parseInt(quantity.getText()), Double.parseDouble(price.getText())
//                                    , Integer.parseInt(quantity.getText()) * Double.parseDouble(rs.getString(ProductDB.productPurchasePriceColumn))));
//
//                            //verifying that record not exist already.
//                            String supplyCheckQuery = "SELECT * FROM "+ SuppliesDB.tableName + " WHERE " +
//                                    SuppliesDB.vendorIdColumn + " = " + 0 + " AND " + SuppliesDB.productCodeColumn +
//                                    " = " + productcode.getText();
//
//                            Connection connection = DriverManager.getConnection(Function.databasePath);
//                            Statement statement = connection.createStatement();
//                            ResultSet rsSupply = statement.executeQuery(supplyCheckQuery);
//                            //if no record exists.
//                            if (!rsSupply.next()){
//                                // Adding to supplies list
//                                supplies.add(new Supplies(0, productcode.getText()));
//                            }
//                            // Adding to products list
//                            productList.add(new CustomerBillDB(productcode.getText(), productName.getText(), Integer.parseInt(quantity.getText()),
//                                    rs.getDouble(ProductDB.productPurchasePriceColumn), Double.parseDouble(price.getText())));
//                        }
//
//                        total += Double.parseDouble(price.getText()) * Integer.parseInt(quantity.getText());
//                        totalBillField.setText(String.valueOf(total));
//
//                        productcode.clear();
//                        productName.clear();
//                        quantity.clear();
//                        price.clear();
//
//                        productcode.setEditable(true);
//                        advanceAmountField.setEditable(true);
//                        productcode.requestFocus();
//                    }
//                }
//                catch (Exception e){
//                    Function.alert(Alert.AlertType.ERROR,"Error",e.getMessage());
//                }
//            }
//            else {
//                Function.alert(Alert.AlertType.ERROR,"Error","Quality Field Empty");
//            }
//        }
    }
//
//
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        try{
//
//            advanceAmountField.setText("0");
//
//            billDate.setDayCellFactory(picker -> new DateCell() {
//                @Override
//                public void updateItem(LocalDate date, boolean empty) {
//                    super.updateItem(date, empty);
//                    LocalDate today = LocalDate.now();
//                    setDisable(empty || date.compareTo(today) > 0 );
//                }
//            });
//
//            Connection connection = DriverManager.getConnection(Function.databasePath);
//            Statement statement1 = connection.createStatement();
//            ResultSet set = statement1.executeQuery("SELECT MAX("+VendorBillDB.idColumn+") FROM "+VendorBillDB.tableName);
//            if(!set.next()){
//                invoiceNoField.setText(String.valueOf(invoiceId));
//            }
//            else {
//                invoiceId  = set.getInt(1)+1;
//                invoiceNoField.setText(String.valueOf(invoiceId));
//            }
//        }
//        catch (Exception e){
//            System.out.println(e.getMessage());
//        }
    }
//
//
    @FXML
    void onAdvanceKeyDown(ActionEvent event) {
//        boolean checkAdvance;
//        checkAdvance = Function.checkForDecimal(advanceAmountField.getText());
//        if(checkAdvance){
//            if(Double.parseDouble(totalBillField.getText())<Double.parseDouble(advanceAmountField.getText())){
//                Function.alert(Alert.AlertType.ERROR,"Error","Advance amount more than total amount");
//                advanceAmountField.clear();
//            }
//            else {
//                balanceAmountField.setText(String.valueOf(Double.parseDouble(totalBillField.getText())-Double.parseDouble(advanceAmountField.getText())));
//            }
//        }
//        else{
//            Function.alert(Alert.AlertType.ERROR,"Error","Advance amount not in correct format");
//            advanceAmountField.clear();
//        }
    }
//
//
    public void clearPage(){
//        table.getItems().clear();
//        productList.clear();
//        billDate.setValue(null);
//        totalBillField.setText("0");
//        advanceAmountField.setText("0");
//        balanceAmountField.clear();
//        billNote.clear();
//        total = 0.00;
    }

}


