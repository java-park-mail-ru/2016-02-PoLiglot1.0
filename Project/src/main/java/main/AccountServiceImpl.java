package main;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rest.UserProfile;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * created by snach
 */
public class AccountServiceImpl implements AccountService{
    private final Map<Long, UserProfile> users = new HashMap<>();
    private final Map<String, UserProfile> sessions = new HashMap<>();
    private final Map<String, UserProfile> usersByLogin = new HashMap<>();

    @SuppressWarnings("ConstantNamingConvention")
    private static final Logger logger = new Logger(AccountServiceImpl.class);


    public AccountServiceImpl() {
        UserProfile bufUser = new UserProfile("admin", "admin", "admin@email.ru");
        users.put(bufUser.getUserID(), bufUser);
        bufUser = new UserProfile("guest", "12345", "guest@email.ru");
        users.put(bufUser.getUserID(), bufUser);
    }

    @Override
    public Collection<UserProfile> getAllUsers() {
        return users.values();
    }

    @Override
    public boolean addUser(UserProfile user) {
        if (this.getUserByLogin(user.getLogin()) != null || this.getUserByEmail(user.getEmail()) != null)
            return false;
        if (user.getLogin().isEmpty() || user.getPassword().isEmpty() || user.getEmail().isEmpty())
            return false;
        user.setUserID();
        users.put(user.getUserID(), user);
        usersByLogin.put(user.getLogin(), user);

        logger.log("Пользователь добавлен: {" + String.valueOf(user.getUserID()) + ", " + String.valueOf(user.getLogin())
                + ", " + String.valueOf(String.valueOf(user.getPassword())) + ", " + String.valueOf(user.getEmail()) + "}\n");

        return true;
    }

    @Override
    @Nullable
    public UserProfile getUserByID(long userID) {

        if (users.get(userID) != null) {
            return users.get(userID);
        } else {
            return null;
        }
    }

    @Override
    @Nullable
    public UserProfile getUserByLogin(String login) {
        if (usersByLogin.get(login) != null) {
            System.out.println("по логину " + login + " есть пользователь " + usersByLogin
            .get(login).getEmail());
            return usersByLogin.get(login);
        } else {
            System.out.println("Пользователь не найден");
            return null;
        }
    }

    @Override
    @Nullable
    public UserProfile getUserByEmail(String email) {
        for (Map.Entry<Long, UserProfile> entry : users.entrySet()) {
            if (entry.getValue().getEmail().equals(email))
                return entry.getValue();
        }
        return null;
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
        logger.log("Сессия добавлена: {" + String.valueOf(user.getUserID()) + ", " + String.valueOf(user.getLogin())
                + ", " + String.valueOf(String.valueOf(user.getPassword())) + ", " + String.valueOf(user.getEmail()) + '}');
    }

    @Override
    public void deleteSession(String sessionID) {
        UserProfile user = getUserBySession(sessionID);
        sessions.remove(sessionID);

        logger.log("Сессия удалена: {" + String.valueOf(user.getUserID()) + ", " + String.valueOf(user.getLogin())
                + ", " + String.valueOf(String.valueOf(user.getPassword())) + ", " + String.valueOf(user.getEmail()) + '}');
    }

    @Override
    public void editUser(@NotNull UserProfile oldUser, UserProfile newUser) {
        if (!newUser.getLogin().isEmpty() && this.getUserByLogin(newUser.getLogin()) == null) {
            users.get(oldUser.getUserID()).setLogin(newUser.getLogin());
        }
        if (!newUser.getEmail().isEmpty() && this.getUserByEmail(newUser.getEmail()) == null) {
            users.get(oldUser.getUserID()).setEmail(newUser.getEmail());
        }
        if (!newUser.getPassword().isEmpty()) {
            users.get(oldUser.getUserID()).setPassword(newUser.getPassword());
        }
        logger.log("Пользователь изменен: {" + String.valueOf(oldUser.getUserID()) + '}');
    }

    @Override
    public void deleteUser(long userID) {
        UserProfile user = users.get(userID);

        users.remove(userID);
        usersByLogin.remove(user.getLogin());
        logger.log("Пользователь удален: {" + String.valueOf(user.getUserID()) + ", " + String.valueOf(user.getLogin())
                + ", " + String.valueOf(String.valueOf(user.getPassword())) + ", " + String.valueOf(user.getEmail()) + '}');
    }

    @Override
    public boolean checkAuth(@NotNull String userName, @NotNull String password){
        return (getUserByLogin(userName) != null && getUserByLogin(userName).getPassword().equals(password));
    }

}

