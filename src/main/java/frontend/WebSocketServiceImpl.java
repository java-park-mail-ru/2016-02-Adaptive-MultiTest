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
    private final Map<String, GameWebSocket> userSockets = new HashMap<>();

    @Override
    public void addUser(GameWebSocket user) {
        userSockets.put(user.getMyName(), user);
    }

    @Override
    public void removeUser(GameWebSocket user) { userSockets.remove(user.getMyName()); }

    @Override
    public void notifyStartGame(GameUser user, Coords red, Coords blue) {
        userSockets.get(user.getMyName()).startGame(user, red, blue);
    }

    @Override
    public void notifyMove(GameUser user, PossibleCourses possibleCourses) {
        userSockets.get(user.getMyName()).sendPossibleCourses(possibleCourses);
    }

    @Override
    public void notifyWait(GameUser user) {
        userSockets.get(user.getMyName()).waitEnemyMove();
    }

    @Override
    public void notifyGameOver(GameUser user, boolean win) {
        userSockets.get(user.getMyName()).gameOver(win);
        userSockets.remove(user.getMyName());
    }
}
