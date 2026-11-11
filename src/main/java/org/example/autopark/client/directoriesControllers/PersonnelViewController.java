package org.example.autopark.client.directoriesControllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.autopark.client.forms.PersonnelFormViewController;
import org.example.autopark.exception.AccessException;
import org.example.autopark.model.Personnel;
import org.example.autopark.service.PersonnelService;

import java.io.IOException;
import java.sql.SQLException;

public class PersonnelViewController {
    @FXML
    private TableView<Personnel> personnelTable;

    @FXML
    private TableColumn<Personnel, Integer> idColumn;

    @FXML
    private TableColumn<Personnel, String> nameColumn;

    @FXML
    private TableColumn<Personnel, String> lastNameColumn;

    @FXML
    private TableColumn<Personnel, String> fatherNameColumn;

    @FXML
    private Button insertButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    private final PersonnelService service;

    public PersonnelViewController(PersonnelService service) {
        this.service = service;
    }

    @FXML
    private void initialize() {
        personnelTable.setColumnResizePolicy(
                TableView.UNCONSTRAINED_RESIZE_POLICY
        );

        idColumn.setCellValueFactory(
                cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().id()));

        nameColumn.setCellValueFactory(
                cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().firstName()));

        lastNameColumn.setCellValueFactory(
                cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().lastName()));

        fatherNameColumn.setCellValueFactory(
                cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().fatherName()));

        personnelTable.getItems().setAll(service.getData(0));

        personnelTable.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double width = newWidth.doubleValue();

            idColumn.setPrefWidth(width * 0.10);
            nameColumn.setPrefWidth(width * 0.25);
            lastNameColumn.setPrefWidth(width * 0.30);
            fatherNameColumn.setPrefWidth(width * 0.35);
        });

        boolean isAdmin = service.isAdmin();

        insertButton.setVisible(isAdmin);
        editButton.setVisible(isAdmin);
        deleteButton.setVisible(isAdmin);
    }

    @FXML
    private void onDeleteClick() {
        Personnel selectedPersonnel = personnelTable.getSelectionModel().getSelectedItem();

        if (selectedPersonnel == null) {
            showError(
                    "Personnel deletion",
                    "No personnel selected",
                    "Для удаления нужно выбрать строку в таблице"
            );
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete personnel");
        alert.setHeaderText("Are you sure?");
        alert.setContentText(
                "Delete personnel " + selectedPersonnel.lastName()
                        + " " + selectedPersonnel.firstName() + "?"
        );

        if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        try {
            service.deletePersonnel(selectedPersonnel);
            personnelTable.getItems().remove(selectedPersonnel);
        } catch (AccessException e) {
            showError(
                    "Access denied",
                    "Operation failed",
                    e.getMessage()
            );
        } catch (SQLException e) {
            showError(
                    "Personnel deletion",
                    "Failed to delete personnel",
                    "Не удалось удалить водителя. Возможно, он используется в других записях."
            );
        }
    }

    @FXML
    private void onInsertClick() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/autopark/forms/personnel-form-view.fxml"));
        loader.setControllerFactory(controllerClass -> {
            if (controllerClass == PersonnelFormViewController.class) {
                return new PersonnelFormViewController(service);
            }
            throw new IllegalArgumentException("Unknown controller: " + controllerClass);
        });
        try {
            Parent root = loader.load();
            PersonnelFormViewController controller = loader.getController();
            controller.setOnSuccess(() -> personnelTable.getItems().setAll(service.getData(0)));
            Stage stage = new Stage();
            stage.setTitle("Add personnel");
            stage.setScene(new Scene(root));
            stage.initOwner(personnelTable.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();
        } catch (IOException e) {
            showError(
                    "Window error",
                    "Failed to open personnel form",
                    "Не удалось открыть форму добавления водителя.\n" + e.getMessage()
            );
        }

    }

    @FXML
    private void onEditClick() {
        Personnel selectedPersonnel = personnelTable.getSelectionModel().getSelectedItem();

        if (selectedPersonnel == null) {
            showError(
                    "Personnel editing",
                    "No personnel selected",
                    "Для редактирования нужно выбрать строку в таблице"
            );
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/autopark/forms/personnel-form-view.fxml"));
        loader.setControllerFactory(controllerClass -> {
            if (controllerClass == PersonnelFormViewController.class) {
                return new PersonnelFormViewController(service, selectedPersonnel);
            }
            throw new IllegalArgumentException("Unknown controller: " + controllerClass);
        });
        try {
            Parent root = loader.load();
            PersonnelFormViewController controller = loader.getController();
            controller.setOnSuccess(() -> personnelTable.getItems().setAll(service.getData(0)));
            Stage stage = new Stage();
            stage.setTitle("Edit personnel");
            stage.setScene(new Scene(root));
            stage.initOwner(personnelTable.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();
        } catch (IOException e) {
            showError(
                    "Window error",
                    "Failed to open car form",
                    "Не удалось открыть форму редактирования водителя.\n" + e.getMessage()
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
