package base;

import org.jetbrains.annotations.NotNull;
import mechanics.Coords;

/**
 * Created by Sasha on 17.04.16.
 */
public interface GameMechanics {
    void addUser(@NotNull long user);

    void removeUser(@NotNull long user);

    void move(Coords coords, @NotNull long user);

    void run();
}
