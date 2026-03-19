module com.kipcollo.demo {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.kipcollo.demo to javafx.fxml;
    exports com.kipcollo.demo;
}