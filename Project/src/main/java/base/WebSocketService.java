package base;

import frontend.GameWebSocket;
import game.GameUser;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Snach on 24.04.16.
 */
@SuppressWarnings("unused")
public interface WebSocketService {
    void addUser(GameWebSocket user);

    void notifyStartGame(GameUser user);

    void notifyGameOver(GameUser user, boolean equality, boolean win);

    void removeUser(@NotNull GameWebSocket user);

    void notifyEnemyLeft(GameUser user);
}
