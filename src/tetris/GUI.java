package tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Date;
import tetris.Utils.GameStatus;
import tetris.Utils.Point;

public class GUI {

    private Board board; //actual game state
    private JFrame frame;
    private JPanel panel; //game panel
    private JPanel info; //info panel
    private JPanel view; //next piece panel
    private JLabel score;
    private DrawnCell[][] cells; //visual representation of the game
    private DrawnCell[][] preview; //visual representation of next piece
    private GridBagConstraints cn;
    private int gamewidth = 500;
    private int gameheight = 500;
    private int baserate; //base pieces drops/sec
    private int accelerationrate = 0; //how many pieces
    private int turnmilliseconds;

    public GUI(int dropsPerSecond) {
        if (dropsPerSecond<1) throw new IllegalArgumentException("piece drops/sec too low");
        board = new Board();
        baserate = dropsPerSecond;
        frame = new JFrame("Tetris");
        frame.setSize(gamewidth+30, gameheight+50);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addKeyListener(new KeyChecker());
        frame.setLayout(new GridLayout(1,2));
        panel = new JPanel(new GridBagLayout());
        info = new JPanel(new GridBagLayout());
        info.setLayout(new GridLayout(2,1));
        view = new JPanel(new GridBagLayout());
        score = new JLabel();
        score.setHorizontalAlignment(JLabel.CENTER);
        cells = new DrawnCell[board.rows][board.cols];
        preview = new DrawnCell[4][4];
        cn = new GridBagConstraints();
        cn.fill = GridBagConstraints.HORIZONTAL;
    }

    /**
     * increase drops/second every {rate} pieces placed. defaults to 0. (0 = no acceleration)
     */
    public GUI setAccelerationRate(int rate) {
        if (rate<0) throw new IllegalArgumentException("acceleration rate too low");
        accelerationrate = rate;
        return this;
    }

    /**
     * score necessary to win the game. defaults to 500
     */
    public GUI setScoreGoal(int score) {
        if (score<1) throw new IllegalArgumentException("score goal too low");
        board.threshold = score;
        return this;
    }

    /**
     * shows game window and starts gameplay
     */
    public void start() {
        fill();
        frame.getContentPane().add(panel);
        frame.getContentPane().add(info);
        info.add(view);
        info.add(score);
        frame.setVisible(true);
        loop();
    }

    /**
     * each frame of the gameplay
     */
    private void loop() {
        long last = new Date().getTime();
        long now;
        boolean b;

        updateScore();
        accelerate();
        draw(board.current);
        preview(board.next);

        while (true) {
            if (last+turnmilliseconds <= (now = new Date().getTime())) {
                last = now;

                if (board.gameStatus==GameStatus.LOSS) {
                    JOptionPane.showMessageDialog(frame,"Defeat. Final score was "+ board.score+".");
                    System.exit(0);
                }

                if (board.gameStatus==GameStatus.WIN) {
                    JOptionPane.showMessageDialog(frame,"Victory! Final score was "+ board.score+".");
                    System.exit(0);
                }

                erase(board.current);
                b = board.current.fall();
                draw(board.current);
                if (!b) nextPiece();
            }
        }
    }

    /**
     * prepares the GUI. first 4 rows are invisible
     */
    private void fill() {
        for (int r = 0; r<board.rows-4; r++) {
            for (int c = 0; c<board.cols; c++) {
                DrawnCell cell = new DrawnCell();
                cn.gridx = c;
                cn.gridy = r;
                cells[r][c] = cell;
                panel.add(cell, cn);
            }
        }

        for (int r=0; r<4; r++) {
            for (int c=0; c<4; c++) {
                DrawnCell cell = new DrawnCell();
                cn.gridx = c;
                cn.gridy = r;
                preview[r][c] = cell;
                view.add(cell, cn);
            }
        }

        board.fill();

    }

    /**
     * reacts to movement commands
     */
    private class KeyChecker extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent event) {
            boolean b;

            switch (event.getKeyCode()) {
                case KeyEvent.VK_UP:
                    erase(board.current);
                    board.current.rotate();
                    draw(board.current);
                    break;
                case KeyEvent.VK_DOWN:
                    erase(board.current);
                    b = board.current.fall();
                    draw(board.current);
                    if (!b) nextPiece();
                    break;
                case KeyEvent.VK_LEFT:
                    erase(board.current);
                    board.current.left();
                    draw(board.current);
                    break;
                case KeyEvent.VK_RIGHT:
                    erase(board.current);
                    board.current.right();
                    draw(board.current);
                    break;
            }
        }
    }

    /**
     * updates score label
     */
    private void updateScore() {
        score.setText("Score: "+board.score+"  /  "+board.threshold);
    }

    /**
     * procedure to follow when a piece is locked in and a new one must be created
     */
    private void nextPiece() {
        checkFilledRows();
        updateScore();
        board.nextPiece();
        draw(board.current);
        preview(board.next);
        accelerate();
    }

    /**
     * increases piece drop speed
     */
    private void accelerate() {
        if (accelerationrate==0) return;

        turnmilliseconds = 1000 / (baserate + (board.piecesGenerated-1)/accelerationrate);
    }

    /**
     * delete filled rows if any exist. update score
     */
    private void checkFilledRows() {
        ArrayList<Integer> rows = board.checkFilledRows();
        for (int row: rows)
            deleteRow(row);
    }

    /**
     * delete row by repainting grid cells
     */
    private void deleteRow(int row) {
        for (int r=row;r>0;r--) { //make rows fall, starting from the bottom
            for (int c=0;c<board.cols;c++) {
                cells[r][c].takeColor(cells[r-1][c]);
            }
        }
        for (int i=0;i<board.cols;i++) { //uppermost row
            cells[0][i].empty();
        }
    }

    /**
     * unregisters the piece's current position
     */
    private void erase(Piece piece) {
        for (Point p: piece.location) {
            if (cell(p) != null) cell(p).empty();
        }

        board.clearCurrent();
    }

    /**
     * registers the piece's current position
     */
    private void draw(Piece piece) {
        for (Point p: piece.location) {
            if (cell(p) != null) cell(p).piece(piece);
        }

        board.markCurrent();
    }

    /**
     * draws the piece in the preview grid
     */
    private void preview(Piece piece) {
        for (int r=0;r<4;r++) {
            for (int c=0;c<4;c++) {
                preview[r][c].empty();
            }
        }

        for (Point p: piece.preview) {
            preview[p.row][p.col].piece(piece);
        }

        board.markCurrent();
    }

    /**
     * convert point to main grid cell reference
     */
    private DrawnCell cell(Point p) {
        return p.row-4<0 ? null : cells[p.row-4][p.col];
    }

    /**
     * each cell of a grid
     */
    private class DrawnCell extends JLabel {

        public DrawnCell() {
            super();
            super.setBorder(BorderFactory.createLineBorder(Color.black));
            super.setOpaque(true);
        }

        public void empty() {
            super.setBackground(null);
        }

        public void takeColor(DrawnCell cell) { super.setBackground(cell.getBackground()); }

        public void piece(Piece piece) {
            super.setBackground(piece.color);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(gamewidth/board.cols/2, gameheight/ board.rows);
        }

        @Override
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        @Override
        public Dimension getMaximumSize() {
            return getPreferredSize();
        }
    }
}
