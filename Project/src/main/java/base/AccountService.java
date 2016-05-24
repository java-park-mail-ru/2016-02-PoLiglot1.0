package base;

import account.UserProfile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by Snach on 26.03.16.
 */

public interface AccountService {

    @NotNull
    List<UserProfile> getAllUsers();

    @NotNull
    List<UserProfile> getTopUsers();

    boolean addUser(UserProfile user);

    @Nullable
    UserProfile getUserByID(long userID);

    @Nullable
    UserProfile getUserByLogin(String login);

    @SuppressWarnings("unused")
    @Nullable
    UserProfile getUserByEmail(String email);

    @Nullable
    UserProfile getUserBySession(@NotNull String sessionID);

    boolean isLoggedIn(String sessionID);

    void addSession(String sessionID, UserProfile user);

    void deleteSession(String sessionID);

    void editUser(@NotNull UserProfile oldUser, UserProfile newUser);

    void deleteUser(long userID);

    boolean checkAuth(@NotNull String userName, @NotNull String password);

    void editScore(String login, int newScore);
}
