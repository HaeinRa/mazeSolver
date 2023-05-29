import sun.awt.image.ImageWatched;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class CeremonyAlgorithm {
    private static Maze maze;
    private static Maze mouseMap;
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
        maze = new Maze(readMaze("Maze1.txt"));
        mouseMap = new Maze(readMaze("Maze1.txt"));
        mouse = new Mouse(new Point(0,1), mouseMap.getHeight()*mouseMap.getWidth(), mouseMap);
        mouse.map.getCell(0,1).setState(Cell.State.VISIT);
        mouse.setMap();
        gui = new GUI(mouseMap, mouse);
        scanList = new ArrayList<>();

        // DFS 안간거 고쳐야함
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
        while(true){
            gui.repaint();
            TimeUnit.MILLISECONDS.sleep(1);
            branchCounter = 0; // 분기점 카운터 초기화
            System.out.println("stack: "+ stack);
            System.out.println("mouse: "+ mouse.getLocation());

            System.out.println("point003: Enter while loop");

            System.out.println("point003-1: gui repaint");

            if (mouse.getEnergy() > 0) {
                System.out.println("point004: Energy condition");

//                // 5x5 스캔하기 (모드에 따라)
                if(mouse.getMana() >= 3 && scanMode == 0){
                    System.out.println("point005: scanMode 0");
                    // map 업데이트
                    System.out.println("point005-1: scanning.. 5x5");
                    //Point scanPoint = new Point(maze.getHeight()- 5 * mouse.getScanCount() -1, maze.getWidth()-2-1);
                    Point scanPoint = new Point( 5 * mouse.getScanCount() - 1, maze.getWidth()-2-1);
                    isFindExit = mouse.map.update(scanPoint,5, maze, isFindExit);

                    mouse.scan();
                    System.out.println("scanPoint: " + scanPoint);
                    System.out.println("scanCount: " + mouse.getScanCount());

                    // 스캔 리스트에 추가
                    scanList.add(scanPoint);
                    System.out.println("point005-2: add Point to scanList");


                }
//                else if (scanMode == 1) {
//                    System.out.println("point006: scanMode 1");
//
//                    // something:1
//                    // map 업데이트
//                    // 스캔 리스트에 추가
//                }

                // 현재 시야 업데이트
                isFindExit = mouse.map.update(mouse.getLocation(),3, maze, isFindExit);
                gui.repaint();
                TimeUnit.MILLISECONDS.sleep(10);
                System.out.println("point007: Update sight");


                // 현재 위치를 확인한다.
                Point now = mouse.getLocation();


                // 현재 위치가 출구라면 Exit
                if (mouse.map.getCell(now.x, now.y).isExit()){
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

                        if(branchCounter>=2){ // 분기점이라면
                            System.out.println("point013: Branch Set");

                            buffer.push(new Point(-1, -1)); // 분기라는 것을 알린다
                            buffer.push(now);
                            maze.getCell(now).setState(Cell.State.BRANCH);
                            isFindExit = mouse.map.update(mouse.getLocation(),3, maze, isFindExit);

                        }
                        if(branchCounter==0){ // 현재는 갈 수 있는 곳이 없어서 이전 분기로 돌아가야한다면
                            System.out.println("point014: Can not go for now, Back to branch, Start buffer pop");
                            //Point prev = new Point();
                            maze.getCell(mouse.getLocation()).setState(Cell.State.NotRecommended); // 현재 위치 추천하지 않음
                            while(true){
                                System.out.println("point015: do Buffer pop");
                                Point back = buffer.pop(); // 돌아갈 좌표를 뽑는다
                                System.out.println("buffer: "+buffer);

                                if(back.x == -1 && back.y == -1) { // 분기점의 끝이라면
                                    System.out.println("point016: Branch arrived");
                                    stack.pop(); // 분기점 중복 제거?
                                    //buffer.push(prev);
                                    break;
                                }else{
                                    System.out.println("point017: Mouse moving");
                                    mouse.move();
                                    mouse.changeLocation(back);
                                    gui.repaint();
                                    TimeUnit.MILLISECONDS.sleep(1);
                                    maze.getCell(mouse.getLocation()).setState(Cell.State.NotRecommended); // 현재 위치 추천하지 않음
                                    //prev = back;
                                }
                                isFindExit = mouse.map.update(mouse.getLocation(),3, maze, isFindExit);
                            }
                        }
                        else{ // 현재 갈 수 있는 곳이 있다면
                            System.out.println("point018: Can go for now, Keep going");
                            now = stack.pop(); // 현재 위치를 결정
                            mouse.move(); // 쥐를 해당 위치로 움직인다
                            mouse.changeLocation(now); // 쥐의 위치를 바꾼다
                            gui.repaint();
                            TimeUnit.MILLISECONDS.sleep(1);
                            maze.getCell(now).setState(Cell.State.VISIT); // 해당 위치 VISIT state로 변경
                            buffer.push(now); // 버퍼에 집어넣는다
                        }
                    }


                }
                else{ // 출구를 알고 있다면
                    System.out.println("point019: Exit already found");
                    System.out.println(maze.getEndPoint());
                    
                    // 경로검사, a* 알고리즘을 통해 쥐가 알고있는 맵에서 출구까지 가는 길이 있는지 확인
                    AstarAlgorithm astarAlgorithm = new AstarAlgorithm(mouse.map, mouse.getLocation().x, mouse.getLocation().y,
                            maze.getEndPoint().x, maze.getEndPoint().y);
                    int[][] path = astarAlgorithm.run();
                    // 경로가 존재하면
                    if (path != null) {
                        System.out.println("경로가 존재합니다. 출구로 이동합니다");
                        System.out.println("출구 위치 : " + maze.getEndPoint().x + ", " + maze.getEndPoint().y);
                        System.out.println("경로의 마지막 위치 : " + path[path.length-1][0] + ", " + path[path.length-1][1]);
                        for (int i=0; i<path.length; i++) {
                            // 출구까지 곧바로 이동
                            mouse.move();
                            now = new Point(path[i][0], path[i][1]);
                            mouse.changeLocation(now);
                            mouse.map.getCell(now).setState(Cell.State.VISIT);
                            gui.repaint();
                            TimeUnit.MILLISECONDS.sleep(10);
                        }
                        // 이동 후 while문 종료
                        continue;
                    } else {
                        System.out.println("경로 없음, dfs로 진행");
                    }

                    // 경로검사1: 현재 시야와 스캔 리스트가 겹치는지 확인한다.
//                    if(isPointOverlap(mouse.getLocation(), scanList)){
//                        System.out.println("point021: Check overlap between sight and scanList ");
//
//                        // 경로검사2: A* 알고리즘을 사용하여 경로가 있는지 확인한다.
//                              //System.out.println("point022: Check path by a*");
//                            // 경로가 있다면 출구까지 간다.
//                    }
//        *   - 선택: 출구 거리 가중치 알고리즘을 사용한다
//        *       - 갈 곳이 있다면
//        *           - 출구로 부터 거리가 가장 작은 한 좌표만을 스택에 추가한다.
//        *           - 다음 위치 스택 pop
//        *           - 쥐를 해당 위치로 움직인다
//        *           - 해당 위치 VISITED state로 변경한다.
//        *           - 버퍼에 추가한다.

                    // 버퍼랑 리스트 다 비우기
                    if(scanMode != 1){
                        System.out.println("point029: clear stack and buffer");
                        stack.clear();
                        buffer.clear();
                        mouse.map.resetVisitedInfo();
                        maze.resetVisitedInfo();
                    }
                    gui.repaint();
                    TimeUnit.MILLISECONDS.sleep(10);

                    scanMode = 1; // 스캔모드를 바꾼다
                    System.out.println("point020: change scanMode to 1");

                    List<Point> points = new ArrayList<>();
                    // 네 가지 방향의 좌표

                    System.out.println("point030: check distance from exit");
                    // 리스트로 거리 먼저 계산하기
                    if(isValidPosByWeight(now.add(-1,0))){ // 상
                        Point up = new Point(now.x-1, now.y);
                        points.add(up);
                        System.out.println("up : " + up);
                        if(!maze.getCell(up).isBranch())
                            branchCounter ++;
                    }
                    if(isValidPosByWeight(now.add(0,-1))){ // 좌
                        Point left = new Point(now.x, now.y-1);
                        points.add(left);
                        System.out.println("left : " + left);
                        if(!maze.getCell(left).isBranch())
                            branchCounter ++;
                    }
                    if(isValidPosByWeight(now.add(0,1))){ // 우
                        Point right = new Point(now.x, now.y+1);
                        points.add(right);
                        System.out.println("right : " + right);
                        if(!maze.getCell(right).isBranch())
                            branchCounter ++;
                    }
                    if(isValidPosByWeight(now.add(1,0))){ // 하
                        Point down = new Point(now.x+1, now.y);
                        points.add(down);
                        System.out.println("down : " + down);
                        if(!maze.getCell(down).isBranch())
                            branchCounter ++;
                    }


//                    Stack<Point> stack = new Stack<>();
                    // 거리에 따라 우선순위를 두는 우선순위큐 distanceQueue를 생성한다.
                    PriorityQueue<Point> distanceQueue = new PriorityQueue<>(Comparator.comparingDouble(p -> -calculateDistance(p)));
                    // point 리스트에 있는 모든 포인트 객체를 distanceQueue에 추가
                    for(Point point : points){
                        distanceQueue.add(point);
                    }

                    // 우선순위 큐에 있는 포인트들은 모두 스택에 넣는다.
                    while(!distanceQueue.isEmpty()){
                        Point point = distanceQueue.poll();
                        stack.push(point);
                    }

                    System.out.println(stack);
                    if(stack.isEmpty()){ // 스택이 완전히 비어있음(더 이상 갈 수 있는 곳이 없음)
                        System.out.println("point031: Stack is totally empty, Done");
                        System.out.println("Fail");
                        return;
                    }


                    else{ // 스택이 비어있지 않음(갈 수 있는 곳이 있음)
                        System.out.println("point032: Stack is not empty");

                        if(branchCounter>=2){ // 분기점이라면
                            System.out.println("point033: Branch Set");

                            buffer.push(new Point(-1, -1)); // 분기라는 것을 알린다
                            buffer.push(now);
                            maze.getCell(now).setState(Cell.State.BRANCH);
                            isFindExit = mouse.map.update(mouse.getLocation(),3, maze, isFindExit);

                        }
                        if(branchCounter==0){ // 현재는 갈 수 있는 곳이 없어서 이전 분기로 돌아가야한다면
                            System.out.println("point034: Can not go for now, Back to branch, Start buffer pop");
                            //Point prev = new Point();
                            maze.getCell(mouse.getLocation()).setState(Cell.State.NotRecommended); // 현재 위치 추천하지 않음
                            while(true){
                                System.out.println("point035: do Buffer pop");
                                Point back = buffer.pop(); // 돌아갈 좌표를 뽑는다
                                System.out.println("buffer: "+buffer);
                                if(back == null)
                                    break;
                                if(back.x == -1 && back.y == -1) { // 분기점의 끝이라면
                                    System.out.println("point036: Branch arrived");
                                    //stack.pop(); // 분기점 중복 제거?
                                    //buffer.push(prev);
                                    break;
                                }else{
                                    System.out.println("point037: Mouse moving");
                                    mouse.move();
                                    mouse.changeLocation(back);
                                    gui.repaint();
                                    TimeUnit.MILLISECONDS.sleep(100);
                                    maze.getCell(mouse.getLocation()).setState(Cell.State.NotRecommended); // 현재 위치 추천하지 않음
                                    //prev = back;
                                }
                                isFindExit = mouse.map.update(mouse.getLocation(),3, maze, isFindExit);


                            }
                        }
                        else{ // 현재 갈 수 있는 곳이 있다면
                            System.out.println("point038: Can go for now, Keep going");
                            now = stack.pop(); // 현재 위치를 결정
                            mouse.move(); // 쥐를 해당 위치로 움직인다
                            mouse.changeLocation(now); // 쥐의 위치를 바꾼다
                            gui.repaint();
                            TimeUnit.MILLISECONDS.sleep(100);
                            maze.getCell(now).setState(Cell.State.VISIT); // 해당 위치 VISIT state로 변경
                            buffer.push(now); // 버퍼에 집어넣는다
                        }
                    }
                    gui.repaint();
                    TimeUnit.MILLISECONDS.sleep(1);
                }

            } else { // 체력이 남아있지 않다면
                System.out.println("point99: No more energy, Done");
                System.out.println("Fail: 체력 없음");
                return;
            }
        }

    }

    static boolean isValidPos(int x, int y){

        if (x<0 || y<0 || x>=maze.getHeight() || y>=maze.getWidth())
            return false;
        else
            return mouse.map.getCell(x, y).isAvailable() && !mouse.map.getCell(x, y).isVisited();
        // 이미 지나간 자리도 추가 해야하나?
    }
    static boolean isValidPos(Point p){

        if (p.x<0 || p.y<0 || p.x>=maze.getHeight() || p.y>=maze.getWidth())
            return false;
        else
            return mouse.map.getCell(p.x, p.y).isAvailable() && !mouse.map.getCell(p.x, p.y).isVisited();
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
            return mouse.map.getCell(p.x, p.y).isAvailable() && !mouse.map.getCell(p.x, p.y).isVisited();
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
            System.out.println("파일을 여는 도중 오류 발생_2");
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
