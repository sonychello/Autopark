
package org.example.autopark.client.forms;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.autopark.model.Personnel;
import org.example.autopark.service.PersonnelService ;
import org.example.autopark.exception.AccessException;

import java.sql.SQLException;

public class PersonnelFormViewController {

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField fatherNameField;

    private final PersonnelService service;
    private final Personnel editPersonnel;
    private Runnable onSuccess;

    public PersonnelFormViewController(PersonnelService service) {
        this(service, null);
    }

    public PersonnelFormViewController(PersonnelService service, Personnel editPersonnel) {
        this.service = service;
        this.editPersonnel = editPersonnel;
    }

    public void setOnSuccess(Runnable onSuccess) {
        this.onSuccess = onSuccess;
    }

    @FXML
    private void initialize() {
        if (editPersonnel != null) {
            firstNameField.setText(editPersonnel.firstName());
            lastNameField.setText(editPersonnel.lastName());
            fatherNameField.setText(editPersonnel.fatherName());
        }
    }

    @FXML
    private void onSaveClick() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String fatherName = fatherNameField.getText().trim();

        if (firstName.isBlank() || lastName.isBlank() || fatherName.isBlank()) {
            showError(
                    "Invalid input",
                    "Missing information",
                    "Please fill in all fields."
            );
            return;
        }

        try {
            Personnel personnel;

            if (editPersonnel != null) {
                personnel = new Personnel(
                        editPersonnel.id(),
                        firstName,
                        lastName,
                        fatherName
                );
                service.updatePersonnel(personnel);
            } else {
                personnel = new Personnel(
                        null,
                        firstName,
                        lastName,
                        fatherName
                );
                service.createNewPersonnel(personnel);
            }

            if (onSuccess != null) {
                onSuccess.run();
            }

            ((Stage) firstNameField.getScene().getWindow()).close();

        } catch (AccessException e) {
            showError(
                    "Access denied",
                    "Operation failed",
                    e.getMessage()
            );
        } catch (SQLException e) {
            showError(
                    "Database error",
                    "Failed to save personnel",
                    "Не удалось сохранить запись. Проверьте введённые данные."
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
