package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;

public class UserDaoHibernateImpl implements UserDao {

    public static Logger logger = Logger.getLogger("UserDaoHibernateImpl");

    private SessionFactory sf = Util.getSessionFactory();

    public UserDaoHibernateImpl() {
    }

    private <T> T tx(final Function<Session, T> command) {
        final Session session = sf.openSession();
        final Transaction tx = session.beginTransaction();
        try {
            T rsl = command.apply(session);
            tx.commit();
            return rsl;
        } catch (final Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public void createUsersTable() {
        Session session = sf.openSession();
        session.beginTransaction();
        session.createSQLQuery(
                "create table if not exists users (" +
                        "id bigint not null auto_increment primary key, " +
                        "name text not null, " +
                        "last_name text not null," +
                        "age tinyint not null)");
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public void dropUsersTable() {
        Session session = sf.openSession();
        session.beginTransaction();
        session.createSQLQuery("drop table if exists users");
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        User user = new User();
        this.tx(session -> {
            user.setName(name);
            user.setLastName(lastName);
            user.setAge(age);
            session.save(user);
            return user;
        });
    }

    @Override
    public void removeUserById(long id) {
        Session session = sf.openSession();
        session.beginTransaction();
        session.createQuery("from users as u where u.id =:id");
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        Session session = sf.openSession();
        session.beginTransaction();
        list = session.createQuery("from users").list();
        session.getTransaction().commit();
        session.close();
        return list;
    }

    @Override
    public void cleanUsersTable() {
        Session session = sf.openSession();
        logger.info("Открывается транзакция по удалению всех пользователей");
        session.beginTransaction();
        session.createQuery("delete from users").executeUpdate();
        logger.info("Выполняется удалению");
        session.getTransaction().commit();
        logger.info("Удаление выполнено");
        session.close();
        logger.info("Транзакция закрыта");
    }
}
