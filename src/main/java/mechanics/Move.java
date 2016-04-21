package mechanics;

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
            if (lastRed.getX() + 1 < GameSession.FIELD_SIZE && !allRed[lastRed.getX() + 1][lastRed.getY()])
                possibleCourses.setRight(new Coords(lastRed.getX() + 1, lastRed.getY()));
            if (!allRed[lastRed.getX()][lastRed.getY() - 1])
                possibleCourses.setTop(new Coords(lastRed.getX(), lastRed.getY() - 1));
            if (lastRed.getY() + 1 < GameSession.FIELD_SIZE && !allRed[lastRed.getX()][lastRed.getY() + 1])
                possibleCourses.setBottom(new Coords(lastRed.getX(), lastRed.getY() + 1));
        }
        return possibleCourses;
    }
}
