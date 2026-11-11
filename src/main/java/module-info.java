module org.example.autopark {
    requires javafx.controls;
    requires javafx.fxml;
    requires jbcrypt;
    requires com.fasterxml.jackson.databind;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires java.instrument;


    opens org.example.autopark to javafx.fxml;
    opens org.example.autopark.client to javafx.fxml;
    exports org.example.autopark;
    exports org.example.autopark.client;
    exports org.example.autopark.client.directoriesControllers;
    opens org.example.autopark.client.directoriesControllers to javafx.fxml;
    exports org.example.autopark.client.journalsControllers;
    opens org.example.autopark.client.journalsControllers to javafx.fxml;
    exports org.example.autopark.client.ReportsControllers;
    opens org.example.autopark.client.ReportsControllers to javafx.fxml;
    opens org.example.autopark.model to com.fasterxml.jackson.databind;
    opens org.example.autopark.client.forms to javafx.fxml;
}