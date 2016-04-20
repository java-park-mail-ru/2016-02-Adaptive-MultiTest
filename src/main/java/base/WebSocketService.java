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

    void notifyStartGame(@Nullable GameUser user, Coords red, Coords blue);

    void notifyMove(GameUser user, PossibleCourses possibleCourses);

    void notifyWait(GameUser user);

    void notifyGameOver(GameUser user, boolean win);
}
