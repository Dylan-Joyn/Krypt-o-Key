package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Panel for battle interface that integrates with the main game GUI
 */
public class BattlePanel extends JPanel {
    private Player player;
    private Monster monster;
    private JLabel monsterImageLabel;
    private JProgressBar monsterHealthBar;
    private JProgressBar playerHealthBar;
    private JLabel challengeLabel;
    private JTextField typingField;
    private JLabel timerLabel;
    private JButton attackButton;
    private JButton potionButton;
    private JButton fleeButton;
    private Timer typingTimer;
    private int timeRemaining;
    private String typingChallenge;

    // For damage calculation
    private TimedTyping typingHandler;
    private DamagePerCharacter damageCalculator;

    // Listener for battle events
    private BattleListener battleListener;

    /**
     * Interface for battle callbacks
     */
    public interface BattleListener {
        void onBattleEnd(boolean victory);
        void showMessage(String message);
    }

    /**
     * Constructor
     */
    public BattlePanel(Player player, Monster monster, BattleListener listener) {
        this.player = player;
        this.monster = monster;
        this.battleListener = listener;
        this.typingHandler = new TimedTyping();
        this.damageCalculator = new DamagePerCharacter();

        setLayout(new BorderLayout(5, 5));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 10));

        setupUI();
        //generateTypingChallenge();
    }

    /**
     * Set up the UI
     */
    private void setupUI() {
        // Top panel for monster
        JPanel monsterPanel = createMonsterPanel();

        // Center panel for typing challenge and input
        JPanel typingPanel = createTypingPanel();

        // Left panel for player stats
        JPanel playerPanel = createPlayerPanel();

        // Bottom panel for buttons
        JPanel buttonPanel = createButtonPanel();

        // Add panels to main layout
        add(monsterPanel, BorderLayout.NORTH);
        add(typingPanel, BorderLayout.CENTER);
        add(playerPanel, BorderLayout.WEST);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Create monster panel
     */
    private JPanel createMonsterPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setOpaque(false);

        // Monster name label
        JLabel nameLabel = new JLabel(monster.getType().getDisplayName() + " " + monster.getName());
        nameLabel.setFont(getPixelFont(24));
        nameLabel.setHorizontalAlignment(JLabel.CENTER);

        // Monster image - use our loader
        ImageIcon monsterImg = MonsterImageLoader.getMonsterImage(monster.getName(), monster.getType());
        monsterImageLabel = new JLabel(monsterImg);
        monsterImageLabel.setHorizontalAlignment(JLabel.CENTER);

        // Monster health bar
        monsterHealthBar = new JProgressBar(0, monster.getMaxHealth());
        monsterHealthBar.setValue(monster.getHealth());
        monsterHealthBar.setStringPainted(true);
        monsterHealthBar.setString("HP: " + monster.getHealth() + "/" + monster.getMaxHealth());
        monsterHealthBar.setForeground(getMonsterColor(monster.getType()));

        // Add components
        panel.add(nameLabel, BorderLayout.NORTH);
        panel.add(monsterImageLabel, BorderLayout.CENTER);
        panel.add(monsterHealthBar, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Create player stats panel
     */
    private JPanel createPlayerPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 10));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(150, 0));

        // Player name
        JLabel nameLabel = new JLabel(player.getName());
        nameLabel.setFont(getPixelFont(24));
        nameLabel.setHorizontalAlignment(JLabel.CENTER);

        // Player level
        JLabel levelLabel = new JLabel("Level: " + player.getLevel());
        levelLabel.setFont(getPixelFont(14));
        levelLabel.setHorizontalAlignment(JLabel.CENTER);

        // Player health bar
        playerHealthBar = new JProgressBar(0, player.getMaxHealth());
        playerHealthBar.setValue(player.getHealth());
        playerHealthBar.setStringPainted(true);
        playerHealthBar.setString("HP: " + player.getHealth() + "/" + player.getMaxHealth());
        playerHealthBar.setForeground(new Color(0, 200, 0));

        // Potions
        JLabel potionLabel = new JLabel("Potions: " + player.getPotionCount());
        potionLabel.setFont(getPixelFont(14));
        potionLabel.setHorizontalAlignment(JLabel.CENTER);

        // Add components
        panel.add(nameLabel);
        panel.add(levelLabel);
        panel.add(playerHealthBar);
        panel.add(potionLabel);

        return panel;
    }

    /**
     * Create typing challenge panel
     */
    private JPanel createTypingPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 10));
        panel.setOpaque(false);

        // Challenge label
        challengeLabel = new JLabel(monster.getName() + ": " + monster.getBattleCry());
        challengeLabel.setFont(getPixelFont(30));
        challengeLabel.setHorizontalAlignment(JLabel.CENTER);
        challengeLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Timer label
        timerLabel = new JLabel("Time: 0");
        timerLabel.setFont(getPixelFont(18));
        timerLabel.setHorizontalAlignment(JLabel.CENTER);

        // Typing field
        typingField = new JTextField();
        typingField.setFont(getPixelFont(18));
        typingField.addActionListener(e -> submitTyping());

        // Top panel for challenge and timer
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(challengeLabel, BorderLayout.CENTER);
        topPanel.add(timerLabel, BorderLayout.SOUTH);

        // Add components
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(typingField, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Create button panel
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 0));
        panel.setOpaque(false);

        // Icon for buttons
        ImageIcon icon = new ImageIcon("resources/assets/battleButton.png");

        // Attack button
        attackButton = new JButton(icon);
        attackButton.setText("Attack!");
        attackButton.setFont(getPixelFont(18));
        attackButton.addActionListener(e -> generateTypingChallenge()); //submitTyping());

        // Potion button
        potionButton = new JButton(icon);
        potionButton.setText("Use Potion");
        potionButton.setFont(getPixelFont(18));
        potionButton.addActionListener(e -> usePotion());

        // Flee button
        fleeButton = new JButton(icon);
        fleeButton.setText("Flee Battle");
        fleeButton.setFont(getPixelFont(18));
        fleeButton.addActionListener(e -> fleeBattle());

        // Add buttons
        panel.add(attackButton);
        panel.add(potionButton);
        panel.add(fleeButton);

        return panel;
    }

    /**
     * Generate a typing challenge based on monster difficulty
     */
    public void generateTypingChallenge() {
        // Enable typing field if disabled
        typingField.setEnabled(true);

        // Generate challenge
        typingChallenge = typingHandler.getChallenge(monster.getDifficulty());
        challengeLabel.setText("Enter text:  " + typingChallenge);
        if(typingChallenge.length() >= 30) {
            challengeLabel.setFont(getPixelFont(24));
        } else {
            challengeLabel.setFont(getPixelFont(30));
        }

        // Clear typing field
        typingField.setText("");
        typingField.requestFocusInWindow();

        // Set timer (roughly 1 second per 4 characters, minimum 3 seconds)
        timeRemaining = Math.max(typingChallenge.length() / 4, 3);
        timerLabel.setText("Time: " + timeRemaining);

        // Start timer
        startTimer();
    }

    /**
     * Start the timer
     */
    private void startTimer() {
        // Stop existing timer if running
        if (typingTimer != null && typingTimer.isRunning()) {
            typingTimer.stop();
        }

        // Create new timer
        typingTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeRemaining--;
                timerLabel.setText("Time: " + timeRemaining);

                if (timeRemaining <= 0) {
                    typingTimer.stop();
                    handleTimeOut();
                }
            }
        });

        typingTimer.start();
    }

    /**
     * Handle time out
     */
    private void handleTimeOut() {
        // Display message
        showMessage("Time's up! The monster attacks!");

        // Disable typing field temporarily
        typingField.setEnabled(false);

        // Clear challenge text
        challengeLabel.setText(". . .");

        // Monster attacks after delay
        Timer attackTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                monsterAttack();
            }
        });

        attackTimer.setRepeats(false);
        attackTimer.start();
    }

    /**
     * Submit typing and process result
     */
    private void submitTyping() {
        // Stop timer
        if (typingTimer != null && typingTimer.isRunning()) {
            typingTimer.stop();
        }

        // Get input
        String userInput = typingField.getText();

        // Disable typing field temporarily
        typingField.setEnabled(false);

        // Clear challenge text
        challengeLabel.setText(". . .");

        // Calculate accuracy
        double accuracy = calculateAccuracy(userInput, typingChallenge);

        // Create typing result
        TimedTyping.TypingResult typingResult = new TimedTyping.TypingResult(accuracy, userInput, false);

        // Calculate damage
        DamagePerCharacter.DamageResult damageResult = damageCalculator.calculateDamage(typingResult);

        // Apply player's level multiplier
        int finalDamage = (int)(damageResult.getDamage() * (player.getDamageMultiplier() / 100.0));

        // Apply damage to monster
        int actualDamage = monster.takeDamage(finalDamage);

        // Update monster health bar
        monsterHealthBar.setValue(monster.getHealth());
        monsterHealthBar.setString("HP: " + monster.getHealth() + "/" + monster.getMaxHealth());

        // Show result message
        String hitType = damageResult.getHitType().toString();
        showMessage(hitType + "! You dealt " + actualDamage + " damage with " +
                String.format("%.1f", accuracy) + "% accuracy!");

        // Make monster flash
        flashMonster();

        // Check if monster is defeated
        if (monster.isDefeated()) {
            handleVictory();
        } else {
            // Monster attacks after delay
            Timer attackTimer = new Timer(1500, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    monsterAttack();
                }
            });

            attackTimer.setRepeats(false);
            attackTimer.start();
        }
    }

    /**
     * Monster attacks player
     */
    private void monsterAttack() {
        // Calculate damage
        int damage = monster.getAttackDamage();
        int actualDamage = player.takeDamage(damage);

        // Update player health bar
        playerHealthBar.setValue(player.getHealth());
        playerHealthBar.setString("HP: " + player.getHealth() + "/" + player.getMaxHealth());

        // Show message
        showMessage("The " + monster.getName() + " attacks you for " + actualDamage + " damage!");

        // Check if player is defeated
        if (player.isDefeated()) {
            handleDefeat();
        }
    }

    /**
     * Use a potion
     */
    private void usePotion() {
        if (player.getPotionCount() <= 0) {
            showMessage("You don't have any potions left!");
            return;
        }

        // Use potion
        int healAmount = player.usePotion();

        // Update player health bar
        playerHealthBar.setValue(player.getHealth());
        playerHealthBar.setString("HP: " + player.getHealth() + "/" + player.getMaxHealth());

        // Show message
        showMessage("You used a potion and restored " + healAmount + " health!");

        // Monster attacks after delay
        Timer attackTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                monsterAttack();

                // Check if player is defeated
                if (!player.isDefeated()) {
                    // Generate new challenge
                    generateTypingChallenge();
                }
            }
        });
    }

    /**
     * Roll to try and flee
     */
    private void fleeBattle() {
        int DC = (int) (((double) player.getExperience() / (player.getExperience() +
                        player.getHealth())) + player.getLevel() - 0.5) + 7;
        Timer delay2 = new Timer(1000, e -> monsterAttack());
        Timer delay = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (UtilFunc.rollD20(DC)) {
                    endBattle(false);
                } else {
                    showMessage("The " + monster.getName() + " stops you from fleeing!");
                    delay2.setRepeats(false);
                    delay2.start();
                }
            }
        });

        if (JOptionPane.showConfirmDialog(this,
                "Roll to flee? DC is: " + DC + "/20",
                "Flee Battle",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            showMessage("Rolling to flee...");
            delay.setRepeats(false);
            delay.start();
        }
    }

    /**
     * Handle player victory
     */
    private void handleVictory() {
        // Stop any timers
        if (typingTimer != null && typingTimer.isRunning()) {
            typingTimer.stop();
        }

        // Show victory message
        showMessage("Victory! You defeated the " + monster.getName() + "!");

        // Award experience
        int expGained = monster.getExpYield();
        String levelUpMessage = player.addExperience(expGained);

        // Check for potion drop
        boolean droppedPotion = monster.dropsPotion();
        if (droppedPotion) {
            player.addPotion();
        }

        // Create victory message
        StringBuilder message = new StringBuilder();
        message.append("Victory! You defeated the ").append(monster.getName()).append("!\n");
        message.append("You gained ").append(expGained).append(" experience points!\n");
        if (!levelUpMessage.isEmpty()) {
            message.append(levelUpMessage).append("\n");
        }
        if (droppedPotion) {
            message.append("The monster dropped a potion!");
        }

        // Show victory message after delay
        Timer victoryTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(BattlePanel.this,
                        message.toString(),
                        "Victory!",
                        JOptionPane.INFORMATION_MESSAGE);

                // End battle
                endBattle(true);
            }
        });

        victoryTimer.setRepeats(false);
        victoryTimer.start();
    }

    /**
     * Handle player defeat
     */
    private void handleDefeat() {
        // Stop any timers
        if (typingTimer != null && typingTimer.isRunning()) {
            typingTimer.stop();
        }

        // Show defeat message
        showMessage("Defeat! You were defeated by the " + monster.getName() + "!");

        // Calculate total xp for final score
        int deathScore = 0;
        for(int i = player.getLevel(); i > 1; i--) {
            deathScore += i * i * 20;
        }
        deathScore += player.getExperience();

        // Show defeat message after delay
        int finalDeathScore = deathScore;
        Timer defeatTimer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(BattlePanel.this,
                        "You were defeated by the " + monster.getName() + "!\nGame Over\n\n"
                        + "Final Score:\n" + finalDeathScore,
                        "Defeat!",
                        JOptionPane.ERROR_MESSAGE);

                // End battle
                endBattle(false);
            }
        });

        defeatTimer.setRepeats(false);
        defeatTimer.start();
    }

    /**
     * End the battle
     */
    private void endBattle(boolean victory) {
        cleanup();
        battleListener.onBattleEnd(victory);
    }

    /**
     * Make monster flash when damaged
     */
    private void flashMonster() {
        Timer flashTimer = new Timer(100, null);
        final int[] count = {0};
        final Color originalBg = monsterImageLabel.getBackground();

        flashTimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (count[0] % 2 == 0) {
                    monsterImageLabel.setOpaque(true);
                    monsterImageLabel.setBackground(Color.RED);
                } else {
                    monsterImageLabel.setOpaque(false);
                    monsterImageLabel.setBackground(originalBg);
                }

                count[0]++;
                if (count[0] >= 6) {
                    flashTimer.stop();
                    monsterImageLabel.setOpaque(false);
                }
            }
        });

        flashTimer.start();
    }

    /**
     * Display a message in the bottom area
     */
    public void showMessage(String message) {
        battleListener.showMessage(message);
    }

    /**
     * Calculate typing accuracy
     */
    private double calculateAccuracy(String input, String target) {
        int editDistance = UtilFunc.editDistance(input, target);
        int maxLength = Math.max(input.length(), target.length());
        if (maxLength == 0) return 0;

        return (1.0 - (double)editDistance / maxLength) * 100.0;
    }

    /**
     * Clean up resources
     */
    public void cleanup() {
        if (typingTimer != null && typingTimer.isRunning()) {
            typingTimer.stop();
        }

        if (typingHandler != null) {
            typingHandler.close();
        }
    }

    /**
     * Get color based on monster type
     */
    private Color getMonsterColor(Monster.MonsterType type) {
        switch (type) {
            case GREEN:
                return new Color(50, 200, 50);  // Green
            case YELLOW:
                return new Color(200, 200, 50); // Yellow
            case RED:
                return new Color(200, 50, 50);  // Red
            case PURPLE:
                return new Color(150, 50, 200); // Purple
            default:
                return Color.GRAY;
        }
    }

    /**
     * Get pixel-style font
     */
    private Font getPixelFont(float size) {
        try {
            return FontLoader.getVT323(size);
        } catch (Exception e) {
            return new Font("Monospaced", Font.PLAIN, (int)size);
        }
    }
}