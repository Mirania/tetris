package tetris;

import java.util.Random;

public class Utils {

    /**
     * max and min both inclusive
     */
    public static int random(int min, int max) {
        if (min > max) throw new IllegalArgumentException("max must be greater than min");
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public static boolean arrayContains(Point[] array, Point p) {
        for (Point a: array)
            if (a.equals(p)) return true;
        return false;
    }

    /**
     * makes an array of x points from 2x coordinates
     */
    public static Point[] coordsToArray(int[] coords) {
        if (coords.length%2!=0) throw new IllegalArgumentException("missing coords");
        Point[] pts = new Point[coords.length/2];
        for (int i=0; i<coords.length; i+=2) {
            pts[i/2] = new Point(coords[i], coords[i+1]);
        }
        return pts;
    }

    public enum GameStatus {
        WIN, LOSS, PLAYING;
    }

    public static class Point {
        public int row;
        public int col;

        public Point(int r, int c) {
            row = r;
            col = c;
        }

        public boolean equals(Point p) {
            return p.row==row && p.col==col;
        }

        @Override
        public String toString() {
            return "(row:"+row+", col:"+col+")";
        }
    }
}
