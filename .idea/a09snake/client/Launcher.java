package a09snake.client;

import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;

/**
 * User: your name
 */
public class Launcher implements Solver<Board> {

    MySolver1 solver = new MySolver1();

    @Override
    public String get(Board board) {
        return solver.solve(board).toString();
    }

    public static void main(String[] args) {
        String url = "http://164.92.225.222/codenjoy-contest/board/player/t0tndlqx6mnlf86q949p?code=8636775441422884";
        WebSocketRunner.runClient(url, new Launcher(), new Board());
    }

}
