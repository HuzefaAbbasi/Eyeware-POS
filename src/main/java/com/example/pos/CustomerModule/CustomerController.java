package com.example.pos.CustomerModule;

import com.example.pos.UtilityClasses.Function;
import com.example.pos.LoginModule.LoginController;
import com.example.pos.SceneController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {

    public static int customerNameId;

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
    private Button profileButton;
    @FXML
    private Button signOutButton;

    @FXML
    private TableView<Customer> table;
    @FXML
    private TableColumn<Customer, String> nameTVColumn;
    @FXML
    private TableColumn<Customer, String> phoneNumberTVColumn;
    @FXML
    private TableColumn<Customer, Double> creditTVColumn;
    @FXML
    private TableColumn<Customer, String> addressTVColumn;
    @FXML
    private TableColumn<Customer, Button> viewButtonTVColumn;
    @FXML
    private ComboBox<String> searchOptionComboBox;
    @FXML
    private TextField searchField;
    ObservableList<String> searchOptionList = FXCollections.observableArrayList("Name", "Phone Number");


    //showing data in table view
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        customerButton.setStyle("-fx-background-color: #2c2483;");
        searchOptionComboBox.setItems(searchOptionList);
        searchOptionComboBox.getSelectionModel().select(0);
        nameTVColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        phoneNumberTVColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        creditTVColumn.setCellValueFactory(new PropertyValueFactory<>("credit"));
        addressTVColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        viewButtonTVColumn.setCellValueFactory(new PropertyValueFactory<>("viewButton"));

        String query = "Select c.customerId, cn.customerNameId, c.customerNo, c.customerAddress, cn.customerName, cn.customerCredit from Customer c Join" +
                " CustomerName cn On c.customerId = cn.customerId";
        ObservableList customerList = showCustomer(query);
        table.setItems(customerList);

        viewButtonTVColumn.setCellFactory(param -> new TableCell<Customer, Button>() {
            @Override
            protected void updateItem(Button button, boolean empty) {
                super.updateItem(button, empty);

                if (empty || button == null) {
                    setGraphic(null);
                } else {
                    setGraphic(button);
                    button.setOnAction(event -> {
                        Customer customer = getTableView().getItems().get(getIndex());
                        try {
                            Connection connection = DriverManager.getConnection(Function.databasePath);
                            Statement statement = connection.createStatement();
                            String query=" SELECT cn.customerNameId from CustomerName cn Join Customer c On c.customerId = cn.customerId" +
                                    " WHERE c.customerNo = '"+customer.getPhoneNumber()+"' And cn.customerName = '"+customer.getCustomerName()+"'";
                            ResultSet rs = statement.executeQuery(query);
                            if (rs.next()){
                                customerNameId = rs.getInt("customerNameId");
                                SceneController.switchScene("CustomerModule/CustomerProfile.fxml",event,"Customer Profile");
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
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
            profileButton.visibleProperty().set(true);
        }
    }

    public ObservableList showCustomer(String query) {

        ObservableList<Customer> customerList = FXCollections.observableArrayList();
        try {
            Connection connection = DriverManager.getConnection(Function.databasePath);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                customerList.add(new Customer(rs.getString("customerName"),
                        rs.getString("customerNo"),rs.getDouble("customerCredit"),rs.getString("customerAddress"),new Button("👁")));
            }
            connection.close();
            statement.close();
        } catch (SQLException e) {
            Function.alert(Alert.AlertType.ERROR,"Error",e.getMessage());
        }catch (Exception e){
            Function.alert(Alert.AlertType.ERROR,"Error",e.getMessage());
        }
        return customerList;
    }

    @FXML
    void keyDown(KeyEvent event) {

        if(searchOptionComboBox.getValue().equals("Name")){
        String query = "Select c.customerId, cn.customerNameId, c.customerNo, c.customerAddress, cn.customerName, cn.customerCredit from Customer c Join"+
                " CustomerName cn On c.customerId = cn.customerId Where cn.customerName Like '%"+searchField.getText().trim()+"%' " ;
        ObservableList customerList = showCustomer(query);
        table.setItems(customerList);}
        else if(searchOptionComboBox.getValue().equals("Phone Number")){
            String query = "Select c.customerId, cn.customerNameId, c.customerNo, c.customerAddress, cn.customerName, cn.customerCredit from Customer c Join"+
                    " CustomerName cn On c.customerId = cn.customerId Where c.customerNo Like '%"+searchField.getText().trim()+"%' " ;
            ObservableList customerList = showCustomer(query);
            table.setItems(customerList);
        }
    }


    // This method refreshes the data in the table view on run time
    public void refreshTableData() {
        String query = "Select c.customerId, cn.customerNameId, c.customerNo, c.customerAddress, cn.customerName, cn.customerCredit from Customer c Join" +
                " CustomerName cn On c.customerId = cn.customerId";
        ObservableList<Customer> customerList = showCustomer(query);
        table.setItems(customerList);
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
    void onProfileClicked(ActionEvent event) {
        try {
            SceneController.switchScene("EmployeeModule/EmployeeProfile.fxml",event,"Employee Profile");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
