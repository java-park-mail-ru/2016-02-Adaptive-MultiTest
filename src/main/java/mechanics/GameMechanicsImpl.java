package mechanics;

import base.AccountService;
import base.GameMechanics;
import base.GameUser;
import base.WebSocketService;
import helpers.TimeHelper;
import org.jetbrains.annotations.NotNull;

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

    private volatile long waiter;

    @NotNull
    private final Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();

    @NotNull
    private final Clock clock = Clock.systemDefaultZone();

    public GameMechanicsImpl(@NotNull WebSocketService webSocketService, @NotNull AccountService accountService) {
        this.webSocketService = webSocketService;
        this.accountService = accountService;
        waiter = -1;
    }

    @Override
    public void addUser(long user) {
        tasks.add(()->addUserInternal(user));
    }

    @Override
    public void removeUser(long user) {
        tasks.add(()->removeUserInternal(user));
    }

    @Override
    public void exitUnexpectedly(long user) { tasks.add(()->exitUnexpectedlyInternal(user));}

    @Override
    public void move(Coords coords, long userId, boolean firstRedMove)  {
        tasks.add(()->moveInternal(coords, userId, firstRedMove));
    }

    private void addUserInternal(long user) {
        if (waiter != -1) {
            startGame(user, waiter);
            waiter = -1;
        } else {
            waiter = user;
        }
    }

    private void removeUserInternal(long user) {
        idToGame.remove(user);
    }

    @SuppressWarnings("ConstantConditions")
    private void moveInternal(Coords coords, long userId, boolean firstRedMove) {
        final GameSession game = idToGame.get(userId);
        final GameUser myUser = game.getSelf(userId);
        final GameUser enemyUser = game.getEnemy(userId);

        if (myUser.getMyColor() != game.getSnake()) {
            webSocketService.notifyError(myUser, "enemy`s turn");
            return;
        }

        if (!firstRedMove && !Move.validCourse(coords, game)) {
            webSocketService.notifyError(myUser, "unreachable point");
            return;
        }

        Move.occupy(coords, game);
        final PossibleCourses possibleCourses = Move.getPossibleCourses(game);

        final boolean hasLeft = !possibleCourses.getLeft().equals(Coords.INVALID);
        final boolean hasRight = !possibleCourses.getRight().equals(Coords.INVALID);
        final boolean hasTop = !possibleCourses.getTop().equals(Coords.INVALID);
        final boolean hasBottom = !possibleCourses.getBottom().equals(Coords.INVALID);
        //noinspection OverlyComplexBooleanExpression
        if (!hasLeft && !hasRight && !hasTop && !hasBottom) {
            accountService.setUserScore(userId);
            webSocketService.notifyGameOver(myUser, true);
            webSocketService.notifyGameOver(enemyUser, false);
            return;
        }

        webSocketService.notifyWait(myUser);
        webSocketService.notifyMove(enemyUser, possibleCourses, coords);

    }

    public void exitUnexpectedlyInternal(long user) {
        final GameSession game = idToGame.get(user);
        final GameUser enemy = game.getEnemy(user);
        assert enemy != null;
        webSocketService.notifyUnexpectedEnemyExit(enemy);
    }

    @Override
    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            final long before = clock.millis();
            gmStep();
            final long after = clock.millis();
            TimeHelper.sleep(STEP_TIME - (after - before));
        }
    }

    private void gmStep() {
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

    private void startGame(long first, long second) {
        final GameSession game = new GameSession(first, second);
        idToGame.put(first, game);
        idToGame.put(second, game);

        final Coords red = getRandomCoords();
        Coords blue = new Coords(red.getX(), red.getY());
        while (blue.getX() == red.getX() && blue.getY() == red.getY())
            blue = getRandomCoords();

        webSocketService.notifyStartGame(game.getSelf(first), red, blue, "blue");
        webSocketService.notifyStartGame(game.getSelf(second), red, blue, "red");

        Move.occupy(blue, game);
        move(red, second, true);
    }

    private Coords getRandomCoords() {
        return new Coords(1 + (int)(Math.random() * 6), 1 + (int)(Math.random() * 6));
    }
}
