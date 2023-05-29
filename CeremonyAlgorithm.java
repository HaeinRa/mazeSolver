import sun.awt.image.ImageWatched;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class CeremonyAlgorithm {
    private static Maze maze;
    private static GUI gui;
    private static List<Point> scanList;
    private static List<Double> compareList;
//    private static Stack<Point> stack = new Stack<>();
    private static Mouse mouse;
    private static LinkedStack<Point> stack, buffer;
    private static int scanMode;
    private static boolean isFindExit;
    private static int branchCounter;

    LinkedStack<Point> AStarPath(){
        LinkedStack<Point> path = null;
        return path;
    }

    public static void main(String[] args) throws InterruptedException {



        /*
         * ------ SetUp 단계 ------
         * - 미로 txt 파일 읽기 (출구, 벽)
         * - 사용 가능한 미로로 변환 (Cell에 저장)
         * - GUI 띄우기 (미로, 쥐)
         * */

        // SetUp: 미로 txt 파일 읽기 (출구, 벽)
        // SetUp: 사용 가능한 미로로 변환 (Cell에 저장)
        stack = new LinkedStack<Point>();
        buffer = new LinkedStack<Point>();
        maze = new Maze(readMaze("C:\\Users\\user\\matrix\\Maze2.txt"));
        mouse = new Mouse(new Point(0,1), maze.getHeight()*maze.getWidth(), maze);
        mouse.map.getCell(0,1).setState(Cell.State.VISIT);
        gui = new GUI(maze,mouse);

        // 열린 목록, fScore가 큰 것을 출력
        PriorityQueue<Point> openList = new PriorityQueue<>((a, b) -> (int)(a.fScore - b.fScore));
        // 닫힌 목록, 닫힌 목록에 들어가면 fasle
        boolean[][] closeList = new boolean[maze.getHeight()][maze.getWidth()];
        for (int i=0; i< maze.getHeight(); i++) {
            for (int j=0; j<maze.getWidth(); j++) {
                closeList[i][j] = false;
            }
        }
        int openListCount = 0; // a* 알고리즘 사전 준비를 최초 한 번만 하기 위한 카운트
        int findCount = 0; // 탐색 횟수 저장 변수
        int[][] path; // 최단 경로 저장 변수
        Point start = new Point(mouse.getLocation().x, mouse.getLocation().y); // 시작 지점 저장 변수
        Point prevPoint = null; // 이전 방문 노드를 비교하기 위한 변수

        // SetUp: GUI 띄우기 (미로, 쥐)
        gui.repaint();
        TimeUnit.SECONDS.sleep(1);

        System.out.println("point001: setup done");

        isFindExit = false;
        scanMode = 0;
        branchCounter = 0; // 분기점인지 체크하는 카운터

        // Run 단계 시작 (반복시켜야함)
        System.out.println("point002: Run state start");

        Point end = maze.getEndPoint();
        // 현재 쥐의 상태를 확인 (체력과 마나)
        // 체력이 남아 있다면
        while(true)
        {
            System.out.println("point019: Exit already found");
            System.out.println(mouse.map.getEndPoint());
            scanMode = 1; // 스캔모드를 바꾼다
            System.out.println("point020: change scanMode to 1");
            // 시야 업데이트 시 출구를 찾으면 a* 알고리즘 준비
            if (openListCount == 0) {
                // 지나온 길(탐색한 길)을 닫힌 목록에 추가
                for (int i=0; i<maze.getHeight(); i++) {
                    for (int j=0; j<maze.getWidth(); j++) {
                        if (maze.getCell(i,j).getState() == Cell.State.NotRecommended || maze.getCell(i,j).getState() == Cell.State.VISIT) {
                            closeList[i][j] = true;
                        }
                    }
                }
                // 현재 위치(시작점)을 열린 목록에 한 번만 추가
                Point current = new Point(mouse.getLocation().x, mouse.getLocation().y, 0,
                        Math.abs(mouse.getLocation().x - maze.getEndPoint().x) + Math.abs(mouse.getLocation().y - maze.getEndPoint().y), null);
                openList.add(current);
                System.out.println("시야 업데이트 후 열린 목록에 시작점 추가 : " + current.x + ", " + current.y);
                System.out.println(openList.size());
                openListCount += 1;
            }

            if (!openList.isEmpty()) {
                Point point = openList.poll();
                System.out.println("열린 목록에서 poll함, 열린 목록 크기: " + openList.size());
                System.out.println("현재 위치 : " + point.x + "," + point.y);
                System.out.println("꺼낸 아이템의 gScore : " + point.gScore + ", 꺼낸 아이템의 fScore : " + point.fScore);
                // 꺼낸 아이템는 닫힌 목록에 추가(다시 탐색하지 않음)
                closeList[point.x][point.y] = true;
                // 내가 이동할 위치와 이전 위치를 비교
                if (point.prevPoint != prevPoint) {
                    AstarAlgorithm astarAlgorithm = new AstarAlgorithm(maze, mouse.getLocation().x, mouse.getLocation().y, point.x, point.y);
                    path = astarAlgorithm.run();
                    for (int i=0; i<path.length; i++) {
                        mouse.move();
                        Point loc = new Point(path[i][0], path[i][1]);
                        mouse.changeLocation(loc);
                        if (maze.getCell(point).getState() == Cell.State.VISIT) {
                            maze.getCell(point).setState(Cell.State.NotRecommended); // 지나온 길을 다시 지나가면 NotRecommended
                        } else {
                            maze.getCell(point).setState(Cell.State.VISIT);
                        }
                        gui.repaint();
                        TimeUnit.MILLISECONDS.sleep(20);
                    }
                } else {
                    // 꺼낸 위치로 이동
                    mouse.move();
                    mouse.changeLocation(point);
                    if (maze.getCell(point).getState() == Cell.State.VISIT) {
                        maze.getCell(point).setState(Cell.State.NotRecommended); // 지나온 길을 다시 지나가면 NotRecommended
                    } else {
                        maze.getCell(point).setState(Cell.State.VISIT);
                    }
                    gui.repaint();
                    TimeUnit.MILLISECONDS.sleep(50);
                }
                if (mouse.getLocation().x == end.x && mouse.getLocation().y == end.y)
                {
                    System.out.println("success, " + findCount + "회 탐색");
                    break;
                }
                // 열린 목록 주위(상하좌우) 중 닫힌 목록에 없는 아이템은 벽을 제외하고 열린 목록에 추가
                for (int i=0; i<4; i++) {
                    if (i==0) {
                        // 가중치가 계산된 Point를 집어넣음
                        // 가중치는 g와 f가 있음
                        int hScore = Math.abs((point.x - 1) - maze.getEndPoint().x) + Math.abs(point.y - maze.getEndPoint().y);
                        // gScore는 시작지점으로부터 현재 노드까지의 비용, 간선의 가중치는 임의로 1로 정함
                        int gScore;
                        // 현재 노드의 이전 노드가 없으면, 즉 시작 노드이면
                        if (point.prevPoint == null) {
                            gScore = 0;
                        } else {
                            gScore = point.prevPoint.gScore + 1;
                        }
                        int fScore = hScore + gScore;
                        // 현재 x, y좌표, gScore, fScore, 이전 노드
                        Point up = new Point(point.x - 1, point.y, gScore, fScore, point);
                        System.out.println("위쪽 좌표 : " + up.x + "," + up.y);
                        if (up.x >= 0 && up.x <= maze.getHeight() -1 && maze.getCell(up).getState() != Cell.State.WALL && !closeList[up.x][up.y])
                        {
                            openList.add(up);
                        }
                    }
                    else if (i==1) {
                        int hScore = Math.abs((point.x + 1) - maze.getEndPoint().x) + Math.abs(point.y - maze.getEndPoint().y);
                        int gScore;
                        if (point.prevPoint == null) {
                            gScore = 0;
                        } else {
                            gScore = point.prevPoint.gScore + 1;
                        }
                        int fScore = hScore + gScore;
                        Point down = new Point(point.x + 1, point.y, gScore, fScore, point);
                        System.out.println("아래쪽 좌표 : " + down.x + "," + down.y);
                        if (down.x >= 0 && down.x <= maze.getHeight() -1 && maze.getCell(down).getState() != Cell.State.WALL && !closeList[down.x][down.y] ) {
                            openList.add(down);
                        }
                    } else if (i==2) {
                        int hScore = Math.abs(point.x - maze.getEndPoint().x) + Math.abs(point.y - 1 - maze.getEndPoint().y);
                        int gScore;
                        if (point.prevPoint == null) {
                            gScore = 0;
                        } else {
                            gScore = point.prevPoint.gScore + 1;
                        }
                        int fScore = hScore + gScore;
                        Point left = new Point(point.x, point.y - 1, gScore, fScore, point);
                        System.out.println("왼쪽 좌표 : " + left.x + "," + left.y);
                        if (left.y >= 0 && left.y <= maze.getWidth() -1 && maze.getCell(left).getState() != Cell.State.WALL && !closeList[left.x][left.y]) {
                            openList.add(left);
                        }
                    } else {
                        int hScore = Math.abs(point.x- maze.getEndPoint().x) + Math.abs(point.y + 1 - maze.getEndPoint().y);
                        int gScore;
                        if (point.prevPoint == null) {
                            gScore = 0;
                        } else {
                            gScore = point.prevPoint.gScore + 1;
                        }
                        int fScore = hScore + gScore;
                        Point right = new Point(point.x, point.y + 1, gScore, fScore, point);
                        System.out.println("오른쪽 좌표 : " + right.x + "," + right.y);
                        if (right.y >= 0 && right.y <= maze.getWidth() -1 && maze.getCell(right).getState() != Cell.State.WALL && !closeList[right.x][right.y]) {
                            openList.add(right);
                        }
                    }
                }
                findCount += 1;
                prevPoint = point;
            } else {
                // 열린 목록에 아이템이 없으면 탐색 실패(길이 없음)
                System.out.println("no route");
            }
        }
    }

    static boolean isValidPos(int x, int y){

        if (x<0 || y<0 || x>=maze.getWidth() || y>=maze.getHeight())
            return false;
        else
            return maze.getCell(x, y).isAvailable() && !maze.getCell(x, y).isVisited();
        // 이미 지나간 자리도 추가 해야하나?
    }
    static boolean isValidPos(Point p){

        if (p.x<0 || p.y<0 || p.x>=maze.getHeight() || p.y>=maze.getWidth())
            return false;
        else
            return maze.getCell(p.x, p.y).isAvailable() && !maze.getCell(p.x, p.y).isVisited();
        // 이미 지나간 자리도 추가 해야하나?
    }
    static boolean isPointOverlap(Point mouse, List<Point> scanList){
        for (Point scanCenter : scanList) {
            for (int i = -2; i <= 2; i++) {
                for (int j = -2; j <= 2; j++) {
                    Point currentScanPoint = new Point(scanCenter.x + i, scanCenter.y + j);
                    // 현재 스캔 중인 포인트가 쥐의 시야 범위 내에 있는지 확인
                    if (Math.abs(currentScanPoint.x - mouse.x) <= 1 && Math.abs(currentScanPoint.y - mouse.y) <= 1) {
                        return true;  // 겹치는 부분 발견
                    }
                }
            }
        }
        return false;  // 겹치는 부분이 없음
    }

    static boolean isValidPosByWeight(Point p){

        if (p.x<0 || p.y<0 || p.x>=maze.getHeight() || p.y>=maze.getWidth())
            return false;
        else
            return maze.getCell(p.x, p.y).isAvailable();
        // 이미 지나간 자리도 추가 해야하나?
    }

    static int[][] readMaze(String path){
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(path));
        } catch (FileNotFoundException e) {
            System.out.println("파일을 여는 도중 오류 발생_1");
            throw new RuntimeException(e);
        }
        int row = 0;
        int column;
        String s = "";
        while(scanner.hasNextLine()) {
            s = scanner.nextLine();
            row += 1;
        }
        String[] split = s.split(" ");
        column = split.length;

        scanner.close();

        Scanner in = null;
        try {
            in = new Scanner(new File(path));
        } catch (FileNotFoundException e) {
            System.out.println("파일열 여는 도중 오류 발생_2");
            throw new RuntimeException(e);
        }
        int[][] maze = new int[row][column];
        int i=0;
        int j=0;
        int c;

        while(in.hasNextInt())
        {
            c = in.nextInt();

            if (j == column) {
                i += 1;
                j = 0;
            }
            if (j!=1 && i!=0) {
                if ((i==0 || i==row-1 || j==0 || j==column-1) && c==0) {
                    maze[i][j] = 2;
                } else maze[i][j] = c;
            } else maze[i][j] = c;
            j += 1;
        }

        return maze;
    }

    public static double calculateDistance(Point p) {
        Point exit = maze.getEndPoint();
        double deltaX = exit.x - p.x;
        double deltaY = exit.y - p.y;
        double distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
        return distance;
    }
}
