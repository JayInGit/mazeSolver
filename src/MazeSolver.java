import javax.swing.*;
import java.util.Arrays;
import java.util.Collections;

public class MazeSolver {
    public static class Maze extends JPanel{
        private static final class MazeGenerator {
            private final int x;
            private final int y;
            private final int[][] maze;
            private String maze_str = "";

            public MazeGenerator(int x, int y) {
                this.x = x;
                this.y = y;
                maze = new int[this.x][this.y];
                generateMaze(0, 0); // generates the maze with bits, mark the cell by bits
                make_string(); // create a string maze that is visible according to the marked bits.
            }

            public void make_string() {
                for (int i = 0; i < y; i++) {
                    // draw the north edge
                    for (int j = 0; j < x; j++)
                        maze_str = maze_str.concat((maze[j][i] & 1) == 0 ? "+-" : "+ ");
                    maze_str = maze_str.concat("+");
                    // draw the west edge
                    for (int j = 0; j < x; j++)
                        maze_str = maze_str.concat((maze[j][i] & 8) == 0 ? "| " : "  ");
                    maze_str = maze_str.concat("|");
                }
                // draw the bottom line
                for (int j = 0; j < x; j++)
                    maze_str = maze_str.concat("+-");
                maze_str = maze_str.concat("+");
            }

            private void generateMaze(int cx, int cy) {
                MazeGenerator.DIR[] dirs = DIR.values(); // gets all possible directions
                Collections.shuffle(Arrays.asList(dirs));
                for (DIR dir : dirs) { // loop through each direction
                    int nx = cx + dir.dx; // new x
                    int ny = cy + dir.dy; // new y
                    if (between(nx, x) && between(ny, y) && (maze[nx][ny] == 0)) { // check if the cell is in bound AND visited or not
                        maze[cx][cy] |= dir.bit; // mark the current cell as connected to the new cell in the direction
                        maze[nx][ny] |= dir.opposite.bit; // mark the new cell as connected to the current cell in the opposite direction
                        generateMaze(nx, ny);
                    }
                }
            }

            private static boolean between(int v, int upper) {
                return (v >= 0) && (v < upper);
            }

            private static enum DIR { // DIRECTIONS
                N(1, 0, -1), S(2, 0, 1), E(4, 1, 0), W(8, -1, 0);
                private final int bit;
                private final int dx;
                private final int dy;
                private DIR opposite;

                static {
                    N.opposite = S;
                    S.opposite = N;
                    E.opposite = W;
                    W.opposite = E;
                }

                private DIR(int bit, int dx, int dy) {
                    this.bit = bit;
                    this.dx = dx;
                    this.dy = dy;
                }
            };
        }
        public Maze(int width, int leingt){ //constructor
            
        }
    }
}
