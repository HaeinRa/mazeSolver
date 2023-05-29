import java.awt.*;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GUI extends JPanel {
    private Mouse mouse;
    private Maze maze;
    private int cellSize = 10;
    private Color SCAN_COLOR = new Color(150, 130, 250, 70);

    public GUI(Maze maze, Mouse mouse) {
        this.maze = maze;
        this.mouse = mouse;

        int width = maze.getWidth() * cellSize;
        int height = maze.getHeight() * cellSize;

        System.out.println(width);
        System.out.println(height);

        JFrame frame = new JFrame("Maze");
        frame.setSize(width + 20, height + 50);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.setVisible(true);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawMaze((Graphics2D) g, maze, mouse);
    }

    public void drawMaze(Graphics2D g2d, Maze maze, Mouse mouse) {

        for (int i = 0; i < maze.getHeight(); i++) {
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
                    //case SCAN:
                    //  g2d.setColor(SCAN_COLOR);
                }
                if (mouse.getLocation().x == i && mouse.getLocation().y == j) {
                    g2d.setColor(Color.BLUE);
                }
                g2d.fillRect(x, y, cellSize, cellSize);
            }
        }
    }
}
