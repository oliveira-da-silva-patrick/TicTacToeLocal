package tictactoe;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

public class TicTacToePanel extends JPanel implements MouseInputListener {

    private static final int SIZE = 3;
    private static final int ROW = SIZE;
    private static final int COL = SIZE;

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

    private boolean flag = true;

    private int widthGapBetweenHorizontalLines = getWidth() / 3;
    private int heightGapBetweenVerticalLines = getHeight() / 3;

    private int[][] grid = new int[COL][ROW];

    private Color backgroundColor = new Color(217, 213, 212);

    public TicTacToePanel() {
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
        if(typeOfWin != -1) return;

        int col = e.getY() / heightGapBetweenVerticalLines;
        int row = e.getX() / widthGapBetweenHorizontalLines;

        if(grid[col][row] != NOTHING) return;

        if(flag) 
            grid[col][row] = CROSS;
        else 
            grid[col][row] = CIRCLE;
        flag = !flag;

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

}