package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameGUI {
    // Main components
    private JFrame frame;
    private Player player;

    // Tutorial animation
    private JLabel tutorialLabel;
    private String tutorialMessage = "Click the buttons on the upper right to select your next move!";
    private String displayedTutorial = "";
    private int tutorialCharIndex = 0;
    private Timer tutorialTimer;

    // Button dimensions
    private static final int BUTTON_WIDTH = 230;
    private static final int BUTTON_X = 100;
    private static final int[] BUTTON_Y = {25, 175, 325, 450};
    private static final int[] BUTTON_HEIGHTS = {75, 45, 85, 90};
    private static final String[] BUTTON_TEXTS = {"FIGHT", "HEAL", "STATS", "QUIT"};

    public GameGUI(Player player) {
        this.player = player;
        this.frame = new JFrame("Fantasy Adventure");
        initialize();
    }

    private void initialize() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Load background
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/resources/screenOverlay.png"));
        JLabel background = new JLabel(bgIcon);
        background.setLayout(null);

        // Create layered pane
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(bgIcon.getIconWidth(), bgIcon.getIconHeight()));

        // Create tutorial text label
        tutorialLabel = new JLabel();
        tutorialLabel.setFont(getVT323Font(24f));
        tutorialLabel.setForeground(Color.WHITE);
        tutorialLabel.setBounds(200, 630, 800, 40);

        // Create action buttons
        createActionButtons(layeredPane);

        // Add components
        background.setBounds(0, 0, bgIcon.getIconWidth(), bgIcon.getIconHeight());
        layeredPane.add(background, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(tutorialLabel, JLayeredPane.PALETTE_LAYER);

        frame.setContentPane(layeredPane);
        frame.pack();
        frame.setLocationRelativeTo(null);

        // Start tutorial animation
        startTutorialAnimation();
    }

    private void createActionButtons(JLayeredPane layeredPane) {
        for (int i = 0; i < BUTTON_TEXTS.length; i++) {
            JButton button = createButton(
                    BUTTON_TEXTS[i],
                    BUTTON_X,
                    BUTTON_Y[i],
                    BUTTON_WIDTH,
                    BUTTON_HEIGHTS[i]
            );

            // Add appropriate action listeners
            switch (i) {
                case 0: button.addActionListener(e -> startBattle()); break;
                case 1: button.addActionListener(e -> usePotion()); break;
                case 2: button.addActionListener(e -> showStats()); break;
                case 3: button.addActionListener(e -> quitGame()); break;
            }

            layeredPane.add(button, JLayeredPane.PALETTE_LAYER);
        }
    }

    private JButton createButton(String text, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(getVT323Font(20f));
        button.setForeground(Color.BLACK);
        button.setBackground(new Color(255, 215, 0));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        button.setBounds(x, y, width, height);
        return button;
    }

    private void startTutorialAnimation() {
        tutorialTimer = new Timer(50, e -> {
            if (tutorialCharIndex < tutorialMessage.length()) {
                displayedTutorial = tutorialMessage.substring(0, tutorialCharIndex + 1);
                tutorialLabel.setText(displayedTutorial);
                tutorialCharIndex++;
            } else {
                ((Timer)e.getSource()).stop();
            }
        });
        tutorialTimer.start();
    }

    // Button action methods
    private void startBattle() {
        // Implement battle logic
        JOptionPane.showMessageDialog(frame, "Battle started!");
    }

    private void usePotion() {
        if (player.getPotionCount() > 0) {
            player.usePotion();
            JOptionPane.showMessageDialog(frame, "Used a potion!");
        } else {
            JOptionPane.showMessageDialog(frame, "No potions left!");
        }
    }

    private void showStats() {
        JOptionPane.showMessageDialog(frame,
                "Player Stats:\n" +
                        "Name: " + player.getName() + "\n" +
                        "Level: " + player.getLevel() + "\n" +
                        "HP: " + player.getHealth() + "/" + player.getMaxHealth() + "\n" +
                        "Potions: " + player.getPotionCount());
    }

    private void quitGame() {
        if (JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to quit?",
                "Quit Game",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private Font getVT323Font(float size) {
        try {
            return Font.createFont(Font.TRUETYPE_FONT,
                            getClass().getResourceAsStream("/resources/fonts/VT323-Regular.ttf"))
                    .deriveFont(size);
        } catch (Exception e) {
            return new Font("Monospaced", Font.PLAIN, (int)size);
        }
    }

    public void show() {
        frame.setVisible(true);
    }
}