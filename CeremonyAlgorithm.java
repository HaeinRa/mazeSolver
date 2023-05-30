import sun.awt.image.ImageWatched;

import javax.management.monitor.MonitorSettingException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class CeremonyAlgorithm {
    private static Maze maze, mouseMap, view, scanMap, ra;
    private static GUI gui;
    private static List<Point> scanList;
    private static List<Double> compareList;
    private static Point scanPoint;
    private static Mouse mouse;
    private static LinkedStack<Point> stack, buffer;
    private static int scanMode;
    private static boolean isFindExit;
    private static boolean isWallBreaker;
    private static int branchCounter;
    private static List<Point> candidateScan;
    static long bufferTime,stackTime,setTime,scanDFStime;

    static PriorityQueue<Point> scanDistanceQueue;


    public static void main(String[] args) throws InterruptedException {

        // JVM 옵션 설정
        String codeCacheSizeOption = "-XX:ReservedCodeCacheSize=256m";

        // JVM에 옵션 적용
        System.setProperty("java.vm.options", codeCacheSizeOption);

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
        scanMap = new Maze(readMaze(filename)); // 스캔한 포인트 위치만 나타내는 맵
        ra = new Maze(readMaze(filename)); // 스캔한 곳의 미로 정보를 나타내는 맵
        mouse = new Mouse(new Point(0, 1), mouseMap.getHeight() * mouseMap.getWidth() * 2, mouseMap);


        int mode = 0;
        int widthCount = 0;
        int heightCount = 0;
        int widthCount2 = 0;

        mouse.setMap();
        gui = new GUI(maze, mouse, scanMap);
        bufferTime = 1;
        stackTime = 1;
        setTime = 1;
        scanList = new ArrayList<>();
        scanDistanceQueue = new PriorityQueue<>(Comparator.comparingDouble(p -> calculateDistance(p))); // 거리가 짧을 수록 우선순위 높음 -> 다시 스택에 넣지 않을 거임


        // 다 벽인 미로 - 스캔한 부분 DFS 사용하려고 만듦

        for (int i = 0; i < ra.getHeight(); i++) {
            for (int j = 0; j < ra.getWidth(); j++) {
                ra.getCell(i, j).setState(Cell.State.NotRecommended);
            }
        }


        // Todo: 스캔모드0 완전제공

        // 스캔 모드
        // SetUp: GUI 띄우기 (미로, 쥐)
        gui.repaint();
        TimeUnit.SECONDS.sleep(3);

        System.out.println("point001: setup done");

        // 스캔 맵 초기화
        for (int i = 0; i < scanMap.getHeight(); i++) {
            for (int j = 0; j < scanMap.getWidth(); j++) {
                scanMap.getCell(i, j).setState(Cell.State.AVAILABLE);
            }
        }

        isFindExit = false;
        isWallBreaker = false;
        scanMode = 0;
        branchCounter = 0; // 분기점인지 체크하는 카운터
        boolean isInitBranch = false;

        // Run 단계 시작 (반복시켜야함)
        System.out.println("point002: Run state start");

        // 현재 쥐의 상태를 확인 (체력과 마나)
        // 체력이 남아 있다면
        while (true) {
            gui.repaint();
            TimeUnit.MILLISECONDS.sleep(setTime);
            branchCounter = 0; // 분기점 카운터 초기화
            System.out.println("stack: " + stack);
            System.out.println("mouse: " + mouse.getLocation());

            System.out.println("point003: Enter while loop");

            System.out.println("point003-1: gui repaint");

            if (mouse.getEnergy() > 0) {
                System.out.println("point004: Energy condition");

                //TODO: 스캔모드 1 일 때, 출구로부터 dfs 할 수 없으면 스캔을 안함
//                // 5x5 스캔하기 (모드에 따라)
                if (mouse.getMana() >= 3 && scanMode == 0) {
                    System.out.println("point005: scanMode 0");
                    // map 업데이트
                    System.out.println("point005-1: scanning.. 5x5");
                    if (maze.getHeight() - 5 * mouse.getScanCount() > -3) { // 높이 변화, x고정
                        scanPoint = new Point(maze.getHeight()- 5 * mouse.getScanCount()-3, maze.getWidth()-3);
                        if (scanPoint.x < 0) {
                            scanPoint = new Point(2, maze.getWidth()-2-1);
                        }
                    } else if (maze.getHeight() - 5 * mouse.getScanCount()  < -3 && maze.getWidth() - 5 * widthCount > -3) { // 너비 변화, y고정
                        scanPoint = new Point(maze.getHeight() - 3, maze.getWidth() - 5 * widthCount - 7 - 1);
                        if (scanPoint.y < 0) {
                            scanPoint = new Point(maze.getHeight() - 2, maze.getWidth() - 5 * widthCount);
                        }
                        widthCount += 1;
                    } else if (maze.getHeight() - 5 * heightCount > -3) { // 높이 변화, x고정
                        scanPoint = new Point(maze.getHeight() - 5 * heightCount - 7 - 1 , 2);
                        if (scanPoint.x < 0) {
                            scanPoint = new Point(2,2);
                        }
                        heightCount += 1;
                    } else if (maze.getWidth() - 5 * widthCount2 > -3) {
                        scanPoint = new Point(2, maze.getWidth() - 5 * widthCount2 - 7 - 1);
                        if (scanPoint.y < 0) {
                            scanPoint = null;
                        }
                        widthCount2 += 1;
                    }

                    // 스캔 리스트에 추가
                    if (scanPoint != null) {
                        isFindExit = mouse.map.update(scanPoint, 5, maze, scanMap, isFindExit);
                        mouse.scan();
                        System.out.println("scanPoint: " + scanPoint);
                        System.out.println("scanCount: " + mouse.getScanCount());
                        scanList.add(scanPoint);
                        System.out.println("point005-2: add Point to scanList");
                        System.out.println(mouse.getScanCount() + ", " + scanList.size());
                    }

                } else if (mouse.getMana() >= 3 && scanMode == 1) { // 출구 찾은 후
                    System.out.println("point006: scanMode 1");
                    scanDistanceQueue.clear();

                    // map 업데이트
                    System.out.println("point005-1: scanning.. 5x5");
                    isFindExit = mouse.map.update(scanPoint, 5, maze, isFindExit);

                    // ra 업데이트 : 방 조명이 켜짐
                    ra.update(scanPoint, 5, maze, isFindExit);
                    gui.repaint();
                    TimeUnit.MILLISECONDS.sleep(setTime);


                    // 출구와 연결된 곳을 유망한 방면이라 하고, 그 방면들의 중점을 모아놓는 후보 리스트 : candidiateScan 리스트
                    // DFS를 이용하여 어디가 뚫려있는지 알아내기
                    // 출구를 입구로 가정하고 DFS 실행,
                    candidateScan = scanDFSAlgorithm(maze.getEndPoint());
                    if (candidateScan.isEmpty()) {
                        System.out.println("point005-3: 후보군 존재하지 않음");
                        scanPoint = scanWithoutDFSAlgoritm();
                    } else {
                        // 뚫린 면의 중점 검사하기 : 후보 중점이 available 한가? 위의 방법에서 remove할 때 인덱스 오류 발생할 수도 있을 것 같아 뒤에서부터 앞으로 순회하여 삭제
                        for (int i = candidateScan.size() - 1; i >= 0; i--) {
                            Point point = candidateScan.get(i);
                            if (point.x > maze.getHeight() - 1 || point.y > maze.getWidth() - 1 || point.x < 0 || point.y < 0) {
                                candidateScan.remove(i);// available하지 않으면 현재 인덱스에 해당하는 요소 삭제 -> candidate에 available한 중점들만 남음
                            }
                        }


                        // 후보 중점의 요소들이 scanList에 있는지 검사하고 없다면 scanList에 추가하기
                        for (int i = 0; i < candidateScan.size(); i++) {
                            boolean isRemoved = false;
                            Point point = candidateScan.get(i);
                            for (int j = 0; j < scanList.size(); j++) {
                                // scanList에 있는 (중복된) Point는 후보에서 제거
                                if (point.x == scanList.get(j).x && point.y == scanList.get(j).y) {
                                    //System.out.println("채승윤님");
                                    candidateScan.remove(i);
                                    isRemoved = true;
                                }
                            }
                            if (isRemoved) {
                                i--;
                            }
                        }

                        // 거리에 따라 우선순위를 두는 우선순위큐 distanceQueue를 생성한다.

                        // 이거 클래스 변수로 바꾸기


                        if (scanDistanceQueue == null) {
                            System.out.println("null");
                        }
                        // scanList 리스트에 있는 모든 포인트 객체를 distanceQueue에 추가
                        for (int i = 0; i < candidateScan.size(); i++) {
                            //System.out.println("라해인님님");
                            scanDistanceQueue.add(candidateScan.get(i));
                        }

                        // 우선순위 큐에서 우선순위가 가장 큰 녀석을 스캔 포인트로 지정
                        if (scanDistanceQueue.peek() != null) {
                            scanPoint = scanDistanceQueue.poll();
                            scanList.add(scanPoint);
                        }
                        System.out.println(scanDistanceQueue.peek() == null);
                    }


                    // something:1
                    // map 업데이트
                    // 스캔 리스트에 추가
                    // mouse 정보 갱신
                    ra.update(scanPoint, 5, maze, scanMap, isFindExit);
                    mouse.scan();
                    scanList.add(scanPoint);
                    System.out.println("scanPoint: " + scanPoint);
                    System.out.println("scanCount: " + mouse.getScanCount());
                }

                // 현재 시야 업데이트
                isFindExit = mouse.map.update(mouse.getLocation(), 3, maze, isFindExit);
                gui.repaint();
                TimeUnit.MILLISECONDS.sleep(setTime);
                System.out.println("point007: Update sight");


                // 현재 위치를 확인한다.
                Point now = mouse.getLocation();


                // 현재 위치가 출구라면 Exit
                if (mouse.map.getCell(now.x, now.y).isExit()) {
                    System.out.println("point008: Exit state, Done");
                    System.out.println("Exit");
                    AstarAlgorithm astarAlgorithm = new AstarAlgorithm(maze, 0, 1, maze.getEndPoint().x, maze.getEndPoint().y);
                    int[][] bestPath = astarAlgorithm.run();
                    // 출구 도착 후 최적 경로(최단 경로) 표시
                    if (bestPath != null) {
                        for (int i=0; i<bestPath.length; i++) {
                            maze.getCell(bestPath[i][0], bestPath[i][1]).setState(Cell.State.BEST);
                            gui.repaint();
                            TimeUnit.MILLISECONDS.sleep(1);
                        }
                    }
                    System.out.println("이미지를 저장 중입니다. 프로그램을 절대 종료하지 마세요!!");
                    gui.saveAsImage("viewResult.png", view);
                    gui.saveAsImage("scanResult.png", scanMap);
                    gui.saveAsImage("mazeResult.png", maze);
                    gui.saveAsImage("mouseMap.png", mouseMap);
                    System.out.println("프로그램 종료");
                    System.out.println("scanList: " + scanList);
                    return;
                }
                // else
                if (!isFindExit) { // 출구를 모르고 있다면
                    System.out.println("point009: no Exit info");

                    // 갈 곳이 있나 체크
                    if (isValidPos(now.add(-1, 0))) {
                        stack.push(now.add(-1, 0));
                        branchCounter++;
                    }
                    if (isValidPos(now.add(0, -1))) {
                        stack.push(now.add(0, -1));
                        branchCounter++;
                    }
                    if (isValidPos(now.add(0, 1))) {
                        stack.push(now.add(0, 1));
                        branchCounter++;
                    }
                    if (isValidPos(now.add(1, 0))) {
                        stack.push(now.add(1, 0));
                        branchCounter++;
                    }
                    System.out.println("point010: isValidPos Done ");
                    System.out.println("test3: " + stack);

                    if (stack.isEmpty()) { // 스택이 완전히 비어있음(더 이상 갈 수 있는 곳이 없음)
                        System.out.println("point011: Stack is totally empty, Done");
                        System.out.println("Fail");
                        System.out.println("이미지를 저장 중입니다. 프로그램을 절대 종료하지 마세요!!");
                        gui.saveAsImage("viewResult.png", view);
                        gui.saveAsImage("scanResult.png", scanMap);
                        gui.saveAsImage("mazeResult.png", maze);
                        gui.saveAsImage("mouseMap.png", mouseMap);
                        System.out.println("프로그램 종료");

                        return;
                    } else { // 스택이 비어있지 않음(갈 수 있는 곳이 있음)
                        System.out.println("point012: Stack is not empty");

                        if (branchCounter >= 2) { // 분기점이라면
                            System.out.println("point013: Branch Set");
                            if (!isInitBranch) { // 출구부터 최조 분기점 이전의 경로를 추천하지 않는다.
                                while (!buffer.isEmpty()) {
                                    Point tmp = buffer.pop();
                                    maze.getCell(tmp).setState(Cell.State.NotRecommended);
                                    mouseMap.getCell(tmp).setState(Cell.State.NotRecommended);
                                }
                                isInitBranch = true;
                            }
                            buffer.push(new Point(-1, -1)); // 분기라는 것을 알린다
                            buffer.push(now);
                            maze.getCell(now).setState(Cell.State.BRANCH);
                            isFindExit = mouse.map.update(mouse.getLocation(), 3, maze, isFindExit);

                        }
                        if (branchCounter == 0) { // 현재는 갈 수 있는 곳이 없어서 이전 분기로 돌아가야한다면
                            System.out.println("point014: Can not go for now, Back to branch, Start buffer pop");
                            //Point prev = new Point();
                            maze.getCell(mouse.getLocation()).setState(Cell.State.NotRecommended); // 현재 위치 추천하지 않음
                            while (true) {
                                System.out.println("point015: do Buffer pop");
                                Point back = buffer.pop(); // 돌아갈 좌표를 뽑는다
                                System.out.println("buffer: " + buffer);
                                if (back == null && stack.isEmpty()) {
                                    System.out.println("point015-1: buffer empty, stack empty, Fail, Done");
                                    System.out.println("이미지를 저장 중입니다. 프로그램을 절대 종료하지 마세요!!");
                                    gui.saveAsImage("viewResult.png", view);
                                    gui.saveAsImage("scanResult.png", scanMap);
                                    gui.saveAsImage("mazeResult.png", maze);
                                    gui.saveAsImage("mouseMap.png", mouseMap);
                                    System.out.println("프로그램 종료");

                                    return;
                                }
                                if (back.x == -1 && back.y == -1) { // 분기점의 끝이라면
                                    System.out.println("point016: Branch arrived");
                                    stack.pop(); // 분기점 중복 제거?
                                    //buffer.push(prev);
                                    break;
                                } else {
                                    System.out.println("point017: Mouse moving");
                                    mouse.move();
                                    mouse.changeLocation(back);
                                    gui.repaint();
                                    TimeUnit.MILLISECONDS.sleep(bufferTime);
                                    maze.getCell(mouse.getLocation()).setState(Cell.State.NotRecommended); // 현재 위치 추천하지 않음
                                    //prev = back;
                                }
                                isFindExit = mouse.map.update(mouse.getLocation(), 3, maze, isFindExit);
                            }
                        } else { // 현재 갈 수 있는 곳이 있다면
                            System.out.println("point018: Can go for now, Keep going");
                            while (true) {
                                now = stack.pop(); // 현재 위치를 결정
                                System.out.println("test2: " + now + stack.peek() + mouse.getLocation());
                                if (stack.peek() == null)
                                    break;
                                if (!now.equals(stack.peek()))
                                    break;
                            }

                            mouse.move(); // 쥐를 해당 위치로 움직인다
                            mouse.changeLocation(now); // 쥐의 위치를 바꾼다
                            gui.repaint();
                            TimeUnit.MILLISECONDS.sleep(stackTime);
                            maze.getCell(now).setState(Cell.State.VISIT); // 해당 위치 VISIT state로 변경
                            view.getCell(now).setState(Cell.State.VISIT);

                            buffer.push(now); // 버퍼에 집어넣는다
                        }
                    }


                } else { // 출구를 알고 있다면
                    System.out.println("point019: Exit already found");
                    System.out.println(maze.getEndPoint());

                    // 경로검사, a* 알고리즘을 통해 쥐가 알고있는 맵에서 출구까지 가는 길이 있는지 확인
                    // 벽을 뚫고 A* 썼을 때, 가능한 경로가 있는가?
                    int[][] path = isPathWithWallBreak();
                    if (path != null) {
                        System.out.println("경로가 존재합니다. 출구로 이동합니다");
                        System.out.println("출구 위치 : " + maze.getEndPoint().x + ", " + maze.getEndPoint().y);
                        System.out.println("경로의 마지막 위치 : " + path[path.length - 1][0] + ", " + path[path.length - 1][1]);
                        for (int i = 0; i < path.length; i++) {
                            // 출구까지 곧바로 이동
                            mouse.move();
                            now = new Point(path[i][0], path[i][1]);
                            System.out.println("최단경로: " + now);
                            mouse.changeLocation(now);
                            mouseMap.getCell(now).setState(Cell.State.VISIT);
                            maze.getCell(now).setState(Cell.State.VISIT);
                            view.getCell(now).setState(Cell.State.VISIT);
                            gui.repaint();
                            TimeUnit.MILLISECONDS.sleep(bufferTime);
                        }
                        continue;
                    }

                    // 버퍼랑 리스트 다 비우기
                    if (scanMode != 1) {
                        System.out.println("point029: clear stack and buffer");
                        stack.clear();
                        buffer.clear();
                        mouse.map.resetVisitedInfo();
                        maze.resetVisitedInfo();
                        for (int i = 0; i < scanList.size(); i++) {
                            Point tmp = scanList.get(i);
                            ra.update(tmp, 5, maze, isFindExit);
                        }
                    }
                    gui.repaint();
                    TimeUnit.MILLISECONDS.sleep(setTime);

                    scanMode = 1; // 스캔모드를 바꾼다
                    System.out.println("point020: change scanMode to 1");

                    List<Point> points = new ArrayList<>();
                    // 네 가지 방향의 좌표

                    System.out.println("point030: check distance from exit");
                    // 리스트로 거리 먼저 계산하기
                    if (isValidPosByWeight(now.add(-1, 0))) { // 상
                        Point up = new Point(now.x - 1, now.y);
                        points.add(up);
                        System.out.println("up : " + up);
                        if (!maze.getCell(up).isBranch())
                            branchCounter++;
                    }
                    if (isValidPosByWeight(now.add(0, -1))) { // 좌
                        Point left = new Point(now.x, now.y - 1);
                        points.add(left);
                        System.out.println("left : " + left);
                        if (!maze.getCell(left).isBranch())
                            branchCounter++;
                    }
                    if (isValidPosByWeight(now.add(0, 1))) { // 우
                        Point right = new Point(now.x, now.y + 1);
                        points.add(right);
                        System.out.println("right : " + right);
                        if (!maze.getCell(right).isBranch())
                            branchCounter++;
                    }
                    if (isValidPosByWeight(now.add(1, 0))) { // 하
                        Point down = new Point(now.x + 1, now.y);
                        points.add(down);
                        System.out.println("down : " + down);
                        if (!maze.getCell(down).isBranch())
                            branchCounter++;
                    }


//                    Stack<Point> stack = new Stack<>();
                    // 거리에 따라 우선순위를 두는 우선순위큐 distanceQueue를 생성한다.
                    PriorityQueue<Point> distanceQueue = new PriorityQueue<>(Comparator.comparingDouble(p -> -calculateDistance(p))); // 거리가 멀수록 우선순위 높음 -> 다시 스택에 넣을 거라서 그럼

                    if (distanceQueue.isEmpty()) {
                        System.out.println("null");
                    }
                    // point 리스트에 있는 모든 포인트 객체를 distanceQueue에 추가
                    distanceQueue.addAll(points);

                    // 우선순위 큐에 있는 포인트들은 모두 스택에 넣는다.
                    while (!distanceQueue.isEmpty()) {
                        Point point = distanceQueue.poll();
                        stack.push(point);
                    }

                    System.out.println(stack);
                    if (stack.isEmpty()) { // 스택이 완전히 비어있음(더 이상 갈 수 있는 곳이 없음)
                        System.out.println("point031: Stack is totally empty, Done");
                        System.out.println("Fail");
                        System.out.println("이미지를 저장 중입니다. 프로그램을 절대 종료하지 마세요!!");
                        gui.saveAsImage("viewResult.png", view);
                        gui.saveAsImage("scanResult.png", scanMap);
                        gui.saveAsImage("mazeResult.png", maze);
                        gui.saveAsImage("mouseMap.png", mouseMap);
                        System.out.println("프로그램 종료");

                        return;
                    } else { // 스택이 비어있지 않음(갈 수 있는 곳이 있음)
                        System.out.println("point032: Stack is not empty");

                        if (branchCounter >= 2) { // 분기점이라면
                            System.out.println("point033: Branch Set");

                            buffer.push(new Point(-1, -1)); // 분기라는 것을 알린다
                            buffer.push(now);
                            maze.getCell(now).setState(Cell.State.BRANCH);
                            isFindExit = mouse.map.update(mouse.getLocation(), 3, maze, isFindExit);

                        }
                        if (branchCounter == 0) { // 현재는 갈 수 있는 곳이 없어서 이전 분기로 돌아가야한다면
                            System.out.println("point034: Can not go for now, Back to branch, Start buffer pop");
                            //Point prev = new Point();
                            maze.getCell(mouse.getLocation()).setState(Cell.State.NotRecommended); // 현재 위치 추천하지 않음
                            while (true) {
                                System.out.println("point035: do Buffer pop");
                                Point back = buffer.pop(); // 돌아갈 좌표를 뽑는다
                                System.out.println("buffer: " + buffer);
                                if (back == null && stack.isEmpty()) {
                                    System.out.println("point015-1: buffer empty, stack empty, Fail, Done");
                                    System.out.println("이미지를 저장 중입니다. 프로그램을 절대 종료하지 마세요!!");

                                    gui.saveAsImage("viewResult.png", view);
                                    gui.saveAsImage("scanResult.png", scanMap);
                                    gui.saveAsImage("mazeResult.png", maze);
                                    gui.saveAsImage("mouseMap.png", mouseMap);
                                    System.out.println("프로그램 종료");

                                    return;
                                }
                                if (back.x == -1 && back.y == -1) { // 분기점의 끝이라면
                                    System.out.println("point036: Branch arrived");
                                    stack.pop(); // 분기점 중복 제거?
                                    //buffer.push(prev);
                                    break;
                                } else {
                                    System.out.println("point037: Mouse moving");
                                    mouse.move();
                                    mouse.changeLocation(back);
                                    gui.repaint();
                                    TimeUnit.MILLISECONDS.sleep(bufferTime);
                                    maze.getCell(mouse.getLocation()).setState(Cell.State.NotRecommended); // 현재 위치 추천하지 않음
                                    //prev = back;
                                }
                                isFindExit = mouse.map.update(mouse.getLocation(), 3, maze, isFindExit);


                            }
                        } else { // 현재 갈 수 있는 곳이 있다면
                            System.out.println("point038: Can go for now, Keep going");
                            while (true) {
                                now = stack.pop(); // 현재 위치를 결정
                                System.out.println("test2: " + now + stack.peek() + mouse.getLocation());
                                if (stack.peek() == null)
                                    break;
                                if (!now.equals(stack.peek()))
                                    break;
                            }

                            mouse.move(); // 쥐를 해당 위치로 움직인다
                            mouse.changeLocation(now); // 쥐의 위치를 바꾼다
                            gui.repaint();
                            TimeUnit.MILLISECONDS.sleep(stackTime);
                            maze.getCell(now).setState(Cell.State.VISIT); // 해당 위치 VISIT state로 변경
                            view.getCell(now).setState(Cell.State.VISIT);
                            buffer.push(now); // 버퍼에 집어넣는다
                        }
                    }
                    gui.repaint();
                    TimeUnit.MILLISECONDS.sleep(setTime);
                }

            } else { // 체력이 남아있지 않다면
                System.out.println("point99: No more energy, Done");
                System.out.println("Fail: 체력 없음");
                System.out.println("이미지를 저장 중입니다. 프로그램을 절대 종료하지 마세요!!");
                gui.saveAsImage("viewResult.png", maze);
                gui.saveAsImage("scanResult.png", scanMap);
                gui.saveAsImage("mazeResult.png", maze);
                gui.saveAsImage("mouseMap.png", mouseMap);
                System.out.println("프로그램 종료");
                return;
            }
        }

    }
    public static String getScanMode(){
        if(scanMode == 0){
            return "ScanMode 0";
        }else return "ScanMode 1";
    }
    static boolean isScanValidPos(int x, int y) {

        if (x < 0 || y < 0 || x >= maze.getHeight() || y >= maze.getWidth())
            return false;
        else
            return ra.getCell(x, y).isAvailable() && !ra.getCell(x, y).isVisited() && ra.getCell(x, y).getState() != Cell.State.NotRecommended;
        // 이미 지나간 자리도 추가 해야하나?
    }

    static boolean isValidPos(int x, int y) {

        if (x < 0 || y < 0 || x >= maze.getHeight() || y >= maze.getWidth())
            return false;
        else
            return mouse.map.getCell(x, y).isAvailable() && !mouse.map.getCell(x, y).isVisited();
        // 이미 지나간 자리도 추가 해야하나?
    }

    static boolean isValidPos(Point p) {

        if (p.x < 0 || p.y < 0 || p.x >= maze.getHeight() || p.y >= maze.getWidth())
            return false;
        else
            return mouse.map.getCell(p.x, p.y).isAvailable() && !mouse.map.getCell(p.x, p.y).isVisited();
        // 이미 지나간 자리도 추가 해야하나?
    }

    static boolean isPointOverlap(Point mouse, List<Point> scanList) {
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

    // TODO: 이거 이제 isValidPos랑 같아졌는데 쓸모 있는지?
    static boolean isValidPosByWeight(Point p) {

        if (p.x < 0 || p.y < 0 || p.x >= maze.getHeight() || p.y >= maze.getWidth())
            return false;
        else
            return mouse.map.getCell(p.x, p.y).isAvailable() && !mouse.map.getCell(p.x, p.y).isVisited();
        // 이미 지나간 자리도 추가 해야하나?
    }


    static int[][] isPathWithWallBreak() throws InterruptedException {
        AstarAlgorithm Astar;
        Cell prevCell;
        if (!isWallBreaker) {
            for (int i = 0; i < mouseMap.getHeight(); i++) {
                for (int j = 0; j < mouseMap.getWidth(); j++) {
                    prevCell = mouseMap.getCell(new Point(i, j));
                    if (prevCell.isWall()) {
                        prevCell.setState(Cell.State.AVAILABLE);

                        Astar = new AstarAlgorithm(mouseMap, mouse.getLocation().x, mouse.getLocation().y,
                                maze.getEndPoint().x, maze.getEndPoint().y);
                        int[][] path = Astar.run();
                        // 경로가 존재하면
                        if (path != null) {
                            System.out.println("벽 부순 위치: " + new Point(i, j));
                            isWallBreaker = true;
                            // maze.getCell(new Point(i,j)).setState(Cell.State.VISIT);
                            // view.getCell(new Point(i,j)).setState(Cell.State.VISIT);
                            return path;
                        } else {
                            //System.out.println("현재 경로 없음");
                            prevCell.setState(Cell.State.WALL);
                        }
                    }
                }
            }
        } else {
            Astar = new AstarAlgorithm(mouseMap, mouse.getLocation().x, mouse.getLocation().y,
                    maze.getEndPoint().x, maze.getEndPoint().y);
            int[][] path = Astar.run();
            // 경로가 존재하면
            return path;
        }

        return null;
    }
    public static boolean getIsWallBreaker(){
        return isWallBreaker;
    }

    static int[][] readMaze(String path) {
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
        while (scanner.hasNextLine()) {
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
        int i = 0;
        int j = 0;
        int c;

        while (in.hasNextInt()) {
            c = in.nextInt();
            if (j == column) {
                i += 1;
                j = 0;
            }

            if (j != 1 || i != 0) {
                if ((i == 0 || i == row - 1 || j == 0 || j == column - 1) && c == 0) {
                    maze[i][j] = 2; // 출구를 2로 설정
                } else {
                    maze[i][j] = c;
                }
            } else {
                maze[i][j] = c;
            }

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

    public static double calculateDistance(Point p1, Point p2) {
        double deltaX = p2.x - p1.x;
        double deltaY = p2.y - p1.y;
        return Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
    }

    // DFS 모드
    static List<Point> scanDFSAlgorithm(Point exit) throws InterruptedException {
        LinkedStack<Point> scanStack = new LinkedStack<>();
        List<Point> candidateScanPoint = new ArrayList<>();
        scanStack.push(exit); // 시작 지점 지정

        while (!scanStack.isEmpty()) {
            System.out.println("스캔 가중치 스택: " + scanStack);
            // 뽑을 때 현재 위치가 5*5에 걸쳐 있으면
            Point now = scanStack.pop();

            // 위쪽 벽으로 막혀서 dfs 진행을 못하면 위쪽 5*5의 중심점을 candidateScanPoint에 추가
            if (!(now.x - 1 < 0 || now.y < 0 || now.x - 1 >= maze.getHeight() || now.y >= maze.getWidth())) {
                if (ra.getCell(now.x - 1, now.y).getState() == Cell.State.NotRecommended) {
                    Point up = findClosestPoint(now);
                    candidateScanPoint.add(up.add(-5));
                }
            }

            // 아래쪽 벽으로 막혀서 dfs 진행 x
            if (!(now.x + 1 < 0 || now.y < 0 || now.x + 1 >= maze.getHeight() || now.y >= maze.getWidth())) {
                if (ra.getCell(now.x + 1, now.y).getState() == Cell.State.NotRecommended) {
                    Point up = findClosestPoint(now);
                    candidateScanPoint.add(up.add(5));
                }
            }
            // 왼쪽
            if (!(now.x < 0 || now.y - 1 < 0 || now.x >= maze.getHeight() || now.y - 1 >= maze.getWidth())) {
                if (ra.getCell(now.x, now.y - 1).getState() == Cell.State.NotRecommended) {
                    Point up = findClosestPoint(now);
                    candidateScanPoint.add(up.add(0, -5));
                }
            }
            // 오른쪽은 필요한 경우에만 추가
            if (!(now.x < 0 || now.y + 1 < 0 || now.x >= maze.getHeight() || now.y + 1 >= maze.getWidth())) {
                if (ra.getCell(now.x, now.y + 1).getState() == Cell.State.NotRecommended) {
                    Point up = findClosestPoint(now);
                    candidateScanPoint.add(up.add(0, 5));
                }
            }
            System.out.println(now);
            ra.getCell(now).setState(Cell.State.VISIT);
            gui.repaint();
            TimeUnit.MILLISECONDS.sleep(scanDFStime);
            //하
            if (isScanValidPos(now.x + 1, now.y)) {
                scanStack.push(new Point(now.x + 1, now.y));
            }
            //상
            if (isScanValidPos(now.x - 1, now.y)) {
                scanStack.push(new Point(now.x - 1, now.y));
            }
            //우
            if (isScanValidPos(now.x, now.y + 1)) {
                scanStack.push(new Point(now.x, now.y + 1));
            }
            //좌
            if (isScanValidPos(now.x, now.y - 1)) {
                scanStack.push(new Point(now.x, now.y - 1));
            }
        }
        ra.resetVisitedInfo();

        return candidateScanPoint;
    }

    static Point scanWithoutDFSAlgoritm() {
        List<Point> candidateList = new ArrayList<>();
        List<Point> closestExitCandidates = new ArrayList<>();
        Point exitCenter = findClosestPoint(maze.getEndPoint());

        // 중점들로부터 상하좌우로 5칸 떨어져있는 중점들을 후보군에 추가
        for (Point center : scanList) {
            candidateList.add(new Point(center.x - 5, center.y)); // 상
            candidateList.add(new Point(center.x + 5, center.y)); // 하
            candidateList.add(new Point(center.x, center.y - 5)); // 좌
            candidateList.add(new Point(center.x, center.y + 5)); // 우
        }

        candidateList.removeAll(scanList);

        double minExitDistance = Double.MAX_VALUE;

        // 출구 중점과 가장 가까운 후보군들을 뽑기
        for (Point candidate : candidateList) {
            if (candidate.x >= 0 && candidate.x < maze.getHeight() && candidate.y >= 0 && candidate.y < maze.getWidth()) {
                double exitDistance = Math.sqrt(Math.pow(exitCenter.x - candidate.x, 2) + Math.pow(exitCenter.y - candidate.y, 2));
                if (exitDistance < minExitDistance) {
                    minExitDistance = exitDistance;
                    closestExitCandidates.clear(); // 새로운 최소 거리가 발견되면 기존 리스트를 비움
                    closestExitCandidates.add(candidate);
                } else if (exitDistance == minExitDistance) {
                    closestExitCandidates.add(candidate); // 거리가 같은 후보군이 있으면 추가
                }
            }
        }

        Point nearestValidPoint = null;
        double minMouseDistance = Double.MAX_VALUE;

        // 쥐와 가장 가까운 거리인 중점을 찾기
        for (Point candidate : closestExitCandidates) {
            double mouseDistance = Math.sqrt(Math.pow(mouse.getLocation().x - candidate.x, 2) + Math.pow(mouse.getLocation().y - candidate.y, 2));
            if (mouseDistance < minMouseDistance) {
                minMouseDistance = mouseDistance;
                nearestValidPoint = candidate;
            }
        }

        return nearestValidPoint;
    }


    public static Point findClosestPoint(Point point) {
        if (scanList.isEmpty()) {
            return null; // 스캔 리스트가 비어있으면 null 반환
        }

        Point closestPoint = scanList.get(0);
        double closestDistance = calculateDistance(closestPoint, point);

        for (int i = 1; i < scanList.size(); i++) {
            Point currentPoint = scanList.get(i);
            double currentDistance = calculateDistance(currentPoint, point);
            if (currentDistance < closestDistance) {
                closestPoint = currentPoint;
                closestDistance = currentDistance;
            }
        }

        return closestPoint;
    }


    public static boolean getBreakCount() {
        return isWallBreaker;
    }
}

