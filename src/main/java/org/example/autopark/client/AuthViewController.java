package org.example.autopark.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.autopark.ApplicationContext;
import org.example.autopark.model.User;
import org.example.autopark.service.AuthService;

import java.io.IOException;
import java.util.Optional;

public class AuthViewController {
    private static final AuthService authService = new AuthService();

    @FXML
    private TextField inputNickName;

    @FXML
    private PasswordField inputPassword;

    @FXML
    private Label logInInformation;

    @FXML
    private void onLoginClick() throws IOException {
        String userNickname = inputNickName.getText();
        String userPassword = inputPassword.getText();

        Optional<User> authenticatedUser =
                authService.checkUser(userNickname, userPassword);

        if (authenticatedUser.isEmpty()) {
            logInInformation.setText("Incorrect nick name or password");
            return;
        }

        User user = authenticatedUser.get();
        logInInformation.setText("Successful log in!");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/autopark/main-view.fxml"));
        loader.setControllerFactory(controllerClass -> {
            if (controllerClass == MainViewController.class) {
                return new MainViewController(new ApplicationContext(user));
            }
            throw new IllegalArgumentException("Unknown controller: " + controllerClass);
        });

        Stage stage = new Stage();
        stage.setTitle("Autopark");
        stage.setScene(new Scene(loader.load()));
        stage.show();
        ((Stage) inputPassword.getScene().getWindow()).close();

    }
}
