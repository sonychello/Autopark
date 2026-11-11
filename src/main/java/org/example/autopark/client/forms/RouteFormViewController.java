
package org.example.autopark.client.forms;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.autopark.exception.AccessException;
import org.example.autopark.model.Route;
import org.example.autopark.service.RoutesService;

import java.sql.SQLException;

public class RouteFormViewController {

    @FXML
    private TextField nameField;

    private final RoutesService service;
    private final Route editRoute;
    private Runnable onSuccess;

    public RouteFormViewController(RoutesService service) {
        this(service, null);
    }

    public RouteFormViewController(RoutesService service, Route editRoute) {
        this.service = service;
        this.editRoute = editRoute;
    }

    public void setOnSuccess(Runnable onSuccess) {
        this.onSuccess = onSuccess;
    }

    @FXML
    private void initialize() {
        if (editRoute != null) {
            nameField.setText(editRoute.name());
        }
    }

    @FXML
    private void onSaveClick() {
        String name = nameField.getText().trim();

        if (name.isBlank()) {
            showError(
                    "Invalid input",
                    "Missing information",
                    "Please enter route name."
            );
            return;
        }

        try {
            Route route;

            if (editRoute != null) {
                route = new Route(
                        editRoute.id(),
                        name
                );
                service.updateRoute(route);
            } else {
                route = new Route(
                        null,
                        name
                );
                service.createNewRoute(route);
            }

            if (onSuccess != null) {
                onSuccess.run();
            }

            ((Stage) nameField.getScene().getWindow()).close();

        } catch (AccessException e) {
            showError(
                    "Access denied",
                    "Operation failed",
                    e.getMessage()
            );
        } catch (SQLException e) {
            showError(
                    "Database error",
                    "Failed to save route",
                    "Не удалось сохранить маршрут. Проверьте введённые данные."
            );
        }
    }

    private void showError(String title, String header, String message) {
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setTitle(title);
        error.setHeaderText(header);
        error.setContentText(message);
        error.showAndWait();
    }
}
