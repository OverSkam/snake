package a09snake.client;

public record Point(int x, int y) {

    public static Point of(int x, int y) {
        return new Point(x, y);
    }

    public Point move(int dx, int dy) {
        return new Point(x + dx, y + dy);
    }

    @Override
    public String toString() {
        return "[%2d,%2d]".formatted(x, y);
    }
}
