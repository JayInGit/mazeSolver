import javax.swing.*;

public class Main {
    public static JFrame mazeFrame;

    public static void main(String[] args) {
        // jframe setting
        int width  = 800;
        int height = 550;
        mazeFrame = new JFrame("Maze Solver");
        mazeFrame.setContentPane(new MazeSolver.Maze(width,height));
        mazeFrame.pack();
        mazeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mazeFrame.setVisible(true);
    }
}