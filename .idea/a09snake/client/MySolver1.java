package a09snake.client;

import com.codenjoy.dojo.services.Direction;
import a09snake.client.Point;


import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class MySolver1 {

    public Direction checkPath(Iterable<Point> result, Point head){
        if (result.iterator().next().x() < head.x())
            return Direction.LEFT;
        else
        if (result.iterator().next().x() > head.x())
            return Direction.RIGHT;
        else
        if (result.iterator().next().y() < head.y())
            return Direction.DOWN;
        else
            return Direction.UP;
    }

    public Direction solve(Board board) {
        if (board.isGameOver()) return Direction.RIGHT;
        Lee lee = new Lee(15, 15);

        Point apple = Point.of(board.getApples().getFirst().getX(), board.getApples().getFirst().getY());
        Point head = Point.of(board.getHead().getX(), board.getHead().getY());
        Point stone = Point.of(board.getStones().getFirst().getX(), board.getStones().getFirst().getY());
        Point tail = Point.of(board.getSnake().getLast().getX(), board.getSnake().getLast().getY());

        Set<Point> obstacles = board.getSnake()
                .stream()
                .map(p -> Point.of(p.getX(), p.getY()))
                .collect(Collectors.toSet());
        for (int i = 0; i < 15; i++) {
            obstacles.add(Point.of(i, 0));
            obstacles.add(Point.of(0, i));
            obstacles.add(Point.of(14, i));
            obstacles.add(Point.of(i, 14));
        }
        obstacles.add(stone);


        Optional<Iterable<Point>> result = lee.trace(head, apple, obstacles);
        if (result.isPresent() && board.getSnake().size() < 45)
            return checkPath(result.get(), head);
        else {
            obstacles.remove(stone);
            result = lee.trace(head, stone, obstacles);
            if (result.isPresent()) {
                return checkPath(result.get(), head);
            }
            else {
                obstacles.remove(tail);
                result = lee.trace(head, tail, obstacles);
                if (result.isPresent()) {
                    return checkPath(result.get(), head);
                }
                else
                    return board.getSnakeDirection();
            }
        }
    }

}
