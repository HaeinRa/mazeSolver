import java.util.Arrays;
import java.util.PriorityQueue;

public class AstarAlgorithm
{
  private Maze maze;
  private int start_x;
  private int start_y;
  private int end_x;
  private int end_y;
  private boolean[][] closed_set;
  private int[][] came_from;
  private int[][] g_score;
  private int[][] f_score;
  private PriorityQueue<int[]> open_set;

  public AstarAlgorithm(Maze maze, int start_x, int start_y, int end_x, int end_y) {
    this.maze = maze;
    this.start_x = start_x; // 시작 지점
    this.start_y = start_y;
    this.end_x = end_x; // 목표 지점
    this.end_y = end_y;
    this.closed_set = new boolean[maze.getWidth()][maze.getHeight()]; // 닫힌 목록
    this.came_from = new int[maze.getWidth()][maze.getHeight()];
    this.g_score = new int[maze.getWidth()][maze.getHeight()]; // 시작점에서부터 가중치
    this.f_score = new int[maze.getWidth()][maze.getHeight()]; // g score + h score, f_score가 큰 것을 먼저 탐색
    this.open_set = new PriorityQueue<>((a, b) -> f_score[a[0]][a[1]] - f_score[b[0]][b[1]]); // 열린 목록
    init();
  }

  private void init() {
    for (int i = 0; i < maze.getWidth(); i++) {
      Arrays.fill(closed_set[i], false);
      Arrays.fill(came_from[i], -1);
      Arrays.fill(g_score[i], Integer.MAX_VALUE);
      Arrays.fill(f_score[i], Integer.MAX_VALUE);
    }
    g_score[start_x][start_y] = 0;
    f_score[start_x][start_y] = heuristic(start_x, start_y);
    open_set.offer(new int[]{start_x, start_y});
  }

  // 맨하탄 거리로 휴리스틱 값을 구한다.
  private int heuristic(int x, int y) {
    return Math.abs(x - end_x) + Math.abs(y - end_y);
  }

  public int[][] run() {
    while (!open_set.isEmpty()) // 열린 목록이 비어있지 않을 동안 반복
    {
      int[] current = open_set.poll();

      if (current[0] == end_x && current[1] == end_y) // 목표 지점 탐색에 성공하면
      {
        return reconstructPath(current); // 시작점부터 목표지점까지의 좌표를 저장한 배열을 반환
      }

      closed_set[current[0]][current[1]] = true; // 현재 위치를 닫힌 목록에 추가

      for (int i = -1; i <= 1; i++) {
        for (int j = -1; j <= 1; j++) {
          if (i == 0 && j == 0) {
            continue;
          }

          int x = current[0] + i;
          int y = current[1] + j;

          // 미로 범위 밖을 벗어나거나 벽 또는 닫힌 목록에 있는 아이템은 고려하지 않음
          if (x < 0 || x >= maze.getWidth() || y < 0 || y >= maze.getHeight() || maze.getCell(x,y).getState() == Cell.State.WALL || closed_set[x][y]) 
          {
            continue;
          }
          
          // currnet[0]는 x좌표, current[1]은 y좌표
          int tentative_g_score = g_score[current[0]][current[1]] + 1;

          if (tentative_g_score < g_score[x][y]) {
            came_from[x][y] = current[0] * maze.getHeight() + current[1];
            g_score[x][y] = tentative_g_score;
            f_score[x][y] = g_score[x][y] + heuristic(x, y);

            if (!open_set.contains(new int[]{x, y})) {
              open_set.offer(new int[]{x, y});
            }
          }
        }
      }
    }

    return null;
  }
  private int[][] reconstructPath(int[] current) {
    int[][] path = new int[g_score[current[0]][current[1]] + 1][2];
    int index = path.length - 1;
    while (current[0] != start_x || current[1] != start_y) {
      path[index--] = new int[]{current[0], current[1]};
      current = new int[]{came_from[current[0]][current[1]] / maze.getHeight(), came_from[current[0]][current[1]] % maze.getHeight()};
    }
    path[index] = new int[]{start_x, start_y};
    return path;
  }
}
