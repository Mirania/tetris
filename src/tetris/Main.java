package tetris;

public class Main {

    public static void main(String[] args) {
        new GUI(2)
                .setScoreGoal(500)
                .setAccelerationRate(20)
                .start();
    }
}
