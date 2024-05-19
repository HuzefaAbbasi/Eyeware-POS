package com.example.pos.VendorModule;

import com.example.pos.UtilityClasses.Function;
import com.example.pos.SceneController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class SearchVendorController implements Initializable {

    static String vendorPhoneNumber;

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

        phoneNumberTVColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        nameTVColumn.setCellValueFactory(new PropertyValueFactory<>("vendorName"));
        creditTVColumn.setCellValueFactory(new PropertyValueFactory<>("credit"));
        addressTVColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        String query = "Select * from Vendor";
        ObservableList vendorList = showVendors(query);
        table.setItems(vendorList);
    }

    public ObservableList showVendors(String query){

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
        String query = "Select * From Vendor Where vendorName Like '%"+searchField.getText().trim()+"%' ";
        ObservableList vendorList = showVendors(query);
        table.setItems(vendorList);
    }

    @FXML
    void onSelectClicked(ActionEvent event) throws IOException {
        if(table.getSelectionModel().isEmpty()){
            Function.alert(Alert.AlertType.ERROR,"Error","No Vendor Selected!!!");
        }
        else{
            Vendor vendor = table.getSelectionModel().getSelectedItem();
            vendorPhoneNumber = vendor.getPhoneNumber();
            SceneController.switchScene(Function.fxmlFileName,event,Function.title);
        }
    }

}
