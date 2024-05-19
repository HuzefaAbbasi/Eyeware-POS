package com.example.pos.CustomerModule;

import com.example.pos.BillModule.Bill;
import com.example.pos.BillModule.BillProductsDB;
import com.example.pos.BillModule.BillsController;
import com.example.pos.BillModule.CustomerBillsDB;
import com.example.pos.UtilityClasses.Function;
import com.example.pos.SceneController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Arrays;
import java.util.ResourceBundle;

import static com.example.pos.UtilityClasses.Function.*;

public class CustomerProfileController implements Initializable {

    int customerId, lensId;

    private String phoneNumber,name,address;
    private String sphRight,sphLeft,cylRight,cylLeft,axisRight,axisLeft,addRight,addLeft,ipd;

    @FXML
    private Button backButton,updateButton,removeButton,updateLensButton;

    @FXML
    private TextField customerNameField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private TextField creditField;
    @FXML
    private TextArea addressTexArea;

    @FXML
    private TableView<Bill> table;
    @FXML
    private TableColumn<Bill, Integer> billIdTVColumn;
    @FXML
    private TableColumn<Bill, String> billDateTVColumn;
    @FXML
    private TableColumn<Bill, String> deliveryDateTVColumn;
    @FXML
    private TableColumn<Bill, Double> totalBillTVColumn;
    @FXML
    private TableColumn<Bill, Double> advanceTVColumn;
    @FXML
    private TableColumn<Bill, Double> finalAmountTVColumn;
    @FXML
    private TableColumn<Bill, Double> balanceTVColumn;
    @FXML
    private TableColumn<Bill, Button> viewButtonTVColumn;

    @FXML
    private TextField addLeft1Field;
    @FXML
    private TextField addRight1Field;
    @FXML
    private TextField axisLeft1Field;
    @FXML
    private TextField axisRight1Field;
    @FXML
    private TextField cylLeft1Field;
    @FXML
    private TextField cylRight1Field;
    @FXML
    private TextField sphLeft1Field;
    @FXML
    private TextField sphRight1Field;
    @FXML
    private TextArea ipd1TextArea;

    @FXML
    private Button updateCustomerButton,cancelCustomerButton,updateLensNoButton,cancelLensNoButton;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try{
            if (CustomerController.customerNameId != 0){
                Connection connection = DriverManager.getConnection(Function.databasePath);
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery("SELECT * FROM "+CustomerDB.customerTableName+
                        " c JOIN CustomerName cn ON c.customerId = cn.customerId WHERE cn.customerNameId ="+ CustomerController.customerNameId);
                if (rs.next()){
                    customerId = rs.getInt("customerId");
                    customerNameField.setText(rs.getString("customerName"));
                    phoneNumberField.setText(rs.getString("customerNo"));
                    creditField.setText(String.valueOf(rs.getDouble("customerCredit")));
                    addressTexArea.setText(rs.getString("customerAddress"));

                    if (rs.getInt("lensId")!=0){
                        lensId = rs.getInt("lensId");
                        ResultSet ls = statement.executeQuery("SELECT * FROM "+ LensDB.lensTableName+ " WHERE "+LensDB.lensIdColumn+" = "+ lensId ) ;
                        if (ls.next()){
                            initializeLensBoxes(ls, sphRight1Field, cylRight1Field, axisRight1Field, addRight1Field, sphLeft1Field, cylLeft1Field, axisLeft1Field, addLeft1Field, ipd1TextArea);
                        }
                        ls.close();
                    }


                    String query = "SELECT b."+ CustomerBillsDB.idColumn+", b."+CustomerBillsDB.dateColumn+", b."+CustomerBillsDB.deliveryDateColumn
                            + ", b."+CustomerBillsDB.advanceColumn+", b.finalAmount, b."+ CustomerBillsDB.balanceColumn+",  b.customerNameId, b.billTotal AS totalPrice, cn.customerName, c."+CustomerDB.phoneNumberColumn+ " FROM "+CustomerBillsDB.customerBillTableName+
                            " b LEFT JOIN CustomerName cn ON cn.customerNameId = b.customerNameId JOIN Customer c on c.customerId = cn.customerId " +
                            "Where cn.customerNameId = "+ CustomerController.customerNameId +" GROUP BY b."+
                            CustomerBillsDB.idColumn+ ", b."+ CustomerBillsDB.dateColumn +", b."+CustomerBillsDB.deliveryDateColumn +", b."+
                            CustomerBillsDB.advanceColumn+", b.finalAmount, b."+CustomerBillsDB.balanceColumn +", cn.customerName, c."+
                            CustomerDB.phoneNumberColumn+ ", b.customerNameId";


                    billIdTVColumn.setCellValueFactory(new PropertyValueFactory<>("billId"));
                    billDateTVColumn.setCellValueFactory(new PropertyValueFactory<>("billDate"));
                    deliveryDateTVColumn.setCellValueFactory(new PropertyValueFactory<>("deliveryDate"));
                    totalBillTVColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
                    advanceTVColumn.setCellValueFactory(new PropertyValueFactory<>("advance"));
                    finalAmountTVColumn.setCellValueFactory(new PropertyValueFactory<>("finalAmount"));
                    balanceTVColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));
                    viewButtonTVColumn.setCellValueFactory(new PropertyValueFactory<>("viewButton"));

                    ObservableList billsList = showBill(query);
                    table.setItems(billsList);
                }

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
                                BillsController.billId = bill.getBillId();
                                Function.fxmlFileName="CustomerModule/CustomerProfile.fxml";
                                Function.title="Customer Profile";
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
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    static void initializeLensBoxes(ResultSet ls, TextField sphRight1, TextField cylRight1, TextField axisRight1, TextField addRight1, TextField sphLeft1, TextField cylLeft1, TextField axisLeft1, TextField addLeft1, TextArea ipd1) throws SQLException {
        sphRight1.setText(ls.getString("rSph"));
        cylRight1.setText(ls.getString("rCyl"));
        axisRight1.setText(ls.getString("rAxis"));
        addRight1.setText(ls.getString("rAdd"));
        sphLeft1.setText(ls.getString("lSph"));
        cylLeft1.setText(ls.getString("lCyl"));
        axisLeft1.setText(ls.getString("lAxis"));
        addLeft1.setText(ls.getString("lAdd"));
        ipd1.setText(ls.getString("ipd"));
    }

    public ObservableList showBill(String query) {

        ObservableList<Bill> billsList = FXCollections.observableArrayList();
        try {
            Connection connection = DriverManager.getConnection(Function.databasePath);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                billsList.add(new Bill(rs.getInt("cBillId"),rs.getString("billDate"),rs.getString("deliveryDate"),
                        rs.getDouble("totalPrice"), rs.getDouble("billAdvance"), rs.getDouble("finalAmount"),
                        rs.getDouble("billBalance"),new Button("👁")));
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
    void onUpdateClicked(ActionEvent event) throws IOException {
        phoneNumber = phoneNumberField.getText().trim();
        name = customerNameField.getText().trim();
        address = addressTexArea.getText().trim();

        customerNameField.setEditable(true);
        phoneNumberField.setEditable(true);
        addressTexArea.setEditable(true);
        updateCustomerButton.visibleProperty().set(true);
        cancelCustomerButton.visibleProperty().set(true);
        updateButton.disableProperty().set(true);
        removeButton.disableProperty().set(true);
        updateLensButton.disableProperty().set(true);
        backButton.disableProperty().set(true);
        customerNameField.requestFocus();
    }

    @FXML
    void onRemoveClicked(ActionEvent event) {

        try {
            int count = 0;
            int customerId = 0;
            int lensId = 0;
            Connection connection = DriverManager.getConnection(Function.databasePath);
            Statement statement = connection.createStatement();
            String query = "Select customerId, lensId From CustomerName Where customerNameId  = "+ CustomerController.customerNameId;
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()){
                customerId = rs.getInt("customerId");
                lensId = rs.getInt("lensId");
                query = "SELECT COUNT(customerId) AS total FROM CustomerName WHERE customerId = "+ customerId;
                ResultSet set = statement.executeQuery(query);
                set.next();
                count = set.getInt("total");
            }



            JOptionPane JOptionPane = null;
            int choice = JOptionPane.showConfirmDialog(null, "Do you really want to Remove?", "Confirmation", JOptionPane.OK_CANCEL_OPTION);
            if (choice == JOptionPane.OK_OPTION) {

                query = "Delete * from CustomerName where customerNameId = "+ CustomerController.customerNameId;
                statement.execute(query);

                query = "Delete * from Lens where lensId = "+lensId;
                statement.execute(query);

                if (count == 1){
                    query = "Delete * from Customer where customerId = "+ customerId;
                    statement.execute(query);
                }

                query = "Update CustomerBills set customerNameId = "+ -1 +" Where customerNameId = "+CustomerController.customerNameId;
                statement.execute(query);

                Function.alert(Alert.AlertType.INFORMATION, "Congratulations", "Customer Removed Successfully");
                statement.close();
                connection.close();
                try {
                    SceneController.switchScene("CustomerModule/Customer.fxml",event,"Customers");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }catch (SQLException ex){
            System.out.println(ex.getMessage());
            Function.alert(Alert.AlertType.ERROR,"Error",ex.getMessage());
        }

    }

    @FXML
    void onUpdateLensClicked(ActionEvent event) {

        if(!isLensBoxEmpty()) {
            sphRight = sphRight1Field.getText().trim();
            sphLeft = sphLeft1Field.getText().trim();
            cylRight = cylRight1Field.getText().trim();
            cylLeft = cylLeft1Field.getText().trim();
            axisRight = axisRight1Field.getText().trim();
            axisLeft = axisLeft1Field.getText().trim();
            addRight = addRight1Field.getText().trim();
            addLeft = addLeft1Field.getText().trim();
            ipd = ipd1TextArea.getText().trim();

            sphLeft1Field.setEditable(true);
            sphRight1Field.setEditable(true);
            cylLeft1Field.setEditable(true);
            cylRight1Field.setEditable(true);
            axisLeft1Field.setEditable(true);
            axisRight1Field.setEditable(true);
            addLeft1Field.setEditable(true);
            addRight1Field.setEditable(true);
            ipd1TextArea.setEditable(true);
            updateLensNoButton.visibleProperty().set(true);
            cancelLensNoButton.visibleProperty().set(true);
            updateButton.disableProperty().set(true);
            removeButton.disableProperty().set(true);
            updateLensButton.disableProperty().set(true);
            backButton.disableProperty().set(true);
            sphRight1Field.requestFocus();
        } else if(isLensBoxEmpty()){
            Function.alert(Alert.AlertType.ERROR,"Error","No Lens Record exists for this customer");
        }
    }

    @FXML
    void onUpdateCustomerClicked(ActionEvent event) {

        try {

            Connection connection = DriverManager.getConnection(databasePath);
            boolean fieldsNotEmpty = true;


            if (phoneNumberField.getText().trim().isEmpty() || customerNameField.getText().trim().isEmpty() || creditField.getText().trim().isEmpty() ) {
                fieldsNotEmpty = false;
            }

                if (fieldsNotEmpty) {
                    if (checkAlphabets(customerNameField.getText().trim())) {
                        if (checkPhoneNo(phoneNumberField.getText().trim())) {
                            String updateCustomerQuery = "UPDATE Customer " +
                                    "SET customerNo = '" + phoneNumberField.getText().trim() + "', " +
                                    "customerAddress = '" + addressTexArea.getText().trim() + "' " +
                                    "WHERE customerId = " + customerId;

                            String updateCustomerNameQuery = "UPDATE CustomerName " +
                                    "SET customerName = '" + customerNameField.getText().trim() + "' " +
                                    "WHERE customerNameId = " + CustomerController.customerNameId;

                            Statement statement = connection.createStatement();
                            statement.execute(updateCustomerQuery);
                            statement.execute(updateCustomerNameQuery);
                            Function.alert(Alert.AlertType.INFORMATION, "Congratulations", "Customer updated successfully");
                            statement.close();
                            connection.close();

                            customerNameField.setEditable(false);
                            phoneNumberField.setEditable(false);
                            addressTexArea.setEditable(false);
                            updateCustomerButton.visibleProperty().set(false);
                            cancelCustomerButton.visibleProperty().set(false);
                            updateButton.disableProperty().set(false);
                            removeButton.disableProperty().set(false);
                            updateLensButton.disableProperty().set(false);
                            backButton.disableProperty().set(false);

                        } else {
                            Function.alert(Alert.AlertType.ERROR, "Error", "Special characters or less than 11 digits are not allowed in Phone Number");
                            phoneNumberField.clear();
                            phoneNumberField.requestFocus();
                        }
                    } else {
                        Function.alert(Alert.AlertType.ERROR, "Error", "Special characters or digits are not allowed in Customer Name");
                        customerNameField.clear();
                        customerNameField.requestFocus();
                    }
                } else {
                    Function.alert(Alert.AlertType.ERROR, "Error", "Fill all the information");
                }


        } catch (SQLException e) {
            Function.alert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }


    }

    @FXML
    void onCancelCustomerClicked(ActionEvent event) {
        phoneNumberField.setText(phoneNumber);
        customerNameField.setText(name);
        addressTexArea.setText(address);
        customerNameField.setEditable(false);
        phoneNumberField.setEditable(false);
        addressTexArea.setEditable(false);
        updateCustomerButton.visibleProperty().set(false);
        cancelCustomerButton.visibleProperty().set(false);
        updateButton.disableProperty().set(false);
        removeButton.disableProperty().set(false);
        updateLensButton.disableProperty().set(false);
        backButton.disableProperty().set(false);
    }

    @FXML
    void onUpdateLensNoClicked(ActionEvent event) {

        try {

            Connection connection = DriverManager.getConnection(databasePath);


            if (!isLensBoxEmpty()) {
                if (checkTheLensBox()) {
                        String query = "Update " + LensDB.lensTableName + "  Set " + LensDB.rSphColumn + " = '" + sphRight1Field.getText().trim() + "', " + LensDB.rCylColumn + " = '" + cylRight1Field.getText().trim() +
                                "', " + LensDB.rAxisColumn + " = '" + axisRight1Field.getText().trim() + "', " + LensDB.rAddColumn + " = '" + addRight1Field.getText().trim() + "'," +
                                " " + LensDB.lSphColumn + " = '" + sphLeft1Field.getText().trim() + "', " + LensDB.lCylColumn + " = '" + cylLeft1Field.getText().trim() + "'," +
                                " " + LensDB.lAxisColumn + " = '" + axisLeft1Field.getText().trim() + "', " + LensDB.lAddColumn + " = '" + addLeft1Field.getText().trim() + "'," +
                                " " + LensDB.ipdColumn + " = '" + ipd1TextArea.getText().trim() + "' Where " + LensDB.lensIdColumn + " = " + lensId;

                        Statement statement = connection.createStatement();
                        statement.execute(query);
                        Function.alert(Alert.AlertType.INFORMATION, "Congratulations", "Lens Details updated successfully ");
                        statement.close();
                        connection.close();

                    sphLeft1Field.setEditable(false);
                    sphRight1Field.setEditable(false);
                    cylLeft1Field.setEditable(false);
                    cylRight1Field.setEditable(false);
                    axisLeft1Field.setEditable(false);
                    axisRight1Field.setEditable(false);
                    addLeft1Field.setEditable(false);
                    addRight1Field.setEditable(false);
                    ipd1TextArea.setEditable(false);
                    updateLensNoButton.visibleProperty().set(false);
                    cancelLensNoButton.visibleProperty().set(false);
                    updateButton.disableProperty().set(false);
                    removeButton.disableProperty().set(false);
                    updateLensButton.disableProperty().set(false);
                    backButton.disableProperty().set(false);

                } else {
                    Function.alert(Alert.AlertType.ERROR, "Error", "Enter only Numbers in SPH, CYL, AXIS, ADD, IPD ");
                    sphRight1Field.requestFocus();
                }
            } else {
                Function.alert(Alert.AlertType.ERROR, "Error", "You have removed all the details for Lens, Cancel and  update again");
            }


        } catch (SQLException e) {
            Function.alert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }

    }

    @FXML
    void onCancelLensNoClicked(ActionEvent event) {
        sphRight1Field.setText(sphRight);
        sphLeft1Field.setText(sphLeft);
        cylRight1Field.setText(cylRight);
        cylLeft1Field.setText(cylLeft);
        axisRight1Field.setText(axisRight);
        axisLeft1Field.setText(axisLeft);
        addRight1Field.setText(addRight);
        addLeft1Field.setText(addLeft);
        ipd1TextArea.setText(ipd);

        sphLeft1Field.setEditable(false);
        sphRight1Field.setEditable(false);
        cylLeft1Field.setEditable(false);
        cylRight1Field.setEditable(false);
        axisLeft1Field.setEditable(false);
        axisRight1Field.setEditable(false);
        addLeft1Field.setEditable(false);
        addRight1Field.setEditable(false);
        ipd1TextArea.setEditable(false);
        updateLensNoButton.visibleProperty().set(false);
        cancelLensNoButton.visibleProperty().set(false);
        updateButton.disableProperty().set(false);
        removeButton.disableProperty().set(false);
        updateLensButton.disableProperty().set(false);
        backButton.disableProperty().set(false);
    }

    @FXML
    void onBackClicked(ActionEvent event) {
        try {
            SceneController.switchScene("CustomerModule/Customer.fxml",event,"Customers");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isLensBoxEmpty() {
        // Checks if all fields are empty
        if (sphRight1Field.getText().trim().isEmpty() && cylRight1Field.getText().trim().isEmpty() && axisRight1Field.getText().trim().isEmpty() &&
                addRight1Field.getText().trim().isEmpty() && sphLeft1Field.getText().trim().isEmpty() && cylLeft1Field.getText().trim().isEmpty() &&
                axisLeft1Field.getText().trim().isEmpty() && addLeft1Field.getText().trim().isEmpty() && ipd1TextArea.getText().trim().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkTheLensBox() {
            boolean[] flag = new boolean[9];
            boolean check = true;
            Arrays.fill(flag, true);
            String values = sphRight1Field.getText().trim();
            flag[0] = Function.checkForDecimalPlusMinus(values);
            values = cylRight1Field.getText().trim();
            flag[1] = Function.checkForDecimalPlusMinus(values);
            values = axisRight1Field.getText().trim();
            flag[2] = Function.checkForDecimalPlusMinus(values);
            values = addRight1Field.getText().trim();
            flag[3] = Function.checkForDecimalPlusMinus(values);
            values = sphLeft1Field.getText().trim();
            flag[4] = Function.checkForDecimalPlusMinus(values);
            values = cylLeft1Field.getText().trim();
            flag[5] = Function.checkForDecimalPlusMinus(values);
            values = axisLeft1Field.getText().trim();
            flag[6] = Function.checkForDecimalPlusMinus(values);
            values = addLeft1Field.getText().trim();
            flag[7] = Function.checkForDecimalPlusMinus(values);
            for (int i = 0; i < flag.length; i++) {
                if (!flag[i]) {
                    check = false;
                }
            }
            return check;
        }

}

