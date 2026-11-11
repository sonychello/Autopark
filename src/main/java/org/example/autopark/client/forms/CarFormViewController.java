package org.example.autopark.client.forms;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.autopark.exception.AccessException;
import org.example.autopark.model.Car;
import org.example.autopark.service.CarsService;

import java.sql.SQLException;

public class CarFormViewController {

    @FXML
    private TextField numberField;

    @FXML
    private TextField colorField;

    @FXML
    private ComboBox<String> markComboBox;

    @FXML
    private TextField personnelIdField;

    private final CarsService service;

    private Car editCar;

    private Runnable onSuccess;

    public CarFormViewController(CarsService service, Car editCar) {
        this.service = service;
        this.editCar = editCar;
    }

    public CarFormViewController(CarsService service) {
        this.service = service;
    }

    public void setOnSuccess(Runnable onSuccess) {
        this.onSuccess = onSuccess;
    }

    @FXML
    private void initialize() {
        markComboBox.getItems().addAll("Газель", "Ford", "Mercedes");

        if (editCar != null) {
            numberField.setText(editCar.num());
            colorField.setText(editCar.color());
            markComboBox.setValue(editCar.mark());
            personnelIdField.setText(String.valueOf(editCar.personal_id()));
        }
    }


    @FXML
    private void onSaveClick() {
        String number = numberField.getText().trim();
        String color = colorField.getText().trim();
        String mark = markComboBox.getValue();
        String personnelIdText = personnelIdField.getText().trim();

        if (number.isBlank() || color.isBlank() || mark == null || personnelIdText.isBlank()) {
            showError(
                    "Invalid input",
                    "Missing information",
                    "Please fill in all fields."
            );
            return;
        }

        try {
            int personnelId = Integer.parseInt(personnelIdText);

            if (personnelId <= 0) {
                throw new IllegalArgumentException();
            }

            Car car;

            if (editCar != null) {
                car = new Car(
                        editCar.id(),
                        number,
                        color,
                        mark,
                        personnelId
                );
                service.updateCar(car);
            } else {
                car = new Car(
                        null,
                        number,
                        color,
                        mark,
                        personnelId
                );
                service.createNewCar(car);
            }

            if (onSuccess != null) {
                onSuccess.run();
            }

            ((Stage) numberField.getScene().getWindow()).close();

        } catch (IllegalArgumentException e) {
            showError(
                    "Invalid input",
                    "Incorrect Personnel ID",
                    "Please enter a valid personnel id."
            );
        } catch (AccessException e) {
            showError(
                    "Access denied",
                    "Operation failed",
                    e.getMessage()
            );
        } catch (SQLException e) {
            showError(
                    "Database error",
                    "Failed to save car",
                    "Не удалось сохранить автомобиль. Проверьте введённые данные."
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
