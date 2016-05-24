package account;

import base.AccountService;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * created by snach
 */
public class AccountServiceImpl implements AccountService {

    private final Map<String, UserProfile> sessions = new HashMap<>();

    final SessionFactory sessionFactory;

    private static final Logger LOGGER = LogManager.getLogger(AccountServiceImpl.class);

    public AccountServiceImpl(Configuration configuration) {
        this.sessionFactory = configuration.buildSessionFactory();
    }

    @Override
    @NotNull
    public List<UserProfile> getAllUsers() {
        try {
            final Session session = sessionFactory.openSession();
            final UserProfileDAO dao = new UserProfileDAO(session);
            return dao.readAll();
        } catch (HibernateException e) {
            LOGGER.error("Ошибка работы с базой данных ", e);
            return new ArrayList<>();
        }
    }

    @Override
    @NotNull
    public List<UserProfile> getTopUsers() {
        try {
            final Session session = sessionFactory.openSession();
            final UserProfileDAO dao = new UserProfileDAO(session);
            return dao.readTop();
        } catch (HibernateException e) {
            LOGGER.error("Ошибка работы с базой данных ", e);
            return new ArrayList<>();
        }
    }

    @Override
    public boolean addUser(UserProfile user) {

        try (Session session = sessionFactory.openSession()) {
            final Transaction transaction = session.beginTransaction();
            final UserProfileDAO dao = new UserProfileDAO(session);
            if (dao.addUser(user)) {
                LOGGER.info("Пользователь добавлен: {" + String.valueOf(user.getUserID()) + ", " + String.valueOf(user.getLogin())
                        + ", " + String.valueOf(String.valueOf(user.getPassword())) + ", " + String.valueOf(user.getEmail()) + '}');
                transaction.commit();
                return true;
            } else {
                LOGGER.info("Пользователь НЕ добавлен");
                return false;
            }
        } catch (HibernateException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void editUser(@NotNull UserProfile oldUser, UserProfile newUser) {
        try (Session session = sessionFactory.openSession()) {
            final Transaction transaction = session.beginTransaction();
            final UserProfileDAO dao = new UserProfileDAO(session);
            dao.editUser(oldUser, newUser);
            transaction.commit();
            LOGGER.info("Пользователь изменен: {" + String.valueOf(oldUser.getUserID()) + '}');
        } catch (HibernateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteUser(long userID) {
        try (Session session = sessionFactory.openSession()) {
            final Transaction transaction = session.beginTransaction();
            final UserProfileDAO dao = new UserProfileDAO(session);
            dao.deleteUser(userID);
            transaction.commit();
            LOGGER.info("Пользователь удален: {" + String.valueOf(userID) + '}');
        } catch (HibernateException e) {
            e.printStackTrace();
        }
    }

    @Override
    @Nullable
    public UserProfile getUserByID(long userID) {
        try (Session session = sessionFactory.openSession()) {
            final UserProfileDAO dao = new UserProfileDAO(session);
            return dao.readUserByID(userID);
        } catch (HibernateException e) {
            e.printStackTrace();
            return null;
        }


    }

    @Override
    @Nullable
    public UserProfile getUserByLogin(String login) {
        try (Session session = sessionFactory.openSession()) {
            final UserProfileDAO dao = new UserProfileDAO(session);
            return dao.readUserByLogin(login);
        } catch (HibernateException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @Nullable
    public UserProfile getUserByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            final UserProfileDAO dao = new UserProfileDAO(session);
            return dao.readUserByEmail(email);
        } catch (HibernateException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @Nullable
    public UserProfile getUserBySession(@Nullable String sessionID) {
        if (sessions.get(sessionID) != null) {
            return sessions.get(sessionID);
        } else {
            return null;
        }
    }

    @Override
    public boolean isLoggedIn(String sessionID) {
        return sessions.containsKey(sessionID);
    }

    @Override
    public void addSession(String sessionID, UserProfile user) {
        sessions.put(sessionID, user);
        LOGGER.info("Сессия добавлена: {" + String.valueOf(user.getUserID()) + ", " + String.valueOf(user.getLogin())
                + ", " + String.valueOf(String.valueOf(user.getPassword())) + ", " + String.valueOf(user.getEmail()) + '}');
    }

    @Override
    public void deleteSession(String sessionID) {
        final UserProfile user = getUserBySession(sessionID);
        if (user != null) {
            sessions.remove(sessionID);
            LOGGER.info("Сессия удалена: {" + String.valueOf(user.getUserID()) + ", " + String.valueOf(user.getLogin())
                    + ", " + String.valueOf(String.valueOf(user.getPassword())) + ", " + String.valueOf(user.getEmail()) + '}');
        } else {
            LOGGER.info("Сессия не может быть удалена, тк не добавлена");
        }
    }

    @Override
    public boolean checkAuth(@NotNull String userName, @NotNull String password) {

        final UserProfile user;
        try {
            user = getUserByLogin(userName);
        } catch (HibernateException e) {
            return false;
        }

        if (user != null) {
            return user.getPassword().equals(password);
        } else {
            LOGGER.info("Юзер " + userName + " не найден");
            return false;
        }
    }

    @Override
    public void editScore(String login, int newScore) {
        try (Session session = sessionFactory.openSession()) {
            final UserProfileDAO dao = new UserProfileDAO(session);
            dao.editUserScore(login, newScore);
        } catch (HibernateException e) {
            e.printStackTrace();
        }
    }
}

