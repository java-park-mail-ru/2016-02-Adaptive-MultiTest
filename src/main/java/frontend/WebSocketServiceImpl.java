package frontend;

import base.GameUser;
import base.WebSocketService;
import mechanics.Coords;
import mechanics.PossibleCourses;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sasha on 17.04.16.
 */
public class WebSocketServiceImpl implements WebSocketService{
    private final Map<Long, GameWebSocket> userSockets = new HashMap<>();

    @Override
    public void addUser(GameWebSocket user) {
        userSockets.put(user.getMyId(), user);
    }

    @Override
    public void removeUser(GameWebSocket user) { userSockets.remove(user.getMyId()); }

    @Override
    public void notifyStartGame(GameUser user, Coords red, Coords blue, String myColor) {
        userSockets.get(user.getMyId()).startGame(user, red, blue, myColor);
    }

    @Override
    public void notifyMove(GameUser user, PossibleCourses possibleCourses, Coords enemyMove) {
        userSockets.get(user.getMyId()).sendPossibleCourses(possibleCourses, enemyMove);
    }

    @Override
    public void notifyWait(GameUser user) {
        userSockets.get(user.getMyId()).waitEnemyMove();
    }

    @Override
    public void notifyGameOver(GameUser user, boolean win) {
        userSockets.get(user.getMyId()).gameOver(win);
        userSockets.remove(user.getMyId());
    }

    @Override
    public void notifyError(GameUser user, String error) {
        userSockets.get(user.getMyId()).sendError(error);
    }

    @Override
    public void notifyUnexpectedEnemyExit(GameUser user) {
        userSockets.get(user.getMyId()).reportUnexpectedEnemyExit();
    }
}
