package base;

import frontend.GameWebSocket;
import mechanics.Coords;
import mechanics.PossibleCourses;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Sasha on 17.04.16.
 */
public interface WebSocketService {
    void addUser(GameWebSocket user);

    void removeUser(GameWebSocket user);

    void notifyStartGame(@Nullable GameUser user, Coords red, Coords blue, String myColor);

    void notifyMove(GameUser user, PossibleCourses possibleCourses, Coords enemyMove);

    void notifyWait(GameUser user);

    void notifyGameOver(GameUser user, boolean win);

    void notifyError(GameUser user, String error);

    void notifyUnexpectedEnemyExit(GameUser user);
}
