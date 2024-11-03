package gh;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Game3 extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 600;

    // Images
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    // Bird class
    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    // Pipe class
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64; // Scaled by 1/6
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    // Game logic
    Bird bird;
    int velocityX = -4; // Move pipes to the left speed (simulates bird moving right)
    int velocityY = 0; // Move bird up/down speed.
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipeTimer;
    boolean gameOver = false;
    double score = 0;
    boolean gameStarted = false; // Check if the game has started

    // Buttons
    JButton startButton;
    JButton playAgainButton;
    JButton exitButton;

    Game3() {
        // Create the JFrame
        JFrame frame = new JFrame("Flappy Bird Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(boardWidth, boardHeight));

        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);
        setLayout(null); // Use null layout for absolute positioning

        // Load images
        backgroundImg = new ImageIcon(getClass().getResource("/gh/flappy/flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("/gh/flappy/flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("/gh/flappy/toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("/gh/flappy/bottompipe.png")).getImage();

        // Bird initialization
        bird = new Bird(birdImg);
        pipes = new ArrayList<>();

        // Start Button
        startButton = new JButton("Start");
        startButton.setBounds((boardWidth - 100) / 2, boardHeight / 2 + 50, 100, 50);
        startButton.setFocusable(false);
        startButton.addActionListener(e -> startGame());
        add(startButton);

        // Play Again Button
        playAgainButton = new JButton("Play Again");
        playAgainButton.setBounds((boardWidth - 100) / 2, boardHeight / 2 + 110, 100, 50);
        playAgainButton.setFocusable(false);
        playAgainButton.addActionListener(e -> startGame());
        playAgainButton.setVisible(false);
        add(playAgainButton);

        // Exit Button
        exitButton = new JButton("Exit");
        exitButton.setBounds((boardWidth - 100) / 2, boardHeight / 2 + 170, 100, 50);
        exitButton.setFocusable(false);
        exitButton.addActionListener(e -> {
            new GameHub(); // Open GameHub window
        });

        exitButton.setVisible(false); // Initially hidden
        add(exitButton);

        // Place pipes timer
        placePipeTimer = new Timer(1500, e -> placePipes());
        placePipeTimer.start();

        // Game timer
        gameLoop = new Timer(1000 / 60, this); // 60 FPS

        // Add this panel to the frame
        frame.add(this);
        frame.pack(); // Adjust the frame size to fit the panel
        frame.setLocationRelativeTo(null); // Center the frame
        frame.setVisible(true); // Show the frame

        // Start the game loop
        gameLoop.start();
    }

    void startGame() {
        gameStarted = true;
        bird.y = birdY; // Reset bird position
        velocityY = 0;
        pipes.clear();
        score = 0;
        gameOver = false;
        startButton.setVisible(false); // Hide start button
        playAgainButton.setVisible(false); // Hide play again button
        exitButton.setVisible(false); // Hide exit button
        gameLoop.start(); // Start the game loop
    }

    void placePipes() {
        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        int openingSpace = boardHeight / 4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Background
        g.drawImage(backgroundImg, 0, 0, this.boardWidth, this.boardHeight, null);

        // Bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        // Pipes
        for (Pipe pipe : pipes) {
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // Score
        g.setColor(Color.ORANGE);
        g.setFont(new Font("Arial", Font.BOLD, 48)); // Chubby font size
        if (gameOver) {
            // Game Over message in the center
            String gameOverText = "Game Over!";
            FontMetrics metrics = g.getFontMetrics();
            int x = (boardWidth - metrics.stringWidth(gameOverText)) / 2; // Center X
            int y = boardHeight / 2 - 30; // Center Y above the buttons
            g.drawString(gameOverText, x, y);

            // Score text below Game Over
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 24)); // Smaller font size for score
            String scoreText = "Score: " + (int) score;
            metrics = g.getFontMetrics(); // Get metrics again for the new font size
            x = (boardWidth - metrics.stringWidth(scoreText)) / 2; // Center X
            y += 30; // Adjust Y position to below the Game Over text
            g.drawString(scoreText, x, y);

            // Show play again and exit buttons
            playAgainButton.setVisible(true);
            exitButton.setVisible(true);
        } else {
            // Score display at the top middle, not too close to the top
            String scoreText = String.valueOf((int) score);
            g.setFont(new Font("Arial", Font.BOLD, 48)); // Chubby font size
            FontMetrics metrics = g.getFontMetrics();
            int x = (boardWidth - metrics.stringWidth(scoreText)) / 2; // Center X
            int y = 60; // Adjusted Y position, not too close to the top
            g.drawString(scoreText, x, y);
        }
    }

    public void move() {
        if (gameStarted) { // Only move if the game has started
            // Bird
            velocityY += gravity;
            bird.y += velocityY;
            bird.y = Math.max(bird.y, 0); // Limit the bird.y to top of the canvas

            // Pipes
            for (int i = 0; i < pipes.size(); i++) {
                Pipe pipe = pipes.get(i);
                pipe.x += velocityX;

                if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                    score += 0.5; // 0.5 for each pipe
                    pipe.passed = true;
                }

                if (collision(bird, pipe)) {
                    gameOver = true;
                    gameStarted = false; // Set gameStarted to false when game over
                }
            }

            if (bird.y > boardHeight) {
                gameOver = true;
                gameStarted = false; // Set gameStarted to false when game over
            }
        }
    }

   

    boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&   // a's top left corner doesn't reach b's top right corner
               a.x + a.width > b.x &&   // a's top right corner passes b's top left corner
               a.y < b.y + b.height &&  // a's top left corner doesn't reach b's bottom left corner
               a.y + a.height > b.y;     // a's bottom left corner passes b's top left corner
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            move();
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            // Apply upward velocity only if the game has started
            if (gameStarted) {
                velocityY = -10; // Jump height
            } else if (gameOver) {
                // Restart the game on space key press
                startGame();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
}
