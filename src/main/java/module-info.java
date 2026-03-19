module com.kipcollo.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;


    opens com.kipcollo.demo to javafx.fxml;
    exports com.kipcollo.demo;
}