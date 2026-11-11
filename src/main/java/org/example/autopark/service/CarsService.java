package org.example.autopark.service;

import org.example.autopark.exception.AccessException;
import org.example.autopark.model.Car;
import org.example.autopark.model.User;
import org.example.autopark.repository.CarsRepository;

import java.sql.SQLException;
import java.util.List;

public class CarsService {
    private final CarsRepository carsRepository;

    private final User user;

    public CarsService(CarsRepository carsRepository, User user) {
        this.carsRepository = carsRepository;
        this.user = user;
    }

    public List<Car> getData(int lastId) {
        return carsRepository.getPage(lastId);
    }

    public void createNewCar(Car car) throws AccessException, SQLException {
        checkUser();
        carsRepository.createNewCar(car);

    }

    public void updateCar(Car car) throws AccessException {
        checkUser();
        carsRepository.updateCar(car);

    }

    public void deleteCar(Car car) throws AccessException, SQLException {
        checkUser();
        carsRepository.deleteCar(car);

    }

    public boolean isAdmin() {
        return user.status().equals("admin");
    }

    private void checkUser() throws AccessException {
        if (!"admin".equals(user.status())) throw new AccessException("Недостаточно прав пользователя для выполнения операции");
    }
}
