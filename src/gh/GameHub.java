package gh;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

class GameHub extends JFrame {

    public GameHub() {
        setTitle("GameHub");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Main container panel with BorderLayout to add title on top
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Welcome text at the top
        JLabel welcomeLabel = new JLabel("Welcome to GameHUB", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.BLACK);  // Optional: Adjust color if needed
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);

        // Custom background panel with direct image name
        BackgroundPanel backgroundPanel = new BackgroundPanel("bg.png"); // No folder structure
        backgroundPanel.setLayout(new GridLayout(2, 3, 20, 20));
        mainPanel.add(backgroundPanel, BorderLayout.CENTER);

        // Manually create game panels with images and play buttons
        backgroundPanel.add(createGamePanel("game1.png", "Play", e -> openGameWindow(1)));
        backgroundPanel.add(createGamePanel("game2.png", "Play", e -> openGameWindow(2)));
        backgroundPanel.add(createGamePanel("game3.png", "Play", e -> openGameWindow(3)));
        backgroundPanel.add(createGamePanel("game4.png", "Play", e -> openGameWindow(4)));
        backgroundPanel.add(createGamePanel("game5.png", "Play", e -> openGameWindow(5)));
        backgroundPanel.add(createGamePanel("game6.png", "Play", e -> openGameWindow(6)));

        add(mainPanel);
        setVisible(true);
        setLocationRelativeTo(null);
    }

    private JPanel createGamePanel(String imagePath, String buttonText, ActionListener action) {
        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.Y_AXIS));
        gamePanel.setOpaque(false); // Make panel transparent for background

        // Spacer to create padding at the top
        gamePanel.add(Box.createVerticalStrut(15)); // Adjust this value as needed

        // Game Image
        JLabel gameImage = new JLabel();
        gameImage.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the image horizontally
        ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));
        Image scaledImage = icon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
        gameImage.setIcon(new ImageIcon(scaledImage));
        gamePanel.add(gameImage);

        // Spacer to separate the image and button
        gamePanel.add(Box.createVerticalStrut(10));

     // Play Button
        JButton playButton = new JButton(buttonText);
        playButton.setFont(new Font("Arial", Font.PLAIN, 18)); // Set font for button text
        playButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center button horizontally
        playButton.setBackground(new Color(255, 165, 0)); // Set button color to orange
        playButton.setForeground(Color.BLACK); // Set text color to black for contrast
        playButton.setFocusPainted(false); // Remove focus outline for a cleaner look

        // Add padding around text
        playButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createBevelBorder(1, Color.WHITE, Color.GRAY), // Bevel effect
            BorderFactory.createEmptyBorder(8, 25, 8, 25) // Padding
        ));

        // Add hover effect with mouse listener
        playButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                playButton.setBackground(new Color(255, 140, 0)); // Darker orange on hover
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                playButton.setBackground(new Color(255, 165, 0)); // Original color when not hovering
            }
        });

        playButton.addActionListener(action);
        gamePanel.add(playButton, BorderLayout.SOUTH);

        return gamePanel;


    }


    private void openGameWindow(int gameNumber) {
        switch (gameNumber) {
            case 1 -> {
                new Game1();
                dispose();
            }
            case 2 -> {
                new Game2();
                dispose(); 
            }
            case 3 -> {
                new Game3();
                dispose(); 
            }
            case 4 -> {
                new Game4();
                dispose(); 
            }
            case 5 -> {
                new Game5();
                dispose(); 
            }
            case 6 -> {
                new Game6();
                dispose(); 
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameHub::new);
    }
}

// Custom JPanel with a background image
class BackgroundPanel extends JPanel {
    private final Image backgroundImage;

    public BackgroundPanel(String imagePath) {
        backgroundImage = new ImageIcon(getClass().getResource(imagePath)).getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}


class Game4 extends JFrame {
    public Game4() {
        setTitle("Game 4");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        add(new JLabel("Welcome to Game 4!", SwingConstants.CENTER));
        setVisible(true);
    }
}

class Game5 extends JFrame {
    public Game5() {
        setTitle("Game 5");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        add(new JLabel("Welcome to Game 5!", SwingConstants.CENTER));
        setVisible(true);
    }
}

class Game6 extends JFrame {
    public Game6() {
        setTitle("Game 6");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        add(new JLabel("Welcome to Game 6!", SwingConstants.CENTER));
        setVisible(true);
    }
}
