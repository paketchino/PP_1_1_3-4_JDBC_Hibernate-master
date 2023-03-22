package jm.task.core.jdbc;

import jm.task.core.jdbc.dao.UserDaoHibernateImpl;
import jm.task.core.jdbc.model.User;

public class Main {
    public static void main(String[] args) {
        byte age1 = 10;
        byte age2 = 20;
        byte age3 = 30;
        byte age4 = 40;
        UserDaoHibernateImpl userDaoJDBC = new UserDaoHibernateImpl();
        userDaoJDBC.createUsersTable();
        userDaoJDBC.saveUser("roman", "serg", age1);
        userDaoJDBC.saveUser("pavel", "filimin", age2);
        userDaoJDBC.saveUser("maxin", "wew", age3);
        userDaoJDBC.saveUser("artemy", "ada", age4);
        for (User user : userDaoJDBC.getAllUsers()) {
            System.out.println(user.toString());
        }
        userDaoJDBC.cleanUsersTable();
        userDaoJDBC.dropUsersTable();
    }
}
