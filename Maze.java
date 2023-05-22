public class Maze {
  Cell[][] cells;
  private int width;
  private int height;
  Maze(int[][] maze) {
    this.width = maze.length;
    this.height = maze[0].length;
    cells = new Cell[width][height];
    for (int i=0; i<this.width; i++) {
      for (int j=0; j<this.height; j++) {
        cells[i][j] = new Cell(maze[i][j]);
      }
    }
  }
  public int getWidth() {
    return width;
  }
  public int getHeight() {
    return height;
  }
  public Cell getCell(int x, int y) // 원하는 좌표의 cell을 반환
  {
    return cells[x][y];
  }
  public Cell getCell(Point point) // 원하는 좌표의 cell을 반환
  {
    return cells[point.x][point.y];
  }
  public void update(Point center, int d, Maze maze) // 기준 미로를 참고하여 마우스의 맵을 업데이트
  {
    int m = d/2;
    int x;
    int y;
    x = center.x - m;
    y = center.y - m;
    Point startPoint = new Point(x, y);

    for (int i=startPoint.x; i<startPoint.x + d; i++) {
      for (int j= startPoint.y; j<startPoint.y + d; j++) {
        this.cells[i][j] = maze.getCell(i, j);
      }
    }
  }

  public void print() {
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        System.out.print(cells[x][y].getState() + " ");
      }
      System.out.println();
    }
  }
}
