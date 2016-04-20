package frontend;

import base.AccountService;
import base.GameMechanics;
import base.WebSocketService;
import frontend.GameWebSocket;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import helpers.Context;

/**
 * Created by Sasha on 17.04.16.
 */
public class GameWebSocketCreator implements WebSocketCreator {
    private final AccountService accountService;
    private final GameMechanics gameMechanics;
    private final WebSocketService webSocketService;

    public GameWebSocketCreator(Context context) {
        this.accountService = context.get(AccountService.class);
        this.gameMechanics = context.get(GameMechanics.class);
        this.webSocketService = context.get(WebSocketService.class);
    }

    @Override
    public GameWebSocket createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
        final String sessionId = req.getHttpServletRequest().getSession().getId();
        final long id = accountService.getUserBySession(sessionId).getId();
        return new GameWebSocket(id, gameMechanics, webSocketService, accountService);
    }
}

