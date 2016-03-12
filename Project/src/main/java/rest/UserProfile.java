package rest;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicLong;

/**
 * created by snach
 */
public class UserProfile {
    private static final AtomicLong ID_GENETATOR = new AtomicLong(0);

    private long userID;
    @NotNull
    private String login;
    @NotNull
    private String password;

    private String email;

    public UserProfile() {
        login = "";
        password = "";
        email = "";
    }

    public UserProfile(@NotNull String login, @NotNull String password, @NotNull String email) {
        this.userID = ID_GENETATOR.getAndIncrement();
        this.login = login;
        this.password = password;
        this.email = email;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID() {
        this.userID = ID_GENETATOR.getAndIncrement();
    }

    @NotNull
    public String getLogin() {
        return login;
    }

    public void setLogin(@NotNull String login) {
        this.login = login;
    }

    @NotNull
    public String getPassword() {
        return password;
    }

    public void setPassword(@NotNull String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(@NotNull String email) {
        this.email = email;
    }


}