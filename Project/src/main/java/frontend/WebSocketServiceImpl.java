package frontend;

import game.GameUser;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import base.WebSocketService;

/**
 * Created by Snach on 19.04.16.
 */
public class WebSocketServiceImpl implements WebSocketService {
    private final Map<String, GameWebSocket> userSockets = new HashMap<>();

    @Override
    public void addUser(GameWebSocket user) {
        userSockets.put(user.getMyName(), user);
    }

    @Override
    public void notifyStartGame(GameUser user) {
        final GameWebSocket gameWebSocket = userSockets.get(user.getMyName());
        gameWebSocket.startGame(user);
    }

    @Override
    public void notifyGameOver(GameUser user, boolean equality, boolean win) {
        if (equality) {
            userSockets.get(user.getMyName()).gameOver(true, false);
        } else {
            userSockets.get(user.getMyName()).gameOver(false, win);
        }

        userSockets.remove(user.getMyName());
    }

    @Override
    public void removeUser(@NotNull GameWebSocket user) {
        userSockets.remove(user.getMyName());
    }

    @Override
    public void notifyEnemyLeft(GameUser user) {
        final GameWebSocket gameWebSocket = userSockets.get(user.getMyName());
        if (gameWebSocket != null) {
            gameWebSocket.finishGameEnemyleft();
        }
    }
}
