
package org.example.autopark.client.forms;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.autopark.model.JournalRecord;
import org.example.autopark.service.OperatorJournalService;
import org.example.autopark.exception.AccessException;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class OperatorJournalFormViewController {

    @FXML
    private TextField timeOutField;

    @FXML
    private TextField autoIdField;

    @FXML
    private TextField routeIdField;

    @FXML
    private TextField timeInField;

    private final OperatorJournalService service;
    private final JournalRecord editRecord;

    private Runnable onSuccess;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm");

    public OperatorJournalFormViewController(OperatorJournalService service) {
        this(service, null);
    }

    public OperatorJournalFormViewController(
            OperatorJournalService service,
            JournalRecord record
    ) {
        this.service = service;
        this.editRecord = record;
    }

    @FXML
    private void initialize() {
        boolean isReturnMode = editRecord != null;

        timeInField.setVisible(isReturnMode);
        timeInField.setManaged(isReturnMode);

        timeOutField.setVisible(!isReturnMode);
        timeOutField.setManaged(!isReturnMode);

        autoIdField.setVisible(!isReturnMode);
        autoIdField.setManaged(!isReturnMode);

        routeIdField.setVisible(!isReturnMode);
        routeIdField.setManaged(!isReturnMode);

        if (isReturnMode && editRecord.time_in() != null) {
            timeInField.setText(
                    editRecord.time_in().toLocalDateTime().format(DATE_FORMATTER)
            );
        }
    }

    public void setOnSuccess(Runnable onSuccess) {
        this.onSuccess = onSuccess;
    }

    @FXML
    private void onSaveClick() {
        try {
            if (editRecord == null) {
                String timeOutText = timeOutField.getText().trim();
                String autoIdText = autoIdField.getText().trim();
                String routeIdText = routeIdField.getText().trim();

                if (timeOutText.isEmpty() || autoIdText.isEmpty() || routeIdText.isEmpty()) {
                    throw new IllegalArgumentException("Fill in all fields.");
                }

                Timestamp timeOut = parseTimestamp(timeOutText);

                JournalRecord record = getJournalRecord(autoIdText, routeIdText, timeOut);

                service.createNewRecord(record);
            } else {
                String timeInText = timeInField.getText().trim();

                if (timeInText.isEmpty()) {
                    throw new IllegalArgumentException("Enter the arrival time.");
                }

                Timestamp timeIn = parseTimestamp(timeInText);

                JournalRecord updatedRecord = new JournalRecord(
                        editRecord.id(),
                        editRecord.time_out(),
                        timeIn,
                        editRecord.auto_id(),
                        editRecord.route_id()
                );

                service.setTimeInOfRecord(updatedRecord);
            }

            if (onSuccess != null) {
                onSuccess.run();
            }

            ((Stage) timeInField.getScene().getWindow()).close();

        } catch (NumberFormatException e) {
            showError("Auto ID and Route ID must be integers.");
        } catch (DateTimeParseException e) {
            showError("Invalid date or time format. Use dd.MM.yyyy HH:mm.");
        } catch (IllegalArgumentException | AccessException e) {
            showError(e.getMessage());
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        }
    }

    private static JournalRecord getJournalRecord(String autoIdText, String routeIdText, Timestamp timeOut) {
        int autoId = Integer.parseInt(autoIdText);
        int routeId = Integer.parseInt(routeIdText);

        if (autoId <= 0 || routeId <= 0) {
            throw new IllegalArgumentException("Auto ID and Route ID must be positive.");
        }

        return new JournalRecord(
                0,
                timeOut,
                null,
                autoId,
                routeId
        );
    }



    private Timestamp parseTimestamp(String value) {
        LocalDateTime dateTime = LocalDateTime.parse(value, DATE_FORMATTER);
        return Timestamp.valueOf(dateTime);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
