module com.example.pos {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires jdk.httpserver;



    exports com.example.pos;
    exports com.example.pos.BillModule;
    exports com.example.pos.CustomerModule;
    exports com.example.pos.InventoryModule;
    exports com.example.pos.EmployeeModule;
    exports com.example.pos.VendorModule;
    exports com.example.pos.ReportModule;
    exports com.example.pos.UtilityClasses;
    exports com.example.pos.LoginModule;

    opens com.example.pos to javafx.fxml;
    opens com.example.pos.BillModule to javafx.fxml;
    opens com.example.pos.EmployeeModule to javafx.fxml;
    opens com.example.pos.CustomerModule to javafx.fxml;
    opens com.example.pos.InventoryModule to javafx.fxml;
    opens com.example.pos.ReportModule to javafx.fxml;
    opens com.example.pos.VendorModule to javafx.fxml;
    opens com.example.pos.UtilityClasses to javafx.fxml;
    opens com.example.pos.LoginModule to javafx.fxml;
}