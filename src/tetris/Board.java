package tetris;

import tetris.Utils.*;
import tetris.Utils.Point;
import java.awt.*;
import java.util.ArrayList;

public class Board {

    // col increases going right
    // row increases going down

    private Cell[][] board;
    private Color colorI = new Color(15, 141, 222);
    private Color colorO = new Color(180, 128, 23);
    private Color colorT = new Color(117, 61, 152);
    private Color colorS = new Color(10, 134, 51);
    private Color colorZ = new Color(197, 7, 13);
    private Color colorJ = new Color(17, 44, 182);
    private Color colorL = new Color(121, 47, 26);

    public int rows = 24; //first 4 rows are invisible in the GUI
    public int cols = 10;
    public int score = 0;
    public GameStatus gameStatus;
    public Piece current;
    public Piece next;
    public int threshold = 500; //score required to win
    public int piecesGenerated = -1;

    public Board() {
        board = new Cell[rows][cols];
        gameStatus = GameStatus.PLAYING;
    }

    /**
     * generates the next piece
     */
    public void nextPiece() {
        for (int i=0; i<cols; i++)
            if (board[3][i].isPiece) { gameStatus = GameStatus.LOSS; return; }

        current = next;

        switch (Utils.random(0, 6)) {
            case 0: next = new Piece.I(this, colorI); break;
            case 1: next = new Piece.O(this, colorO); break;
            case 2: next = new Piece.T(this, colorT); break;
            case 3: next = new Piece.S(this, colorS); break;
            case 4: next = new Piece.Z(this, colorZ); break;
            case 5: next = new Piece.J(this, colorJ); break;
            case 6: next = new Piece.L(this, colorL); break;
        }

        piecesGenerated++;
    }

    /**
     * delete filled rows if any exist. return indexes of deleted rows
     */
    public ArrayList<Integer> checkFilledRows() {
        int min = rows; //min possible row index where full line may have happened
        int max = -1; //max possible row index where full line may have happened
        for (int i=0;i<current.location.length;i++) {
            if (current.location[i].row>max) max = current.location[i].row;
            if (current.location[i].row<min) min = current.location[i].row;
        }

        if (min==rows && max==-1) return null; //nothing happened

        ArrayList<Integer> rows = new ArrayList<>();

        for (int i=min;i<=max;i++) {
            if (rowIsFull(i)) { deleteRow(i); rows.add(i-4); }
        }

        if (rows.size()>0) score += rows.size()*25+(rows.size()-1)*10;
        if (score>=threshold) gameStatus = GameStatus.WIN;

        return rows;
    }

    /**
     * delete row by changing status of cells
     */
    private void deleteRow(int row) {
        for (int r=row;r>4;r--) { //make rows fall, starting from the bottom
            for (int c=0;c<cols;c++) {
                board[r][c].isPiece = board[r-1][c].isPiece;
            }
        }
        for (int i=0;i<cols;i++) { //uppermost row
            board[4][i].isPiece = false;
        }
    }

    private boolean rowIsFull(int row) {
        for (int i=0; i<cols; i++) {
            if (!board[row][i].isPiece) return false;
        }
        return true;
    }

    /**
     *
     */
    public void markCurrent() {
        for (Point p: current.location)
            cell(p).isPiece = true;
    }

    public void clearCurrent() {
        for (Point p: current.location)
            cell(p).isPiece = false;
    }

    /**
     * convert point to cell reference
     */
    public Cell cell(Point p) {
        return board[p.row][p.col];
    }

    /**
     * prepares the board
     */
    public void fill() {
        for (int r=0; r<rows; r++) {
            for (int c=0; c<cols; c++) {
                board[r][c] = new Cell();
            }
        }

        nextPiece(); //current=null, next=piece1
        nextPiece(); //current=piece1, next=piece2
    }

    /**
     * game state cell
     */
    public class Cell {
        public boolean isPiece = false;
    }

}
