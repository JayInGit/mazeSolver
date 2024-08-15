import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
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

        private class Cell {
            int row;
            int col;
            Cell prev;   // Each state corresponds to a cell
            // and each state has a predecessor which
            // is stored in this variable

            public Cell(int row, int col){
                this.row = row;
                this.col = col;
            }

        }
        private class RepaintAction implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent evt) {
                //////////////////////////////////////////////////////// checkTermination(); //////////////////////////////////////////////////
                repaint();
                if (endOfSearch)
                {
                    animation = false;
                    timer.stop();
                }
            }
        }

        int rows;
        int columns;
        int squareSize;
        int[][] grid;
        int delay;
        int expanded;
        boolean found;
        Cell robotStart;
        Cell targetPos;
        Point[][] centers;
        JRadioButton square;

        // basic buttons
        JButton mazeButton;
        JButton clearButton;
        JButton stepButton;
        JButton animationButton;

        // buttons for selecting the algorithm
        JRadioButton dfs, bfs;
        boolean searching;
        boolean endOfSearch;
        boolean animation;
        ArrayList<Cell> openSet   = new ArrayList();
        ArrayList<Cell> closedSet = new ArrayList();


        // the object that controls the animation
        RepaintAction action = new RepaintAction();

        // the Timer which governs the execution speed of the animation
        Timer timer;
        public Maze(int width, int height){ //constructor
            super.setLayout(null);
            super.setPreferredSize( new Dimension(width,height) );

            grid = new int[rows][columns];

            square = new JRadioButton("Square");
            square.setSelected(true);



            mazeButton = new JButton("Maze");
            mazeButton.setBackground(Color.lightGray);
            mazeButton.addActionListener(this::mazeButtonAction);


            clearButton = new JButton("Clear");
            clearButton.setBackground(Color.lightGray);
            clearButton.addActionListener(this::clearButtonAction);


            stepButton = new JButton("Step-by-Step");
            stepButton.setBackground(Color.lightGray);
            stepButton.addActionListener(this::stepButtonAction);


            animationButton = new JButton("Animation");
            animationButton.setBackground(Color.lightGray);
            animationButton.addActionListener(this::animationButtonAction);



            JLabel delayLbl = new JLabel("Delay (0-1000 msec)", JLabel.CENTER);
            delayLbl.setFont(new Font("Helvetica",Font.PLAIN,10));



            delay = 1; // this is in msec

            // ButtonGroup that synchronizes the five RadioButtons
            // choosing the algorithm, so that only one
            // can be selected anytime
            ButtonGroup algoGroup = new ButtonGroup();

            dfs = new JRadioButton("DFS");
            dfs.setToolTipText("Depth First Search algorithm");
            algoGroup.add(dfs);
            dfs.setSelected(true);  // DFS is initially selected

            bfs = new JRadioButton("BFS");
            bfs.setToolTipText("Breadth First Search algorithm");
            algoGroup.add(bfs);

            JLabel robot = new JLabel("Robot", JLabel.CENTER);
            robot.setForeground(Color.red);
            robot.setFont(new Font("Helvetica",Font.PLAIN,14));

            JLabel target = new JLabel("Target", JLabel.CENTER);
            target.setForeground(Color.GREEN);
            target.setFont(new Font("Helvetica",Font.PLAIN,14));

            JLabel frontier = new JLabel("Frontier", JLabel.CENTER);
            frontier.setForeground(Color.blue);
            frontier.setFont(new Font("Helvetica",Font.PLAIN,14));

            JLabel closed = new JLabel("Closed set", JLabel.CENTER);
            closed.setForeground(Color.CYAN);
            closed.setFont(new Font("Helvetica",Font.PLAIN,14));


            super.add(square);
            super.add(mazeButton);
            super.add(clearButton);
            super.add(stepButton);
            super.add(animationButton);
            super.add(delayLbl);
            super.add(dfs);
            super.add(bfs);
            super.add(robot);
            super.add(target);
            super.add(frontier);
            super.add(closed);


            square.setBounds(610, 80, 70, 25);

            mazeButton.setBounds(520, 165, 170, 25);
            clearButton.setBounds(520, 195, 170, 25);
            stepButton.setBounds(520, 255, 170, 25);
            animationButton.setBounds(520, 285, 170, 25);
            delayLbl.setBounds(520, 315, 170, 10);

            dfs.setBounds(530, 370, 70, 25);
            bfs.setBounds(605, 370, 70, 25);

            robot.setBounds(520, 475, 80, 25);
            target.setBounds(605, 475, 80, 25);
            frontier.setBounds(520, 495, 80, 25);
            closed.setBounds(605, 495, 80, 25);


            timer = new Timer(delay, action);


            // the first step of the algorithms
            initializeGrid(false);
        }
        private void initializeGrid(Boolean makeMaze) {

            grid = new int[rows][columns];
            centers = new Point[rows][columns];
            if (square.isSelected()){
                robotStart = new Cell(rows-2,1);
                targetPos = new Cell(1,columns-2);
            } else {
                robotStart = new Cell(rows-1,0);
                targetPos = new Cell(0,columns-1);
            }

            //  Calculation of the size of the square cell
            if (rows > columns){
                squareSize = 500/rows;
            } else{
                squareSize = 500/columns;
            }

            //  Calculation of the coordinates of the cells' centers
            int y=0;
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < columns; c++){
                    centers[r][c] = new Point(11+c*squareSize+squareSize/2, 11+r*squareSize+squareSize/2);
                }
            fillGrid();

            if (makeMaze) {
                MazeGenerator maze = new MazeGenerator(rows/2,columns/2);
                for (int r = 0; r < rows; r++)
                    for (int c = 0; c < columns; c++)
                        if (maze.maze_str.substring(r*columns+c, r*columns+c+1).matches(".*[+-|].*"))
                            grid[r][c] = MazeConstants.OBST;
            }
        }
        private void fillGrid() {

            if (searching || endOfSearch){ // if maze in progress or finished
                for (int r = 0; r < rows; r++)
                    for (int c = 0; c < columns; c++) {
                        if (grid[r][c] == MazeConstants.FRONTIER || grid[r][c] == MazeConstants.CLOSED || grid[r][c] == MazeConstants.ROUTE) // delete every cell
                            grid[r][c] = MazeConstants.EMPTY;
                        if (grid[r][c] == MazeConstants.ROBOT)
                            robotStart = new Cell(r,c); // if its starting position = not deleting
                        if (grid[r][c] == MazeConstants.TARGET)
                            targetPos = new Cell(r,c); // if its ending position = not deleting
                    }
                searching = false;
            } else { // if nothing going on in maze
                for (int r = 0; r < rows; r++)
                    for (int c = 0; c < columns; c++)
                        grid[r][c] = MazeConstants.EMPTY; // delete everything
                //set the start, end position
                robotStart = new Cell(rows-2,1);
                targetPos = new Cell(1,columns-2);

            }

            expanded = 0;
            found = false;
            searching = false;
            endOfSearch = false;

            // The first step of the other four algorithms is here
            // 1. OPEN SET: = [So], CLOSED SET: = []
            openSet.removeAll(openSet);
            openSet.add(robotStart);
            closedSet.removeAll(closedSet);

            grid[targetPos.row][targetPos.col] = MazeConstants.TARGET;
            grid[robotStart.row][robotStart.col] = MazeConstants.ROBOT;
            timer.stop();
            repaint();

        }
        private void mazeButtonAction(java.awt.event.ActionEvent event){
        }
        private void clearButtonAction(java.awt.event.ActionEvent event){
        }
        private void stepButtonAction(java.awt.event.ActionEvent event){
        }
        private void animationButtonAction(java.awt.event.ActionEvent event){
        }

    }
}
