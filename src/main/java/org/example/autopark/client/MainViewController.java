
package org.example.autopark.client;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import org.example.autopark.ApplicationContext;
import org.example.autopark.client.ReportsControllers.ReportsViewController;
import org.example.autopark.client.directoriesControllers.CarsViewController;
import org.example.autopark.client.directoriesControllers.PersonnelViewController;
import org.example.autopark.client.directoriesControllers.RoutesViewController;
import org.example.autopark.client.journalsControllers.OperatorJournalViewController;

import java.io.IOException;

public class MainViewController {
    private final ApplicationContext context;

    @FXML
    private BorderPane mainPane;

    public MainViewController(ApplicationContext context) {
        this.context = context;
    }

    @FXML
    private void initialize() {
        mainPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obs, oldWindow, newWindow) -> {
                    if (newWindow instanceof Stage stage) {
                        stage.setMinWidth(600);
                        stage.setMinHeight(400);
                    }
                });
            }
        });
    }

    @FXML
    private void onPersonnelClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/autopark/directories/personnel-view.fxml"));
        loader.setControllerFactory(controllerClass -> {
            if (controllerClass == PersonnelViewController.class) {
                return new PersonnelViewController(context.getPersonnelService());
            }
            throw new IllegalArgumentException("Unknown controller: " + controllerClass);
        });

        showView(loader.load());
    }

    @FXML
    private void onRoutesClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/autopark/directories/routes-view.fxml"));
        loader.setControllerFactory(controllerClass -> {
            if (controllerClass == RoutesViewController.class) {
                return new RoutesViewController(context.getRoutesService());
            }
            throw new IllegalArgumentException("Unknown controller: " + controllerClass);
        });

        showView(loader.load());
    }

    @FXML
    private void onCarClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/autopark/directories/cars-view.fxml"));
        loader.setControllerFactory(controllerClass -> {
            if (controllerClass == CarsViewController.class) {
                return new CarsViewController(context.getCarsService());
            }
            throw new IllegalArgumentException("Unknown controller: " + controllerClass);
        });

        showView(loader.load());
    }

    @FXML
    private void onJournalClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/autopark/journals/operatorJournal-view.fxml"));
        loader.setControllerFactory(controllerClass -> {
            if (controllerClass == OperatorJournalViewController.class) {
                return new OperatorJournalViewController(context.getOperatorJournalService());
            }
            throw new IllegalArgumentException("Unknown controller: " + controllerClass);
        });

        showView(loader.load());
    }

    @FXML
    private void onRecordsClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/autopark/reports/reports-view.fxml"));
        loader.setControllerFactory(controllerClass -> {
            if (controllerClass == ReportsViewController.class) {
                return new ReportsViewController(context.getReportsService(), ReportsViewController.ReportType.RECORDS);
            }
            throw new IllegalArgumentException("Unknown controller: " + controllerClass);
        });

        showView(loader.load());
    }

    @FXML
    private void onCarInRoutClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/autopark/reports/reports-view.fxml"));
        loader.setControllerFactory(controllerClass -> {
            if (controllerClass == ReportsViewController.class) {
                return new ReportsViewController(context.getReportsService(), ReportsViewController.ReportType.CARS_ON_ROUTE);
            }
            throw new IllegalArgumentException("Unknown controller: " + controllerClass);
        });

        showView(loader.load());
    }

    private void showView(Parent view) {
        mainPane.setCenter(view);

        javafx.application.Platform.runLater(() -> {
            mainPane.applyCss();
            mainPane.layout();

            Stage stage = (Stage) mainPane.getScene().getWindow();
            var scene = stage.getScene();
            Parent root = scene.getRoot();

            root.applyCss();
            root.layout();

            // Размеры системных границ окна
            double widthDecoration = stage.getWidth() - scene.getWidth();
            double heightDecoration = stage.getHeight() - scene.getHeight();

            // Минимальные размеры всего окна
            double minWidth = root.minWidth(-1) + widthDecoration;
            double minHeight = root.minHeight(-1) + heightDecoration;

            stage.setMinWidth(minWidth);
            stage.setMinHeight(minHeight);

        });
    }
}
