package org.example.autopark.client.directoriesControllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.autopark.client.forms.RouteFormViewController;
import org.example.autopark.exception.AccessException;
import org.example.autopark.model.Route;
import org.example.autopark.service.RoutesService;

import java.io.IOException;
import java.sql.SQLException;

public class RoutesViewController {
    @FXML
    private TableView<Route> routeTable;

    @FXML
    private TableColumn<Route, Integer> idColumn;

    @FXML
    private TableColumn<Route, String> nameColumn;

    @FXML
    private Button insertButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    private final RoutesService service;

    public RoutesViewController(RoutesService service) {
        this.service = service;
    }

    @FXML
    private void initialize() {
        routeTable.setColumnResizePolicy(
                TableView.UNCONSTRAINED_RESIZE_POLICY
        );

        idColumn.setCellValueFactory(
                cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().id()));

        nameColumn.setCellValueFactory(
                cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().name()));

        routeTable.getItems().setAll(service.getData(0));

        routeTable.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double width = newWidth.doubleValue();

            idColumn.setPrefWidth(width * 0.20);
            nameColumn.setPrefWidth(width * 0.80);
        });

        boolean isAdmin = service.isAdmin();

        insertButton.setVisible(isAdmin);
        editButton.setVisible(isAdmin);
        deleteButton.setVisible(isAdmin);

        routeTable.setOnKeyPressed(event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.C) {
                Route selectedRoute = routeTable.getSelectionModel().getSelectedItem();

                if (selectedRoute != null) {
                    ClipboardContent content = new ClipboardContent();
                    content.putString(selectedRoute.name());

                    Clipboard.getSystemClipboard().setContent(content);
                }
            }
        });
    }

    @FXML
    private void onDeleteClick() {
        Route selectedRoute = routeTable.getSelectionModel().getSelectedItem();

        if (selectedRoute == null) {
            showError(
                    "Route deletion",
                    "No route selected",
                    "Для удаления нужно выбрать строку в таблице"
            );
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete route");
        alert.setHeaderText("Are you sure?");
        alert.setContentText(
                "Delete route " + selectedRoute.name() + "?"
        );

        if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        try {
            service.deleteRoute(selectedRoute);
            routeTable.getItems().remove(selectedRoute);
        } catch (AccessException e) {
            showError(
                    "Access denied",
                    "Operation failed",
                    e.getMessage()
            );
        } catch (SQLException e) {
            showError(
                    "Route deletion",
                    "Failed to delete route",
                    "Не удалось удалить маршрут. Возможно, он используется в других записях."
            );
        }
    }

    @FXML
    private void onInsertClick() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/autopark/forms/route-form-view.fxml"));
        loader.setControllerFactory(controllerClass -> {
            if (controllerClass == RouteFormViewController.class) {
                return new RouteFormViewController(service);
            }
            throw new IllegalArgumentException("Unknown controller: " + controllerClass);
        });
        try {
            Parent root = loader.load();
            RouteFormViewController controller = loader.getController();
            controller.setOnSuccess(() -> routeTable.getItems().setAll(service.getData(0)));
            Stage stage = new Stage();
            stage.setTitle("Add route");
            stage.setScene(new Scene(root));
            stage.initOwner(routeTable.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();
        } catch (IOException e) {
            showError(
                    "Window error",
                    "Failed to open route form",
                    "Не удалось открыть форму добавления маршрута.\n" + e.getMessage()
            );
        }

    }

    @FXML
    private void onEditClick() {
        Route selectedRoute = routeTable.getSelectionModel().getSelectedItem();

        if (selectedRoute == null) {
            showError(
                    "Route editing",
                    "No route selected",
                    "Для редактирования нужно выбрать строку в таблице"
            );
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/autopark/forms/route-form-view.fxml"));
        loader.setControllerFactory(controllerClass -> {
            if (controllerClass == RouteFormViewController.class) {
                return new RouteFormViewController(service, selectedRoute);
            }
            throw new IllegalArgumentException("Unknown controller: " + controllerClass);
        });
        try {
            Parent root = loader.load();
            RouteFormViewController controller = loader.getController();
            controller.setOnSuccess(() -> routeTable.getItems().setAll(service.getData(0)));
            Stage stage = new Stage();
            stage.setTitle("Edit route");
            stage.setScene(new Scene(root));
            stage.initOwner(routeTable.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();
        } catch (IOException e) {
            showError(
                    "Window error",
                    "Failed to open route form",
                    "Не удалось открыть форму редактирования маршрута.\n" + e.getMessage()
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
