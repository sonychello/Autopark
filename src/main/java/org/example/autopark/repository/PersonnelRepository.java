package org.example.autopark.repository;

import org.example.autopark.model.Personnel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PersonnelRepository {
    private final DatabaseConnection databaseConnection;

    public PersonnelRepository(DatabaseConnection newConnection) {
        databaseConnection = newConnection;
    }

    public List<Personnel> getPage(int lastSeenId) {
        String sql = """
                select * from auto_personal
                where id > ?
                order by id
                limit 50
                """;


        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, lastSeenId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return convertResultSetToList(resultSet);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createNewPersonnel(Personnel newPersonnel) throws SQLException {
        String sql = """
                insert into auto_personal(first_name, last_name, father_name)
                values (?, ?, ?)
                """;


        try (Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, newPersonnel.firstName());
            preparedStatement.setString(2, newPersonnel.lastName());
            preparedStatement.setString(3, newPersonnel.fatherName());

            if (preparedStatement.executeUpdate() != 1) {
                throw new SQLException("Не удалось добавить сотрудника: ожидалось добавление одной записи");
            }

        }

    }

    public void updatePersonnel(Personnel personnel) throws SQLException {
        String sql = """
                update auto_personal
                set first_name = ?, last_name = ?, father_name = ?
                where id = ?
                """;

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, personnel.firstName());
            preparedStatement.setString(2, personnel.lastName());
            preparedStatement.setString(3, personnel.fatherName());
            preparedStatement.setInt(4, personnel.id());

            if (preparedStatement.executeUpdate() != 1) {
                throw new SQLException("Не удалось изменить сотрудника: ожидалось изменение одной записи");
            }

        }

    }

    public void deletePersonnel(Personnel personnel) throws SQLException {
        String sql = """
                delete from auto_personal
                where id = ?
                """;

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, personnel.id());

            if (preparedStatement.executeUpdate() != 1) {
                throw new SQLException("Не удалось удалить сотрудника: ожидалось удаление одной записи");
            }

        }

    }

    private List<Personnel> convertResultSetToList(ResultSet resultSet) throws SQLException {
        List<Personnel> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(convertResultToPersonnel(resultSet));
        }
        return list;
    }

    private Personnel convertResultToPersonnel(ResultSet result) throws SQLException {
        return new Personnel(
                result.getInt("id"),
                result.getString("first_name"),
                result.getString("last_name"),
                result.getString("father_name"));
    }

}
