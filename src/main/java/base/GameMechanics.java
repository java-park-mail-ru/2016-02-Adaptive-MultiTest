package base;

import mechanics.Coords;

/**
 * Created by Sasha on 17.04.16.
 */
public interface GameMechanics {
    void addUser(long user);

    void removeUser(long user);

    void move(Coords coords, long user);

    void run();
}
