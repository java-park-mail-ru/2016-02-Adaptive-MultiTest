package base;

import mechanics.GameSession;

/**
 * Created by Sasha on 17.04.16.
 */
public class GameUser {
    private final long myId;
    private long enemyId;
    private final GameSession.Snake color;

    public GameUser(long id, GameSession.Snake color) {
        this.myId = id;
        this.color = color;
    }

    public long getMyId() { return myId; }

    public long getEnemyId() { return enemyId; }

    public void setEnemyId(long enemyId) { this.enemyId = enemyId; }

    public GameSession.Snake getMyColor() { return color; }
}
