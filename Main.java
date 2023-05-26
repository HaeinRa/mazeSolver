import java.util.concurrent.TimeUnit;

public class Main {
  public static void main(String[] arg) throws InterruptedException {
/*
    System.out.println("채승윤");
    int[][] m = {
            {1,0,1,1,1},
            {1,0,0,0,1},
            {1,0,0,0,0},
            {1,0,0,0,1},
            {1,1,1,1,1}
    };
    int[][] m2 = {
            {1,0,1,1,1},
            {1,1,1,1,1},
            {1,1,1,1,1},
            {1,1,1,1,1},
            {1,1,1,1,1},
    };
    Maze willChangeMaze = new Maze(m);
    willChangeMaze.print();

    Maze baseMaze = new Maze(m2);
    Point point = new Point(2, 2);
    willChangeMaze.update(point, 3, baseMaze);
*/
/*
    willChangeMaze.print();*/
    int[][] readMaze = CeremonyAlgorithm.readMaze("C:\\Users\\user\\matrix\\Maze1.txt");

    Point initialLocation = new Point(3, 25); // 초기 위치
    int initialEnergy = 100; // 초기 에너지

    Maze maze = new Maze(readMaze);
    Mouse mouse = new Mouse(initialLocation, initialEnergy);

    Point scanPoint = new Point(3, 27);

    //maze, mouse
    GUI gui = new GUI(maze, mouse);
    gui.repaint();

    TimeUnit.SECONDS.sleep(1);

    maze.update(scanPoint, 3, maze, false);
    mouse.changeLocation(scanPoint);

    gui.repaint();
    //gui.drawMaze(maze, mouse);

  }
}
