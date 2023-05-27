import javax.swing.*;
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

    int[][] readMaze = CeremonyAlgorithm.readMaze("C:\\Users\\herje\\OneDrive\\바탕 화면\\한기대\\3학년 1학기\\민고리즘\\민고리즘 텀프\\mazeSolver\\test.txt");

    Point initialLocation = new Point(0, 1); // 초기 위치
    int initialEnergy = 100; // 초기 에너지

    Maze maze = new Maze(readMaze);
    Mouse mouse = new Mouse(initialLocation, initialEnergy);

    Point scanPoint = new Point(3, 27);

    //maze, mouse
    GUI gui = new GUI(maze, mouse);
    gui.repaint();

    Point scanPoint = new Point(27, 45);

    TimeUnit.SECONDS.sleep(1);

    maze.update(scanPoint, 3, maze, false);
    mouse.changeLocation(scanPoint);

    gui.repaint();
    //gui.drawMaze(maze, mouse);

  }
}
