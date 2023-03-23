package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;

public class UserDaoHibernateImpl implements UserDao {

    public static Logger logger = Logger.getLogger("UserDaoHibernateImpl");

    private SessionFactory sf = Util.getSessionFactory();

    public UserDaoHibernateImpl() {
    }

    private <T> T tx(Function<Session, T> command) {
        Session session = sf.openSession();
        Transaction tx = session.beginTransaction();
        try {
            T rsl = command.apply(session);
            tx.commit();
            return rsl;
        } catch (Exception e) {
            session.getTransaction().rollback();
            String rsl = String.format("Во время выполнения транзакция что-то пошло не так %s", e.getMessage());
            logger.warning(rsl);
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public void createUsersTable() {
        logger.info("Выполняется команда добавления таблицы users");
        Session session = sf.openSession();
        session.beginTransaction();
        session.createSQLQuery(
                "create table if not exists users (" +
                        "id bigint not null auto_increment primary key, " +
                        "name text not null, " +
                        "last_name text not null," +
                        "age tinyint not null)");
        session.getTransaction().commit();
        logger.info("Таблица была успешно создана");
        session.close();
    }

    @Override
    public void dropUsersTable() {
        logger.info("Выполняется команда удаления таблицы users");
        Session session = sf.openSession();
        session.beginTransaction();
        session.createSQLQuery("drop table if exists users");
        session.getTransaction().commit();
        logger.info("Таблица была успешно удалена");
        session.close();
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        logger.info("Выполняется команда добавления пользователя");
        User user = new User();
        this.tx(session -> {
            user.setName(name);
            user.setLastName(lastName);
            user.setAge(age);
            session.save(user);
            logger.info("Добавления пользователя успешно завершено");
            return user;
        });
    }

    @Override
    public void removeUserById(long id) {
        logger.info("Выполняется команда удаление пользователя по id");
        tx(session -> session.createQuery("from users as u where u.id =:id"));
    }

    @Override
    public List<User> getAllUsers() {
        logger.info("Выполняется команда поиска всех пользователей");
        return (List<User>) tx(session -> session.createQuery("from users").getResultList());
    }

    @Override
    public void cleanUsersTable() {
        logger.info("Выполняется команда удаления всех пользователей");
        tx(session -> session.createQuery("delete from users").executeUpdate());
    }
}
