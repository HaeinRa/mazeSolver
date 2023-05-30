import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class MazeGenerator {

    public static void saveMazeToFile(int[][] maze, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            for (int[] row : maze) {
                for (int cell : row) {
                    writer.write(cell + " ");
                }
                writer.write(System.lineSeparator());
            }
            System.out.println("미로가 텍스트 파일로 저장되었습니다: " + filePath);
        } catch (IOException e) {
            System.out.println("파일을 저장하는 도중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    public static int[][] generateMaze(int width, int height) {
        int[][] maze = new int[height][width];
        Random random = new Random();

        // 미로 내부를 벽으로 초기화
        for (int row = 0; row < height; row++) {
            Arrays.fill(maze[row], 1);
        }

        // 입구 (0, 1) 설정
        maze[0][1] = 0;

        generatePath(0, 1, maze, random);

        // 출구 설정
        int exitSide = random.nextInt(4); // 0: 위, 1: 오른쪽, 2: 아래, 3: 왼쪽
        int exitX = 0;
        int exitY = 0;

        switch (exitSide) {
            case 0: // 위
                exitX = random.nextInt(width - 2) + 1;
                exitY = 0;
                break;
            case 1: // 오른쪽
                exitX = width - 1;
                exitY = random.nextInt(height - 2) + 1;
                break;
            case 2: // 아래
                exitX = random.nextInt(width - 2) + 1;
                exitY = height - 1;
                break;
            case 3: // 왼쪽
                exitX = 0;
                exitY = random.nextInt(height - 2) + 1;
                break;
        }



        for (int row = 0; row < height; row++) {
            maze[row][0] = 1;
            maze[row][width - 1] = 1;
        }
        for (int col = 0; col < width; col++) {
            maze[0][col] = 1;
            maze[height - 1][col] = 1;
        }
        maze[0][1] = 0;
        maze[exitY][exitX] = 0;

        return maze;
    }

    public static void generatePath(int row, int col, int[][] maze, Random random) {
        int[] directions = {0, 1, 2, 3};
        shuffleArray(directions, random);

        for (int direction : directions) {
            int newRow = row;
            int newCol = col;

            if (direction == 0 && newRow - 2 >= 0) { // Up
                newRow -= 2;
                if (maze[newRow][newCol] == 1) {
                    maze[newRow + 1][newCol] = 0;
                    maze[newRow][newCol] = 0;
                    generatePath(newRow, newCol, maze, random);
                }
            } else if (direction == 1 && newCol + 2 < maze[0].length) { // Right
                newCol += 2;
                if (maze[newRow][newCol] == 1) {
                    maze[newRow][newCol - 1] = 0;
                    maze[newRow][newCol] = 0;
                    generatePath(newRow, newCol, maze, random);
                }
            } else if (direction == 2 && newRow + 2 < maze.length) { // Down
                newRow += 2;
                if (maze[newRow][newCol] == 1) {
                    maze[newRow - 1][newCol] = 0;
                    maze[newRow][newCol] = 0;
                    generatePath(newRow, newCol, maze, random);
                }
            } else if (direction == 3 && newCol - 2 >= 0) { // Left
                newCol -= 2;
                if (maze[newRow][newCol] == 1) {
                    maze[newRow][newCol + 1] = 0;
                    maze[newRow][newCol] = 0;
                    generatePath(newRow, newCol, maze, random);
                }
            }
        }
    }

    public static void shuffleArray(int[] array, Random random) {
        for (int i = array.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    public static void main(String[] args) {
        int width = 60;
        int height = 40;
        int[][] maze = generateMaze(width, height);
        String filePath = "exMaze.txt";
        saveMazeToFile(maze, filePath);
    }

}
