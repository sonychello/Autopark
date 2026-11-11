package org.example.autopark.service;

import org.example.autopark.exception.AccessException;
import org.example.autopark.model.Route;
import org.example.autopark.model.User;
import org.example.autopark.repository.RoutesRepository;

import java.sql.SQLException;
import java.util.List;

public class RoutesService {
    private final RoutesRepository routesRepository;

    private final User user;

    public RoutesService(RoutesRepository routesRepository, User user) {
        this.routesRepository = routesRepository;
        this.user = user;
    }

    public List<Route> getData(int lastId) {
        return routesRepository.getPage(lastId);
    }

    public void createNewRoute(Route route) throws AccessException, SQLException {
        checkUser();
        routesRepository.createNewRoute(route);

    }

    public void updateRoute(Route route) throws AccessException, SQLException {
        checkUser();
        routesRepository.updateRoute(route);

    }

    public void deleteRoute(Route route) throws AccessException, SQLException {
        checkUser();
        routesRepository.deleteRoute(route);

    }

    public boolean isAdmin() {
        return user.status().equals("admin");
    }

    private void checkUser() throws AccessException {
        if (!"admin".equals(user.status())) throw new AccessException("Недостаточно прав пользователя для выполнения операции");
    }
}
