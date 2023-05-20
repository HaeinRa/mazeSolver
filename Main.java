public class Main {
  public static void main(String[] arg) {
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

    willChangeMaze.print();
  }
}
