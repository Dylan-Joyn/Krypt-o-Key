package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MonsterGUI extends JFrame {

    public static void launchWelcomeScreen() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Welcome Screen");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            //black boarder to surround extra space around image
            int borderSize = 50;
            int imgWidth = 940;
            int imgHeight = 788;
            frame.setSize(imgWidth + borderSize*2, imgHeight + borderSize*2);

            try {
                // Load and scale image (maintaining aspect ratio)
                ImageIcon originalIcon = new ImageIcon(MonsterGUI.class.getResource("/resources/welcome.png"));
                Image scaledImage = originalIcon.getImage().getScaledInstance(
                        imgWidth,
                        imgHeight,
                        Image.SCALE_SMOOTH
                );

                // Create main panel with black border
                JPanel mainPanel = new JPanel(new BorderLayout()) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        // Draw black border
                        g.setColor(Color.BLACK);
                        g.fillRect(0, 0, getWidth(), getHeight());
                        // Draw image centered
                        int x = (getWidth() - scaledImage.getWidth(null)) / 2;
                        int y = (getHeight() - scaledImage.getHeight(null)) / 2;
                        g.drawImage(scaledImage, x, y, this);
                    }
                };
                mainPanel.setPreferredSize(new Dimension(
                        imgWidth + borderSize*2,
                        imgHeight + borderSize*2
                ));

                // Create invisible clickable area
                JPanel clickArea = new JPanel();
                clickArea.setOpaque(false);
                clickArea.setBounds(250 + borderSize, 420 + borderSize, 430, 150);
                clickArea.setCursor(new Cursor(Cursor.HAND_CURSOR));
                clickArea.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        frame.dispose();
                        System.out.println("Start Game clicked!");

                    }
                });

                // Use layered pane for proper z-ordering
                JLayeredPane layeredPane = new JLayeredPane();
                layeredPane.setPreferredSize(mainPanel.getPreferredSize());

                mainPanel.setBounds(0, 0, layeredPane.getPreferredSize().width, layeredPane.getPreferredSize().height);
                layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);
                layeredPane.add(clickArea, JLayeredPane.PALETTE_LAYER);

                frame.setContentPane(layeredPane);

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