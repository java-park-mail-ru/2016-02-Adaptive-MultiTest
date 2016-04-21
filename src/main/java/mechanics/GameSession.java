package mechanics;

import base.GameUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import mechanics.Coords;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sasha on 17.04.16.
 */
public class GameSession {
    public static final int FIELD_SIZE = 8;

    @NotNull
    private final Map<Long, GameUser> users = new HashMap<>();

    @SuppressWarnings("EnumeratedConstantNamingConvention")
    public enum Snake { RED, BLUE }

    private Snake snake;

    private boolean[][] allRed;

    private boolean[][] allBlue;

    private final Coords lastRed;

    private final Coords lastBlue;

    public GameSession(@NotNull long user1, @NotNull long user2) {
        final GameUser gameUser1 = new GameUser(user1);
        gameUser1.setEnemyId(user2);

        final GameUser gameUser2 = new GameUser(user2);
        gameUser2.setEnemyId(user1);

        users.put(user1, gameUser1);
        users.put(user2, gameUser2);

        snake = Snake.BLUE;
        allRed = new boolean[FIELD_SIZE][FIELD_SIZE];
        allBlue = new boolean[FIELD_SIZE][FIELD_SIZE];
        for (int redx = 0; redx < FIELD_SIZE; redx++) allRed[redx][0] = true;
        for (int redy = 1; redy < FIELD_SIZE; redy++) allRed[0][redy] = true;
        for (int bluex = 0; bluex < FIELD_SIZE; bluex++) allBlue[bluex][FIELD_SIZE - 1] = true;
        for (int bluey = 0; bluey < FIELD_SIZE - 1; bluey++) allBlue[FIELD_SIZE - 1][bluey] = true;
        lastRed = new Coords(-1, -1);
        lastBlue = new Coords(-1, -1);
    }

    @Nullable
    public GameUser getEnemy(@NotNull long user) {
        final long enemyId = users.containsKey(user) ? users.get(user).getEnemyId() : -1;
        return enemyId == -1 ? null : users.get(enemyId);
    }

    @Nullable
    public GameUser getSelf(@NotNull long user) {
        return users.get(user);
    }

    public Snake getSnake() { return snake; }

    public void changeSnake() {
        if (snake == Snake.RED)
            snake = Snake.BLUE;
        else
            snake = Snake.RED;
    }

    public boolean[][] getAllRed() { return allRed; }

    public void setAllRed(boolean[][] allRed) { this.allRed = allRed; }

    public boolean[][] getAllBlue() { return allBlue; }

    public void setAllBlue(boolean[][] allBlue) { this.allBlue = allBlue; }

    public Coords getLastRed() { return lastRed; }

    public void setLastRed(Coords lastRed) {
        this.lastRed.setX(lastRed.getX());
        this.lastRed.setY(lastRed.getY());
    }

    public Coords getLastBlue() { return lastBlue; }

    public void setLastBlue(Coords lastBlue) {
        this.lastBlue.setX(lastBlue.getX());
        this.lastBlue.setY(lastBlue.getY());
    }

    public void occupyBlue(Coords coords) { allBlue[coords.getX()][coords.getY()] = true; }

    public void occupyRed(Coords coords) { allRed[coords.getX()][coords.getY()] = true; }
}
