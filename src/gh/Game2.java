package gh;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.io.*;
import javax.sound.sampled.*;

public class Game2 extends JFrame {
    private Board board;
    private JPanel leftPanel;
    private JPanel rightPanel;

    public Game2() {
        setTitle("Tetris");

        // Main panel without background
        setLayout(new BorderLayout());

        // Board (center)
        board = new Board(this); // Pass the JFrame instance to the Board
        board.setOpaque(true); // Set opaque to true for the board

        // Left side panel
        leftPanel = new JPanel();
        leftPanel.setBackground(new Color(50, 50, 50)); // Dark gray background
        leftPanel.setPreferredSize(new Dimension(100, 600)); // Width of 100, height matches game

        // Right side panel
        rightPanel = new JPanel();
        rightPanel.setBackground(new Color(50, 50, 50)); // Dark gray background
        rightPanel.setPreferredSize(new Dimension(100, 600));

        // Adding "Next" label to the right panel
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        JLabel scoreLabel = new JLabel("Next");
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add labels to the right panel with spacing
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacing at the top
        rightPanel.add(scoreLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Additional spacing

        // Add the panels to the main layout
        add(leftPanel, BorderLayout.WEST);
        add(board, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        // Frame settings
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600); // Total width includes side panels
        setResizable(false);
        setVisible(true);
        setLocationRelativeTo(null);

        // Repaint to ensure the board is rendered
        board.repaint();

        board.startGame();
    }
}


class Board extends JPanel implements ActionListener {
    private final int BoardWidth = 10;
    private final int BoardHeight = 20;
    private Timer timer;
    private boolean isFallingFinished = false;
    private boolean isStarted = false;
    private boolean isPaused = false;
    private int currentX = 0;
    private int currentY = 0;
    private Tetrominoes[] board;
    private Shape currentPiece;
    private final int InitialDelay = 400;
    private int currentRotation = 0; // Added to track the current rotation
    private int elapsedTime = 0; // Timer variable
    private Timer timerLabelTimer; // Timer for updating the label
    private Clip clip;
    private int score;

    public Board(JFrame frame) {
        setFocusable(true);
        timer = new Timer(InitialDelay, this);
        board = new Tetrominoes[BoardWidth * BoardHeight];
        addKeyListener(new TAdapter());
        clearBoard();
        score = 0;

        // Timer for updating the elapsed time label
        timerLabelTimer = new Timer(1000, e -> {
            elapsedTime++;
            repaint(); // Trigger a repaint to update the timer display
        });
        timerLabelTimer.start(); // Start the timer
    }

    private void playBackgroundMusic() {
        try {
            File musicPath = new File("music.wav"); // Replace with your .wav file name
            if (musicPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.loop(Clip.LOOP_CONTINUOUSLY); // Loop the music continuously
            } else {
                System.out.println("Music file not found.");
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    
    private void stopBackgroundMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
    public void startGame() {
    	elapsedTime = 0;
    	score = 0;
    	timerLabelTimer.start(); // Start the timer
        isStarted = true;
        isPaused = false; // Reset pause state if needed
        isFallingFinished = false;
        clearBoard();
        newPiece();
        
        timer.start(); 
        
        playBackgroundMusic();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isFallingFinished) {
            isFallingFinished = false;
            newPiece();
        } else {
            movePieceDown();
        }
    }

  
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawCheckerboard(g); // Draw the checkered background
        drawBoard(g); // Draw the Tetris board and pieces

        // Draw the timer
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Time: " + formatTime(elapsedTime), 10, 30); // Positioning the timer at the top left
   
        g.drawString("Score: " + score, 10, 60); // Display the score
    }

    private void clearBoard() {
        for (int i = 0; i < BoardWidth * BoardHeight; i++) {
            board[i] = Tetrominoes.NoShape;
        }
    }

    private void newPiece() {
        currentPiece = new Shape();
        currentPiece.setRandomShape();
        currentX = BoardWidth / 2 - 1;
        currentY = BoardHeight - 1; // Start at the bottom

        if (!tryMove(currentPiece, currentX, currentY - 1)) {
            currentPiece.setShape(Tetrominoes.NoShape);
            gameOver(); // Call game over method
            
        }
    }
    
    private String formatTime(int elapsedSeconds) {
        if (elapsedSeconds < 60) {
            return elapsedSeconds + "s"; // Display in seconds under 60 seconds
        } else {
            int minutes = elapsedSeconds / 60;
            int seconds = elapsedSeconds % 60;
            return String.format("%d:%02d", minutes, seconds); // Display in mm:ss format
        }
    }

    private void gameOver() {
    	timer.stop(); // Stop the game timer
        timerLabelTimer.stop(); // Stop the elapsed time timer
        stopBackgroundMusic();
        
        String scoreDisplay = "Your Score: " + score;
        
        // Show a dialog asking the player if they want to play again
        int choice = JOptionPane.showOptionDialog(
            SwingUtilities.getWindowAncestor(this), 
            "Game Over!\n" +scoreDisplay, 
            "Game Over",
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE, 
            null, 
            new String[]{"Play Again", "Exit"}, 
            "Play Again"
        );

        // Check the player's choice
        if (choice == JOptionPane.YES_OPTION) {
            startGame(); // Call startGame() to reset and start a new game
        } else {
            new GameHub(); // Open GameHub window
        }
    }


    // Modify the movePieceDown method to adjust for the ending condition
    private void movePieceDown() {
        if (!tryMove(currentPiece, currentX, currentY - 1)) {
            pieceDropped();
        }
    }

    private void pieceDropped() {
        for (int i = 0; i < 4; i++) {
            int x = currentX + currentPiece.x(i);
            int y = currentY - currentPiece.y(i);
            board[(y * BoardWidth) + x] = currentPiece.getShape();
        }

        removeFullLines();

        if (!isFallingFinished) {
            newPiece();
        }
    }

    private void removeFullLines() {   
    	
    	int linesRemoved = 0;

        for (int i = BoardHeight - 1; i >= 0; i--) {
            boolean lineIsFull = true;

            for (int j = 0; j < BoardWidth; j++) {
                if (shapeAt(j, i) == Tetrominoes.NoShape) {
                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {
                linesRemoved++;

                int finalI = i; // Capture the current line index for use in lambda
                fadeOutLine(finalI); // Initiate fade-out effect

                // Delay shifting down lines until fade-out is complete
                Timer timer = new Timer(500, e -> {
                    // Shift down lines above the removed line
                    for (int k = finalI; k < BoardHeight - 1; k++) {
                        for (int j = 0; j < BoardWidth; j++) {
                            board[(k * BoardWidth) + j] = shapeAt(j, k + 1);
                        }
                    }
                    ((Timer) e.getSource()).stop(); // Stop this timer once shift is complete
                });
                timer.setRepeats(false);
                timer.start();
            }
        }

        if (linesRemoved > 0) {
            score += calculateScore(linesRemoved); // Calculate score based on the number of lines removed
        }
    }

    private void fadeOutLine(int lineIndex) {
        // Implement your fading effect here
        // You can use a separate timer or a thread to animate the fading effect over a period of time
        // For simplicity, we’ll just call repaint() to refresh the UI
        for (int j = 0; j < BoardWidth; j++) {
            board[(lineIndex * BoardWidth) + j] = Tetrominoes.NoShape; // Remove the line immediately
        }
        repaint(); // Refresh the UI to show the effect
    }


    private int calculateScore(int linesRemoved) {
        switch (linesRemoved) {
            case 1: return 100; // Single line cleared
            case 2: return 300; // Double line cleared
            case 3: return 500; // Triple line cleared
            case 4: return 800; // Tetris (four lines cleared)
            default: return 0;
        }
    }
    
    private Tetrominoes shapeAt(int x, int y) {
        return board[(y * BoardWidth) + x];
    }

    private boolean tryMove(Shape newPiece, int newX, int newY) {
        for (int i = 0; i < 4; i++) {
            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);
            
            if (x < 0 || x >= BoardWidth || y < 0 || y >= BoardHeight) {
                return false; // Out of bounds
            }
            if (shapeAt(x, y) != Tetrominoes.NoShape) {
                return false; // Collision with existing shapes
            }
        }

        currentPiece = newPiece;
        currentX = newX;
        currentY = newY;
        repaint();
        return true;
    }

    private void drawCheckerboard(Graphics g) {
        // Define tile size for the checkerboard
        final int TILE_SIZE = squareWidth(); // Assuming squareWidth corresponds to the checkered tile size

        // Dark gray checkerboard background
        for (int i = 0; i < getWidth(); i += TILE_SIZE) {
            for (int j = 0; j < getHeight(); j += TILE_SIZE) {
                if ((i + j) % (TILE_SIZE * 2) == 0) {
                    g.setColor(new Color(60, 60, 60)); // Dark gray
                } else {
                    g.setColor(new Color(70, 70, 70)); // Slightly lighter gray
                }
                g.fillRect(i, j, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    private void drawBoard(Graphics g) {
        Dimension size = getSize();
        int boardTop = (int) size.getHeight() - BoardHeight * squareHeight();

        for (int i = 0; i < BoardHeight; i++) {
            for (int j = 0; j < BoardWidth; j++) {
                Tetrominoes shape = shapeAt(j, BoardHeight - i - 1);
                if (shape != Tetrominoes.NoShape) {
                    drawSquare(g, j * squareWidth(), boardTop + i * squareHeight(), shape);
                }
            }
        }

        if (currentPiece.getShape() != Tetrominoes.NoShape) {
            for (int i = 0; i < 4; i++) {
                int x = currentX + currentPiece.x(i);
                int y = currentY - currentPiece.y(i);
                drawSquare(g, x * squareWidth(), boardTop + (BoardHeight - y - 1) * squareHeight(), currentPiece.getShape());
            }
        }
    }

    private int squareWidth() {
        return (int) getSize().getWidth() / BoardWidth;
    }

    private int squareHeight() {
        return (int) getSize().getHeight() / BoardHeight;
    }

    private void drawSquare(Graphics g, int x, int y, Tetrominoes shape) {
        Color colors[] = {
            new Color(0, 0, 0),         // NoShape (invisible)
            new Color(255, 0, 0),       // ZShape (Red)
            new Color(0, 255, 0),       // SShape (Green)
            new Color(0, 255, 255),     // LineShape (Cyan)
            new Color(255, 255, 0),     // SquareShape (Yellow)
            new Color(255, 140, 0),      // LShape (Orange)
            new Color(30, 144, 255),    // MirroredLShape (Lighter Blue)
            new Color(186, 85, 211)     // TShape (Lighter Purple)
        };

        Color color = colors[shape.ordinal()];
        
        // Create a gradient effect
        GradientPaint gradient = new GradientPaint(x, y, color.brighter(), x, y + squareHeight(), color.darker());
        ((Graphics2D) g).setPaint(gradient);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

        // Draw borders with lighter and darker shades for depth
        g.setColor(color.brighter().brighter()); // Light color for the top border
        g.drawLine(x + 1, y + 1, x + squareWidth() - 2, y + 1); // Top
        g.drawLine(x + 1, y + 1, x + 1, y + squareHeight() - 2); // Left

        g.setColor(color.darker()); // Darker color for the right and bottom borders
        g.drawLine(x + squareWidth() - 1, y + 1, x + squareWidth() - 1, y + squareHeight() - 2); // Right
        g.drawLine(x + 1, y + squareHeight() - 1, x + squareWidth() - 2, y + squareHeight() - 1); // Bottom
    }


    // Add the dropToBottom method
    public void dropToBottom() {
        while (!checkCollision(currentX, currentY - 1, currentRotation)) {
            currentY--; // Move down
        }
        pieceDropped(); // Drop the piece
    }

    private boolean checkCollision(int x, int y, int rotation) {
        // Check for collisions with the walls and other pieces
        for (int i = 0; i < 4; i++) {
            int newX = x + currentPiece.x(i);
            int newY = y - currentPiece.y(i);
            if (newX < 0 || newX >= BoardWidth || newY < 0 || newY >= BoardHeight) {
                return true;
            }
            if (shapeAt(newX, newY) != Tetrominoes.NoShape) {
                return true;
            }
        }
        return false;
    }

    class TAdapter extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            if (!isStarted || currentPiece.getShape() == Tetrominoes.NoShape) {
                return;
            }

            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (!checkCollision(currentX - 1, currentY, currentRotation)) {
                        currentX--;
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (!checkCollision(currentX + 1, currentY, currentRotation)) {
                        currentX++;
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    actionPerformed(null);
                    break;
                case KeyEvent.VK_UP:
                    rotateTile();
                    break;
                case KeyEvent.VK_SPACE:
                    dropToBottom(); // Automatic drop when space is pressed
                    break;
            }
            repaint();
        }
    }

    private void rotateTile() {
        Shape temp = currentPiece;
        currentPiece.rotate();
        if (checkCollision(currentX, currentY, currentRotation)) {
            currentPiece = temp; // Revert to original piece if rotation fails
        }
    }
}

enum Tetrominoes {
    NoShape, ZShape, SShape, LineShape, TShape, SquareShape, LShape, MirroredLShape
}

class Shape {
    private Tetrominoes pieceShape;
    private int[][] coords;
    private static final int[][][] coordsTable = {
    	    {{0, 0}, {0, 0}, {0, 0}, {0, 0}},         // NoShape
    	    {{0, -1}, {0, 0}, {1, 0}, {1, 1}},        // ZShape
    	    {{0, 1}, {0, 0}, {1, 0}, {1, -1}},        // SShape
    	    {{0, -1}, {0, 0}, {0, 1}, {0, 2}},        // LineShape
    	    {{0, 0}, {1, 0}, {0, 1}, {1, 1}},         // SquareShape
    	    {{-1, -1}, {0, -1}, {0, 0}, {0, 1}},      // LShape
    	    {{1, -1}, {0, -1}, {0, 0}, {0, 1}},       // MirroredLShape
    	    {{0, -1}, {0, 0}, {0, 1}, {1, 0}}         // TShape
    	};


    public Shape() {
        coords = new int[4][2];
        setShape(Tetrominoes.NoShape);
    }

    public void setShape(Tetrominoes shape) {
        pieceShape = shape;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; j++) {
                coords[i][j] = coordsTable[shape.ordinal()][i][j];
            }
        }
    }

    public void setRandomShape() {
        Random rand = new Random();
        pieceShape = Tetrominoes.values()[1 + rand.nextInt(7)];
        setShape(pieceShape);
    }

    public Tetrominoes getShape() {
        return pieceShape;
    }

    public int x(int index) {
        return coords[index][0];
    }

    public int y(int index) {
        return coords[index][1];
    }

    public void rotate() {
        int[][] newCoords = new int[4][2];
        for (int i = 0; i < 4; i++) {
            newCoords[i][0] = -coords[i][1];
            newCoords[i][1] = coords[i][0];
        }
        coords = newCoords;
    }
    
}

