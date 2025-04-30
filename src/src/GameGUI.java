package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GameGUI {
    private JFrame frame;
    private Player player;
    private ImageIcon cinnaIcon;

    // Battle components
    private BattlePanel battlePanel;
    private JPanel mainContentPanel;
    private JLabel messageLabel;
    private boolean inBattle = false;

    // Tutorial animation variables
    private JLabel tutorialLabel;
    private String tutorialMessage = "Click the areas on the upper right to select your next move!";
    private String displayedTutorial = "";
    private int tutorialCharIndex = 0;
    private Timer tutorialTimer;

    // Monster names for generation
    private static final String[] MONSTER_NAMES = {
            "Goblin", "Orc", "Troll", "Skeleton", "Zombie", "Ghost",
            "Slime", "Bat", "Spider", "Wolf", "Bear", "Snake",
            "Dragon", "Demon", "Witch", "Warlock", "Banshee", "Ogre"
    };

    public GameGUI(Player player) {
        this.player = player;
        this.cinnaIcon = new ImageIcon(getClass().getResource("/resources/assets/cinnaSprite.png"));
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Fantasy Adventure");
        frame.setIconImage(cinnaIcon.getImage());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create layered pane
        JLayeredPane layeredPane = new JLayeredPane();

        // Load background image
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/resources/screenOverlay.png"));
        JLabel background = new JLabel(bgIcon);
        background.setBounds(0, 0, bgIcon.getIconWidth(), bgIcon.getIconHeight());
        layeredPane.add(background, JLayeredPane.DEFAULT_LAYER);

        // Create main content panel (where battle/game content will go)
        mainContentPanel = new JPanel();
        mainContentPanel.setOpaque(false);
        mainContentPanel.setLayout(new BorderLayout());
        // Position in the big blue rectangle area
        mainContentPanel.setBounds(430, 25, 780, 520);
        layeredPane.add(mainContentPanel, JLayeredPane.PALETTE_LAYER);

        // Create message label (for the bottom rectangle)
        messageLabel = new JLabel("Welcome to the game! Click the buttons to play.");
        messageLabel.setFont(getVT323Font(22f));
        messageLabel.setForeground(Color.decode("#345f92"));
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        // Position in the bottom rectangle area
        messageLabel.setBounds(430, 590, 780, 100);
        layeredPane.add(messageLabel, JLayeredPane.PALETTE_LAYER);

        // Create tutorial label
        tutorialLabel = new JLabel();
        tutorialLabel.setFont(getVT323Font(34f));
        tutorialLabel.setForeground(Color.decode("#345f92"));
        tutorialLabel.setBounds(130, 650, 1000, 40);
        layeredPane.add(tutorialLabel, JLayeredPane.PALETTE_LAYER);

        // Create transparent clickable areas
        createClickAreas(layeredPane);

        frame.setContentPane(layeredPane);
        frame.setSize(bgIcon.getIconWidth(), bgIcon.getIconHeight());
        frame.setLocationRelativeTo(null);

        // Start the typing animation
        startTutorialAnimation();
    }

    private void createClickAreas(JLayeredPane layeredPane) {
        // Fight area (100-330x, 25-100y)
        createClickArea(layeredPane, 100, 25, 230, 75, e -> {
            if (!inBattle) startBattle();
        });

        // Heal area (100-330x, 175-220y)
        createClickArea(layeredPane, 100, 175, 230, 45, e -> {
            if (!inBattle) usePotion();
        });

        // Stats area (100-330x, 325-410y)
        createClickArea(layeredPane, 100, 325, 230, 85, e -> {
            if (!inBattle) showStats();
        });

        // Quit area (100-330x, 450-540y)
        createClickArea(layeredPane, 100, 450, 230, 90, e -> {
            if (!inBattle) quitGame();
        });
    }

    private void createClickArea(JLayeredPane parent, int x, int y, int w, int h, ActionListener action) {
        JPanel area = new JPanel();
        area.setOpaque(false);
        area.setBounds(x, y, w, h);
        area.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        area.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
            }
        });
        parent.add(area, JLayeredPane.PALETTE_LAYER);
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
        // Generate a random monster
        Random rand = new Random();
        String monsterName = MONSTER_NAMES[rand.nextInt(MONSTER_NAMES.length)];

        // Create monster with appropriate difficulty
        int difficulty = Math.max(1, player.getLevel());
        Monster monster = new Monster(monsterName, difficulty);

        // Show battle message
        messageLabel.setText("A " + monster.getType().getDisplayName() + " " +
                monster.getName() + " appears! Get ready to battle!");

        // Create battle panel
        battlePanel = new BattlePanel(player, monster, new BattlePanel.BattleListener() {
            @Override
            public void onBattleEnd(boolean victory) {
                endBattle(victory);
            }

            @Override
            public void showMessage(String message) {
                messageLabel.setText(message);
            }
        });

        // Add battle panel to content area
        mainContentPanel.removeAll();
        mainContentPanel.add(battlePanel, BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();

        // Set battle mode
        inBattle = true;
    }

    private void endBattle(boolean victory) {
        // Remove battle panel
        mainContentPanel.removeAll();
        mainContentPanel.revalidate();
        mainContentPanel.repaint();

        // Show appropriate message
        if (victory) {
            messageLabel.setText("You won the battle! Click FIGHT to battle again!");
        } else {
            if (player.isDefeated()) {
                messageLabel.setText("Game Over! You were defeated!");
            } else {
                messageLabel.setText("You fled from battle! Click FIGHT to try again!");
            }
        }

        // End battle mode
        inBattle = false;
    }

    private void usePotion() {
        if (player.getPotionCount() > 0) {
            player.usePotion();
            messageLabel.setText("Used a potion! Health restored to full.");
        } else {
            messageLabel.setText("No potions left!");
        }
    }

    private void showStats() {
        JOptionPane.showMessageDialog(frame,
                "Player Stats:\n" +
                        "Name: " + player.getName() + "\n" +
                        "Level: " + player.getLevel() + "\n" +
                        "HP: " + player.getHealth() + "/" + player.getMaxHealth() + "\n" +
                        "XP: " + player.getExperience() + "/" + player.getExperienceToNextLevel() + "\n" +
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