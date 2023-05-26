import java.awt.*;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GUI extends JPanel {
    private Maze maze;
    private Mouse mouse;
    private int cellSize = 10;

    public GUI(Maze maze,Mouse mouse) {
        this.maze = maze;
        this.mouse = mouse;
        int width = maze.getWidth() * cellSize;
        int height = maze.getHeight() * cellSize;

        System.out.println(width);
        System.out.println(height);

        JFrame frame = new JFrame("Maze");
        frame.setSize(width, height);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
//        JFrame frame = new JFrame("Maze");
//        frame.setSize(1000, 1000);
//        frame.setVisible(true);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.add(this);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawMaze((Graphics2D) g, maze, mouse);
    }

    public void drawMaze(Graphics2D g2d, Maze maze, Mouse mouse) {
        for (int i = 0; i < maze.getWidth(); i++) {
            for (int j = 0; j < maze.getHeight(); j++) {
                int x = j * cellSize;
                int y = i * cellSize;
                if (maze.getCell(i, j).getState() == Cell.State.WALL) {
                    g2d.setColor(Color.GRAY);
                } else if (maze.getCell(i, j).getState() == Cell.State.AVAILABLE) {
                    g2d.setColor(Color.WHITE);
                } else if (maze.getCell(i, j).getState() == Cell.State.VISIT) {
                    g2d.setColor(Color.YELLOW);
                } else if (maze.getCell(i, j).getState() == Cell.State.EXIT) {
                    g2d.setColor(Color.RED);
                } else if (maze.getCell(i, j).getState() == Cell.State.NotRecommended) {
                    g2d.setColor(Color.ORANGE);
                }
                if (mouse.getLocation().x == i && mouse.getLocation().y == j) {
                    g2d.setColor(Color.BLUE);
                }
                g2d.fillRect(x, y, cellSize, cellSize);
            }
        }
    }
}
