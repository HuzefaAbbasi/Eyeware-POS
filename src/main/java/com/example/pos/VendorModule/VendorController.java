package com.example.pos.VendorModule;

import com.example.pos.UtilityClasses.Function;
import com.example.pos.SceneController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class VendorController implements Initializable {

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
    private Button viewButton;
    @FXML
    private Button newVendorButton;
    @FXML
    private Button updateVendorButton;
    @FXML
    private Button removeVendorButton;
    @FXML
    private Button signOutButton;


    @FXML
    private TableView<Vendor> table;
    @FXML
    private TableColumn<Vendor, String> nameTVColumn;
    @FXML
    private TableColumn<Vendor, String> phoneNumberTVColumn;
    @FXML
    private TableColumn<Vendor, Double> creditTVColumn;
    @FXML
    private TableColumn<Vendor, String> addressTVColumn;
    @FXML
    private TextField searchField;


    //showing data in table view
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        vendorButton.setStyle("-fx-background-color: #2c2483");
        phoneNumberTVColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        nameTVColumn.setCellValueFactory(new PropertyValueFactory<>("vendorName"));
        creditTVColumn.setCellValueFactory(new PropertyValueFactory<>("credit"));
        addressTVColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        String query = "Select * from Vendor";
        ObservableList vendorList = showVendor(query);
        table.setItems(vendorList);
    }

    public ObservableList showVendor(String query) {

        ObservableList<Vendor> vendorList = FXCollections.observableArrayList();
        try {
            Connection connection = DriverManager.getConnection(Function.databasePath);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                vendorList.add(new Vendor(rs.getString("vendorPhoneNo"),
                        rs.getString("vendorName"),rs.getDouble("vendorCredit"),rs.getString("vendorAddress")));
            }
            connection.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return vendorList;
    }


    @FXML
    void keyDown(KeyEvent event) {
        String query = " Select * From Vendor Where vendorName Like '%"+searchField.getText().trim()+"%' ";
        ObservableList vendorList = showVendor(query);
        table.setItems(vendorList);
    }

    @FXML
    void onNewVendorClicked(ActionEvent event) throws IOException {
        Stage dialog = new Stage();
        Scene scene;
        Parent root;
//        Function.fxmlFileName = "Vendor.fxml";
//        Function.title = "Vendor";

        root = FXMLLoader.load(SceneController.class.getResource("VendorModule/NewVendor.fxml"));

        //This is setting the value of the static variable vendorControllerInNewVendor
        NewVendorController.setVendorControllerInNewVendor(this);

        dialog.initModality(Modality.APPLICATION_MODAL);
        scene = new Scene(root);
        dialog.setScene(scene);
        dialog.initOwner(SceneController.stage);
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.resizableProperty().set(false);
        dialog.setTitle("New Vendor");
        dialog.showAndWait();
    }

    @FXML
    void onUpdateVendorClicked(ActionEvent event) throws IOException {
        Stage dialog = new Stage();
        Scene scene;
        Parent root;
//        Function.fxmlFileName = "Vendor.fxml";
//        Function.title = "Vendor";

        root = FXMLLoader.load(SceneController.class.getResource("VendorModule/UpdateVendor.fxml"));

        //This is setting the value of the static variable vendorControllerInUpdateVendor
        UpdateVendorController.setVendorControllerInUpdateVendor(this);

        dialog.initModality(Modality.APPLICATION_MODAL);
        scene = new Scene(root);
        dialog.setScene(scene);
        dialog.initOwner(SceneController.stage);
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.resizableProperty().set(false);
        dialog.setTitle("Update Vendor");
        dialog.showAndWait();
    }

    @FXML
    void onRemoveVendorClicked(ActionEvent event) throws IOException {
        Stage dialog = new Stage();
        Scene scene;
        Parent root;
//        Function.fxmlFileName = "Vendor.fxml";
//        Function.title = "Vendor";

        root = FXMLLoader.load(SceneController.class.getResource("VendorModule/RemoveVendor.fxml"));

        //This is setting the value of the static variable vendorControllerInRemoveVendor
        RemoveVendorController.setVendorControllerInRemoveVendor(this);

        dialog.initModality(Modality.APPLICATION_MODAL);
        scene = new Scene(root);
        dialog.setScene(scene);
        dialog.initOwner(SceneController.stage);
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.resizableProperty().set(false);
        dialog.setTitle("Remove Vendor");
        dialog.showAndWait();
    }

    // This method refreshes the data in the table view on run time
    public void refreshTableData() {
        String query = "Select * from Vendor";
        ObservableList<Vendor> vendorList = showVendor(query);
        table.setItems(vendorList);
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

}
