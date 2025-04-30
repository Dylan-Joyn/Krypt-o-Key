package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.Timer;

public class TitleSequence {
    private static JFrame frame;
    private static JPanel blackOverlay;
    private static JLabel backgroundLabel;
    private static float currentAlpha = 0f;
    private static final int FADE_DURATION = 1000; // 1 second fade
    private static final int FPS = 60;
    private static JPanel currentClickArea;

    public static void welcomeScreen() {
        frame = new JFrame("Game Screen");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            ImageIcon welcomeIcon = new ImageIcon(TitleSequence.class.getResource("/resources/welcomeGame.png"));
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

            // Start game button (only on welcome screen)
            currentClickArea = createWelcomeClickArea();

            JLayeredPane layeredPane = new JLayeredPane();
            layeredPane.setLayout(new OverlayLayout(layeredPane));
            layeredPane.add(backgroundLabel, Integer.valueOf(0));
            layeredPane.add(blackOverlay, Integer.valueOf(1));
            layeredPane.add(currentClickArea, Integer.valueOf(2));

            frame.setContentPane(layeredPane);
            frame.setSize(welcomeIcon.getIconWidth(), welcomeIcon.getIconHeight());
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading resources", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static JPanel createWelcomeClickArea() {
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
        return clickArea;
    }

    private static void startTransition() {
        frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        // Remove the welcome screen click area immediately
        if (currentClickArea != null) {
            ((JLayeredPane)frame.getContentPane()).remove(currentClickArea);
        }

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
                    transitionToNarrationScene();
                }

                blackOverlay.repaint();
            }
        });
        fadeOutTimer.start();
    }

    private static void transitionToNarrationScene() {
        // Dispose of the current frame
        frame.dispose();

        // Create and show the NarrationScene with cinnarration background
        JFrame narrationFrame = new JFrame("Cin-narrator");
        narrationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Load the background image
        ImageIcon narrationBg = new ImageIcon(TitleSequence.class.getResource("/resources/cinnarration.png"));

        // Create background panel
        JPanel backgroundPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(narrationBg.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setPreferredSize(
                new Dimension(narrationBg.getIconWidth(), narrationBg.getIconHeight()));

        // Create the narration scene
        NarrationScene narrationScene = new NarrationScene();
        narrationScene.setOpaque(false); // Make transparent to show background

        // Add components to layered pane
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(backgroundPanel.getPreferredSize());

        backgroundPanel.setBounds(0, 0,
                narrationBg.getIconWidth(), narrationBg.getIconHeight());
        narrationScene.setBounds(0, 0,
                narrationBg.getIconWidth(), narrationBg.getIconHeight());

        layeredPane.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(narrationScene, JLayeredPane.PALETTE_LAYER);

        narrationFrame.setContentPane(layeredPane);
        narrationFrame.pack();
        narrationFrame.setLocationRelativeTo(null);
        narrationFrame.setVisible(true);

        // Start with fade-in effect
        narrationScene.startFadeIn();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> welcomeScreen());
    }
}