package com.example.pos.EmployeeModule;

import com.example.pos.UtilityClasses.Function;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class AddAttendanceController implements Initializable {

    // Declaration of EmployeeProfileController Class static Object
    // so that we can call the method (refreshTableData) of the EmployeeProfileController Class on runtime
    private static EmployeeProfileController employeeProfileControllerInAddAttendanceController;

    // This Method is setting the value of the static object created above
    public static void setEmployeeProfileControllerInAddAttendanceController(EmployeeProfileController controller) {
        employeeProfileControllerInAddAttendanceController = controller;
    }

    @FXML
    private ComboBox<String> type;
    @FXML
    private DatePicker attendanceDate;
    @FXML
    private Spinner<Integer> hourSpinner,minuteSpinner;

    ObservableList<String> attendanceTypeList = FXCollections.observableArrayList("Time In", "Time Out");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        attendanceDate.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) > 0);
            }
        });

        type.setItems(attendanceTypeList);
        type.setValue("Time In");
        // Set up the SpinnerValueFactory for hours (range from 0 to 23)
        SpinnerValueFactory<Integer> hourValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, LocalTime.now().getHour());
        hourSpinner.setValueFactory(hourValueFactory);

        // Set up the SpinnerValueFactory for minutes (range from 0 to 59)
        SpinnerValueFactory<Integer> minuteValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, LocalTime.now().getMinute());
        minuteSpinner.setValueFactory(minuteValueFactory);


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
            String query1 = "Select * from Attendance where employeeId = " + EmployeeController.employeeId+ " AND attendanceDate = '"+attendanceDate.getValue().toString()+"' ";
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
            String hour= "", minute= "";
            if (hourSpinner.getValue()>=0 && hourSpinner.getValue()<=9){
                hour = "0"+hourSpinner.getValue();
            } else if (hourSpinner.getValue()>=10 && hourSpinner.getValue()<=23) {
                hour = hourSpinner.getValue().toString();
            }

            if (minuteSpinner.getValue()>=0 && minuteSpinner.getValue()<=9){
                minute = "0"+minuteSpinner.getValue();
            } else if (minuteSpinner.getValue()>=10 && minuteSpinner.getValue()<=59) {
                minute = minuteSpinner.getValue().toString();
            }

            if( attendanceType==1 && timeIn==false  ) {
                // Inserting new attendance record
                String query = "Insert into Attendance(employeeId, attendanceDate, timeIn, timeOut) values ( " + EmployeeController.employeeId + ", '" + attendanceDate.getValue().toString() + "', '" +hour+ ":" +minute+ "' , 'null' ) ";
                statement.execute(query);
                Function.alert(Alert.AlertType.INFORMATION,"Congratulations!!","Attendance Added");
                connection.close();
                statement.close();

                if (employeeProfileControllerInAddAttendanceController != null) {
                    employeeProfileControllerInAddAttendanceController.refreshTableData();
                }

                //Closing the Add Attendance User Interface
                Stage stage= (Stage) ((Node)event.getSource()).getScene().getWindow();
                stage.close();
            }else if(((attendanceType==0 && timeIn== true) && timeOut== false)){

                String query = "Update Attendance set timeOut =  '"+ hour + ":" + minute + "' where employeeId = "+EmployeeController.employeeId+" AND attendanceDate = '"+attendanceDate.getValue().toString()+"' ";
                statement.execute(query);
                Function.alert(Alert.AlertType.INFORMATION,"Congratulations!!","Attendance Added");
                connection.close();
                statement.close();

                if (employeeProfileControllerInAddAttendanceController != null) {
//                    System.out.println("One");
                    employeeProfileControllerInAddAttendanceController.refreshTableData();
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
