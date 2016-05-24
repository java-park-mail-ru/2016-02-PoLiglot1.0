package frontend;

import account.UserProfile;
import base.AccountService;
import main.Context;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Snach on 19.04.16.
 */
public class GameWebSocketCreator implements WebSocketCreator {

    private static final Logger LOGGER = LogManager.getLogger(GameWebSocketCreator.class);

    private final Context context;

    public GameWebSocketCreator(Context context) {
        this.context = context;
    }

    @Override
    @Nullable
    public GameWebSocket createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {

        final String sessionId = req.getHttpServletRequest().getSession().getId();
        final UserProfile userBySession = context.get(AccountService.class).getUserBySession(sessionId);

        if (userBySession == null) {
            LOGGER.error("Can't create websocket, user don't authenticate");
            return null;
        } else {
            return new GameWebSocket(userBySession.getLogin(), context);
        }
    }
}
