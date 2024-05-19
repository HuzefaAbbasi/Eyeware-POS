package com.example.pos.InventoryModule;

import com.example.pos.UtilityClasses.Function;
import com.example.pos.SceneController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class SearchProductController implements Initializable {

    public static String productCode;
    @FXML
    private TextField searchField;
    @FXML
    public TableView<Product> table;
    public TableColumn<Product, String> categoryTVColumn;
    @FXML
    public TableColumn<Product, String> productCodeTVColumn;
    @FXML
    public TableColumn<Product, String> nameTVColumn;
    @FXML
    public TableColumn<Product, Integer> stockTVColumn;
    @FXML
    public TableColumn<Product, Double> minimumRetailPriceTVColumn;
    @FXML
    public TableColumn<Product, Double> discountTVColumn;

    @FXML
    private Button selectButton;
    @FXML
    private ComboBox<String> categoryComboBox;
    private String category = "All";

    ObservableList<String> categoryList = FXCollections.observableArrayList("All", "Contact Lens", "Opthalmic Lens", "Frame", "Lens Solution", "Sun Glasses", "Batteries", "Other");


    //showing data in table view
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        categoryComboBox.setItems(categoryList);
        categoryComboBox.getSelectionModel().select(0);
        categoryTVColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        productCodeTVColumn.setCellValueFactory(new PropertyValueFactory<>("productCode"));
        nameTVColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        stockTVColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        minimumRetailPriceTVColumn.setCellValueFactory(new PropertyValueFactory<>("minimumRetailPrice"));
        discountTVColumn.setCellValueFactory(new PropertyValueFactory<>("discount"));
        String query = "Select * from Inventory";
        ObservableList list = showInventory(query);
        table.setItems(list);
    }

    public ObservableList showInventory(String query) {

        ObservableList<Product> inventoryList = FXCollections.observableArrayList();
        try {
            Connection connection = DriverManager.getConnection(Function.databasePath);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                inventoryList.add(new Product(rs.getString(ProductDB.productCategoryColumn), rs.getString(ProductDB.productCodeColumn),
                        rs.getString(ProductDB.productNameColumn), rs.getInt(ProductDB.productStockColumn), rs.getDouble(ProductDB.productMinimumRetailPriceColumn), rs.getDouble("discount") ) );
            }
            connection.close();
            statement.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }catch(Exception e) {
            System.out.println(e.getMessage());
        }

        return inventoryList;
    }

    @FXML
    void keyDown(KeyEvent event) {
        String query = "";
        if (category.equals("All")){
            query = "Select * From Inventory WHERE productName like '%"+searchField.getText().trim()+"%' ";
        }
        else {
            query = "Select * From Inventory Where category = '"+ category +"'" +
                    " AND productName like '%"+searchField.getText().trim()+"%' ";
        }


        ObservableList list = showInventory(query);
        table.setItems(list);
    }

    @FXML
    void categoryComboBoxAction(ActionEvent event) {
        String query = "";
        String selectedValue = categoryComboBox.getValue();

        if (selectedValue.equals("Contact Lens")){
            category = "Contact Lens";
        }
        else if (selectedValue.equals("Opthalmic Lens")){
            category = "Opthalmic Lens";
        }
        else if (selectedValue.equals("Frame")){
            category = "Frame";
        }
        else if (selectedValue.equals("Lens Solution")){
            category = "Lens Solution";
        }
        else if (selectedValue.equals("Sun Glasses")) {
            category = "Sun Glasses";
        }
        else if (selectedValue.equals("Batteries")) {
            category = "Batteries";
        }
        else if (selectedValue.equals("Other")){
            category = "Other";
        }
        else{
            category  = "All";
        }

        if (category.equals("All")){
            query = "Select * From Inventory WHERE productName like '%"+searchField.getText().trim()+"%' ";
        }
        else {
            query = "Select * From Inventory Where category = '"+ category +"'" +
                    " AND productName like '%"+searchField.getText().trim()+"%' ";
        }

        ObservableList list = showInventory(query);
        table.setItems(list);
    }
    @FXML
    void onSelectClicked(ActionEvent event) throws IOException {

        if (table.getSelectionModel().isEmpty()) {
            Function.alert(Alert.AlertType.ERROR, "Error", "No Product Selected!!!");
        }
        else if (Function.fxmlFileName.equals("BillModule/CustomerBill.fxml") ) {
            Product product = table.getSelectionModel().getSelectedItem();
            productCode = product.getProductCode();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        }

    }

    @FXML
    void onCancelClicked(ActionEvent event) {
        productCode = "";
        Stage stage= (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }

}
