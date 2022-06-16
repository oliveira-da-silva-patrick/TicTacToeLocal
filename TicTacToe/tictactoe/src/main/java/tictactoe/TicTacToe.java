package tictactoe;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class TicTacToe {

    public static final int WINDOW_WIDTH = 400;
    public static final int WINDOW_HEIGHT = 400;
    
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setTitle("TicTacToe");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        TicTacToePanel panel = new TicTacToePanel();
        frame.getContentPane().add(panel);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setVisible(true);
        frame.setResizable(false);
    }
}
