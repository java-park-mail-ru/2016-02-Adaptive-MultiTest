package base;

import org.jetbrains.annotations.NotNull;
import mechanics.Coords;

/**
 * Created by Sasha on 17.04.16.
 */
public interface GameMechanics {
    void addUser(@NotNull String user);

    void removeUser(@NotNull String user);

    void move(Coords coords, @NotNull String user);
}
