package mechanics;

/**
 * Created by Sasha on 05.04.16.
 */
public class PossibleCourses {
    private Coords left;

    private Coords right;

    private Coords top;

    private Coords bottom;

    public PossibleCourses() {
        left = new Coords(-1, -1);
        right = new Coords(-1, -1);
        top = new Coords(-1, -1);
        bottom = new Coords(-1, -1);
    }

    public Coords getLeft() { return left; }

    public void setLeft(Coords left) {
        this.left = new Coords(left.getX(), left.getY());
    }

    public Coords getRight() { return right; }

    public void setRight(Coords right) {
        this.right = new Coords(right.getX(), right.getY());
    }

    public Coords getTop() { return top; }

    public void setTop(Coords top) { this.top = new Coords(top.getX(), top.getY()); }

    public Coords getBottom() { return bottom; }

    public void setBottom(Coords bottom) { this.bottom = new Coords(bottom.getX(), bottom.getY()); }

}

