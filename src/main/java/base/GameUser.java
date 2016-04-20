package base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Sasha on 17.04.16.
 */
public class GameUser {
    @NotNull
    private final String myName;
    @Nullable
    private String enemyName;

    public GameUser(@NotNull String myName) {
        this.myName = myName;
    }

    @NotNull
    public String getMyName() {
        return myName;
    }
    @Nullable
    public String getEnemyName() { return enemyName; }

    public void setEnemyName(@NotNull String enemyName) { this.enemyName = enemyName; }
}
