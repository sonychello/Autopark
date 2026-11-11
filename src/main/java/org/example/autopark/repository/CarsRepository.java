package org.example.autopark.repository;

import org.example.autopark.model.Car;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CarsRepository {
    private final DatabaseConnection databaseConnection;

    public CarsRepository(DatabaseConnection newConnection) {
        databaseConnection = newConnection;
    }

    public List<Car> getPage(int lastSeenId) {
        String sql = """
                select * from auto
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

    public void createNewCar(Car car) throws SQLException {
        String sql = """
                insert into auto(num, color, mark, personal_id)
                values (?, ?, ?, ?)
                """;


        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, car.num());
            preparedStatement.setString(2, car.color());
            preparedStatement.setString(3, car.mark());
            preparedStatement.setInt(4, car.personal_id());

            if (preparedStatement.executeUpdate() != 1) {
                throw new SQLException("Не удалось добавить машину: ожидалось добавление одной записи");
            }

        }

    }

    public void updateCar(Car car) {
        String sql = """
                update auto
                set num = ?, color = ?, mark = ?, personal_id = ?
                where id = ?
                """;

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, car.num());
            preparedStatement.setString(2, car.color());
            preparedStatement.setString(3, car.mark());
            preparedStatement.setInt(4, car.personal_id());
            preparedStatement.setInt(5, car.id());

            if (preparedStatement.executeUpdate() != 1) {
                throw new SQLException("Не удалось изменить машину: ожидалось изменение одной записи");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void deleteCar(Car car) throws SQLException {
        String sql = """
                delete from auto
                where id = ?
                """;

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, car.id());

            if (preparedStatement.executeUpdate() != 1) {
                throw new SQLException("Не удалось удалить машину: ожидалось удаление одной записи");
            }

        }

    }

    private List<Car> convertResultSetToList(ResultSet resultSet) throws SQLException {
        List<Car> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(convertResultToCar(resultSet));
        }
        return list;
    }

    private Car convertResultToCar(ResultSet result) throws SQLException {
        return new Car(
                result.getInt("id"),
                result.getString("num"),
                result.getString("color"),
                result.getString("mark"),
                result.getInt("personal_id"));
    }
}
