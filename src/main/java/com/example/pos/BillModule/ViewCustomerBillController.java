package com.example.pos.BillModule;

import com.example.pos.CustomerModule.CustomerDB;
import com.example.pos.CustomerModule.LensDB;
import com.example.pos.EmployeeModule.EmployeeDB;
import com.example.pos.ReportModule.ReportController;
import com.example.pos.UtilityClasses.Function;
import com.example.pos.SceneController;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;

public class ViewCustomerBillController implements Initializable {

    String billTime = "";
    String Name = "";

    int customerNameId = 0;
    double credit = 0;

    @FXML
    private TextField billIdField;
    @FXML
    private TextField billDateField;
    @FXML
    private TextField deliveryDateField;
    @FXML
    private TextField customerPhoneField;
    @FXML
    private TextField customerNameField;
    @FXML
    private TextArea customerAddressTextArea;
    @FXML
    private TextField lAdd1Field;
    @FXML
    private TextField lAxis1Field;
    @FXML
    private TextField lCyl1Field;
    @FXML
    private TextField lSph1Field;
    @FXML
    private TextField rAdd1Field;
    @FXML
    private TextField rAxis1Field;
    @FXML
    private TextField rCyl1Field;
    @FXML
    private TextField rSph1Field;
    @FXML
    private TextArea ipd1TextArea;
    @FXML
    private TableView<CustomerBillTable> table;
    @FXML
    private TableColumn<CustomerBillTable, String> productCodeTVColumn;
    @FXML
    private TableColumn<CustomerBillTable, String> productNameTVColumn;
    @FXML
    private TableColumn<CustomerBillTable, Integer> productQuantityTVColumn;
    @FXML
    private TableColumn<CustomerBillTable, Double> productPriceTVColumn;
    @FXML
    private TableColumn<CustomerBillTable, Double> discountPercentageTVColumn;
    @FXML
    private TableColumn<CustomerBillTable, Double> discountTVColumn;
    @FXML
    private TableColumn<CustomerBillTable, Double> productTotalTVColumn;
    @FXML
    private TextField totalBillField;
    @FXML
    private TextField advanceField;
    @FXML
    private TextField finalAmountField;
    @FXML
    private TextField balanceField;
    @FXML
    private TextArea billNoteTextArea;
    @FXML
    private Button addFinalAmountButton;

    @FXML
    private TableView<CustomerBillTable> returnedItemsTable;
    @FXML
    private TableColumn<CustomerBillTable, String> productCodeTVColumn1;
    @FXML
    private TableColumn<CustomerBillTable, String> productNameTVColumn1;
    @FXML
    private TableColumn<CustomerBillTable, Integer> productQuantityTVColumn1;
    @FXML
    private TableColumn<CustomerBillTable, Integer> returnedQuantityTVColumn1;
    @FXML
    private TableColumn<CustomerBillTable, Double> productPriceTVColumn1;
    @FXML
    private TableColumn<CustomerBillTable, Double> discountPercentageTVColumn1;
    @FXML
    private TableColumn<CustomerBillTable, Double> discountTVColumn1;
    @FXML
    private TableColumn<CustomerBillTable, Double> productTotalTVColumn1;
    @FXML
    private Label wholeBillReturnedLabel;
    @FXML
    private Label itemReturnedLabel;
    @FXML
    private Button showReturnedItemsButton;
    @FXML
    private Button returnItemButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Integer viewBillId = 0;

        if(Function.fxmlFileName.equals("BillModule/Bills.fxml" ) || Function.fxmlFileName.equals("CustomerModule/CustomerProfile.fxml") ){
            viewBillId = BillsController.billId;
        }
        else if(Function.fxmlFileName.equals("ReportModule/Report.fxml")){
            viewBillId = ReportController.billId;
        }
        if(viewBillId != 0){
            try{
                int cashierId=-1;
                Connection connection = DriverManager.getConnection(Function.databasePath);
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery("SELECT * FROM "+CustomerBillsDB.customerBillTableName+
                        " WHERE "+CustomerBillsDB.idColumn+" = "+ viewBillId);

                ResultSet rsProducts = statement.executeQuery("SELECT * FROM "+ BillProductsDB.billProductsTableName+" WHERE "
                +BillProductsDB.billIdColumn+ " = "+viewBillId);

                if (rs.next()){

                    cashierId = rs.getInt(CustomerBillsDB.employeeIdColumn);
                    billTime = rs.getString(CustomerBillsDB.timeColumn);
                    billIdField.setText(String.valueOf(rs.getInt(CustomerBillsDB.idColumn)));
                    billDateField.setText(rs.getString(CustomerBillsDB.dateColumn));
                    deliveryDateField.setText(rs.getString(CustomerBillsDB.deliveryDateColumn));
                    totalBillField.setText(String.valueOf(rs.getDouble("billTotal")));
                    advanceField.setText(String.valueOf(rs.getDouble(CustomerBillsDB.advanceColumn)));
                    finalAmountField.setText(String.valueOf(rs.getDouble("finalAmount")));
                    balanceField.setText(String.valueOf(rs.getDouble(CustomerBillsDB.balanceColumn)));
                    billNoteTextArea.setText(rs.getString(CustomerBillsDB.noteColumn));


                    if(rs.getInt("customerNameId") != -1){
                        ResultSet cs = statement.executeQuery("SELECT * FROM "+ CustomerDB.customerTableName+
                                " c JOIN CustomerName cn ON c.customerId = cn.customerId WHERE cn.customerNameId ="+ rs.getInt("customerNameId"));
                        if (cs.next()){
                            customerPhoneField.setText(cs.getString(CustomerDB.phoneNumberColumn));
                            customerNameField.setText(cs.getString("customerName"));
                            customerAddressTextArea.setText(cs.getString("customerAddress"));
                            customerNameId = cs.getInt("customerNameId");
                            credit = cs.getDouble("customerCredit");

                            if (cs.getInt("lensId")!=0){
                                ResultSet ls = statement.executeQuery("SELECT * FROM "+ LensDB.lensTableName+
                                        " WHERE "+LensDB.lensIdColumn+" = "+ cs.getInt("lensId"));
                                if (ls.next()){
                                    initializeLensBoxes(ls, rSph1Field, rCyl1Field, rAxis1Field, rAdd1Field, lSph1Field, lCyl1Field, lAxis1Field, lAdd1Field, ipd1TextArea);
                                }
                                ls.close();
                            }
                        }
                        cs.close();
                    }

                    productCodeTVColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
                    productNameTVColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
                    productQuantityTVColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
                    productPriceTVColumn.setCellValueFactory(new PropertyValueFactory<>("retailPrice"));
                    discountPercentageTVColumn.setCellValueFactory(new PropertyValueFactory<>("discountPercentage"));
                    discountTVColumn.setCellValueFactory(new PropertyValueFactory<>("discount"));
                    productTotalTVColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

                    productCodeTVColumn1.setCellValueFactory(new PropertyValueFactory<>("code"));
                    productNameTVColumn1.setCellValueFactory(new PropertyValueFactory<>("name"));
                    productQuantityTVColumn1.setCellValueFactory(new PropertyValueFactory<>("quantity"));
                    returnedQuantityTVColumn1.setCellValueFactory(new PropertyValueFactory<>("returnedQuantity"));
                    productPriceTVColumn1.setCellValueFactory(new PropertyValueFactory<>("retailPrice"));
                    discountPercentageTVColumn1.setCellValueFactory(new PropertyValueFactory<>("discountPercentage"));
                    discountTVColumn1.setCellValueFactory(new PropertyValueFactory<>("discount"));
                    productTotalTVColumn1.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

//                    double totalBill = 0.00;

                    boolean returnedItemFlag = false;
                    int completeProductReturnFlag=0;
                    int productCount=0;

                    while (rsProducts.next()){
                        productCount++;
                        if(rsProducts.getInt("returnedQuantity")>0){
                            returnedItemFlag = true;

                            returnedItemsTable.getItems().add(new CustomerBillTable(rsProducts.getString(BillProductsDB.codeColumn),
                                    rsProducts.getString(BillProductsDB.nameColumn), rsProducts.getInt(BillProductsDB.quantityColumn),
                                    rsProducts.getInt("returnedQuantity"), rsProducts.getDouble(BillProductsDB.retailPriceColumn), rsProducts.getDouble("purchasePrice") , rsProducts.getDouble("discount"),
                                    rsProducts.getDouble(BillProductsDB.retailPriceColumn)*(rsProducts.getDouble("discount")/100),false,
                                    ( ( rsProducts.getDouble(BillProductsDB.retailPriceColumn) - ( rsProducts.getDouble(BillProductsDB.retailPriceColumn)*(rsProducts.getDouble("discount")/100) ) ) * rsProducts.getInt(BillProductsDB.quantityColumn))));
                        }

                        if(rsProducts.getInt("returnedQuantity") == rsProducts.getInt("quantity")){
                            completeProductReturnFlag++;
                        }

                        double discount = rsProducts.getDouble(BillProductsDB.retailPriceColumn) * (rsProducts.getDouble("discount")/100);
                        //Calculating total bill
//                        totalBill = totalBill + (rsProducts.getDouble(BillProductsDB.retailPriceColumn)
//                                * rsProducts.getInt(BillProductsDB.quantityColumn) - discount*rsProducts.getInt(BillProductsDB.quantityColumn));
                        //initializing products in table
                        table.getItems().add(new CustomerBillTable(rsProducts.getString(BillProductsDB.codeColumn),
                                rsProducts.getString(BillProductsDB.nameColumn), rsProducts.getInt(BillProductsDB.quantityColumn),
                                rsProducts.getDouble(BillProductsDB.retailPriceColumn), rsProducts.getDouble("purchasePrice") ,rsProducts.getDouble("discount"),discount
                                ,false, (rsProducts.getDouble(BillProductsDB.retailPriceColumn)*rsProducts.getInt(BillProductsDB.quantityColumn) - discount*rsProducts.getInt(BillProductsDB.quantityColumn))));
                    }
//                    totalBillField.setText(String.valueOf(totalBill));

                    if(returnedItemFlag){
                        itemReturnedLabel.setVisible(true);
                        showReturnedItemsButton.setVisible(true);
                        table.setPrefHeight(355);
                        table.setLayoutY(425);
                    }

                    if( (completeProductReturnFlag == productCount) && (completeProductReturnFlag != 0) ){
                        itemReturnedLabel.setVisible(false);
                        showReturnedItemsButton.setVisible(false);
                        wholeBillReturnedLabel.setVisible(true);
                        returnItemButton.setVisible(false);
                        table.setPrefHeight(355);
                        table.setLayoutY(425);
                    }


                    //Ob products
                    int isOb = rs.getInt("hasOBProd");
                    int billId = rs.getInt("cBillId");
                    if (isOb == 1){
                        String query = "Select * From OrderBasedProducts Where cBillId = "+ billId;
                        ResultSet obSet = statement.executeQuery(query);
                        while (obSet.next()){
                            double discount = obSet.getDouble("retailPrice") * (obSet.getDouble("discountPercentage")/100);
//                            totalBill += obSet.getDouble("retailPrice") * obSet.getInt("productQuantity") - discount*obSet.getInt("productQuantity");
                            table.getItems().add(new CustomerBillTable(String.valueOf("OB-"+obSet.getInt("productCode")), obSet.getString("productName"),
                                    obSet.getInt("productQuantity"), obSet.getDouble("retailPrice"), obSet.getDouble("purchasePrice"),obSet.getDouble("discountPercentage"),
                                    discount, true, (obSet.getDouble("retailPrice")*obSet.getInt("productQuantity") - discount*obSet.getInt("productQuantity"))
                                    ));
                        }
//                        totalBillField.setText(String.valueOf(totalBill));
                    }

                    if(rs.getDouble(CustomerBillsDB.balanceColumn)==0.0){
                        addFinalAmountButton.setVisible(false);
                    }


                }

                rs.close();
                rsProducts.close();

                if(cashierId==0){
                    // Cashier Name
                    Name = "M.Muzammil";
                }
                else if(cashierId>0) {
                    // Cashier Name
                    ResultSet em = statement.executeQuery("Select Employee_Name from Employee where employeeId = " + cashierId);
                    if (em.next()) {
                        Name = em.getString(EmployeeDB.employeeNameColumn);
                    }
                    em.close();
                }

                statement.close();
                connection.close();
            }
            catch (Exception e){
                System.out.println(e.getMessage());
                Function.alert(Alert.AlertType.ERROR,"Error","An Error Occurred");
            }

        }
    }

    @FXML
    void onShowReturnedItemsClicked(ActionEvent event) {
        if(returnedItemsTable.isVisible()){
            table.setVisible(true);
            returnedItemsTable.setVisible(false);
            showReturnedItemsButton.setText("Show Returned Items");

        } else if(!returnedItemsTable.isVisible()){
            table.setVisible(false);
            returnedItemsTable.setVisible(true);
            showReturnedItemsButton.setText("Show All Items");
        }
    }

    @FXML
    void onAddFinalAmountClicked(ActionEvent event) throws IOException {
        Stage dialog = new Stage();
        Scene scene;
        Parent root;
        AddFinalAmountController.balanceAmount = Double.parseDouble(balanceField.getText());
        AddFinalAmountController.customerBillId = Integer.parseInt(billIdField.getText());
        AddFinalAmountController.customerName = customerNameField.getText();
        AddFinalAmountController.customerPhoneNo = customerPhoneField.getText();

        root = FXMLLoader.load(SceneController.class.getResource("BillModule/AddFinalAmount.fxml"));

        //This is setting the value of the static variable viewCustomerBillController
        AddFinalAmountController.setViewCustomerBillController(this);

        dialog.initModality(Modality.APPLICATION_MODAL);
        scene = new Scene(root);
        dialog.setScene(scene);
        dialog.initOwner(SceneController.stage);
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.resizableProperty().set(false);
        dialog.setTitle("Add Final Amount");
        dialog.showAndWait();
    }

    @FXML
    void onReturnItemClicked(ActionEvent event) throws IOException, SQLException {

        if (table.getSelectionModel().isEmpty()) {
            int choice = JOptionPane.showConfirmDialog(null, "No item is selected. Do you want to return the whole bill? Click OK to return the whole bill or select an item before clicking the button.", "Confirmation", JOptionPane.OK_CANCEL_OPTION);
            if (choice == JOptionPane.OK_OPTION) {
                System.out.println("Hi! How are you doing?");
                ObservableList<CustomerBillTable> productsList = table.getItems();
                int returnedQuantity = 0;

                Connection connection = DriverManager.getConnection(Function.databasePath);
                Statement statement = connection.createStatement();

                for(int i=0; i<productsList.size(); i++) {
                    if (productsList.get(i).isOBProduct()) {
                        ResultSet rs = statement.executeQuery("Select * from BillProducts where (( cBillId = " + billIdField.getText().trim() +
                                " AND productCode =  '" + productsList.get(i).getCode() + "' ) AND retailPrice = " + productsList.get(i).getRetailPrice() +
                                ") AND discount = " + productsList.get(i).getDiscountPercentage());

                        if (rs.next()) {
                            returnedQuantity = rs.getInt("returnedQuantity");
                        }

                        returnItem(productsList.get(i), productsList.get(i).getQuantity() - returnedQuantity, productsList.get(i).getQuantity());
                    }
                }

                statement.close();
                connection.close();

                Function.alert(Alert.AlertType.INFORMATION, "Congratulations", "Whole Bill Returned Successfully!!");

                itemReturnedLabel.setVisible(false);
                showReturnedItemsButton.setVisible(false);
                wholeBillReturnedLabel.setVisible(true);
                returnItemButton.setVisible(false);
                table.setPrefHeight(355);
                table.setLayoutY(425);

            }
        } else {
            CustomerBillTable product = table.getSelectionModel().getSelectedItem();
            int returnedQuantity = 0;

            if(!product.isOBProduct()){
            Connection connection = DriverManager.getConnection(Function.databasePath);
            Statement statement = connection.createStatement();

            ResultSet rs = statement.executeQuery("Select * from BillProducts where (( cBillId = " + billIdField.getText().trim() +
                    " AND productCode =  '" + product.getCode() + "' ) AND retailPrice = " + product.getRetailPrice() +
                    ") AND discount = " + product.getDiscountPercentage() );
            if (rs.next()){
                returnedQuantity = rs.getInt("returnedQuantity");
            }
            statement.close();
            connection.close();

            if(product.getQuantity() > returnedQuantity){

                if(product.getQuantity()-returnedQuantity > 1){

                    Stage dialog = new Stage();
                    Scene scene;
                    Parent root;

                    ReturnItemQuantityController.product = product;
                    ReturnItemQuantityController.returnedQuantity = returnedQuantity;

                    root = FXMLLoader.load(SceneController.class.getResource("BillModule/ReturnItemQuantity.fxml"));

                    //This is setting the value of the static variable viewCustomerBillController
                    ReturnItemQuantityController.setViewCustomerBillController(this);

                    dialog.initModality(Modality.APPLICATION_MODAL);
                    scene = new Scene(root);
                    dialog.setScene(scene);
                    dialog.initOwner(SceneController.stage);
                    dialog.initStyle(StageStyle.UNDECORATED);
                    dialog.resizableProperty().set(false);
                    dialog.setTitle("Select The Quantity");
                    dialog.showAndWait();

                } else if(product.getQuantity() - returnedQuantity == 1){
                    returnItem(product,1,product.getQuantity());
                    Function.alert(Alert.AlertType.INFORMATION, "Congratulations", "Item Returned Successfully!!");
                }

            }else if(product.getQuantity() == returnedQuantity){
                Function.alert(Alert.AlertType.INFORMATION, "Information!", "This item has already been returned.");
            }


            }
        }
    }

    @FXML
    void onPrintBillClicked(ActionEvent event) {

        CustomerBillController CBController = new CustomerBillController();
        CBController.printBill(event,billIdField.getText().trim(),billDateField.getText().trim(), billTime,deliveryDateField.getText().trim(),
                customerPhoneField.getText().trim(),customerNameField.getText().trim(),Name,totalBillField.getText().trim(),advanceField.getText().trim(),
                finalAmountField.getText().trim(),balanceField.getText().trim(),rSph1Field.getText().trim(), rCyl1Field.getText().trim(),
                rAxis1Field.getText().trim(),rAdd1Field.getText().trim(),lSph1Field.getText().trim(),lCyl1Field.getText().trim(),
                lAxis1Field.getText().trim(),lAdd1Field.getText().trim(),ipd1TextArea.getText().trim(),table,0);
    }

    @FXML
    void onCloseBillClicked(ActionEvent event) {
        try {
            SceneController.switchScene(Function.fxmlFileName,event,Function.title);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void initializeLensBoxes(ResultSet ls, TextField rSph2, TextField rCyl2, TextField rAxis2, TextField rAdd2, TextField lSph2, TextField lCyl2, TextField lAxis2, TextField lAdd2, TextArea ipd2) throws SQLException {
        rSph2.setText(ls.getString(LensDB.rSphColumn));
        rCyl2.setText(ls.getString(LensDB.rCylColumn));
        rAxis2.setText(ls.getString(LensDB.rAxisColumn));
        rAdd2.setText(ls.getString(LensDB.rAddColumn));
        lSph2.setText(ls.getString(LensDB.lSphColumn));
        lCyl2.setText(ls.getString(LensDB.lCylColumn));
        lAxis2.setText(ls.getString(LensDB.lAxisColumn));
        lAdd2.setText(ls.getString(LensDB.lAddColumn));
        ipd2.setText(ls.getString(LensDB.ipdColumn));
    }

    public void setFinalAmountField(String finalAmount) {
        finalAmountField.setText(finalAmount);
    }

    public void setFinalAmountButtonInvisible(){
        addFinalAmountButton.setVisible(false);
    }

    public void setBalanceField(String balanceAmount) {
        balanceField.setText(balanceAmount);
    }

    void returnItem(CustomerBillTable product, int quantityBeingReturned ,int newReturnedQuantity){

        try {
            Connection connection = DriverManager.getConnection(Function.databasePath);
            Statement statement = connection.createStatement();

            if(!product.isOBProduct()) {
                //Updating the returnedQuantity column in BillProducts Table
                statement.execute("Update BillProducts set returnedQuantity = " + newReturnedQuantity + " where (( cBillId = " + billIdField.getText().trim() + " AND productCode =  '" +
                        product.getCode() + "' ) AND retailPrice = " + product.getRetailPrice()+") AND discount = "+product.getDiscountPercentage() );

                //Updating the stock
                ResultSet rs = statement.executeQuery("Select * from Inventory where productCode = '" + product.getCode() + "' ");
                int stock = -1;
                if (rs.next()) {
                    stock = rs.getInt("stock");
                }

                if (stock > -1) {
                    stock = stock + quantityBeingReturned;
                    statement.execute("Update Inventory set stock = " + stock + " where productCode = '" + product.getCode() + "' ");
                }
            }

            //Updating the Balance and Credit
            double balance = Double.parseDouble(balanceField.getText().trim());
            if(balance > 0){
                double difference = balance - ( (product.getRetailPrice() - (product.getRetailPrice() * (product.getDiscountPercentage()/100) ) ) * quantityBeingReturned ) ;
                if(difference > 0){
                    balance = difference;
                    balanceField.setText(Double.toString(balance));
                    statement.execute("Update CustomerBills set billBalance = "+balance+" where cBillId = "+billIdField.getText().trim());
                    credit = credit -  ( (product.getRetailPrice() - (product.getRetailPrice() * (product.getDiscountPercentage()/100) ) ) * quantityBeingReturned ) ;
                    statement.execute("Update CustomerName set customerCredit = "+credit+" where customerNameId = "+customerNameId);

                } else if(difference <= 0){
                    credit = credit - balance;
                    balance = 0.0;
                    balanceField.setText(Double.toString(balance));
                    statement.execute("Update CustomerBills set billBalance = "+balance+" where cBillId = "+billIdField.getText().trim());
                    statement.execute("Update CustomerName set customerCredit = "+credit+" where customerNameId = "+customerNameId);
                }
            }

            product.setReturnedQuantity(newReturnedQuantity);

            ResultSet rsProducts = statement.executeQuery("SELECT * FROM "+BillProductsDB.billProductsTableName+" WHERE "
                    +BillProductsDB.billIdColumn+ " = "+billIdField.getText().trim());

            boolean returnedItemFlag = false;
            int completeProductReturnFlag=0;
            int productCount=0;

            returnedItemsTable.getItems().clear();

            while (rsProducts.next()){
                productCount++;
                if(rsProducts.getInt("returnedQuantity")>0){
                    returnedItemFlag = true;
                    returnedItemsTable.getItems().add(new CustomerBillTable(rsProducts.getString(BillProductsDB.codeColumn),
                            rsProducts.getString(BillProductsDB.nameColumn), rsProducts.getInt(BillProductsDB.quantityColumn),
                            rsProducts.getInt("returnedQuantity"), rsProducts.getDouble(BillProductsDB.retailPriceColumn), rsProducts.getDouble("purchasePrice"),rsProducts.getDouble("discount"),
                            rsProducts.getDouble(BillProductsDB.retailPriceColumn)*(rsProducts.getDouble("discount")/100),
                            false, (rsProducts.getDouble(BillProductsDB.retailPriceColumn) * rsProducts.getInt(BillProductsDB.quantityColumn))));
                }

                if(rsProducts.getInt("returnedQuantity") == rsProducts.getInt("quantity")){
                    completeProductReturnFlag++;
                }
            }

            if(returnedItemFlag){
                itemReturnedLabel.setVisible(true);
                showReturnedItemsButton.setVisible(true);
                table.setPrefHeight(355);
                table.setLayoutY(425);
            }

            if((completeProductReturnFlag == productCount) && (completeProductReturnFlag != 0)){
                itemReturnedLabel.setVisible(false);
                showReturnedItemsButton.setVisible(false);
                wholeBillReturnedLabel.setVisible(true);
                returnItemButton.setVisible(false);
                addFinalAmountButton.setLayoutY(550);
                table.setPrefHeight(355);
                table.setLayoutY(425);
            }

            statement.close();
            connection.close();

            if(Double.parseDouble(balanceField.getText().trim()) == 0.0){
                addFinalAmountButton.setVisible(false);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
