package base;

/**
 * Created by Sasha on 17.04.16.
 */
public class GameUser {
    private final long myId;
    private long enemyId;

    public GameUser(long id) { this.myId = id; }

    public long getMyId() { return myId; }
    public long getEnemyId() { return enemyId; }

    public void setEnemyId(long enemyId) { this.enemyId = enemyId; }
}
