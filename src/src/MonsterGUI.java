package src;

import javax.swing.*;
import java.awt.*;

public class MonsterGUI extends JFrame {

    public static void launchWelcomeScreen() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Welcome Screen");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(940, 788);

            try {
                // Proper way to load image from resources
                ImageIcon icon = new ImageIcon(MonsterGUI.class.getResource("/resources/welcome.png"));
                JLabel background = new JLabel(icon);
                background.setLayout(new BorderLayout());

                // Add a start button
                JButton startButton = new JButton("Start Game");
                startButton.addActionListener(e -> {
                    frame.dispose();
                    System.out.println("Game starting...");
                    // Add your game initialization here
                });

                JPanel buttonPanel = new JPanel();
                buttonPanel.setOpaque(false); // Make panel transparent
                buttonPanel.add(startButton);

                background.add(buttonPanel, BorderLayout.SOUTH);
                frame.setContentPane(background);
            } catch (Exception e) {
                System.err.println("Error loading image: " + e.getMessage());
                frame.add(new JLabel("<html><h1>Welcome Screen</h1><p>Image failed to load</p></html>"));
            }

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    public static void main(String[] args) {
        launchWelcomeScreen();
    }
}