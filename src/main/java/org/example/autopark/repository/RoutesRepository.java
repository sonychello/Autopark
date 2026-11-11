package org.example.autopark.repository;

import org.example.autopark.model.Route;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoutesRepository {
    private final DatabaseConnection databaseConnection;

    public RoutesRepository(DatabaseConnection newConnection) {
        databaseConnection = newConnection;
    }

    public List<Route> getPage(int lastSeenId) {
        String sql = """
                select * from routes
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

    public void createNewRoute(Route newRoute) throws SQLException {
        String sql = """
                insert into routes(name)
                values (?)
                """;


        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, newRoute.name());

            if (preparedStatement.executeUpdate() != 1) {
                throw new SQLException("Не удалось добавить маршрут: ожидалось добавление одной записи");
            }

        }

    }

    public void updateRoute(Route route) throws SQLException {
        String sql = """
                update routes
                set name = ?
                where id = ?
                """;

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, route.name());
            preparedStatement.setInt(2, route.id());

            if (preparedStatement.executeUpdate() != 1) {
                throw new SQLException("Не удалось изменить маршрут: ожидалось изменение одной записи");
            }

        }

    }

    public void deleteRoute(Route route) throws SQLException {
        String sql = """
                delete from routes
                where id = ?
                """;

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, route.id());

            if (preparedStatement.executeUpdate() != 1) {
                throw new SQLException("Не удалось удалить маршрут: ожидалось удаление одной записи");
            }

        }

    }

    private List<Route> convertResultSetToList(ResultSet resultSet) throws SQLException {
        List<Route> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(convertResultToRoute(resultSet));
        }
        return list;
    }

    private Route convertResultToRoute(ResultSet result) throws SQLException {
        return new Route(
                result.getInt("id"),
                result.getString("name"));
    }
}
