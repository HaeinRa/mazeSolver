import java.util.concurrent.TimeUnit;

public class Main {
  public static void main(String[] arg) throws InterruptedException {

    int[][] readMaze = CeremonyAlgorithm.readMaze("C:\\Users\\herje\\OneDrive\\바탕 화면\\한기대\\3학년 1학기\\민고리즘\\민고리즘 텀프\\mazeSolver\\test.txt");

    Point initialLocation = new Point(0, 1); // 초기 위치
    int initialEnergy = 100; // 초기 에너지

    Maze maze = new Maze(readMaze);
    Mouse mouse = new Mouse(initialLocation, initialEnergy);

    GUI gui = new GUI(maze, mouse);
    gui.repaint();

    Point scanPoint = new Point(27, 45);

    TimeUnit.SECONDS.sleep(1);

    maze.update(scanPoint, 3, maze, false);
    mouse.changeLocation(scanPoint);

    gui.repaint();

  }
}
