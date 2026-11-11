package org.example.autopark.client.journalsControllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.autopark.client.forms.OperatorJournalFormViewController;
import org.example.autopark.model.JournalRecord;
import org.example.autopark.service.OperatorJournalService;

import java.io.IOException;
import java.sql.Timestamp;

public class OperatorJournalViewController {
    @FXML
    private TableView<JournalRecord> journalTable;

    @FXML
    private TableColumn<JournalRecord, Integer> idColumn;

    @FXML
    private TableColumn<JournalRecord, Timestamp> timeOutColumn;

    @FXML
    private TableColumn<JournalRecord, Timestamp> timeInColumn;

    @FXML
    private TableColumn<JournalRecord, Integer> autoIdColumn;

    @FXML
    private TableColumn<JournalRecord, Integer> routeIdColumn;

    @FXML
    private Button sendButton;

    @FXML
    private Button timeButton;

    private final OperatorJournalService service;

    public OperatorJournalViewController(OperatorJournalService service) {
        this.service = service;
    }

    @FXML
    private void initialize() {
        journalTable.setColumnResizePolicy(
                TableView.UNCONSTRAINED_RESIZE_POLICY
        );

        idColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().id()));

        timeInColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().time_in()));

        timeOutColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().time_out()));

        autoIdColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().auto_id()));

        routeIdColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().route_id()));

        journalTable.getItems().setAll(service.getData(0));

        journalTable.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double width = newWidth.doubleValue();

            idColumn.setPrefWidth(width * 0.10);
            timeOutColumn.setPrefWidth(width * 0.35);
            timeInColumn.setPrefWidth(width * 0.35);
            autoIdColumn.setPrefWidth(width * 0.10);
            routeIdColumn.setPrefWidth(width * 0.10);
        });

        boolean isAdmin = service.isAdmin();

        sendButton.setVisible(isAdmin);
        timeButton.setVisible(isAdmin);
    }

    @FXML
    private void onSendOffClick() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/autopark/forms/operator-journal-form-view.fxml"));
        loader.setControllerFactory(controllerClass -> {
            if (controllerClass == OperatorJournalFormViewController.class) {
                return new OperatorJournalFormViewController(service);
            }
            throw new IllegalArgumentException("Unknown controller: " + controllerClass);
        });
        try {
            Parent root = loader.load();
            OperatorJournalFormViewController controller = loader.getController();
            controller.setOnSuccess(() -> journalTable.getItems().setAll(service.getData(0)));
            Stage stage = new Stage();
            stage.setTitle("Send car to route");
            stage.setScene(new Scene(root));
            stage.initOwner(journalTable.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError(
                    "Window error",
                    "Failed to open journal form",
                    "Не удалось открыть форму отправления автомобиля в рейс.\n" + e.getMessage()
            );
        }
    }

    @FXML
    private void onSetTimeInClick() {
        JournalRecord selectedRecord = journalTable.getSelectionModel().getSelectedItem();

        if (selectedRecord == null) {
            showError(
                    "Set time in journal",
                    "No journal record selected",
                    "Для редактирования нужно выбрать строку в таблице"
            );
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/autopark/forms/operator-journal-form-view.fxml"));
        loader.setControllerFactory(controllerClass -> {
            if (controllerClass == OperatorJournalFormViewController.class) {
                return new OperatorJournalFormViewController(service, selectedRecord);
            }
            throw new IllegalArgumentException("Unknown controller: " + controllerClass);
        });
        try {
            Parent root = loader.load();
            OperatorJournalFormViewController controller = loader.getController();
            controller.setOnSuccess(() -> journalTable.getItems().setAll(service.getData(0)));
            Stage stage = new Stage();
            stage.setTitle("Set time in journal");
            stage.setScene(new Scene(root));
            stage.initOwner(journalTable.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError(
                    "Window error",
                    "Failed to open journal form",
                    "Не удалось открыть форму установления времени прибытия.\n" + e.getMessage()
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
