
package org.example.autopark;

import org.example.autopark.model.User;
import org.example.autopark.repository.*;
import org.example.autopark.service.*;

public class ApplicationContext {
    // Подключение к БД
    private final DatabaseConfig databaseConfig;
    private final DatabaseConnection databaseConnection;

    // Репозитории
    private final CarsRepository carsRepository;
    private final PersonnelRepository personnelRepository;
    private final RoutesRepository routesRepository;
    private final OperatorJournalRepository operatorJournalRepository;
    private final ReportsRepository reportsRepository;

    // Сервисы
    private final CarsService carsService;
    private final PersonnelService personnelService;
    private final RoutesService routesService;
    private final OperatorJournalService operatorJournalService;
    private final ReportsService reportsService;

    public ApplicationContext(User user) {
        // Создание подключения
        databaseConfig = new DatabaseConfig();
        databaseConnection = new DatabaseConnection(databaseConfig);

        // Создание репозиториев
        carsRepository = new CarsRepository(databaseConnection);
        personnelRepository = new PersonnelRepository(databaseConnection);
        routesRepository = new RoutesRepository(databaseConnection);
        operatorJournalRepository = new OperatorJournalRepository(databaseConnection);
        reportsRepository = new ReportsRepository(databaseConnection);

        // Создание сервисов
        carsService = new CarsService(carsRepository, user);
        personnelService = new PersonnelService(personnelRepository, user);
        routesService = new RoutesService(routesRepository, user);
        operatorJournalService = new OperatorJournalService(operatorJournalRepository, user);
        reportsService = new ReportsService(reportsRepository);
    }

    // Геттеры сервисов
    public CarsService getCarsService() {
        return carsService;
    }

    public PersonnelService getPersonnelService() {
        return personnelService;
    }

    public RoutesService getRoutesService() {
        return routesService;
    }

    public OperatorJournalService getOperatorJournalService() {
        return operatorJournalService;
    }

    public ReportsService getReportsService() {
        return reportsService;
    }
}
