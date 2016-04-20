package mechanics;

import base.AccountService;
import base.GameMechanics;
import base.GameUser;
import base.WebSocketService;
import helpers.TimeHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Clock;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Sasha on 17.04.16.
 */
public class GameMechanicsImpl implements GameMechanics {
    private static final long STEP_TIME = 100;

    @NotNull
    private final WebSocketService webSocketService;

    @NotNull
    private final AccountService accountService;

    @NotNull
    private final Map<Long, GameSession> idToGame = new HashMap<>();

    @Nullable
    private volatile long waiter;

    @NotNull
    private final Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();

    @NotNull
    private Clock clock = Clock.systemDefaultZone();

    public GameMechanicsImpl(@NotNull WebSocketService webSocketService, @NotNull AccountService accountService) {
        this.webSocketService = webSocketService;
        this.accountService = accountService;
        waiter = -1;
    }

    @Override
    public void addUser(@NotNull long user) {
        tasks.add(()->addUserInternal(user));
    }

    @Override
    public void removeUser(@NotNull long user) {
        tasks.add(()->removeUserInternal(user));
    }

    @Override
    public void move(@NotNull Coords coords, @NotNull long userId)  {
        tasks.add(()->moveInternal(coords, userId));
    }

    private void addUserInternal(@NotNull long user) {
        if (waiter != -1) {
            //noinspection ConstantConditions
            startGame(user, waiter);
            waiter = -1;
        } else {
            waiter = user;
        }
    }

    private void removeUserInternal(@NotNull long user) {
        idToGame.remove(user);
    }

    @SuppressWarnings("ConstantConditions")
    private void moveInternal(Coords coords, @NotNull long userId) {
        final GameSession game = idToGame.get(userId);
        final GameUser myUser = game.getSelf(userId);
        final GameUser enemyUser = game.getEnemy(userId);

        occupy(coords, game);
        final PossibleCourses possibleCourses = getPossibleCourses(game);

        final boolean hasLeft = possibleCourses.getLeft().getX() != -1 && possibleCourses.getLeft().getY() != -1;
        final boolean hasRight = possibleCourses.getRight().getX() != -1 && possibleCourses.getRight().getY() != -1;
        final boolean hasTop = possibleCourses.getTop().getX() != -1 && possibleCourses.getTop().getY() != -1;
        final boolean hasBottom = possibleCourses.getBottom().getX() != -1 && possibleCourses.getBottom().getY() != -1;
        //noinspection OverlyComplexBooleanExpression
        if (!hasLeft && !hasRight && !hasTop && !hasBottom) {
            accountService.setUserScore(userId);
            webSocketService.notifyGameOver(myUser, true);
            webSocketService.notifyGameOver(enemyUser, false);
            return;
        }

        webSocketService.notifyWait(myUser);
        webSocketService.notifyMove(enemyUser, possibleCourses);

    }

    @Override
    public void run() {
        //noinspection InfiniteLoopStatement
        long lastFrameMillis = STEP_TIME;
        while (true) {
            final long before = clock.millis();
            gmStep(lastFrameMillis);
            final long after = clock.millis();
            TimeHelper.sleep(STEP_TIME - (after - before));

            final long afterSleep = clock.millis();
            lastFrameMillis = afterSleep - before;
        }
    }

    private void gmStep(long frameTime) {
        while (!tasks.isEmpty()) {
            final Runnable nextTask = tasks.poll();
            if (nextTask != null) {
                try {
                    nextTask.run();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }
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

    private void startGame(@NotNull long first, @NotNull long second) {
        final GameSession game = new GameSession(first, second);
        idToGame.put(first, game);
        idToGame.put(second, game);

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
