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
    @NotNull
    private final Map<String, GameUser> users = new HashMap<>();

    @SuppressWarnings("EnumeratedConstantNamingConvention")
    public enum Snake { RED, BLUE }

    private Snake snake;

    private boolean[][] allRed;

    private boolean[][] allBlue;

    private final Coords lastRed;

    private final Coords lastBlue;

    public GameSession(@NotNull String user1, @NotNull String user2) {
        final GameUser gameUser1 = new GameUser(user1);
        gameUser1.setEnemyName(user2);

        final GameUser gameUser2 = new GameUser(user2);
        gameUser2.setEnemyName(user1);

        users.put(user1, gameUser1);
        users.put(user2, gameUser2);

        snake = Snake.BLUE;
        allRed = new boolean[8][8];
        allBlue = new boolean[8][8];
        for (int redx = 0; redx < 8; redx++) allRed[redx][0] = true;
        for (int redy = 1; redy < 8; redy++) allRed[0][redy] = true;
        for (int bluex = 0; bluex < 8; bluex++) allBlue[bluex][7] = true;
        for (int bluey = 0; bluey < 7; bluey++) allBlue[7][bluey] = true;
        lastRed = new Coords();
        lastBlue = new Coords();
    }

    @Nullable
    public GameUser getEnemy(@NotNull String user) {
        final String enemyName = users.containsKey(user) ? users.get(user).getEnemyName() : null;
        return enemyName == null ? null : users.get(enemyName);
    }

    @Nullable
    public GameUser getSelf(String user) {
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
}
