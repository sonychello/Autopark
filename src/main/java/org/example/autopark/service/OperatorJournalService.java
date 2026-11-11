package org.example.autopark.service;

import org.example.autopark.exception.AccessException;
import org.example.autopark.model.JournalRecord;
import org.example.autopark.model.User;
import org.example.autopark.repository.OperatorJournalRepository;

import java.sql.SQLException;
import java.util.List;

public class OperatorJournalService {
    private final OperatorJournalRepository operatorJournalRepository;

    private final User user;

    public OperatorJournalService(OperatorJournalRepository operatorJournalRepository, User user) {
        this.operatorJournalRepository = operatorJournalRepository;
        this.user = user;
    }

    public List<JournalRecord> getData(int lastId) {
        return operatorJournalRepository.getPage(lastId);
    }

    public void createNewRecord(JournalRecord rcd) throws AccessException, SQLException {
        checkUser();
        operatorJournalRepository.createNewRecord(rcd);

    }

    public void setTimeInOfRecord(JournalRecord rcd) throws AccessException, SQLException {
        checkUser();
        operatorJournalRepository.setTimeInOfRecord(rcd);

    }

    public boolean isAdmin() {
        return user.status().equals("admin");
    }

    private void checkUser() throws AccessException {
        if (!"admin".equals(user.status())) throw new AccessException("Недостаточно прав пользователя для выполнения операции");
    }
}
