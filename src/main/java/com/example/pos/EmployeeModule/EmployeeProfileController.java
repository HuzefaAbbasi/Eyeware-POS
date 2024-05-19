package com.example.pos.EmployeeModule;

import com.example.pos.LoginModule.LoginController;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import static com.example.pos.UtilityClasses.Function.*;

public class EmployeeProfileController implements Initializable {

    int currentEmployeeId = 0;

    private String name,phone1,phone2,userName,password;

    @FXML
    private Button addAttendanceButton,removeAttendanceButton,updateEmployeeButton,removeEmployeeButton, updateButton, cancelButton, backButton;

    @FXML
    private TextField nameField, phoneNumber1Field, phoneNumber2Field, userNameField, passwordField;

    @FXML
    private DatePicker searchDatePicker;

    @FXML
    private TableColumn<Attendance, String> dateTVColumn,dayTVColumn,timeInTVColumn,timeOutTVColumn,workedTimeTVColumn;

    @FXML
    private TableView<Attendance> table;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        searchDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) > 0);
            }
        });



        if(LoginController.currentUserName.equals(LoginController.adminUserName)){

            currentEmployeeId = EmployeeController.employeeId;

            try{
                    Connection connection = DriverManager.getConnection(Function.databasePath);
                    Statement statement = connection.createStatement();
                    String query="Select * from Employee where employeeId = " +EmployeeController.employeeId;
                    ResultSet rs = statement.executeQuery(query);
                    if (rs.next()) {
                        nameField.setText(rs.getString("Employee_Name"));
                        phoneNumber1Field.setText(rs.getString("Employee_Ph_1"));
                        phoneNumber2Field.setText(rs.getString("Employee_Ph_2"));
                        userNameField.setText(rs.getString("Employee_UserName"));
                        passwordField.setText(rs.getString("Employee_Password"));
                    }
                  }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }else if (!LoginController.currentUserName.equals(LoginController.adminUserName)){


            removeAttendanceButton.visibleProperty().set(false);
            updateEmployeeButton.visibleProperty().set(false);
            removeEmployeeButton.visibleProperty().set(false);

            try{
                Connection connection = DriverManager.getConnection(Function.databasePath);
                Statement statement = connection.createStatement();
                String query="Select * from Employee where Employee_UserName = '"+LoginController.currentUserName+"' ";
                ResultSet rs = statement.executeQuery(query);

                if (rs.next()) {
                    currentEmployeeId = rs.getInt("employeeId");
                    NewAttendanceController.employeeId = currentEmployeeId;
                    nameField.setText(rs.getString("Employee_Name"));
                    phoneNumber1Field.setText(rs.getString("Employee_Ph_1"));
                    phoneNumber2Field.setText(rs.getString("Employee_Ph_2"));
                    userNameField.setText(rs.getString("Employee_UserName"));
                    passwordField.setText(rs.getString("Employee_Password"));

                }


            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }

        dateTVColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dayTVColumn.setCellValueFactory(new PropertyValueFactory<>("day"));
        timeInTVColumn.setCellValueFactory(new PropertyValueFactory<>("timeIn"));
        timeOutTVColumn.setCellValueFactory(new PropertyValueFactory<>("timeOut"));
        workedTimeTVColumn.setCellValueFactory(new PropertyValueFactory<>("workedTime"));

        String query = "Select * from Attendance where employeeId = " +currentEmployeeId;

        ObservableList attendanceList = showAttendance(query);
       table.setItems(attendanceList);

    }

    public ObservableList showAttendance(String query) {

        ObservableList<Attendance> list = FXCollections.observableArrayList();
        try {
            Connection connection = DriverManager.getConnection(Function.databasePath);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            String timeOut = "";
            String workedTime = "";
            while (rs.next()) {
                Date date =  rs.getDate("attendanceDate");
                String day = date.toLocalDate().getDayOfWeek().toString();

                if(!rs.getString("timeOut").equals("null")){
                    String timeIn1 = rs.getString("timeIn");
                    String timeOut1 = rs.getString("timeOut");

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

                    LocalTime timeIn2 = LocalTime.parse(timeIn1, formatter);
                    LocalTime timeOut2 = LocalTime.parse(timeOut1, formatter);

                    LocalTime timeDifference = timeOut2.minusHours(timeIn2.getHour())
                        .minusMinutes(timeIn2.getMinute())
                        .minusSeconds(timeIn2.getSecond());

                    timeOut = timeOut1;
                    workedTime = timeDifference.toString();

                }else {
                    timeOut = "";
                    workedTime = "";
                }
                list.add(new Attendance(rs.getString("attendanceDate"),day,rs.getString("timeIn"),timeOut ,workedTime ) );
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


    public void refreshTableData() {
        String query = "Select * from Attendance where employeeId = " +currentEmployeeId;
        ObservableList attendanceList = showAttendance(query);
        table.setItems(attendanceList);
    }

    @FXML
    void onAddAttendanceClicked(ActionEvent event) throws IOException {
        Stage dialog = new Stage();
        Scene scene;
        Parent root;
        String fxmlName = null;

        if(LoginController.currentUserName.equals(LoginController.adminUserName)){
            fxmlName ="EmployeeModule/AddAttendance.fxml";
        } else if(!LoginController.currentUserName.equals(LoginController.adminUserName)){
            fxmlName ="EmployeeModule/NewAttendance.fxml";
        }

        root = FXMLLoader.load(SceneController.class.getResource(fxmlName));

        if(LoginController.currentUserName.equals(LoginController.adminUserName)){
            //This is setting the value of the static variable employeeProfileControllerInAddAttendanceController
            AddAttendanceController.setEmployeeProfileControllerInAddAttendanceController(this);
        } else if(!LoginController.currentUserName.equals(LoginController.adminUserName)){
            //This is setting the value of the static variable employeeProfileControllerInAddAttendanceController
            NewAttendanceController.setEmployeeProfileControllerInNewAttendanceController(this);
        }


        dialog.initModality(Modality.APPLICATION_MODAL);
        scene = new Scene(root);
        dialog.setScene(scene);
        dialog.initOwner(SceneController.stage);
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.resizableProperty().set(false);
        dialog.setTitle("Attendance");
        dialog.showAndWait();
    }

    @FXML
    void onDateSelected(ActionEvent event) {
        LocalDate selectedDate = searchDatePicker.getValue();

        String query;

        if (selectedDate != null) {
            query = "SELECT * FROM Attendance WHERE employeeId = " + currentEmployeeId + " AND attendanceDate = '"+selectedDate.toString()+"'";
        } else {
            query = "SELECT * FROM Attendance WHERE employeeId = " + currentEmployeeId;
        }

        ObservableList attendanceList = showAttendance(query);
        table.setItems(attendanceList);
    }

    @FXML
    void onRemoveAttendanceClicked(ActionEvent event) {

        if (table.getSelectionModel().isEmpty()) {
            Function.alert(Alert.AlertType.ERROR, "Error", "No Attendance Selected!!!");
        } else {
            Attendance attendance = table.getSelectionModel().getSelectedItem();
            JOptionPane JOptionPane = null;
            int choice = JOptionPane.showConfirmDialog(null, "Do you really want to Remove?", "Confirmation", JOptionPane.OK_CANCEL_OPTION);
            if (choice == JOptionPane.OK_OPTION) {
                try {
                    Connection connection = DriverManager.getConnection(Function.databasePath);
                    Statement statement = connection.createStatement();
                    String query = "Delete * from Attendance where employeeId = " + currentEmployeeId + " AND attendanceDate = '" + attendance.getDate() + "' ";
                    statement.execute(query);
                    Function.alert(Alert.AlertType.INFORMATION, "Congratulations", "Attendance Removed Successfully");
                    refreshTableData();

                    connection.close();
                    statement.close();

                } catch (SQLException e){
                    Function.alert(Alert.AlertType.ERROR,"Error",e.getMessage());
                }
            }
        }

    }

    @FXML
    void onUpdateEmployeeClicked(ActionEvent event) {
        name = nameField.getText().trim();
        phone1 = phoneNumber1Field.getText().trim();
        phone2 = phoneNumber2Field.getText().trim();
        userName = userNameField.getText().trim();
        password = passwordField.getText().trim();

        nameField.setEditable(true);
        phoneNumber1Field.setEditable(true);
        phoneNumber2Field.setEditable(true);
        userNameField.setEditable(true);
        passwordField.setEditable(true);

        addAttendanceButton.setDisable(true);
        removeAttendanceButton.setDisable(true);
        updateEmployeeButton.setDisable(true);
        removeEmployeeButton.setDisable(true);
        searchDatePicker.setDisable(true);
        backButton.setDisable(true);

        updateButton.setVisible(true);
        cancelButton.setVisible(true);
    }

    @FXML
    void onRemoveEmployeeClicked(ActionEvent event) {
        try {

            Connection connection = DriverManager.getConnection(Function.databasePath);
            Statement statement = connection.createStatement();

            JOptionPane JOptionPane = null;
            int choice = JOptionPane.showConfirmDialog(null, "Do you really want to Remove?", "Confirmation", JOptionPane.OK_CANCEL_OPTION);
            if (choice == JOptionPane.OK_OPTION) {

                String query = "Delete * from Employee where employeeId = "+ currentEmployeeId;
                statement.execute(query);

                Function.alert(Alert.AlertType.INFORMATION, "Congratulations", "Employee Removed Successfully");
                statement.close();
                connection.close();
                try {
                    SceneController.switchScene("EmployeeModule/Employee.fxml",event,"Employees");
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
    void onUpdateClicked(ActionEvent event) {

        try {

            Connection connection = DriverManager.getConnection(Function.databasePath);

            boolean fieldsNotEmpty = true;


            if (userNameField.getText().trim().isEmpty() || nameField.getText().trim().isEmpty() || passwordField.getText().trim().isEmpty()
                    || phoneNumber1Field.getText().trim().isEmpty()) {
                fieldsNotEmpty = false;
            }


                if (fieldsNotEmpty) {
                    if (checkAlphabets(nameField.getText().trim())) {
                        if (checkPhoneNo(phoneNumber1Field.getText().trim())) {
                            if (checkPhoneNo(phoneNumber2Field.getText().trim()) || phoneNumber2Field.getText().trim().isEmpty()) {

                                String query = "Update Employee set Employee_UserName= '"+userNameField.getText().trim()+"' , Employee_Name= '"+nameField.getText().trim()+"' ," +
                                        " Employee_Password= '"+passwordField.getText().trim()+"' , Employee_Ph_1= '"+phoneNumber1Field.getText().trim()+"' , " +
                                        " Employee_Ph_2= '"+phoneNumber2Field.getText().trim()+"' where employeeId = "+currentEmployeeId ;

                                Statement statement = connection.createStatement();
                                statement.execute(query);
                                Function.alert(Alert.AlertType.INFORMATION, "Congratulations", "Employee Updated successfully");
                                statement.close();
                                connection.close();

                                nameField.setEditable(false);
                                phoneNumber1Field.setEditable(false);
                                phoneNumber2Field.setEditable(false);
                                userNameField.setEditable(false);
                                passwordField.setEditable(false);

                                addAttendanceButton.setDisable(false);
                                removeAttendanceButton.setDisable(false);
                                updateEmployeeButton.setDisable(false);
                                removeEmployeeButton.setDisable(false);
                                searchDatePicker.setDisable(false);
                                backButton.setDisable(false);

                                updateButton.setVisible(false);
                                cancelButton.setVisible(false);


                            } else {
                                Function.alert(Alert.AlertType.ERROR, "Error", "Less or more than 11 digits & Special characters  are not allowed for Phone Number 2");
                                phoneNumber2Field.clear();
                                phoneNumber2Field.requestFocus();
                            }
                        } else {
                            Function.alert(Alert.AlertType.ERROR, "Error", "Less or more than 11 digits & Special characters  are not allowed for Phone Number 1");
                            phoneNumber1Field.clear();
                            phoneNumber1Field.requestFocus();
                        }
                    } else {
                        Function.alert(Alert.AlertType.ERROR, "Error", "Numbers or special characters are not allowed in name");
                        nameField.clear();
                        nameField.requestFocus();
                    }
                } else {
                    Function.alert(Alert.AlertType.ERROR, "Error", "Fill all the information");
                }


        } catch (SQLException e) {
            Function.alert(Alert.AlertType.ERROR, "Error", e.getMessage());

        }
    }

    @FXML
    void onCancelClicked(ActionEvent event) {
         nameField.setText(name);
         phoneNumber1Field.setText(phone1);
         phoneNumber2Field.setText(phone2);
         userNameField.setText(userName);
         passwordField.setText(password);

        nameField.setEditable(false);
        phoneNumber1Field.setEditable(false);
        phoneNumber2Field.setEditable(false);
        userNameField.setEditable(false);
        passwordField.setEditable(false);

        addAttendanceButton.setDisable(false);
        removeAttendanceButton.setDisable(false);
        updateEmployeeButton.setDisable(false);
        removeEmployeeButton.setDisable(false);
        searchDatePicker.setDisable(false);
        backButton.setDisable(false);

        updateButton.setVisible(false);
        cancelButton.setVisible(false);
    }

    @FXML
    void onBackClicked(ActionEvent event) {

        if(LoginController.currentUserName.equals(LoginController.adminUserName)){
         try {
            SceneController.switchScene("EmployeeModule/Employee.fxml",event,"Employees");
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
        }else if(!LoginController.currentUserName.equals(LoginController.adminUserName)){
            try {
                SceneController.switchScene("BillModule/Bills.fxml",event,"Bills");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
