package org.example.autopark.client.ReportsControllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.example.autopark.model.RecordTimeAndAuto;
import org.example.autopark.service.ReportsService;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class ReportsViewController {
    public enum ReportType {
        RECORDS,
        CARS_ON_ROUTE
    }

    @FXML
    private Label label;

    @FXML
    private TextField routeNameField;

    @FXML
    private VBox reportContainer;

    @FXML
    private Button saveButton;

    @FXML
    private ScrollPane reportScroll;

    private final ReportType reportType;

    private final ReportsService service;

    private final StringBuilder reportText =  new StringBuilder();

    public ReportsViewController(ReportsService service, ReportType reportType) {
        this.service = service;
        this.reportType = reportType;
    }

    @FXML
    private void initialize() {
        reportContainer.getChildren().clear();
        switch (reportType) {
            case RECORDS -> label.setText("Record time by route");
            case CARS_ON_ROUTE -> label.setText("Count auto at route");
        }
    }

    @FXML
    private void onGenerateClick() {

        String routeName = routeNameField.getText().trim();
        if (routeName.isBlank()) {
            showError(
                    "Input error",
                    "Route name is empty",
                    "Please enter a route name."
            );
            return;
        }

        switch (reportType) {
            case RECORDS -> {
                RecordTimeAndAuto rcd = service.getRecordTimeAndAuto(routeName);
                if (rcd == null) {
                    showError(
                            "Report generation",
                            "No route records found",
                            "There are no completed trips for this route. No record time is available."
                    );
                    return;
                }

                String report = routeName + "\n" +
                        "Record Time: " + rcd.time() + "\n" +
                        "Auto number: " + rcd.num() + "\n";
                reportContainer.getChildren().add(new Label(report));
                reportText.append(report);

            }
            case CARS_ON_ROUTE -> {
                int cnt = service.countAuto(routeName);
                String report = routeName + "\n" +
                        "Cars on route: " + cnt + "\n";
                reportContainer.getChildren().add(new Label(report));
                reportText.append(report);
            }
        }
        saveButton.setVisible(true);
        saveButton.setManaged(true);

        reportContainer.setVisible(true);
        reportContainer.setManaged(true);

        reportScroll.setVisible(true);
        reportScroll.setManaged(true);

    }

    @FXML
    private void onSaveClick() {

        if (reportText.isEmpty()) {
            showError(
                    "Save report",
                    "Report is empty",
                    "Please generate at least one report before saving."
            );
            return;
        }

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Save report");
        fileChooser.setInitialFileName("Report.txt");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt")
        );

        File file = fileChooser.showSaveDialog(
                saveButton.getScene().getWindow()
        );

        if (file == null) {
            return;
        }

        try {
            Files.writeString(
                    file.toPath(),
                    reportText.toString(),
                    StandardCharsets.UTF_8
            );

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Report saved");
            alert.setHeaderText("Report successfully saved");
            alert.setContentText("File: " + file.getAbsolutePath());
            alert.showAndWait();

        } catch (IOException e) {
            showError(
                    "Saving error",
                    "Failed to save report",
                    e.getMessage()
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
