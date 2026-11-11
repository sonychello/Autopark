package org.example.autopark.service;

import org.example.autopark.exception.AccessException;
import org.example.autopark.model.Personnel;
import org.example.autopark.model.User;
import org.example.autopark.repository.PersonnelRepository;

import java.sql.SQLException;
import java.util.List;

public class PersonnelService {
    private final PersonnelRepository personnelRepository;

    private final User user;

    public PersonnelService(PersonnelRepository personnelRepository, User user) {
        this.personnelRepository = personnelRepository;
        this.user = user;
    }

    public List<Personnel> getData(int lastId) {
        return personnelRepository.getPage(lastId);
    }

    public void createNewPersonnel(Personnel personnel) throws AccessException, SQLException {
        chekUser();
        personnelRepository.createNewPersonnel(personnel);

    }

    public void updatePersonnel(Personnel personnel) throws AccessException, SQLException {
        chekUser();
        personnelRepository.updatePersonnel(personnel);

    }

    public void deletePersonnel(Personnel personnel) throws AccessException, SQLException {
        chekUser();
        personnelRepository.deletePersonnel(personnel);

    }

    public boolean isAdmin() {
        return user.status().equals("admin");
    }

    private void chekUser() throws AccessException {
        if (!user.status().equals("admin")) throw new AccessException("Недостаточно прав пользователя для выполнения операции");
    }
}
