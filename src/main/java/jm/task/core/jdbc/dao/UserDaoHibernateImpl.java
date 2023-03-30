package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.transaction.Transactional;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;

public class UserDaoHibernateImpl implements UserDao {

    public static Logger logger = Logger.getLogger("UserDaoHibernateImpl");

    private final SessionFactory sessionFactory = Util.getSessionFactory();

    public UserDaoHibernateImpl() {
    }

    private <T> T tx(Function<Session, T> command) {
        Session session = sessionFactory.openSession();
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
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.createSQLQuery(
                "create table if not exists users (" +
                        "id bigint not null auto_increment primary key, " +
                        "name text not null, " +
                        "last_name text not null," +
                        "age tinyint not null)").executeUpdate();
        session.getTransaction().commit();
        logger.info("Таблица была успешно создана");
        session.close();
    }

    @Override
    public void dropUsersTable() {
        logger.info("Выполняется команда удаления таблицы users");
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.createSQLQuery("drop table if exists users").executeUpdate();
        session.getTransaction().commit();
        logger.info("Таблица была успешно удалена");
        session.close();
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        logger.info("Выполняется команда добавления пользователя");
        User user = new User();
        tx(session -> {
            user.setAge(age);
            user.setName(name);
            user.setLastName(lastName);
            session.save(user);
            return user;
        });
        logger.info("Добавления пользователя успешно завершено");
    }

    @Override
    public void removeUserById(long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.delete(session.get(User.class, id));
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> list = null;
        try (Session session = sessionFactory.openSession()) {
            list = session.createQuery("from User", User.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void cleanUsersTable() {
        logger.info("Выполняется команда удаления всех пользователей");
        tx(session -> session.createQuery("delete from User").executeUpdate());
    }
}
