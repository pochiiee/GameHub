package gh;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Random;

public class Game1 extends JFrame {

    public Game1() {
        setTitle("Snake Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(new GamePanel());
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true); // Make the frame visible here
    }

    class GamePanel extends JPanel implements ActionListener {
        private final int TILE_SIZE = 20;
        private final int WIDTH = 800 / TILE_SIZE;
        private final int HEIGHT = 600 / TILE_SIZE;
        private LinkedList<Point> snake;
        private Point food;
        private char direction;
        private boolean gameOver;
        private Timer timer;
        private Color snakeColor;
        private Color foodColor;

        public GamePanel() {
            startGame();
        }

        private void startGame() {
            snake = new LinkedList<>();
            snake.add(new Point(WIDTH / 2, HEIGHT / 2));
            direction = 'R'; // Initial direction
            spawnFood();
            snakeColor = Color.GREEN;
            gameOver = false;

            setBackground(new Color(50, 50, 50)); // Background color
            setFocusable(true);
            requestFocusInWindow(); // Ensure the panel can receive key events

            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_UP:
                            if (direction != 'D') direction = 'U';
                            break;
                        case KeyEvent.VK_DOWN:
                            if (direction != 'U') direction = 'D';
                            break;
                        case KeyEvent.VK_LEFT:
                            if (direction != 'R') direction = 'L';
                            break;
                        case KeyEvent.VK_RIGHT:
                            if (direction != 'L') direction = 'R';
                            break;
                    }
                }
            });

            timer = new Timer(150, this);
            timer.start();
        }

        private void spawnFood() {
            Random rand = new Random();
            do {
                int x = rand.nextInt(WIDTH - 2) + 1;
                int y = rand.nextInt(HEIGHT - 3) + 2;
                food = new Point(x, y);
            } while (snake.contains(food) || !isCheckeredPosition(food));

            int r = rand.nextInt(128) + 128; // Lighter colors
            int g = rand.nextInt(128) + 128;
            int b = rand.nextInt(128) + 128;
            foodColor = new Color(r, g, b);
        }

        private boolean isCheckeredPosition(Point p) {
            return (p.x + p.y) % 2 == 0; // Checkered pattern
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!gameOver) {
                moveSnake();
                checkCollision();
                repaint();
            }
        }

        private void moveSnake() {
            Point head = snake.getFirst();
            Point newHead = new Point(head);

            switch (direction) {
                case 'U': newHead.y--; break;
                case 'D': newHead.y++; break;
                case 'L': newHead.x--; break;
                case 'R': newHead.x++; break;
            }

            snake.addFirst(newHead);

            if (newHead.equals(food)) {
                snakeColor = foodColor; // Change snake color to food color
                spawnFood(); // Respawn food with new color
            } else {
                snake.removeLast();
            }
        }

        private void checkCollision() {
            Point head = snake.getFirst();

            // Check for wall collisions
            if (head.x < 0 || head.x >= WIDTH || head.y < 2 || head.y >= HEIGHT - 1) {
                gameOver = true;
                timer.stop();
                showGameOver("You hit the wall!");
            }

            // Check if the snake collides with itself
            for (int i = 1; i < snake.size(); i++) {
                if (head.equals(snake.get(i))) {
                    gameOver = true;
                    timer.stop();
                    showGameOver("You ran into yourself!");
                    break;
                }
            }
        }

        private void showGameOver(String message) {
            int response = JOptionPane.showOptionDialog(this,
                    "Game Over!\n" + message + "\nYour score: " + (snake.size() - 1),
                    "Game Over",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    new Object[] {"Play Again", "Exit"},
                    "Play Again");

            if (response == JOptionPane.YES_OPTION) {
                startGame(); // Restart game
            } else {
                System.exit(0); // Exit game
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Draw background
            for (int i = 0; i < getWidth(); i += TILE_SIZE) {
                for (int j = 0; j < getHeight(); j += TILE_SIZE) {
                    g.setColor((i + j) % (TILE_SIZE * 2) == 0 ? new Color(60, 60, 60) : new Color(70, 70, 70));
                    g.fillRect(i, j, TILE_SIZE, TILE_SIZE);
                }
            }

            // Draw score background
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, getWidth(), TILE_SIZE * 2);

            // Display score
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + (snake.size() - 1), 10, 30);

            // Draw snake
            g.setColor(snakeColor);
            for (Point p : snake) {
                g.fillOval(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }

            // Draw food
            g.setColor(foodColor);
            g.fillOval(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }
    }
}
