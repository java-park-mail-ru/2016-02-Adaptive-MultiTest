package base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Sasha on 17.04.16.
 */
public class GameUser {
    @NotNull
    private final long myId;
    @Nullable
    private long enemyId;

    public GameUser(@NotNull long id) { this.myId = id; }

    @NotNull
    public long getMyId() { return myId; }
    @Nullable
    public long getEnemyId() { return enemyId; }

    public void setEnemyId(@NotNull long enemyId) { this.enemyId = enemyId; }
}
