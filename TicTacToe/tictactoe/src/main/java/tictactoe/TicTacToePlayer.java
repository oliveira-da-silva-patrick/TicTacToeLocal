package tictactoe;

import java.net.*;
import java.io.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class TicTacToePlayer extends JPanel implements MouseInputListener{
    private static final int SIZE = 3;
    private static final int ROW = SIZE;
    private static final int COL = SIZE;
    public static final int WINDOW_WIDTH = 400;
    public static final int WINDOW_HEIGHT = 400;

    private static final int NOTHING = 0;
    private static final int CROSS = 1;
    private static final int CIRCLE = 2;

    private static final int CROSS_WIN = (int) Math.pow(CROSS, 3);
    private static final int CIRCLE_WIN = (int) Math.pow(CIRCLE, 3);

    private static final int HORIZONTAL = 0;
    private static final int VERTICAL = 1;
    private static final int LURD = 2;
    private static final int LDRU = 3;

    private int winCol = -1;
    private int winRow = -1;
    private int typeOfWin = -1;

    private int otherPlayer;
    private ClientSideConnection csc;

    private int playerID;
    private int turnsMade = 0;
    private int maxTurns;
    private boolean buttonsEnabled;

    private int widthGapBetweenHorizontalLines = getWidth() / 3;
    private int heightGapBetweenVerticalLines = getHeight() / 3;

    private int[][] grid = new int[COL][ROW];

    private Color backgroundColor = new Color(217, 213, 212);

    public TicTacToePlayer() {
        setFocusable(true);
        grabFocus();
        fillGrid();
        addMouseListener(this);
    }

    private void fillGrid() {
        for(int i = 0; i < COL; i++)
            for(int j = 0; j < ROW; j++)
                grid[i][j] = NOTHING;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        widthGapBetweenHorizontalLines = getWidth() / 3;
        heightGapBetweenVerticalLines = getHeight() / 3;

        fillBackground(g);
        drawLines(g);
        drawGame(g);
        checkAndWin(g); 
    }

    private void fillBackground(Graphics g) {
        g.setColor(backgroundColor);
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    private void drawLines(Graphics g) {
        g.setColor(Color.BLACK);
        for(int i = 1; i < COL; i++)        
            g.drawLine(i * widthGapBetweenHorizontalLines, 0, i * widthGapBetweenHorizontalLines, getHeight());
        for(int i = 1; i < ROW; i++)        
            g.drawLine(0, i * heightGapBetweenVerticalLines, getWidth(), i * heightGapBetweenVerticalLines);
    }

    private void drawCross(int row, int col, Graphics g) {
        int x1 = col * widthGapBetweenHorizontalLines + 10;
        int x2 = (col + 1) * widthGapBetweenHorizontalLines - 10;
        int y1 = row * heightGapBetweenVerticalLines + 10;
        int y2 = (row + 1) * heightGapBetweenVerticalLines - 10;

        g.setColor(Color.RED);
        g.drawLine(x1, y1, x2, y2);
        g.drawLine(x1, y2, x2, y1);        
    }

    private void drawCircle(int row, int col, Graphics g) {
        int x = col * widthGapBetweenHorizontalLines + 10;
        int y = row * heightGapBetweenVerticalLines + 10;
        int width = widthGapBetweenHorizontalLines - 20;
        int height = heightGapBetweenVerticalLines - 20;

        g.setColor(Color.BLUE);
        g.drawOval(x, y, width, height);
    }

    private void drawGame(Graphics g) {
        for(int i = 0; i < COL; i++) 
            for(int j = 0; j < ROW; j++)
                if(grid[i][j] == CROSS) drawCross(i, j, g);
                else if(grid[i][j] == CIRCLE) drawCircle(i, j, g);
    }

    public void checkAndWin(Graphics g) {
        g.setColor(Color.GREEN);

        switch(typeOfWin) {
            case HORIZONTAL:
                g.drawLine(10, (int) ((winCol + 0.5) * heightGapBetweenVerticalLines), getWidth() - 10, (int) ((winCol + 0.5) * heightGapBetweenVerticalLines));
                break;
            case VERTICAL:
                g.drawLine((int) ((winRow + 0.5) * widthGapBetweenHorizontalLines), 10, (int) ((winRow + 0.5) * widthGapBetweenHorizontalLines), getHeight() - 10);
                break;
            case LDRU:
                g.drawLine(10, getHeight() - 10, getWidth() - 10, 10);
                break;
            case LURD:
                g.drawLine(10, 10, getWidth() - 10, getHeight() - 10);
                break;
            default: break;
        }
    }

    private int getHorizontalProduct(int col) {
        int product = 1;
        for(int i = 0; i < ROW; i++)
            product *= grid[col][i];
        return product;
    }

    private int getVerticalProduct(int row) {
        int product = 1;
        for(int i = 0; i < COL; i++)
            product *= grid[i][row];
        return product;
    }

    private int getLURDDiagonalProduct() {
        int product = 1;
        for(int i = 0; i < COL; i++)
            product *= grid[i][i];
        return product;
    }

    private int getLDRUDiagonalProduct() {
        int product = 1;
        for(int i = 0; i < COL; i++)
            product *= grid[COL-(i+1)][i];
        return product;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        System.out.println(buttonsEnabled);
        if(typeOfWin != -1 || !buttonsEnabled) return;

        int col = e.getY() / heightGapBetweenVerticalLines;
        int row = e.getX() / widthGapBetweenHorizontalLines;

        if(grid[col][row] != NOTHING) return;

        if(playerID == 1) 
            grid[col][row] = CROSS;
        else 
            grid[col][row] = CIRCLE;

        turnsMade++;
        buttonsEnabled = false;
        csc.sendButtonNum(col*3 + row);
        Thread t = new Thread(new Runnable() {
            public void run() {
                updateTurn();
            }
        });
        t.start();

        if(getHorizontalProduct(col) == CIRCLE_WIN || getHorizontalProduct(col) == CROSS_WIN) {
            typeOfWin = HORIZONTAL;
            winCol = col;
        } else if(getVerticalProduct(row) == CIRCLE_WIN || getVerticalProduct(row) == CROSS_WIN) {
            typeOfWin = VERTICAL;
            winRow = row;
        } else if(getLDRUDiagonalProduct() == CIRCLE_WIN || getLDRUDiagonalProduct() == CROSS_WIN) {
            typeOfWin = LDRU;
        } else if(getLURDDiagonalProduct() == CIRCLE_WIN || getLURDDiagonalProduct() == CROSS_WIN) {
            typeOfWin = LURD;
        }

        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {}

    //Client connection Inner class (fancy stuff)
    private class ClientSideConnection {
        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;
        public ClientSideConnection(){
            System.out.println("----Client----");
            try{
                socket = new Socket("localhost", 5000);
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
                // first thing sent from server is int containing playerID
                playerID = dataIn.readInt();
                if(playerID == 1){
                    buttonsEnabled = true;
                    maxTurns = 5;
                } else{
                    buttonsEnabled = false;
                    maxTurns = 4;
                    Thread t = new Thread(new Runnable() {
                        public void run(){
                            // TODO updateTurn is called before csc is created, error needs to be fixed
                            updateTurn();
                        }
                    });
                    t.start();
                }
                System.out.println("Connected to server as number: " + playerID);
                System.out.println("Max turns: " + maxTurns);

            } catch (IOException ex){
                System.out.println("IOException from ClientSideConnection constructor");
            }
        }

        public void sendButtonNum(int n){
            try{
                dataOut.writeInt(n);
                dataOut.flush();
            } catch (IOException ex){
                System.out.println("IOException from sendButtonNum() CSC");
            }
        }

        public int receiveButtonNum(){
            int n = -1;
            try{
                n = dataIn.readInt();
                System.out.println("Player #" + otherPlayer + "clicked button #" + n);
            } catch (IOException ex){
                System.out.println("IOEsception from receiveButtonNum() CSC");
            }
            return n;
        }
    }

    public void connectToServer(){
        csc = new ClientSideConnection();
    }

    public void updateTurn(){
        int n = csc.receiveButtonNum();
        buttonsEnabled = true;
        // TODO implement what n does

    }
    
    public static void main(String[] args) {
        TicTacToePlayer t = new TicTacToePlayer();
        t.connectToServer();

        JFrame frame = new JFrame();
        frame.setTitle("TicTacToe " + t.playerID);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.getContentPane().add(t);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        if(t.playerID == 1){
            // TODO maybe message to show which player has to go first
            t.otherPlayer = 2;
        } else{
            t.otherPlayer = 1;
        }

        frame.setVisible(true);
        frame.setResizable(false);
    }
}
