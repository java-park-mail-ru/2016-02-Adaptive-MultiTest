package mechanics;

/**
 * Created by Sasha on 05.04.16.
 */
@SuppressWarnings("InstanceVariableNamingConvention")
public class Coords {
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

        Coords coords = (Coords) o;

        if (x != coords.getX()) return false;
        return y == coords.getY();
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}
