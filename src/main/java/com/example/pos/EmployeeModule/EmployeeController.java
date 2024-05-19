package com.example.pos.EmployeeModule;


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

public class EmployeeController implements Initializable {

    public static int employeeId;

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
    private Button newEmployeeButton;
    @FXML
    private Button signOutButton;


    @FXML
    private TableColumn<Employee, String> employeeUserNameTVColumn;

    @FXML
    private TableColumn<Employee, String> nameTVColumn;

    @FXML
    private TableColumn<Employee, String> passwordTVColumn;

    @FXML
    private TableColumn<Employee, String> phoneNumber1TVColumn;

    @FXML
    private TableColumn<Employee, String> phoneNumber2TVColumn;

    @FXML
    private TableColumn<Employee, Button> viewButtonTVColumn;

    @FXML
    private TableView<Employee> table;

    @FXML
    private TextField searchField;

    //showing data in table view
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        employeeButton.setStyle("-fx-background-color: #2c2483;");
        employeeUserNameTVColumn.setCellValueFactory(new PropertyValueFactory<>("employeeUserName"));
        nameTVColumn.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        passwordTVColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        phoneNumber1TVColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber1"));
        phoneNumber2TVColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber2"));
        viewButtonTVColumn.setCellValueFactory(new PropertyValueFactory<>("viewButton"));
        String query = "Select * from Employee";
        ObservableList employeeList = showEmployee(query);
        table.setItems(employeeList);

        viewButtonTVColumn.setCellFactory(param -> new TableCell<Employee, Button>() {
            @Override
            protected void updateItem(Button button, boolean empty) {
                super.updateItem(button, empty);

                if (empty || button == null) {
                    setGraphic(null);
                } else {
                    setGraphic(button);
                    button.setOnAction(event -> {
                        try {
                        Employee employee= getTableView().getItems().get(getIndex());
                        Connection connection = DriverManager.getConnection(Function.databasePath);
                        Statement statement = connection.createStatement();
                        String query=" SELECT employeeId from Employee where Employee_UserName = '"+employee.getEmployeeUserName()+"'";
                        ResultSet rs = statement.executeQuery(query);
                        if (rs.next()){
                            employeeId = rs.getInt("employeeId");
                            SceneController.switchScene("EmployeeModule/EmployeeProfile.fxml",event,"Employee Profile");
                        }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
        });

    }

    public ObservableList showEmployee(String query) {

        ObservableList<Employee> list = FXCollections.observableArrayList();
        try {
            Connection connection = DriverManager.getConnection(Function.databasePath);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                list.add(new Employee( rs.getString("Employee_UserName"), rs.getString("Employee_Name"),
                        rs.getString("Employee_Password"), rs.getString("Employee_Ph_1"),
                        rs.getString("Employee_Ph_2") ,new Button("👁") ) );
            }
            connection.close();
            statement.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            Function.alert(Alert.AlertType.ERROR,"Error",e.getMessage());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return list;
    }

    @FXML
    void keyDown(KeyEvent event) {
        String query = " Select * From Employee Where Employee_Name Like '%"+searchField.getText().trim()+"%'" ;
        ObservableList employeeList = showEmployee(query);
        table.setItems(employeeList);
    }

    @FXML
    void onNewEmployeeClicked(ActionEvent event) throws IOException{
        Stage dialog = new Stage();
        Scene scene;
        Parent root;
//        Function.fxmlFileName = "Employee.fxml";
//        Function.title = "Employee";

        root = FXMLLoader.load(SceneController.class.getResource("EmployeeModule/NewEmployee.fxml"));

        //This is setting the value of the static variable employeeControllerInNewEmployee
        NewEmployeeController.setEmployeeControllerInNewEmployee(this);

        dialog.initModality(Modality.APPLICATION_MODAL);
        scene = new Scene(root);
        dialog.setScene(scene);
        dialog.initOwner(SceneController.stage);
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.resizableProperty().set(false);
        dialog.setTitle("New Employee");
        dialog.showAndWait();
    }


    // This method refreshes the data in the table view on run time
    public void refreshTableData(){
        String query = "Select * from Employee";
        ObservableList<Employee> employeeList = showEmployee(query);
        table.setItems(employeeList);
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
