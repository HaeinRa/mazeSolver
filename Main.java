public class Main {
  public static void main(String[] arg) {
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
    Maze baseMaze = new Maze(m2);
    Point point = new Point(2, 2);
    willChangeMaze.update(point, 3, baseMaze);
*/
/*
    willChangeMaze.print();*/
    int[][] readMaze = CeremonyAlgorithm.readMaze("C:\\Users\\herje\\OneDrive\\바탕 화면\\한기대\\3학년 1학기\\민고리즘\\민고리즘 텀프\\mazeSolver\\test.txt");

    Point initialLocation = new Point(3, 50); // 초기 위치
    int initialEnergy = 100; // 초기 에너지

    Maze maze = new Maze(readMaze);
    Mouse mouse = new Mouse(initialLocation, initialEnergy);

    GUI gui = new GUI(maze, mouse);
    gui.repaint();
  }
}
