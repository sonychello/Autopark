package org.example.autopark.repository;

import org.example.autopark.model.RecordTimeAndAuto;
import org.postgresql.util.PGInterval;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportsRepository {
    private final DatabaseConnection databaseConnection;

    public ReportsRepository(DatabaseConnection newConnection) {
        databaseConnection = newConnection;
    }

    public RecordTimeAndAuto getRecordTimeAndAuto(String routeName) {
        String sql = "CALL get_min_time(?, NULL, NULL)";


        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, routeName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return convertResultToRecordTimeAndAuto(resultSet);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int countAutoOnRoute(String routeName) {
        String sql = "CALL count_auto_on_route(?, NULL)";


        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, routeName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("cars_count");
                } else throw new SQLException("Не удалось получить результаты запроса");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

//    private List<RecordTimeAndAuto> convertResultSetToList(ResultSet resultSet) throws SQLException {
//        List<RecordTimeAndAuto> list = new ArrayList<>();
//        while (resultSet.next()) {
//            list.add(convertResultToRecordTimeAndAuto(resultSet));
//        }
//        return list;
//    }

    private RecordTimeAndAuto convertResultToRecordTimeAndAuto(ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            return null;
        }
        return new RecordTimeAndAuto(
                (PGInterval) resultSet.getObject("result_time"),
                resultSet.getString("auto_num"));
    }
}
