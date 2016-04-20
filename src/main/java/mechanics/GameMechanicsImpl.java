package mechanics;

import base.AccountService;
import base.GameMechanics;
import base.GameUser;
import base.WebSocketService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Created by Sasha on 17.04.16.
 */
public class GameMechanicsImpl implements GameMechanics {
    @NotNull
    private final WebSocketService webSocketService;

    @NotNull
    private final AccountService accountService;

    @NotNull
    private final Map<String, GameSession> nameToGame = new HashMap<>();

    @Nullable
    private volatile String waiter;

    public GameMechanicsImpl(@NotNull WebSocketService webSocketService, @NotNull AccountService accountService) {
        this.webSocketService = webSocketService;
        this.accountService = accountService;
    }

    @Override
    public void addUser(@NotNull String user) {
        if (waiter != null) {
            //noinspection ConstantConditions
            startGame(user, waiter);
            waiter = null;
        } else {
            waiter = user;
        }
    }

    @Override
    public void removeUser(@NotNull String user) {
        nameToGame.remove(user);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void move(Coords coords, @NotNull String username) {
        final GameSession game = nameToGame.get(username);
        final GameUser myUser = game.getSelf(username);
        final GameUser enemyUser = game.getEnemy(username);

        occupy(coords, game);
        final PossibleCourses possibleCourses = getPossibleCourses(game);

        final boolean hasLeft = possibleCourses.getLeft().getX() != -1 && possibleCourses.getLeft().getY() != -1;
        final boolean hasRight = possibleCourses.getRight().getX() != -1 && possibleCourses.getRight().getY() != -1;
        final boolean hasTop = possibleCourses.getTop().getX() != -1 && possibleCourses.getTop().getY() != -1;
        final boolean hasBottom = possibleCourses.getBottom().getX() != -1 && possibleCourses.getBottom().getY() != -1;
        //noinspection OverlyComplexBooleanExpression
        if (!hasLeft && !hasRight && !hasTop && !hasBottom) {
            accountService.setUserScore(username);
            webSocketService.notifyGameOver(myUser, true);
            webSocketService.notifyGameOver(enemyUser, false);
            return;
        }

        webSocketService.notifyWait(myUser);
        webSocketService.notifyMove(enemyUser, possibleCourses);

    }

    private void occupy(Coords coords, GameSession game) {
        final boolean[][] allRed = game.getAllRed();
        final boolean[][] allBlue = game.getAllBlue();
        allRed[coords.getX()][coords.getY()] = true;
        allBlue[coords.getX()][coords.getY()] = true;
        game.setAllRed(allRed);
        game.setAllBlue(allBlue);

        if (game.getSnake() == GameSession.Snake.RED)
            game.setLastRed(coords);
        else
            game.setLastBlue(coords);

        game.changeSnake();
    }

    @SuppressWarnings("OverlyComplexMethod")
    private PossibleCourses getPossibleCourses(GameSession game) {
        final PossibleCourses possibleCourses = new PossibleCourses();
        final boolean[][] allRed = game.getAllRed();
        final boolean[][] allBlue = game.getAllBlue();
        final Coords lastRed = new Coords(game.getLastRed().getX(), game.getLastRed().getY());
        final Coords lastBlue = new Coords(game.getLastBlue().getX(), game.getLastBlue().getY());

        if (game.getSnake() == GameSession.Snake.BLUE) {
            if (lastBlue.getX() - 1 >= 0 && !allBlue[lastBlue.getX() - 1][lastBlue.getY()])
                possibleCourses.setLeft(new Coords(lastBlue.getX() - 1, lastBlue.getY()));
            if (!allBlue[lastBlue.getX() + 1][lastBlue.getY()])
                possibleCourses.setRight(new Coords(lastBlue.getX() + 1, lastBlue.getY()));
            if (lastBlue.getY() - 1 >= 0 && !allBlue[lastBlue.getX()][lastBlue.getY() - 1])
                possibleCourses.setTop(new Coords(lastBlue.getX(), lastBlue.getY() - 1));
            if (!allBlue[lastBlue.getX()][lastBlue.getY() + 1])
                possibleCourses.setBottom(new Coords(lastBlue.getX(), lastBlue.getY() + 1));
        } else {
            if (!allRed[lastRed.getX() - 1][lastRed.getY()])
                possibleCourses.setLeft(new Coords(lastRed.getX() - 1, lastRed.getY()));
            if (lastRed.getX() + 1 < 8 && !allRed[lastRed.getX() + 1][lastRed.getY()])
                possibleCourses.setRight(new Coords(lastRed.getX() + 1, lastRed.getY()));
            if (!allRed[lastRed.getX()][lastRed.getY() - 1])
                possibleCourses.setTop(new Coords(lastRed.getX(), lastRed.getY() - 1));
            if (lastRed.getY() + 1 < 8 && !allRed[lastRed.getX()][lastRed.getY() + 1])
                possibleCourses.setBottom(new Coords(lastRed.getX(), lastRed.getY() + 1));
        }
        return possibleCourses;
    }

    private void startGame(@NotNull String first, @NotNull String second) {
        final GameSession game = new GameSession(first, second);
        nameToGame.put(first, game);
        nameToGame.put(second, game);

        final Coords red = getRandomCoords();
        Coords blue = new Coords(red.getX(), red.getY());
        while (blue.getX() == red.getX() && blue.getY() == red.getY())
            blue = getRandomCoords();

        webSocketService.notifyStartGame(game.getSelf(first), red, blue);
        webSocketService.notifyStartGame(game.getSelf(second), red, blue);

        occupy(blue, game);
        move(red, second);
    }

    private Coords getRandomCoords() {
        return new Coords(1 + (int)(Math.random() * 6), 1 + (int)(Math.random() * 6));
    }
}
