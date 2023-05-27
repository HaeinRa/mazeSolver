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
        gui = new GUI(maze,mouse);

        // 열린 목록, fScore가 큰 것을 출력 
        PriorityQueue<Point> openList = new PriorityQueue<>((a, b) -> (int)(a.fScore - b.fScore));
        //Set<Point> closedList = new HashSet<>(); // 닫힌 목록
        boolean[][] closeList = new boolean[maze.getWidth()][maze.getHeight()];
        for (int i=0; i< maze.getWidth(); i++) {
            for (int j=0; j<maze.getHeight(); j++) {
                closeList[i][j] = false;
            }
        }
        int openListCount = 0;

        // SetUp: GUI 띄우기 (미로, 쥐)
        gui.repaint();
        TimeUnit.SECONDS.sleep(1);

        System.out.println("point001: setup done");


        isFindExit = false;
        scanMode = 0;
        branchCounter = 0; // 분기점인지 체크하는 카운터

        // Run 단계 시작 (반복시켜야함)
        System.out.println("point002: Run state start");

        // 현재 쥐의 상태를 확인 (체력과 마나)
        // 체력이 남아 있다면
        while(true)
        {
            branchCounter = 0; // 분기점 카운터 초기화
            System.out.println("stack: "+ stack);
            System.out.println("mouse: "+ mouse.getLocation());
            TimeUnit.MILLISECONDS.sleep(20);
            System.out.println("point003: Enter while loop");
            gui.repaint();
            System.out.println("point003-1: gui repaint");

            if (mouse.getEnergy() > 0) {
                System.out.println("point004: Energy condition");

                // 5x5 스캔하기 (모드에 따라)
//                if(scanMode == 0){
//                    System.out.println("point005: scanMode 0");
//
//                    // map 업데이트
//                    // 스캔 리스트에 추가
//                    Point scanPoint = new Point(maze.getHeight()-4, maze.getWidth()-4);
//                    isFindExit = mouse.map.update(scanPoint,5, maze, isFindExit);
//                    scanList.add(scanPoint);
//                }
//                else if (scanMode == 1) {
//                    System.out.println("point006: scanMode 1");
//
//                    // something:1
//                    // map 업데이트
//                    // 스캔 리스트에 추가
//                }

                // 현재 시야 업데이트
                isFindExit = mouse.map.update(mouse.getLocation(),3,maze, isFindExit);
                System.out.println("point007: Update sight");

                // 현재 위치를 확인한다.
                Point now = mouse.getLocation();


                // 현재 위치가 출구라면 Exit
                if (maze.getCell(now.x, now.y).isExit()){
                    System.out.println("point008: Exit state, Done");
                    System.out.println("Exit");
                    return;
                }
                // else
                if (!isFindExit){ // 출구를 모르고 있다면
                    System.out.println("point009: no Exit info");

                    // 갈 곳이 있나 체크
                    if(isValidPos(now.add(-1,0))){
                        stack.push(now.add(-1,0));
                        branchCounter ++;
                    }
                    if(isValidPos(now.add(0,-1))){
                        stack.push(now.add(0,-1));
                        branchCounter ++;
                    }
                    if(isValidPos(now.add(0,1))){
                        stack.push(now.add(0,1));
                        branchCounter ++;
                    }
                    if(isValidPos(now.add(1,0))){
                        stack.push(now.add(1,0));
                        branchCounter ++;
                    }
                    System.out.println("point010: isValidPos Done ");


                    if(stack.isEmpty()){ // 스택이 완전히 비어있음(더 이상 갈 수 있는 곳이 없음)
                        System.out.println("point011: Stack is totally empty, Done");
                        System.out.println("Fail");
                        return;
                    }


                    else{ // 스택이 비어있지 않음(갈 수 있는 곳이 있음)
                        System.out.println("point012: Stack is not empty");

                        if(branchCounter>0){ // 분기점이라면
                            System.out.println("point013: Branch Set");
                            buffer.push(new Point(-1, -1)); // 분기라는 것을 알린다
                        }
                        if(branchCounter==0){ // 현재는 갈 수 있는 곳이 없어서 이전 분기로 돌아가야한다면
                            System.out.println("point014: Can not go for now, Back to branch, Start buffer pop");
                            Point prev = new Point();
                            mouse.map.getCell(mouse.getLocation()).setState(Cell.State.NotRecommended); // 현재 위치 추천하지 않음
                            while(true){
                                System.out.println("point015: do Buffer pop");
                                Point back = buffer.pop(); // 돌아갈 좌표를 뽑는다
                                System.out.println("buffer: "+buffer);

                                if(back.x == -1 && back.y == -1) { // 분기점의 끝이라면
                                    System.out.println("point016: Branch arrived");
                                    buffer.push(prev);
                                    break;
                                }else{
                                    System.out.println("point017: Mouse moving");
                                    mouse.move();
                                    mouse.changeLocation(back);
                                    gui.repaint();
                                    TimeUnit.MILLISECONDS.sleep(10);
                                    mouse.map.getCell(mouse.getLocation()).setState(Cell.State.NotRecommended); // 현재 위치 추천하지 않음
                                    prev = back;
                                }

                            }
                        }
                        else{ // 현재 갈 수 있는 곳이 있다면
                            System.out.println("point018: Can go for now, Keep going");
                            now = stack.pop(); // 현재 위치를 결정
                            mouse.move(); // 쥐를 해당 위치로 움직인다
                            mouse.changeLocation(now); // 쥐의 위치를 바꾼다
                            mouse.map.getCell(now).setState(Cell.State.VISIT); // 해당 위치 VISIT state로 변경
                            buffer.push(now); // 버퍼에 집어넣는다
                        }
                    }
                }
                else{ // 출구를 알고 있다면
                    System.out.println("point019: Exit already found");
                    System.out.println(mouse.map.getEndPoint());
                    scanMode = 1; // 스캔모드를 바꾼다
                    System.out.println("point020: change scanMode to 1");
                    // 시야 업데이트 시 출구를 찾으면 a* 알고리즘 준비
                    if (mouse.map.getEndPoint() != null && openListCount == 0) {
                        // 지나온 길(탐색한 길)을 닫힌 목록에 추가
                        for (int i=0; i<maze.getWidth(); i++) {
                            for (int j=0; j<maze.getHeight(); j++) {
                                if (maze.getCell(i,j).getState() == Cell.State.NotRecommended) {
                                    //closedList.add(new Point(i, j));
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


/*                    // 경로검사1: 현재 시야와 스캔 리스트가 겹치는지 확인한다.
                    if(isPointOverlap(mouse.getLocation(), scanList))
                    {
                        System.out.println("point021: Check overlap between sight and scanList ");
                        // 경로검사2: A* 알고리즘을 사용하여 경로가 있는지 확인한다.
                        AstarAlgorithm aStar = new AstarAlgorithm(maze, mouse.getLocation().x, mouse.getLocation().y, maze.getEndPoint().x, maze.getEndPoint().y);
                        int[][] path = aStar.run();

                        // 경로가 있다면 출구까지 간다.
                        if (path == null)
                        {
                            System.out.println("no route");
                        }
                        else {
                            for (int i=0;i<path.length; i++) {
                                mouse.move();
                                Point point = new Point(path[i][0], path[i][1]);
                                System.out.println(point.x + ", " + point.y);
                                mouse.changeLocation(point);
                            }
                        }
                    }*/
                    //else
                    {
                        // a* 알고리즘 시작
                        // 열린 목록에서 아이템을 꺼냄, 가중치가 가장 낮은(fScore가 가장 작은) 아이템이 poll됨
                        if (!openList.isEmpty()) {
                            Point point = openList.poll();
                            System.out.println("열린 목록에서 poll함, 열린 목록 크기: " + openList.size());
                            System.out.println("현재 위치 : " + point.x + "," + point.y);
                            // 꺼낸 아이템는 닫힌 목록에 추가(다시 탐색하지 않음)
                            //closedList.add(point);
                            closeList[point.x][point.y] = true;
                            // 꺼낸 위치로 이동
                            mouse.move();
                            mouse.changeLocation(point);
                            // 열린 목록 주위(상하좌우) 중 닫힌 목록에 없는 아이템은 벽을 제외하고 열린 목록에 추가
                            for (int i=0; i<4; i++) {
                                if (i==0) {
                                    // 가중치가 계산된 Point를 집어넣음
                                    // 가중치는 g와 f가 있음
                                    int hScore = Math.abs(point.x - 1 - maze.getEndPoint().x) + Math.abs(point.y - maze.getEndPoint().x);
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
                                    if (up.x > 0 || up.x < maze.getWidth() -1 || maze.getCell(up).getState() != Cell.State.WALL || !closeList[up.x][up.y])
                                    {
                                        openList.add(up);
                                    }
                                }
                                else if (i==1) {
                                    int hScore = Math.abs(point.x + 1 - maze.getEndPoint().x) + Math.abs(point.y - maze.getEndPoint().x);
                                    int gScore;
                                    if (point.prevPoint == null) {
                                        gScore = 0;
                                    } else {
                                        gScore = point.prevPoint.gScore + 1;
                                    }
                                    int fScore = hScore + gScore;
                                    Point down = new Point(point.x + 1, point.y, gScore, fScore, point);
                                    System.out.println("아래쪽 좌표 : " + down.x + "," + down.y);
                                    if (down.x > 0 || down.x < maze.getWidth() -1 || maze.getCell(down).getState() != Cell.State.WALL || !closeList[down.x][down.y] ) {
                                        System.out.println(maze.getCell(down).getState());
                                        openList.add(down);
                                        System.out.println(openList.size());
                                    }
                                } else if (i==2) {
                                    int hScore = Math.abs(point.x - maze.getEndPoint().x) + Math.abs(point.y - 1 - maze.getEndPoint().x);
                                    int gScore;
                                    if (point.prevPoint == null) {
                                        gScore = 0;
                                    } else {
                                        gScore = point.prevPoint.gScore + 1;
                                    }
                                    int fScore = hScore + gScore;
                                    Point left = new Point(point.x, point.y - 1, gScore, fScore, point);
                                    System.out.println("왼쪽 좌표 : " + left.x + "," + left.y);
                                    if (left.y > 0 || left.y < maze.getHeight() -1 || maze.getCell(left).getState() != Cell.State.WALL || !closeList[left.x][left.y]) {
                                        openList.add(left);
                                    }
                                } else {
                                    int hScore = Math.abs(point.x- maze.getEndPoint().x) + Math.abs(point.y + 1 - maze.getEndPoint().x);
                                    int gScore;
                                    if (point.prevPoint == null) {
                                        gScore = 0;
                                    } else {
                                        gScore = point.prevPoint.gScore + 1;
                                    }
                                    int fScore = hScore + gScore;
                                    Point right = new Point(point.x, point.y + 1, gScore, fScore, point);
                                    System.out.println("오른쪽 좌표 : " + right.x + "," + right.y);
                                    if (right.y > 0 || right.y < maze.getHeight() -1 || maze.getCell(right).getState() != Cell.State.WALL || !closeList[right.x][right.y]) {
                                        openList.add(right);
                                    }
                                }
                            }
                        } else {
                            // 열린 목록에 아이템이 없으면 탐색 실패(길이 없음)
                            System.out.println("no route");
                        }
                    }
                }
            } else { // 체력이 남아있지 않다면
                System.out.println("Fail: 체력 없음");
                return;
            }
        }

    }

    static boolean isValidPos(int x, int y){

        if (x<0 || y<0 || x>=maze.getWidth()-1 || y>=maze.getHeight()-1)
            return false;
        else
            return maze.getCell(x, y).isAvailable() && !maze.getCell(x, y).isVisited();
        // 이미 지나간 자리도 추가 해야하나?
    }
    static boolean isValidPos(Point p){

        if (p.x<0 || p.y<0 || p.x>=maze.getWidth()-1 || p.y>=maze.getHeight()-1)
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
}
