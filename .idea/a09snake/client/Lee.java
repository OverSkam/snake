package a09snake.client;

import a09lee.colored.Ansi;
import a09lee.colored.Attribute;
import a09lee.colored.Colored;

import a09snake.client.Point;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/** https://www.youtube.com/watch?v=Tebc6J0qxNA&t=5s */
public class Lee {
    private final static int EMPTY = 0;
    private final static int START = 1;
    private final static int OBSTACLE = -10;
    private final int width;
    private final int height;
    private final int[][] board;

    public Lee(int width, int height) {
        this.width = width;
        this.height = height;
        board = new int[height][width];
    }

    private int get(int x, int y) {
        return board[y][x];
    }

    private void set(int x, int y, int value) {
        board[y][x] = value;
    }

    private int get(Point p) {
        return get(p.x(), p.y());
    }

    private void set(Point p, int value) {
        set(p.x(), p.y(), value);
    }

    private boolean isOnboard(Point p) {
        return p.x() >= 0 && p.x() < width && p.y() >= 0 && p.y() < height;
    }

    private boolean isUnvisited(Point p) {
        return get(p) == EMPTY;
    }

    private Stream<Point> deltas() {
        return Stream.of(
                //      dx, dy
                Point.of(0, 1),  // y+1
                Point.of(0, -1), // y-1
                Point.of(1, 0),  // x+1
                Point.of(-1, 0)  // x-1
        );
    }

    private Stream<Point> neighbours(Point p) {
        return deltas()
                .map(d -> p.move(d.x(), d.y()))
                .filter(this::isOnboard);
    }

    private Stream<Point> neighboursUnvisited(Point p) {
        return neighbours(p)
                .filter(this::isUnvisited);
    }

    private Stream<Point> neighboursByValue(Point p, int value) {
        return neighbours(p)
                .filter(pt -> get(pt) == value);
    }

    private void cleanBoard() {
        IntStream.range(0, height).forEach(y ->
                IntStream.range(0, width).forEach(x ->
                        set(x, y, EMPTY)
                )
        );
    }

    private void initializeBoard(Set<Point> obstacles) {
        cleanBoard();
        obstacles.forEach(p -> set(p, OBSTACLE));
    }

    public Optional<Iterable<Point>> trace(Point src, Point dst, Set<Point> obstacles) {
        initializeBoard(obstacles);
        int[] counter = {START};
        set(src, counter[0]);
        counter[0]++;
        boolean found = false;
        for (Set<Point> curr = Set.of(src); !(found || curr.isEmpty()); counter[0]++) {
            Set<Point> next = curr.stream()
                    .flatMap(this::neighboursUnvisited)
                    .collect(Collectors.toSet());
            next.forEach(p -> set(p, counter[0]));
            found = next.contains(dst);
            curr = next;
        }
        // 2. backtracking (reconstruct path)
        System.out.println(this);
        if (!found) return Optional.empty();
        LinkedList<Point> path = new LinkedList<>();
        path.add(dst);
        counter[0]--;
        Point curr = dst;
        while (counter[0] > START + 1) {
            counter[0]--;
            Point prev = neighboursByValue(curr, counter[0])
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("impossible by design"));
            path.addFirst(prev);
            curr = prev;
        }
        return Optional.of(path);
    }

    String cellFormatted(Point p, Set<Point> path) {
        int value = get(p);
        String valueF = String.format("%3d", value);

        if (value == OBSTACLE) {
            Attribute a = new Attribute(Ansi.ColorFont.BLUE);
            return Colored.build(" XX", a);
        }

        if (path.isEmpty()) return valueF;

        if (path.contains(p)) {
            Attribute a = new Attribute(Ansi.ColorFont.RED);
            return Colored.build(valueF, a);
        }

        return valueF;
    }

    public String boardFormatted(Iterable<Point> path0) {
        Set<Point> path = StreamSupport
                .stream(path0.spliterator(), false)
                .collect(Collectors.toSet());
        return IntStream.range(0, height)
                .mapToObj(y ->
                        IntStream.range(0, width).mapToObj(x -> Point.of(x, y))
                                .map(p -> cellFormatted(p, path))
                                .collect(Collectors.joining())
                )
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String toString() {
        return boardFormatted(Set.of());
    }

}
