import sun.awt.image.ImageWatched;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

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

    public static void main(String[] args){



        /*
         * ------ SetUp 단계 ------
         * - 미로 txt 파일 읽기 (출구, 벽)
         * - 사용 가능한 미로로 변환 (Cell에 저장)
         * - GUI 띄우기 (미로, 쥐)
         * */

        // SetUp: 미로 txt 파일 읽기 (출구, 벽)
        // SetUp: 사용 가능한 미로로 변환 (Cell에 저장)
        maze = new Maze(readMaze("test.txt"));

        // SetUp: GUI 띄우기 (미로, 쥐)
        //gui.draw(maze);






        isFindExit = false;
        scanMode = 0;
        branchCounter = 0; // 분기점인지 체크하는 카운터

        // Run 단계 시작 (반복시켜야함)
        branchCounter = 0; // 분기점 카운터 초기화

        // 현재 쥐의 상태를 확인 (체력과 마나)
        mouse = new Mouse(new Point(0,1), maze.getHeight()*maze.getWidth());
        // 체력이 남아 있다면
        while(true){
            if (mouse.getEnergy() > 0) {
                // 5x5 스캔하기 (모드에 따라)
                if(scanMode == 0){
                    // map 업데이트
                    // 스캔 리스트에 추가
                    Point scanPoint = new Point(maze.getHeight()-4, maze.getWidth()-4);
                    mouse.map.update(scanPoint,2, maze);
                    scanList.add(scanPoint);
                }
                else if (scanMode == 1) {
                    // something:1
                    // map 업데이트
                    // 스캔 리스트에 추가
                }

                // 현재 시야 업데이트
                mouse.map.update(mouse.getLocation(),1,maze);


                // 현재 위치를 확인한다.
                Point now = mouse.getLocation();
                // 현재 위치가 출구라면 Exit
                if (maze.getCell(now.x, now.y).isExit()){
                    System.out.println("Exit");
                    return;
                }
                // else
                if (!isFindExit){ // 출구를 모르고 있다면
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

                    if(stack.isEmpty()){ // 스택이 완전히 비어있음(더 이상 갈 수 있는 곳이 없음)
                        System.out.println("Fail");
                        return;
                    }


                    else{ // 스택이 비어있지 않음(갈 수 있는 곳이 있음)
                        if(branchCounter>0){ // 분기점이라면
                            buffer.push(new Point(-1, -1)); // 분기라는 것을 알린다
                        }
                        if(branchCounter==0){ // 현재는 갈 수 있는 곳이 없어서 이전 분기로 돌아가야한다면
                            mouse.map.getCell(mouse.getLocation()).setState(Cell.State.NotRecommended); // 현재 위치 추천하지 않음
                            while(true){
                                Point back = buffer.pop(); // 돌아갈 좌표를 뽑는다
                                if(back.equals(new Point(-1, -1))) // 분기점의 끝이라면
                                    break;
                                else{
                                    mouse.move();
                                    mouse.changeLocation(back);
                                    mouse.map.getCell(mouse.getLocation()).setState(Cell.State.NotRecommended); // 현재 위치 추천하지 않음
                                }
                            }
                        }
                        else{ // 현재 갈 수 있는 곳이 있다면
                            now = stack.pop(); // 현재 위치를 결정
                            mouse.move(); // 쥐를 해당 위치로 움직인다
                            mouse.changeLocation(now); // 쥐의 위치를 바꾼다
                            mouse.map.getCell(now).setState(Cell.State.VISIT); // 해당 위치 VISIT state로 변경
                            buffer.push(now); // 버퍼에 집어넣는다
                        }
                    }
                }
                else{ // 출구를 알고 있다면

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
