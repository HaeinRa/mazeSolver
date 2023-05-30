import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GUI extends JPanel {
    private final Maze scanMap;
    private Mouse mouse;
    private Maze maze;
    private int cellSize = 15;
    private Color SCAN_COLOR = new Color(150, 130, 250, 70);

    public GUI(Maze maze, Mouse mouse, Maze scanMap) {
        this.maze = maze;
        this.mouse = mouse;
        this.scanMap = scanMap;

        int width = maze.getWidth() * cellSize;
        int height = maze.getHeight() * cellSize;

        System.out.println(width);
        System.out.println(height);

        JFrame contentFrame = new JFrame("Maze");
        contentFrame.setSize(width + 220, height + 50);
        contentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        contentFrame.add(this);
        contentFrame.setVisible(true);

        /*saveImage(contentFrame);*/
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawMaze((Graphics2D) g, maze, mouse);
        drawStatus(g, maze, mouse);
    }

    private void drawMaze(Graphics2D g2d, Maze maze, Mouse mouse) {

        for (int i = 0; i < maze.getHeight(); i++) {
            for (int j = 0; j < maze.getWidth(); j++) {
                int x = j * cellSize;
                int y = i * cellSize;

                Cell.State state = maze.getCell(i, j).getState();
                switch (state) {
                    case WALL:
                        g2d.setColor(Color.LIGHT_GRAY);
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
                    case BROKEN:
                        g2d.setColor(Color.CYAN);
                        break;
                    case UNKNOWN:
                        g2d.setColor(Color.BLACK);
                        break;
                    case BEST:
                        g2d.setColor(Color.red);
                        break;
                }

                if (mouse.getLocation().x == i && mouse.getLocation().y == j) {
                    g2d.setColor(Color.BLUE);
                }
                g2d.fillRect(x, y, cellSize, cellSize);
                fillScanArea(maze, scanMap, g2d);
            }
        }
    }

    public void saveAsImage(String filename, Maze modelImage) {
        maze = modelImage;
        // 컴포넌트 크기로 BufferedImage 생성
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        // 컴포넌트를 BufferedImage에 그림
        paint(graphics);
        graphics.dispose();

        // 이미지 파일로 저장
        try {
            ImageIO.write(image, "png", new File(filename));
            System.out.println(filename+" 이미지 저장 완료");
        } catch (IOException e) {
            System.out.println("파일 저장 실패: " + e.getMessage());
        }
    }

    private void fillScanArea(Maze maze, Maze scanMap, Graphics2D g2d) {
        for (int yIndex = 0; yIndex < scanMap.getHeight(); yIndex++) {
            for (int xIndex = 0; xIndex < scanMap.getWidth(); xIndex++) {
                if (scanMap.getCell(yIndex, xIndex).getState() == Cell.State.WALL && maze.getCell(yIndex, xIndex).getState() == Cell.State.WALL) {
                    g2d.setColor(SCAN_COLOR);
                    g2d.fillRect(xIndex * cellSize, yIndex * cellSize, cellSize, cellSize);
                }
            }
        }
    }

    private void drawStatus(Graphics g, Maze maze, Mouse mouse) {
        int statusX = getWidth() - 200; // 상태 창 위치 X 좌표
        int statusY = 20; // 상태 창 위치 Y 좌표

        g.setColor(Color.CYAN);
        g.fillRect(statusX, 1, 400, maze.getHeight() * cellSize);
        g.setColor(Color.BLACK);
        g.drawRect(statusX - 1, 0, 400, maze.getHeight() * cellSize + 1);

        Font font = new Font("Arial Rounded MT 굵게", Font.BOLD, 15);
        g.setFont(font);

        int beginEnergy = maze.getHeight() * maze.getWidth() * 2;
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
        g.drawString(CeremonyAlgorithm.getScanMode(), statusX + 60, statusY + 160);

        g.drawString("벽 뚫기 사용 : ", statusX + 10, statusY + 180);
        if(CeremonyAlgorithm.getIsWallBreaker()){
            g.drawString( "O", statusX + 110, statusY + 180);
        }else{
            g.drawString("X", statusX + 110, statusY + 180);

        }

        Font teamFont = new Font("Arial Rounded MT 굵게", Font.BOLD, 15);
        g.setFont(teamFont);

        g.drawString("팀원", statusX + 80, statusY + 480);
        g.drawString("2019136012 권예찬", statusX + 30, statusY + 500);
        g.drawString("2019136131 채승윤", statusX + 30, statusY + 520);
        g.drawString("2019136139 허준기", statusX + 30, statusY + 540);
        g.drawString("2021136043 라해인", statusX + 30, statusY + 560);

        g.setColor(Color.RED);
        g.fillRect(statusX + 15, statusY + 470, 60, 10);
        g.fillRect(statusX + 115, statusY + 470, 60, 10);
        g.fillRect(statusX + 15, statusY + 480, 10, 90);
        g.fillRect(statusX + 165, statusY + 480, 10, 90);
        g.fillRect(statusX + 15, statusY + 570, 160, 10);
    }
    /*private void saveImage(JFrame panel) {
        BufferedImage img = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
        panel.paint(img.getGraphics());
        try {
            ImageIO.write(img, "jpg", new File("C:\\사진"));
            System.out.println("panel saved as image");

        } catch (Exception e) {
            System.out.println("panel not saved" + e.getMessage());
        }
    }*/
}
