package org.example.autopark;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("authorization-view.fxml"));
        Parent root = fxmlLoader.load();
        VBox authorizationContainer =
                (VBox) root.lookup("#authorizationContainer");
        Scene scene = new Scene(root);

        stage.setTitle("Autopark");
        stage.setScene(scene);

        stage.setMinWidth(authorizationContainer.getMinWidth());
        stage.setMinHeight(authorizationContainer.getMinHeight());
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}