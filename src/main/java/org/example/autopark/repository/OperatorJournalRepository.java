package org.example.autopark.repository;

import org.example.autopark.model.JournalRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OperatorJournalRepository {
    private final DatabaseConnection databaseConnection;

    public OperatorJournalRepository(DatabaseConnection newConnection) {
        databaseConnection = newConnection;
    }

    public List<JournalRecord> getPage(int lastSeenId) {
        String sql = """
                select * from journal
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

    public void createNewRecord(JournalRecord newRcd) throws SQLException {
        String sql = """
                insert into journal(time_out, auto_id, route_id)
                values (?, ?, ?)
                """;


        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setTimestamp(1, newRcd.time_out());
            preparedStatement.setInt(2, newRcd.auto_id());
            preparedStatement.setInt(3, newRcd.route_id());

            if (preparedStatement.executeUpdate() != 1) {
                throw new SQLException("Не удалось отправить автомобиль в рейс: ожидалось добавление одной записи");
            }

        }

    }

    public void setTimeInOfRecord(JournalRecord rcd) throws SQLException {
        String sql = """
                update journal
                set time_in = ?
                where id = ?
                """;

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setTimestamp(1, rcd.time_in());
            preparedStatement.setInt(2, rcd.id());

            if (preparedStatement.executeUpdate() != 1) {
                throw new SQLException("Не удалось указать время прибытия автомобиля: ожидалось изменение одной записи");
            }

        }

    }

    private List<JournalRecord> convertResultSetToList(ResultSet resultSet) throws SQLException {
        List<JournalRecord> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(convertResultToJournal(resultSet));
        }
        return list;
    }

    private JournalRecord convertResultToJournal(ResultSet result) throws SQLException {
        return new JournalRecord(
                result.getInt("id"),
                result.getTimestamp("time_out"),
                result.getTimestamp("time_in"),
                result.getInt("auto_id"),
                result.getInt("route_id"));
    }
}
