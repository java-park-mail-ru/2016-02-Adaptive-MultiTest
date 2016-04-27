package mechanics;

import javafx.geometry.Pos;

/**
 * Created by Sasha on 21.04.16.
 */
public class Move {
    public static void occupy(Coords coords, GameSession game) {
        game.occupyRed(coords);
        game.occupyBlue(coords);

        if (game.getSnake() == GameSession.Snake.RED)
            game.setLastRed(coords);
        else
            game.setLastBlue(coords);

        game.changeSnake();
    }

    @SuppressWarnings("OverlyComplexMethod")
    public static PossibleCourses getPossibleCourses(GameSession game) {
        final boolean[][] allRed = game.getAllRed();
        final boolean[][] allBlue = game.getAllBlue();
        final Coords lastRed = new Coords(game.getLastRed().getX(), game.getLastRed().getY());
        final Coords lastBlue = new Coords(game.getLastBlue().getX(), game.getLastBlue().getY());

        if (game.getSnake() == GameSession.Snake.BLUE)
            return getPossibleCoursesForSpecificSnake(allBlue, lastBlue);
        else
            return getPossibleCoursesForSpecificSnake(allRed, lastRed);
    }

    private static PossibleCourses getPossibleCoursesForSpecificSnake(boolean[][] allSnake, Coords lastSnake) {
        final PossibleCourses possibleCourses = new PossibleCourses();

        if (lastSnake.getX() - 1 >= 0 && !allSnake[lastSnake.getX() - 1][lastSnake.getY()])
            possibleCourses.setLeft(new Coords(lastSnake.getX() - 1, lastSnake.getY()));
        if (lastSnake.getX() + 1 < GameSession.FIELD_SIZE && !allSnake[lastSnake.getX() + 1][lastSnake.getY()])
            possibleCourses.setRight(new Coords(lastSnake.getX() + 1, lastSnake.getY()));
        if (lastSnake.getY() - 1 >= 0 && !allSnake[lastSnake.getX()][lastSnake.getY() - 1])
            possibleCourses.setTop(new Coords(lastSnake.getX(), lastSnake.getY() - 1));
        if (lastSnake.getY() + 1 < GameSession.FIELD_SIZE && !allSnake[lastSnake.getX()][lastSnake.getY() + 1])
            possibleCourses.setBottom(new Coords(lastSnake.getX(), lastSnake.getY() + 1));

        return possibleCourses;
    }
}
