package torusverse;

import java.util.Objects;

public class Point {
    public int x;
    public int y;
    public Point previous;

    public Point(int x, int y, Point previous) {
        this.x = x;
        this.y = y;
        this.previous = previous;
    }

    @Override
    public String toString() { return String.format("(%d, %d)", x, y); }

    @Override
    public boolean equals(Object o) {
        Point point = (Point) o;
        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() { return Objects.hash(x, y); }
}


