import java.awt.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.util.*;
import java.util.List;

public class GUI extends JPanel {
    private Mouse mouse;
    private Maze maze;
    private List<Point> scanList;
    private int cellSize = 17;
    private Color SCAN_COLOR = new Color(150, 130, 250, 70);

    public GUI(Maze maze, Mouse mouse, List<Point> scanList) {
        this.maze = maze;
        this.mouse = mouse;
        this.scanList = scanList;

        int width = maze.getWidth() * cellSize;
        int height = maze.getHeight() * cellSize;

        System.out.println(width);
        System.out.println(height);

        JFrame frame = new JFrame("Maze");
        frame.setSize(width + 220, height + 50);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.setVisible(true);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawMaze((Graphics2D) g, maze, mouse, scanList);
        drawStatus(g, maze, mouse);
    }

    private void drawMaze(Graphics2D g2d, Maze maze, Mouse mouse, List<Point> scanList) {


        for (int i = 0; i < maze.getHeight(); i++) {
            List<Point> scanArea = new ArrayList<>();
            for (int j = 0; j < maze.getWidth(); j++) {
                int x = j * cellSize;
                int y = i * cellSize;

                Cell.State state = maze.getCell(i, j).getState();
                switch (state) {
                    case WALL:
                        g2d.setColor(Color.GRAY);
                        break;
                    case AVAILABLE:
                        g2d.setColor(Color.WHITE);
                        break;
                    case VISIT:
                        g2d.setColor(Color.YELLOW);
                        break;
                    case EXIT:
                        g2d.setColor(Color.RED);
                        break;
                    case NotRecommended:
                        g2d.setColor(Color.ORANGE);
                        break;
                    case BRANCH:
                        g2d.setColor(Color.PINK);
                        break;
                }

                if (mouse.getLocation().x == i && mouse.getLocation().y == j) {
                    g2d.setColor(Color.BLUE);
                }
                g2d.fillRect(x, y, cellSize, cellSize);



                for (int index = 0; index < scanList.size(); index++) {
                    Point center = scanList.get(index);
                    int xScan = center.x;
                    int yScan = center.y;
                    for (int yIndex = yScan - 2; yIndex <= yScan + 2; yIndex++) {
                        for (int xIndex = xScan - 2; xIndex <= xScan + 2; xIndex++) {
                            Point point = new Point(xIndex, yIndex);
                            scanArea.add(point);
                        }
                    }
                }

                for (int checkPointIndex = 0; checkPointIndex < scanArea.size(); checkPointIndex++) {
                    Point checkPoint = scanArea.get(checkPointIndex);

                    Cell.State scanState = maze.getCell(checkPoint.x - 2, checkPoint.y).getState();
                    if (scanState == Cell.State.WALL) {
                        g2d.setColor(SCAN_COLOR);
                        g2d.fillRect((checkPoint.y)  * cellSize, (checkPoint.x-2) * cellSize, cellSize, cellSize);
                    }
                }
            }
        }
    }

    public void drawStatus(Graphics g, Maze maze, Mouse mouse) {
        int statusX = getWidth() - 200; // 상태 창 위치 X 좌표
        int statusY = 20; // 상태 창 위치 Y 좌표

        g.setColor(Color.CYAN);
        g.fillRect(statusX, 1, 400, maze.getHeight() * cellSize);
        g.setColor(Color.BLACK);
        g.drawRect(statusX - 1, 0, 400, maze.getHeight() * cellSize + 1);

        Font font = new Font("Arial Rounded MT 굵게", Font.BOLD, 15);
        g.setFont(font);

        int beginEnergy = maze.getHeight() * maze.getWidth();
        int usedEnergy = beginEnergy - mouse.getEnergy();
        String manaCount = Double.toString(mouse.getMana()).substring(0, 3);

        g.drawString("미로     찾기", statusX + 50, statusY);

        g.drawString("미로 크기 : ", statusX + 10, statusY + 40);
        g.drawString(Integer.toString(maze.getWidth()), statusX + 90, statusY + 40);
        g.drawString(" X ", statusX + 110, statusY + 40);
        g.drawString(Integer.toString(maze.getHeight()), statusX + 130, statusY + 40);

        g.drawString("초기 에너지 : ", statusX + 10, statusY + 60);
        g.drawString(Integer.toString(beginEnergy), statusX + 110, statusY + 60);

        g.drawString("사용 에너지 : ", statusX + 10, statusY + 80);
        g.drawString(Integer.toString(usedEnergy), statusX + 110, statusY + 80);

        g.drawString("잔여 에너지 : ", statusX + 10, statusY + 100);
        g.drawString(Integer.toString(mouse.getEnergy()), statusX + 110, statusY + 100);

        g.drawString("마나 : ", statusX + 10, statusY + 120);
        g.drawString(manaCount, statusX + 60, statusY + 120);

        g.drawString("스캔 횟수 : ", statusX + 10, statusY + 140);
        g.drawString(Integer.toString(mouse.getScanCount()), statusX + 100, statusY + 140);

        g.drawString("모드 : ", statusX + 10, statusY + 160);

        g.drawString("벽 뚫기 사용 : ", statusX + 10, statusY + 180);

        Font teamFont = new Font("Arial Rounded MT 굵게", Font.BOLD, 15);
        g.setFont(teamFont);

        g.drawString("팀원", statusX + 80, statusY + 380);
        g.drawString("2019136012 권예찬", statusX + 30, statusY + 400);
        g.drawString("2019136131 채승윤", statusX + 30, statusY + 420);
        g.drawString("2019136139 허준기", statusX + 30, statusY + 440);
        g.drawString("2021136043 라해인", statusX + 30, statusY + 460);

        g.setColor(Color.RED);
        g.fillRect(statusX + 15, statusY + 370, 60, 10);
        g.fillRect(statusX + 115, statusY + 370, 60, 10);
        g.fillRect(statusX + 15, statusY + 380, 10, 90);
        g.fillRect(statusX + 165, statusY + 380, 10, 90);
        g.fillRect(statusX + 15, statusY + 470, 160, 10);

    }
}
