public class Maze {
  Cell[][] cells;
  private int width;
  private int height;
  private Point point;
  Maze(int[][] maze) {
    this.width = maze.length;
    this.height = maze[0].length;
    this.point = new Point();
    cells = new Cell[width][height];
    for (int i=0; i<this.width; i++) {
      for (int j=0; j<this.height; j++) {
        cells[i][j] = new Cell(maze[i][j]);
        if(cells[i][j].getState() == Cell.State.EXIT) {
          point.x = i;
          point.y = j;
        }
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
  public Point getEndPoint() {
    return point; // null을 반환하면 출구가 없음
  }
  public boolean update(Point center, int d, Maze maze, boolean isFindExit) {
    int m = d / 2;
    int x = center.x - m;
    int y = center.y - m;
    if (x < 0) // 계산된 x좌표가 왼쪽 벽이거나 벗어나면
    {
      x = 0;
    }
    else if (x >= maze.getWidth())
    {
      x = maze.getWidth() - 1;;
    }
    y = center.y - m;
    if (y < 0)
    {
      y = 0;
    }
    else if (y >= maze.getHeight())
    {
      y = maze.getHeight() - 1;
    }
    Point startPoint = new Point(x, y);

    for (int i = startPoint.x; i < startPoint.x + d; i++) {
      for (int j = startPoint.y; j < startPoint.y + d; j++) {
        this.cells[i][j] = maze.getCell(i, j);
        if (!isFindExit) {
          if (this.cells[i][j].getState() == Cell.State.EXIT)
            return true;
        } else {
          return true;
        }
      }
    }
    return false;
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
