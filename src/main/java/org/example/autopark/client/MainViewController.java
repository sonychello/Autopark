
package org.example.autopark.client;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;
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
    private void onPersonnelClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/autopark/directories/personnel-view.fxml"));
        loader.setControllerFactory(controllerClass -> {
            if (controllerClass == PersonnelViewController.class) {
                return new PersonnelViewController(context.getPersonnelService());
            }
            throw new IllegalArgumentException("Unknown controller: " + controllerClass);
        });

        mainPane.setCenter(loader.load());
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

        mainPane.setCenter(loader.load());
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

        mainPane.setCenter(loader.load());
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

        mainPane.setCenter(loader.load());
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

        mainPane.setCenter(loader.load());
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

        mainPane.setCenter(loader.load());
    }
}
