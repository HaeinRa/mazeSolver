import java.awt.*;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GUI extends JPanel {
    private Mouse mouse;
    private Maze maze;

    public GUI(Maze maze, Mouse mouse) {
        this.maze = maze;
        this.mouse = mouse;
        JFrame frame = new JFrame("Maze");
        frame.setSize(1000, 1000);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawMaze((Graphics2D) g);
    }

    public void drawMaze(Graphics2D g2d) {
        int cellWidth = 15;
        int cellHeight = 15;

        for (int i = 0; i < maze.getWidth(); i++) {
            for (int j = 0; j < maze.getHeight(); j++) {
                int x = j * cellWidth;
                int y = i * cellHeight;
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
                if (mouse.getLocation().x == j && mouse.getLocation().y == i) {
                    g2d.setColor(Color.BLUE);
                }


                /*if (new Point(i, j).equals(mouse.getLocation())) {
                    g2d.setColor(Color.BLUE);
                }*///(mouse.getLocation().x == i && mouse.getLocation().y == j)
                g2d.fillRect(x, y, cellWidth, cellHeight);
            }
        }
    }

}
