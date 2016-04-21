package mechanics;

/**
 * Created by Sasha on 05.04.16.
 */
@SuppressWarnings("InstanceVariableNamingConvention")
public class Coords {
    public static final Coords INVALID = new Coords(-1, -1);

    private int x;

    private int y;

    public Coords() {
        x = -1;
        y = -1;
    }

    public Coords(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }

    public void setX(int x) { this.x = x; }

    public int getY() { return y; }

    public void setY(int y) { this.y = y; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Coords coords = (Coords) o;

        //noinspection SimplifiableIfStatement
        if (x != coords.x) return false;
        return y == coords.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}
