package com.example.pos.EmployeeModule;

import com.example.pos.UtilityClasses.Function;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class NewAttendanceController implements Initializable {

    // Declaration of EmployeeProfileController Class static Object
    // so that we can call the method (refreshTableData) of the EmployeeProfileController Class on runtime
    private static EmployeeProfileController employeeProfileControllerInNewAttendanceController;

    // This Method is setting the value of the static object created above
    public static void setEmployeeProfileControllerInNewAttendanceController(EmployeeProfileController controller) {
        employeeProfileControllerInNewAttendanceController = controller;
    }

    public static int employeeId;
    @FXML
    private ComboBox<String> type;

    ObservableList<String> attendanceTypeList = FXCollections.observableArrayList("Time In", "Time Out");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        type.setItems(attendanceTypeList);
        type.setValue("Time In");
    }


    @FXML
    void onAddClicked(ActionEvent event) {

        try {

            int attendanceType=-1;
            if(type.getValue().equals("Time In")){
                attendanceType = 1;
            }else if(type.getValue().equals("Time Out")){
                attendanceType = 0;
            }

            boolean timeIn= false;
            boolean timeOut= false;

            Connection connection1 = DriverManager.getConnection(Function.databasePath);
            Statement statement1 = connection1.createStatement();
            String query1 = "Select * from Attendance where employeeId = " + employeeId+ " AND attendanceDate = '"+LocalDate.now().toString()+"' ";
            ResultSet rs= statement1.executeQuery(query1);
            while (rs.next()){
                if(!rs.getString("timeIn").isEmpty()){
                    timeIn= true;
                }else  if(!rs.getString("timeOut").isEmpty()){
                    timeOut= true;
                }
            }

            connection1.close();
            statement1.close();

            Connection connection = DriverManager.getConnection(Function.databasePath);
            Statement statement = connection.createStatement();


            if( attendanceType==1 && timeIn==false  ) {

                // Inserting new attendance record
                String query = "Insert into Attendance(employeeId, attendanceDate, timeIn, timeOut) values ( " + employeeId + ", '" + LocalDate.now().toString() + "', '" +LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")).toString()+ "', 'null' ) ";
                statement.execute(query);
                Function.alert(Alert.AlertType.INFORMATION,"Congratulations!!","Attendance Added");
                connection.close();
                statement.close();

                if (employeeProfileControllerInNewAttendanceController != null) {
                    employeeProfileControllerInNewAttendanceController.refreshTableData();
                }

                //Closing the Add Attendance User Interface
                Stage stage= (Stage) ((Node)event.getSource()).getScene().getWindow();
                stage.close();
            }else if(((attendanceType==0 && timeIn== true) && timeOut== false)){

                String query = "Update Attendance set timeOut =  '"+LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")).toString()+ "' where employeeId = "+employeeId+" AND attendanceDate = '"+LocalDate.now().toString()+"' ";
                statement.execute(query);
                Function.alert(Alert.AlertType.INFORMATION,"Congratulations!!","Attendance Added");
                connection.close();
                statement.close();

                if (employeeProfileControllerInNewAttendanceController != null) {
                    employeeProfileControllerInNewAttendanceController.refreshTableData();
                }

                //Closing the Add Attendance User Interface
                Stage stage= (Stage) ((Node)event.getSource()).getScene().getWindow();
                stage.close();

            } else if(attendanceType==1 && timeIn==true){
                Function.alert(Alert.AlertType.ERROR,"Error","You have already Timed In");
            } else if(attendanceType==0 && timeIn==false){
                Function.alert(Alert.AlertType.ERROR,"Error","You have not Timed In yet, Please first Time In");
            }else if(((attendanceType==0 && timeIn== true) && timeOut== true)){
                Function.alert(Alert.AlertType.ERROR,"Error","You have already Timed Out");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            Function.alert(Alert.AlertType.ERROR,"Error",e.getMessage());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void onCancelClicked(ActionEvent event) {
        Stage stage= (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }


}
