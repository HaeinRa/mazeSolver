public class Maze {
    public Cell[][] cells;
    private int width;
    private int height;
    private Point point;

    Maze(int[][] maze) {
        this.width = maze[0].length;
        this.height = maze.length;
        this.point = new Point();
        cells = new Cell[height][width];
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                cells[i][j] = new Cell(maze[i][j]);
                if (cells[i][j].getState() == Cell.State.EXIT) {
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

    public boolean update(Point center, int d, Maze maze, boolean isFindExit) // 기준 미로를 참고하여 마우스의 맵을 업데이트
    {
        int m = d / 2;
        int x;
        int y;
        x = center.x - m;
        if (x < 0) // 계산된 x좌표가 왼쪽 벽이거나 벗어나면
        {
            x = 0;
        } else if (x >= maze.getHeight()) {
            x = maze.getHeight() - 1;
        }
        y = center.y - m;
        if (y < 0) {
            y = 0;
        } else if (y >= maze.getWidth()) {
            y = maze.getWidth() - 1;
        }
        Point startPoint = new Point(x, y);

        for (int i = startPoint.x; i < Math.min(startPoint.x + d, this.cells.length); i++) {
            for (int j = startPoint.y; j < Math.min(startPoint.y + d, this.cells[i].length); j++) {
                this.cells[i][j] = Cell.createCopy(maze.getCell(i, j));
                if (!isFindExit) {
                    if (this.cells[i][j].getState() == Cell.State.EXIT)
                        isFindExit = true;
                }
            }
        }

        return isFindExit;
    }

    public void resetVisitedInfo(){
        for (int i = 0; i < this.getHeight(); i++) {
            for (int j = 0; j < this.getWidth(); j++) {
                if ( this.getCell(i,j).getState() == Cell.State.VISIT || this.getCell(i,j).getState() == Cell.State.BRANCH) {
                    this.getCell(i,j).setState(Cell.State.AVAILABLE);
                }
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
