import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class CeremonyAlgorithm {
    private static Maze maze, ra, view, mouseMap;
    private static GUI gui;
    private static List<Point> scanList;
    private static List<Double> compareList;
//    private static Stack<Point> stack = new Stack<>();
    private static Mouse mouse;
    private static LinkedStack<Point> stack, buffer;
    private static int scanMode;
    private static boolean isFindExit;
    private static int branchCounter;
    private static Point scanPoint;

    static PriorityQueue<Point> scanDistanceQueue;

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
        String filename = "Maze1.txt";
        maze = new Maze(readMaze(filename)); // 처음 그대로의 원본 미로 + 쥐로 인해 변경된 정보
        mouseMap = new Maze(readMaze(filename)); // 쥐의 시야, maze에 영향을 받음
        view = new Maze(readMaze(filename)); // 처음 그대로의 원본 미로 + 쥐가 간 길만 표시 (visit)
        mouse = new Mouse(new Point(0,1), mouseMap.getHeight()*mouseMap.getWidth(), mouseMap);

        maze.getCell(0,1).setState(Cell.State.VISIT);
        view.getCell(0,1).setState(Cell.State.VISIT);

        mouse.setMap();
        scanList = new ArrayList<>();
        gui = new GUI(maze, mouse, scanList);


        // 다 벽인 미로 - 스캔한 부분 DFS 사용하려고 만듦
        ra = new Maze(readMaze("Maze1.txt"));
        for (int i=0; i<ra.getHeight(); i++) {
            for (int j=0; j<ra.getWidth(); j++) {
                ra.getCell(i, j).setState(Cell.State.NotRecommended);
            }
        }


        // SetUp: GUI 띄우기 (미로, 쥐)
        gui.repaint();
        TimeUnit.SECONDS.sleep(1);

        System.out.println("point001: setup done");


        isFindExit = false;
        scanMode = 0;
        branchCounter = 0; // 분기점인지 체크하는 카운터
        boolean isInitBranch = false;

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
                    scanPoint = new Point(maze.getHeight()- 5 * mouse.getScanCount() -2 -1, maze.getWidth()-2-1);
                    isFindExit = mouse.map.update(scanPoint,5, maze, isFindExit);

                    mouse.scan();
                    System.out.println("scanPoint: " + scanPoint);
                    System.out.println("scanCount: " + mouse.getScanCount());

                    // 스캔 리스트에 추가
                    scanList.add(scanPoint);
                    System.out.println("point005-2: add Point to scanList");


                }
                else if (scanMode == 1) { // 출구 찾은 후
                    System.out.println("point006: scanMode 1");

                    // map 업데이트
                    System.out.println("point005-1: scanning.. 5x5");
                    isFindExit = mouse.map.update(scanPoint,5, maze, isFindExit);

                    // ra 업데이트 : 방 조명이 켜짐
                    ra.update(scanPoint, 5, maze, isFindExit);

                    // mouse 정보 갱신
                    mouse.scan();
                    System.out.println("scanPoint: " + scanPoint);
                    System.out.println("scanCount: " + mouse.getScanCount());

                    // 출구와 연결된 곳을 유망한 방면이라 하고, 그 방면들의 중점을 모아놓는 후보 리스트 : candidiateScan 리스트
                    // DFS를 이용하여 어디가 뚫려있는지 알아내기
                    // 출구를 입구로 가정하고 DFS 실행,
                    List<Point> candidateScan = scanDFSAlgorithm(maze.getEndPoint());


                    // 뚫린 면의 중점 검사하기 : 후보 중점이 available 한가? 위의 방법에서 remove할 때 인덱스 오류 발생할 수도 있을 것 같아 뒤에서부터 앞으로 순회하여 삭제
                    for (int i = candidateScan.size() - 1; i >= 0; i--) {
                        Point point = candidateScan.get(i);
                        if (point.x > maze.getHeight() -1 || point.y > maze.getWidth() -1 || point.x < 0 || point.y < 0) {
                            candidateScan.remove(i);// available하지 않으면 현재 인덱스에 해당하는 요소 삭제 -> candidate에 available한 중점들만 남음
                        }
                    }

                    // 후보 중점의 요소들이 scanList에 있는지 검사하고 없다면 scanList에 추가하기
                    for (int i = 0; i<candidateScan.size(); i++){
                        Point point = candidateScan.get(i);
                        for(int j = 0; j<scanList.size(); j++){
                            if(point != scanList.get(j)){
                                scanList.add(point);
                            }
                        }
                    }

                    // 거리에 따라 우선순위를 두는 우선순위큐 distanceQueue를 생성한다.
                    scanDistanceQueue = new PriorityQueue<>(Comparator.comparingDouble(p -> calculateDistance(p))); // 거리가 짧을 수록 우선순위 높음 -> 다시 스택에 넣지 않을 거임
                    // 이거 클래스 변수로 바꾸기


                    if(scanDistanceQueue == null){
                        System.out.println("null");
                    }
                    // scanList 리스트에 있는 모든 포인트 객체를 distanceQueue에 추가
                    for(Point point : scanList){
                        scanDistanceQueue.add(point);
                    }

                    // 우선순위 큐에서 우선순위가 가장 큰 녀석을 스캔 포인트로 지정
                    scanPoint = scanDistanceQueue.poll();

                    // something:1
                    // map 업데이트
                    // 스캔 리스트에 추가
                }

                // 현재 시야 업데이트
                isFindExit = mouse.map.update(mouse.getLocation(),3, maze, isFindExit);
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
                            if(!isInitBranch){ // 출구부터 최조 분기점 이전의 경로를 추천하지 않는다.
                                while(!buffer.isEmpty()){
                                    Point tmp = buffer.pop();
                                    maze.getCell(tmp).setState(Cell.State.NotRecommended);
                                    mouseMap.getCell(tmp).setState(Cell.State.NotRecommended);
                                }
                                isInitBranch = true;
                            }
                            buffer.push(new Point(-1, -1)); // 분기라는 것을 알린다
                            buffer.push(now);
                            maze.getCell(now).setState(Cell.State.BRANCH);
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
                            view.getCell(now).setState(Cell.State.VISIT);

                            buffer.push(now); // 버퍼에 집어넣는다
                        }
                    }


                }
                else{ // 출구를 알고 있다면
                    System.out.println("point019: Exit already found");
                    System.out.println(mouse.map.getEndPoint());
                    scanMode = 1; // 스캔모드를 바꾼다
                    System.out.println("point020: change scanMode to 1");

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
                    TimeUnit.MILLISECONDS.sleep(1);


                    List<Point> points = new ArrayList<>();
                    // 네 가지 방향의 좌표

                    System.out.println("point030: check distance from exit");
                    // 리스트로 거리 먼저 계산하기
                    if(isValidPosByWeight(now.add(-1,0))){ // 상
                        Point up = new Point(now.x-1, now.y);
                        points.add(up);
                        System.out.println("up : " + up);
                        branchCounter ++;
                    }
                    if(isValidPosByWeight(now.add(0,-1))){ // 좌
                        Point left = new Point(now.x, now.y-1);
                        points.add(left);
                        System.out.println("left : " + left);
                        branchCounter ++;
                    }
                    if(isValidPosByWeight(now.add(0,1))){ // 우
                        Point right = new Point(now.x, now.y+1);
                        points.add(right);
                        System.out.println("right : " + right);
                        branchCounter ++;
                    }
                    if(isValidPosByWeight(now.add(1,0))){ // 하
                        Point down = new Point(now.x+1, now.y);
                        points.add(down);
                        System.out.println("down : " + down);
                        branchCounter ++;
                    }


//                    Stack<Point> stack = new Stack<>();
                    // 거리에 따라 우선순위를 두는 우선순위큐 distanceQueue를 생성한다.
                    PriorityQueue<Point> distanceQueue = new PriorityQueue<>(Comparator.comparingDouble(p -> -calculateDistance(p))); // 거리가 멀수록 우선순위 높음 -> 다시 스택에 넣을 거라서 그럼

                    if(distanceQueue == null){
                        System.out.println("null");
                    }
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

                        if(branchCounter>0){ // 분기점이라면
                            System.out.println("point033: Branch Set");

                            buffer.push(new Point(-1, -1)); // 분기라는 것을 알린다
                            buffer.push(now);
                            maze.getCell(now).setState(Cell.State.BRANCH);
                            isFindExit = mouse.map.update(mouse.getLocation(),3, maze, isFindExit);

                        }
                        if(branchCounter==0){ // 현재는 갈 수 있는 곳이 없어서 이전 분기로 돌아가야한다면
                            System.out.println("point034: Can not go for now, Back to branch, Start buffer pop");
                            Point prev = new Point();
                            mouse.map.getCell(mouse.getLocation()).setState(Cell.State.NotRecommended); // 현재 위치 추천하지 않음
                            while(true){
                                System.out.println("point035: do Buffer pop");
                                Point back = buffer.pop(); // 돌아갈 좌표를 뽑는다
                                System.out.println("buffer: "+buffer);

                                if(back.x == -1 && back.y == -1) { // 분기점의 끝이라면
                                    System.out.println("point036: Branch arrived");
                                    buffer.push(prev);
                                    break;
                                }else{
                                    System.out.println("point037: Mouse moving");
                                    mouse.move();
                                    mouse.changeLocation(back);
                                    gui.repaint();
                                    TimeUnit.MILLISECONDS.sleep(10);
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
                            view.getCell(now).setState(Cell.State.VISIT);
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

    // DFS 모드
    static List<Point> scanDFSAlgorithm(Point exit){
        LinkedStack<Point> scanStack = new LinkedStack<>();
        List<Point> candidateScanPoint = new ArrayList<>();
        scanStack.push(exit); // 시작 지점 지정

        while(!scanStack.isEmpty()){
            System.out.println("현재 스택: " + scanStack);
            // 뽑을 때 현재 위치가 5*5에 걸쳐 있으면
            Point now = scanStack.pop();
            // 위쪽 벽으로 막혀서 dfs 진행을 못하면 위쪽 5*5의 중심점을 candidateScanPoint에 추가
            if (ra.cells[now.x - 1][now.y].getState() == Cell.State.NotRecommended) {
                Point up = new Point(scanPoint.x - 5, scanPoint.y);
                candidateScanPoint.add(up);
            }
            // 아래쪽 벽으로 막혀서 dfs 진행 x
            else if (ra.cells[now.x + 1][now.y].getState() == Cell.State.NotRecommended) {
                Point down = new Point(scanPoint.x + 5, scanPoint.y);
                candidateScanPoint.add(down);
            }
            // 왼쪽
            else if (ra.cells[now.x][now.y - 1].getState() == Cell.State.NotRecommended) {
                Point left = new Point(scanPoint.x, scanPoint.y - 5);
                candidateScanPoint.add(left);
            }
            // 오른쪽은 필요한 경우에만 추가
            else {
                if (now.x < ra.getHeight() && now.y < ra.getWidth() + 5) {
                    Point right = new Point(scanPoint.x, scanPoint.y + 5);
                    candidateScanPoint.add(right);
                }
            }
            System.out.println(now);
            if (maze.getCell(now.x, now.y).isExit()) {
                System.out.println("Exit");
                return candidateScanPoint;
            }
            else {
                maze.getCell(now.x, now.y).setState(Cell.State.VISIT);
                if(isValidPos(now.x+1, now.y)){
                    scanStack.push(new Point(now.x+1, now.y));
                }
                if(isValidPos(now.x-1, now.y)){
                    scanStack.push(new Point(now.x-1, now.y));
                }
                if(isValidPos(now.x, now.y+1)){
                    scanStack.push(new Point(now.x+1, now.y+1));
                }
                if(isValidPos(now.x, now.y-1)){
                    scanStack.push(new Point(now.x, now.y-1));
                }
            }
        }
        return candidateScanPoint;
    }
}
