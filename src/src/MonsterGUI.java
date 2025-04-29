package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.Timer;

public class MonsterGUI {
    private static JFrame frame;
    private static JPanel blackOverlay;
    private static JLabel backgroundLabel;
    private static float currentAlpha = 0f;
    private static final int FADE_DURATION = 1000; // 1 second fade
    private static final int FPS = 60;


    // First frame - welcome in
    public static void welcomeScreen() {
        frame = new JFrame("Game Screen");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            ImageIcon welcomeIcon = new ImageIcon(MonsterGUI.class.getResource("/resources/welcomeGame.png"));
            backgroundLabel = new JLabel(welcomeIcon);

            // Black overlay for fading
            blackOverlay = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D)g.create();
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, currentAlpha));
                    g2d.setColor(Color.BLACK);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.dispose();
                }
            };
            blackOverlay.setOpaque(false);

            // Start game button
            JPanel clickArea = new JPanel();
            clickArea.setOpaque(false);
            clickArea.setBounds(250, 420, 430, 150);
            clickArea.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            clickArea.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    startTransition();
                }
            });

            JLayeredPane layeredPane = new JLayeredPane();
            layeredPane.setLayout(new OverlayLayout(layeredPane));
            layeredPane.add(backgroundLabel, Integer.valueOf(0));
            layeredPane.add(blackOverlay, Integer.valueOf(1));
            layeredPane.add(clickArea, Integer.valueOf(2));

            frame.setContentPane(layeredPane);
            frame.setSize(welcomeIcon.getIconWidth(), welcomeIcon.getIconHeight());
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading resources", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void startTransition() {
        frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        // Fade out timer
        Timer fadeOutTimer = new Timer(FADE_DURATION/FPS, null);
        fadeOutTimer.addActionListener(new ActionListener() {
            long startTime = -1;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (startTime < 0) {
                    startTime = System.currentTimeMillis();
                }

                long elapsed = System.currentTimeMillis() - startTime;
                currentAlpha = (float)elapsed / FADE_DURATION;

                if (currentAlpha >= 1.0f) {
                    currentAlpha = 1.0f;
                    fadeOutTimer.stop();
                    changeBackground();
                    fadeIn();
                }

                blackOverlay.repaint();
            }
        });
        fadeOutTimer.start();
    }

    private static void changeBackground() {
        try {
            ImageIcon newIcon = new ImageIcon(MonsterGUI.class.getResource("/resources/cinnarration.png"));
            backgroundLabel.setIcon(newIcon);
            frame.setSize(newIcon.getIconWidth(), newIcon.getIconHeight());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void fadeIn() {
        Timer fadeInTimer = new Timer(FADE_DURATION/FPS, null);
        fadeInTimer.addActionListener(new ActionListener() {
            long startTime = -1;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (startTime < 0) {
                    startTime = System.currentTimeMillis();
                }

                long elapsed = System.currentTimeMillis() - startTime;
                currentAlpha = 1.0f - ((float)elapsed / FADE_DURATION);

                if (currentAlpha <= 0.0f) {
                    currentAlpha = 0.0f;
                    fadeInTimer.stop();
                    frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }

                blackOverlay.repaint();
            }
        });
        fadeInTimer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> welcomeScreen());
    }
}