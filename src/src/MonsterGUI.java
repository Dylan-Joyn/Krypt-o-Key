package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MonsterGUI extends JFrame {

    public static void welcomeScreen() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Welcome Screen");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            try {
                // Load the image directly without scaling
                ImageIcon welcomeIcon = new ImageIcon(MonsterGUI.class.getResource("/resources/welcomeGame.png"));

                // Set frame size to match image dimensions
                frame.setSize(welcomeIcon.getIconWidth(), welcomeIcon.getIconHeight());

                // Create main panel with the image as background
                JLabel background = new JLabel(welcomeIcon);
                background.setLayout(new BorderLayout());

                // Create invisible clickable area
                JPanel clickArea = new JPanel();
                clickArea.setOpaque(false);
                clickArea.setBounds(250, 420, 430, 150);
                clickArea.setCursor(new Cursor(Cursor.HAND_CURSOR));
                clickArea.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        frame.dispose();
                        System.out.println("Start Game clicked!");
                        // Add game logic
                    }
                });

                // Use layered pane for proper z-ordering
                JLayeredPane layeredPane = new JLayeredPane();
                layeredPane.setPreferredSize(new Dimension(
                        welcomeIcon.getIconWidth(),
                        welcomeIcon.getIconHeight()
                ));

                background.setBounds(0, 0, welcomeIcon.getIconWidth(), welcomeIcon.getIconHeight());
                layeredPane.add(background, JLayeredPane.DEFAULT_LAYER);
                layeredPane.add(clickArea, JLayeredPane.PALETTE_LAYER);

                frame.setContentPane(layeredPane);
                frame.setResizable(false); // Prevent window resizing
                frame.setLocationRelativeTo(null); // Center the window
                frame.setVisible(true);

            } catch (Exception e) {
                System.err.println("Error loading image: " + e.getMessage());
                frame.add(new JLabel("<html><h1>Welcome Screen</h1><p>Image failed to load</p></html>"));
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    public static void main(String[] args) {
        welcomeScreen();
    }
}