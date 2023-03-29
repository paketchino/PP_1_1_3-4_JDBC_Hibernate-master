package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class UserDaoJDBCImpl implements UserDao {

    private static Logger logger = Logger.getLogger("UserDaoJDBCImpl");

    private final Connection cn = Util.getConnection();
    
    public UserDaoJDBCImpl() {

    }

    @Override
    public void createUsersTable() {
        logger.info("Производится создание таблицы");
        try (Statement statement = cn.createStatement()) {
            String sql = "create table if not exists users (" +
                    "id bigint not null auto_increment primary key, " +
                    "name text not null, " +
                    "last_name text not null," +
                    "age tinyint not null)";
            statement.execute(sql);
            logger.info("Таблица успешно создана");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dropUsersTable() {
        logger.warning("Удаление таблицы");
        try (Statement statement = cn.createStatement()) {
            statement.executeUpdate("drop table if exists users");
            logger.info("Удаление таблицы выполнено");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        logger.info("Начато добавления пользователя");
        try (PreparedStatement preparedStatement =
                     cn.prepareStatement("insert into users (name, last_name, age)" +
                             " values (?, ?, ?)")) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, lastName);
            preparedStatement.setByte(3, age);
            preparedStatement.executeUpdate();
            String message = String.format("Добавления пользователя успешно выполнено с %s, %s, %s",
                    name, lastName, age);
            logger.info(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeUserById(long id) {
        logger.info("Запрос на удаления пользователя по id");
        try (PreparedStatement preparedStatement =
                     cn.prepareStatement("delete from users where id = ?")) {
            preparedStatement.setLong(1, id);
            logger.info("Попытка удаления");
            preparedStatement.executeUpdate();
            logger.info("Удаление по id произведено");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<User> getAllUsers() {
        logger.info("Запрос на получения всех пользователей");
        List<User> users = new ArrayList<>();
        try (PreparedStatement preparedStatement = cn.prepareStatement("select * from users")) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    users.add(new User(
                            resultSet.getLong("id"),
                            resultSet.getString("name"),
                            resultSet.getString("last_name"),
                            resultSet.getByte("age")));
                    }
            }
        } catch (Exception e) {
          e.printStackTrace();
        }
        return users;
    }

    @Override
    public void cleanUsersTable() {
        logger.warning("Попытка удалить всех пользователей из таблицы");
        try (Statement statement = cn.createStatement()) {
            statement.executeUpdate("delete from users");
            logger.info("Коммит был сохранен. Пользователи удалены");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
