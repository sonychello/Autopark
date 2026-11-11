package org.example.autopark.client.directoriesControllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.autopark.client.forms.CarFormViewController;
import org.example.autopark.exception.AccessException;
import org.example.autopark.model.Car;
import org.example.autopark.service.CarsService;

import java.io.IOException;
import java.sql.SQLException;

public class CarsViewController {
    @FXML
    private TableView<Car> carsTable;

    @FXML
    private TableColumn<Car, Integer> idColumn;

    @FXML
    private TableColumn<Car, String> numberColumn;

    @FXML
    private TableColumn<Car, String> markColumn;

    @FXML
    private TableColumn<Car, String> colorColumn;

    @FXML
    private TableColumn<Car, Integer> personnelIdColumn;

    @FXML
    private Button insertButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    private final CarsService service;

    public CarsViewController(CarsService service) {
        this.service = service;
    }

    @FXML
    private void initialize() {
        carsTable.setColumnResizePolicy(
                TableView.UNCONSTRAINED_RESIZE_POLICY
        );

        idColumn.setCellValueFactory(
                cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().id()));

        numberColumn.setCellValueFactory(
                cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().num()));

        colorColumn.setCellValueFactory(
                cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().color()));

        markColumn.setCellValueFactory(
                cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().mark()));

        personnelIdColumn.setCellValueFactory(
                cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().personal_id()));

        carsTable.getItems().setAll(service.getData(0));

        carsTable.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double width = newWidth.doubleValue();

            idColumn.setPrefWidth(width * 0.10);
            numberColumn.setPrefWidth(width * 0.20);
            colorColumn.setPrefWidth(width * 0.30);
            markColumn.setPrefWidth(width * 0.30);
            personnelIdColumn.setPrefWidth(width * 0.10);
        });

        boolean isAdmin = service.isAdmin();

        insertButton.setVisible(isAdmin);
        editButton.setVisible(isAdmin);
        deleteButton.setVisible(isAdmin);
    }

    @FXML
    private void onDeleteClick() {
        Car selectedCar = carsTable.getSelectionModel().getSelectedItem();

        if (selectedCar == null) {
            showError(
                    "Car deletion",
                    "No car selected",
                    "Для удаления нужно выбрать строку в таблице"
            );
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete car");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("Delete car with number " + selectedCar.num() + "?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        try {
            service.deleteCar(selectedCar);
            carsTable.getItems().remove(selectedCar);
        } catch (AccessException e) {
            showError(
                    "Access denied",
                    "Operation failed",
                    e.getMessage()
            );
        } catch (SQLException e) {
            showError(
                    "Car deletion",
                    "Failed to delete car",
                    "Не удалось удалить автомобиль. Возможно, он используется в других записях."
            );
        }
    }

    @FXML
    private void onInsertClick() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/autopark/forms/car-form-view.fxml"));
        loader.setControllerFactory(controllerClass -> {
            if (controllerClass == CarFormViewController.class) {
                return new CarFormViewController(service);
            }
            throw new IllegalArgumentException("Unknown controller: " + controllerClass);
        });
        try {
            Parent root = loader.load();
            CarFormViewController controller = loader.getController();
            controller.setOnSuccess(() -> carsTable.getItems().setAll(service.getData(0)));
            Stage stage = new Stage();
            stage.setTitle("Add car");
            stage.setScene(new Scene(root));
            stage.initOwner(carsTable.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();
        } catch (IOException e) {
            showError(
                    "Window error",
                    "Failed to open car form",
                    "Не удалось открыть форму добавления автомобиля.\n" + e.getMessage()
            );
        }

    }

    @FXML
    private void onEditClick() {
        Car selectedCar = carsTable.getSelectionModel().getSelectedItem();

        if (selectedCar == null) {
            showError(
                    "Car editing",
                    "No car selected",
                    "Для редактирования нужно выбрать строку в таблице"
            );
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/autopark/forms/car-form-view.fxml"));
        loader.setControllerFactory(controllerClass -> {
            if (controllerClass == CarFormViewController.class) {
                return new CarFormViewController(service, selectedCar);
            }
            throw new IllegalArgumentException("Unknown controller: " + controllerClass);
        });
        try {
            Parent root = loader.load();
            CarFormViewController controller = loader.getController();
            controller.setOnSuccess(() -> carsTable.getItems().setAll(service.getData(0)));
            Stage stage = new Stage();
            stage.setTitle("Edit car");
            stage.setScene(new Scene(root));
            stage.initOwner(carsTable.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();
        } catch (IOException e) {
            showError(
                    "Window error",
                    "Failed to open car form",
                    "Не удалось открыть форму редактирования автомобиля.\n" + e.getMessage()
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

