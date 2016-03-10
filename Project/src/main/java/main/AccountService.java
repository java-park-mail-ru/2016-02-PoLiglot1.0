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
public class AccountService {
    private final Map<Long, UserProfile> users = new HashMap<>();
    private final Map<String, UserProfile> sessions = new HashMap<>();

    private Logger logAccountService = new Logger(AccountService.class);


    public AccountService() {
        UserProfile bufUser = new UserProfile("admin", "admin","admin@email.ru");
        users.put(bufUser.getUserID(), bufUser);
        bufUser = new UserProfile("guest", "12345","guest@email.ru");
        users.put(bufUser.getUserID(), bufUser);
    }

    public Collection<UserProfile> getAllUsers() {
        return users.values();
    }

    public boolean addUser(UserProfile user) {
        if (this.getUserByLogin(user.getLogin()) != null || this.getUserByEmail(user.getEmail()) != null)
            return false;
        user.setUserID();
        users.put(user.getUserID(), user);

        logAccountService.log("Пользователь добавлен: {" + String.valueOf(user.getUserID()) + ", " + String.valueOf(user.getLogin())
            + ", " + String.valueOf(String.valueOf(user.getPassword())) + ", " + String.valueOf(user.getEmail()) + "}\n");

        return true;
    }
    @Nullable
    public UserProfile getUserByID(long userID) {

        if (users.get(userID) != null){
            return users.get(userID);
        } else {
            return null;
        }
    }
    @Nullable
    public UserProfile getUserByLoginInSession(String login){
        for(Map.Entry<String,UserProfile> entry: sessions.entrySet()) {
            if (entry.getValue().getLogin().equals(login))
                return entry.getValue();
        }
        return  null;
    }

    @Nullable
    public UserProfile getUserByLogin(String login){
        for(Map.Entry<Long,UserProfile> entry: users.entrySet()) {
            if (entry.getValue().getLogin().equals(login))
                return entry.getValue();
        }
        return  null;
    }

    @Nullable
    public UserProfile getUserByEmail(String email){
        for(Map.Entry<Long,UserProfile> entry: users.entrySet()) {
            if (entry.getValue().getEmail().equals(email))
                return entry.getValue();
        }
        return  null;
    }
    @Nullable
    public UserProfile getUserBySession(@Nullable  String sessionID){
        if (sessions.get(sessionID) != null){
            return sessions.get(sessionID);
        } else {
            return null;
        }
    }
    public boolean isEnter(String sessionID) {
        return sessions.containsKey(sessionID);
    }
    public void addSession(String sessionID, UserProfile user) {
        sessions.put(sessionID,user);
        logAccountService.log("Сессия добавлена: {" + String.valueOf(user.getUserID()) + ", " + String.valueOf(user.getLogin())
                + ", " + String.valueOf(String.valueOf(user.getPassword())) + ", " + String.valueOf(user.getEmail()) + '}');
        }
    public void deleteSession(String sessionID){
        UserProfile user = getUserBySession(sessionID);
        sessions.remove(sessionID);

        logAccountService.log("Сессия удалена: {" + String.valueOf(user.getUserID()) + ", " + String.valueOf(user.getLogin())
                + ", " + String.valueOf(String.valueOf(user.getPassword())) + ", " + String.valueOf(user.getEmail()) + '}');
    }
    public void editUser(@NotNull UserProfile oldUser, UserProfile newUser) {
        if(!newUser.getLogin().isEmpty() && this.getUserByLogin(newUser.getLogin()) == null) {
            users.get(oldUser.getUserID()).setLogin(newUser.getLogin());
        }
        if(!newUser.getEmail().isEmpty() && this.getUserByEmail(newUser.getEmail()) == null) {
            users.get(oldUser.getUserID()).setEmail(newUser.getEmail());
        }
        if(!newUser.getPassword().isEmpty()) {
            users.get(oldUser.getUserID()).setPassword(newUser.getPassword());
        }
        logAccountService.log("Пользователь изменен: {" + String.valueOf(oldUser.getUserID()) + '}');
    }
    public void deleteUser(long userID){
        UserProfile user = users.get(userID);
        users.remove(userID);
        logAccountService.log("Пользователь удален: {" + String.valueOf(user.getUserID()) + ", " + String.valueOf(user.getLogin())
                + ", " + String.valueOf(String.valueOf(user.getPassword())) + ", " + String.valueOf(user.getEmail()) + '}');
    }
    public boolean userIsCorrect(UserProfile testUser) {
        UserProfile realUser = this.getUserByLogin(testUser.getLogin());
        return (realUser != null && realUser.getPassword().equals(testUser.getPassword()));
    }
}

