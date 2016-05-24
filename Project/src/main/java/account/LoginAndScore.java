package account;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Created by Snach on 21.05.16.
 */
@SuppressWarnings("unused")
public class LoginAndScore {
    private String login;
    private int score;

    public LoginAndScore() {
    }

    public LoginAndScore(String login, int score) {
        this.login = login;
        this.score = score;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getLogin() {
        return login;
    }

    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        final JsonObject response = new JsonObject();
        response.add("login", new JsonPrimitive(login));
        response.add("score", new JsonPrimitive(score));
        return response.toString();
    }
}
