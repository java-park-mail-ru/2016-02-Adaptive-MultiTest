package mechanics;

/**
 * Created by Sasha on 05.04.16.
 */
public class PossibleCourses {
    private final Coords left;

    private final Coords right;

    private final Coords top;

    private final Coords bottom;

    public PossibleCourses() {
        left = new Coords(-1, -1);
        right = new Coords(-1, -1);
        top = new Coords(-1, -1);
        bottom = new Coords(-1, -1);
    }

    public Coords getLeft() { return left; }

    public void setLeft(Coords left) {
        this.left.setX(left.getX());
        this.left.setY(left.getY());
    }

    public Coords getRight() { return right; }

    public void setRight(Coords right) {
        this.right.setX(right.getX());
        this.right.setY(right.getY());
    }

    public Coords getTop() { return top; }

    public void setTop(Coords top) {
        this.top.setX(top.getX());
        this.top.setY(top.getY());
    }

    public Coords getBottom() { return bottom; }

    public void setBottom(Coords bottom) {
        this.bottom.setX(bottom.getX());
        this.bottom.setY(bottom.getY());
    }

}

