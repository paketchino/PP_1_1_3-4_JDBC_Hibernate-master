package jm.task.core.jdbc;

import jm.task.core.jdbc.dao.UserDaoJDBCImpl;
import jm.task.core.jdbc.model.User;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        byte age1 = 10;
        byte age2 = 20;
        byte age3 = 30;
        byte age4 = 40;
        UserDaoJDBCImpl userDaoJDBC = new UserDaoJDBCImpl();
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

        int[] arr = new int[]{0, 0, -7, -7, -7, 1, 2, 5, 3, 1, 2};
        int n = getFirstUnique(arr);
        System.out.println("Найдено уникальное значение " + n);
    }

    private static int getFirstUnique(int[] arr) {
        Map <Integer, Integer> map = new LinkedHashMap<>();
        for (Integer integer : arr) {
            map.merge(integer, 1, Integer::sum);
        }
        return map.entrySet().stream().filter(n -> map.get(n.getKey()) == 1)
                .findFirst().get().getKey();
    }
}
