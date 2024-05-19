package com.example.pos.InventoryModule;


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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class InventoryController implements Initializable {

    static String productCode;

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
    private Button printInventoryButton;
    @FXML
    private Button newProductButton;

    @FXML
    private Button signOutButton;
    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    private ComboBox<String> searchOptionComboBox;
    @FXML
    private TextField searchField;
    @FXML
    public TableView<Product> table;
    @FXML
    public TableColumn<Product, String> productCodeTVColumn;
    @FXML
    public TableColumn<Product, String> nameTVColumn;
    @FXML
    public TableColumn<Product, String> categoryTVColumn;
    @FXML
    public TableColumn<Product, Integer> stockTVColumn;
    @FXML
    public TableColumn<Product, Double> purchasePriceTVColumn;
    @FXML
    public TableColumn<Product, Double> minimumRetailPriceTVColumn;
    @FXML
    public TableColumn<Product, Double> discountTVColumn;
    @FXML
    private TableColumn<Product, Button> updateButtonTVColumn;
    @FXML
    private TableColumn<Product, Button> removeButtonTVColumn;
    private String searchType = "productCode";
    private String category = "All";


    ObservableList<String> categoryList = FXCollections.observableArrayList("All", "Contact Lens", "Opthalmic Lens", "Frame", "Lens Solution", "Sun Glasses", "Batteries", "Other");
    ObservableList<String> searchOptionList = FXCollections.observableArrayList("Product Code", "Name");



    // Initializing the data in the table view by fetching the data from the database
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        inventoryButton.setStyle("-fx-background-color: #2c2483;");
        categoryComboBox.setItems(categoryList);
        categoryComboBox.getSelectionModel().select(0);
        searchOptionComboBox.setItems(searchOptionList);
        searchOptionComboBox.getSelectionModel().select(0);
        categoryTVColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        productCodeTVColumn.setCellValueFactory(new PropertyValueFactory<>("productCode"));
        nameTVColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        stockTVColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        purchasePriceTVColumn.setCellValueFactory(new PropertyValueFactory<>("purchasePrice"));
        minimumRetailPriceTVColumn.setCellValueFactory(new PropertyValueFactory<>("minimumRetailPrice"));
        discountTVColumn.setCellValueFactory(new PropertyValueFactory<>("discount"));
        updateButtonTVColumn.setCellValueFactory(new PropertyValueFactory<>("updateButton"));
        removeButtonTVColumn.setCellValueFactory(new PropertyValueFactory<>("removeButton"));
        String query = "Select * from Inventory" ;
        ObservableList inventoryProductList = showInventory(query);
        table.setItems(inventoryProductList);

        updateButtonTVColumn.setCellFactory(param -> new TableCell<Product, Button>() {
            @Override
            protected void updateItem(Button button, boolean empty) {
                super.updateItem(button, empty);

                if (empty || button == null) {
                    setGraphic(null);
                } else {
                    setGraphic(button);
                    button.setOnAction(event -> {
                        Product product = getTableView().getItems().get(getIndex());
                        productCode = product.getProductCode();
                        Stage dialog = new Stage();
                        Scene scene;
                        Parent root;

                        try {
                            root = FXMLLoader.load(SceneController.class.getResource("InventoryModule/UpdateProduct.fxml"));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        //This is setting the value of the static variable inventoryControllerInUpdateProduct
                        UpdateProductController.setInventoryControllerInUpdateProduct(InventoryController.this);

                        dialog.initModality(Modality.APPLICATION_MODAL);
                        scene = new Scene(root);
                        dialog.setScene(scene);
                        dialog.initOwner(SceneController.stage);
                        dialog.initStyle(StageStyle.UNDECORATED);
                        dialog.resizableProperty().set(false);
                        dialog.setTitle("Update Product");
                        dialog.showAndWait();
                    });
                }
            }
        });

        removeButtonTVColumn.setCellFactory(param -> new TableCell<Product, Button>() {
            @Override
            protected void updateItem(Button button, boolean empty) {
                super.updateItem(button, empty);

                if (empty || button == null) {
                    setGraphic(null);
                } else {
                    setGraphic(button);
                    button.setOnAction(event -> {
                        Product product = getTableView().getItems().get(getIndex());
                        productCode = product.getProductCode();
                        Stage dialog = new Stage();
                        Scene scene;
                        Parent root;

                        try {
                            root = FXMLLoader.load(SceneController.class.getResource("InventoryModule/RemoveProduct.fxml"));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        //This is setting the value of the static variable inventoryControllerInUpdateProduct
                        RemoveProductController.setInventoryControllerInRemoveProduct(InventoryController.this);

                        dialog.initModality(Modality.APPLICATION_MODAL);
                        scene = new Scene(root);
                        dialog.setScene(scene);
                        dialog.initOwner(SceneController.stage);
                        dialog.initStyle(StageStyle.UNDECORATED);
                        dialog.resizableProperty().set(false);
                        dialog.setTitle("Remove Product");
                        dialog.showAndWait();
                    });
                }
            }
        });



    }

    public ObservableList showInventory(String query) {

        ObservableList<Product> inventoryList = FXCollections.observableArrayList();
        try {
            Connection connection = DriverManager.getConnection(Function.databasePath);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                inventoryList.add(new Product(rs.getString(ProductDB.productCategoryColumn), rs.getString(ProductDB.productCodeColumn),
                        rs.getString(ProductDB.productNameColumn), rs.getInt(ProductDB.productStockColumn),
                        rs.getDouble(ProductDB.productPurchasePriceColumn), rs.getDouble(ProductDB.productMinimumRetailPriceColumn),
                        rs.getDouble("discount"),new Button("✎"), new Button("🗑")));
            }

//            Remove Icon "🗑"
//            Update Icon "✎" -fx-background-radius: 5;-fx-border-radius: 5; -fx-border-color: black;
            connection.close();
            statement.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            Function.alert(Alert.AlertType.ERROR,"Error",e.getMessage());
        }catch(Exception e) {
            System.out.println(e.getMessage());
        }

        return inventoryList;
    }

    @FXML
    void keyDown(KeyEvent event) {
        String query ;
        if(searchOptionComboBox.getValue().equals("Name")){
            searchType = "productName";
        }else if(searchOptionComboBox.getValue().equals("Product Code")){
            searchType = "productCode";
        }

        if (category.equals("All")){
            query = "Select * From Inventory WHERE "+searchType+" like '%"+searchField.getText().trim()+"%' ";
        }
        else {
            query = "Select * From Inventory Where category = '"+ category +"'" +
                    " AND "+searchType+" like '%"+searchField.getText().trim()+"%' ";
        }

        ObservableList list = showInventory(query);
        table.setItems(list);
    }

    @FXML
    void categoryComboBoxAction(ActionEvent event) {
        String query ;
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
            query = "Select * From Inventory WHERE "+searchType+" like '%"+searchField.getText().trim()+"%' ";
        }
        else {
            query = "Select * From Inventory Where category = '"+ category +"'" +
                    " AND "+searchType+" like '%"+searchField.getText().trim()+"%' ";
        }


        ObservableList list = showInventory(query);
        table.setItems(list);
    }


    @FXML
    void onNewProductClicked(ActionEvent event) throws IOException {
        Stage dialog = new Stage();
        Scene scene;
        Parent root;

        root = FXMLLoader.load(SceneController.class.getResource("InventoryModule/NewProduct.fxml"));

        //This is setting the value of the static variable inventoryControllerInNewProduct
        NewProductController.setInventoryControllerInNewProduct(this);

        dialog.initModality(Modality.APPLICATION_MODAL);
        scene = new Scene(root);
        dialog.setScene(scene);
        dialog.initOwner(SceneController.stage);
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.resizableProperty().set(false);
        dialog.setTitle("New Product");
        dialog.showAndWait();
    }


    // This method refreshes the data in the table view on run time
    public void refreshTableData() {
        String query = "Select * from Inventory";
        ObservableList<Product> inventoryProductList = showInventory(query);
        table.setItems(inventoryProductList);
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

}

