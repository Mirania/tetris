package tetris;

import java.awt.Color;
import tetris.Utils.Point;

abstract public class Piece {

    public Board board; //rows 0-3 are invisible
    public Point[] location;
    public Point[] preview; //for a 4x4 grid
    public Color color;
    private int centerPoint;

    public Piece(Board b, Color color, int centerPoint, int[] initCoords, int[] previewCoords) {
        if (centerPoint<0 || centerPoint>3) throw new IllegalArgumentException("bad center point");
        this.board = b;
        this.color = color;
        this.centerPoint = centerPoint;
        location = Utils.coordsToArray(initCoords);
        preview = Utils.coordsToArray(previewCoords);
    }

    /**
     * makes an array of 4 points from 8 coordinates
     */
    private Point[] coordsToArray(int... coords) {
        if (coords.length!=8) throw new IllegalArgumentException("missing coords");
        Point[] pts = new Point[4];
        for (int i=0; i<coords.length; i+=2) {
            pts[i/2] = new Point(coords[i], coords[i+1]);
        }
        return pts;
    }

    /**
     * changes piece coordinates by rotating it left, if possible
     */
    public void rotate() {
        Point[] newlocation = new Point[4];

        for (int p=0; p<location.length; p++) {
            if (p==centerPoint) {
                newlocation[p] = location[centerPoint];
            } else {
                newlocation[p] = new Point(
                        (location[centerPoint].row + location[p].col) - location[centerPoint].col,
                        location[centerPoint].col - location[p].row + location[centerPoint].row);
                if (blocked(newlocation[p])) return; //abort early
            }
        }

        location = newlocation;
    }

    /**
     * changes piece coordinates by letting it fall one square, if possible
     */
    public boolean fall() {
        if (!blockedUnder()) {
            for (Point p : location)
                p.row++;
            return true;
        }
        return false;
    }

    /**
     * changes piece coordinates by pushing it left one square, if possible
     */
    public boolean left() {
        if (!blockedLeft()) {
            for (Point p : location)
                p.col--;
            return true;
        }
        return false;
    }

    /**
     * changes piece coordinates by pushing it right one square, if possible
     */
    public boolean right() {
        if (!blockedRight()) {
            for (Point p : location)
                p.col++;
            return true;
        }
        return false;
    }

    /**
     * checks if piece is not allowed to move to this point
     */
    private boolean blocked(Point p) {
        if (p.row>=board.rows || p.row<0 || p.col<0 || p.col>=board.cols) return true;
        if (board.cell(p).isPiece && !Utils.arrayContains(location, p)) return true;
        return false;
    }

    /**
     * checks if piece is not allowed to fall
     */
    private boolean blockedUnder() {
        for (Point p: location) {
            Point under = new Point(p.row+1,p.col);
            if (under.row==board.rows) return true;
            if (board.cell(under).isPiece && !Utils.arrayContains(location, under)) return true;
        }
        return false;
    }

    /**
     * checks if piece is not allowed to move left
     */
    private boolean blockedLeft() {
        for (Point p: location) {
            Point left = new Point(p.row,p.col-1);
            if (left.col==-1) return true;
            if (board.cell(left).isPiece && !Utils.arrayContains(location, left)) return true;
        }
        return false;
    }

    /**
     * checks if piece is not allowed to move right
     */
    private boolean blockedRight() {
        for (Point p: location) {
            Point right = new Point(p.row,p.col+1);
            if (right.col==board.cols) return true;
            if (board.cell(right).isPiece && !Utils.arrayContains(location, right)) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Point p: location)
            sb.append(p.toString()).append(" ");
        return sb.toString();
    }

    public static class I extends Piece {

        public I(Board b, Color color) {
            super(b,color,1,
                    new int[]{3,4,2,4,1,4,0,4},
                    new int[]{0,2,1,2,2,2,3,2});
        }

    }

    public static class O extends Piece {

        public O(Board b, Color color) {
            super(b,color,0,
                    new int[]{3,4,3,5,2,4,2,5},
                    new int[]{1,1,1,2,2,1,2,2});
        }

        @Override
        public void rotate() { } //pointless to rotate this one

    }

    public static class T extends Piece {

        public T(Board b, Color color) {
            super(b,color,3,
                    new int[]{3,3,3,4,3,5,2,4},
                    new int[]{2,1,2,2,2,3,1,2});
        }

    }

    public static class S extends Piece {

        public S(Board b, Color color) {
            super(b,color,1,
                    new int[]{3,5,2,5,2,4,1,4},
                    new int[]{3,2,2,2,2,1,1,1});
        }

    }

    public static class Z extends Piece {

        public Z(Board b, Color color) {
            super(b,color,1,
                    new int[]{3,4,2,4,2,5,1,5},
                    new int[]{3,1,2,1,2,2,1,2});
        }

    }

    public static class J extends Piece {

        public J(Board b, Color color) {
            super(b,color,0,
                    new int[]{3,3,3,4,3,5,2,3},
                    new int[]{2,1,2,2,2,3,1,1});
        }

    }

    public static class L extends Piece {

        public L(Board b, Color color) {
            super(b,color,2,
                    new int[]{3,3,3,4,3,5,2,5},
                    new int[]{2,1,2,2,2,3,1,3});
        }

    }

}
