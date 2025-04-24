package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
    public static void main(String[] args) {
        // Create player and initial monster
        Player player = new Player("Test Player");
        Monster monster = new Monster("Goblin", 3); // Level 3 monster

        // Create the GUI components
        MonsterGUI monsterGUI = new MonsterGUI(monster);
        JFrame playerFrame = createPlayerFrame(player);

        // Create battle logic components
        TimedTyping typingHandler = new TimedTyping();
        DamagePerCharacter damageCalculator = new DamagePerCharacter();

        // Create control panel
        JPanel controlPanel = createControlPanel(player, monster, monsterGUI, typingHandler, damageCalculator);

        // Add control panel to monster GUI
        monsterGUI.add(controlPanel, BorderLayout.EAST);
        monsterGUI.pack();
    }

    private static JFrame createPlayerFrame(Player player) {
        JFrame frame = new JFrame("Player Stats");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));

        JLabel nameLabel = new JLabel("Name: " + player.getName());
        JLabel levelLabel = new JLabel("Level: " + player.getLevel());
        JLabel healthLabel = new JLabel("Health: " + player.getHealth() + "/" + player.getMaxHealth());
        JLabel expLabel = new JLabel("EXP: " + player.getExperience() + "/" + player.getExperienceToNextLevel());
        JLabel potionsLabel = new JLabel("Potions: " + player.getPotionCount());

        panel.add(nameLabel);
        panel.add(levelLabel);
        panel.add(healthLabel);
        panel.add(expLabel);
        panel.add(potionsLabel);

        frame.add(panel);
        frame.setLocation(400, 0);
        frame.setVisible(true);

        return frame;
    }

    private static JPanel createControlPanel(Player player, Monster monster, MonsterGUI monsterGUI,
                                             TimedTyping typingHandler, DamagePerCharacter damageCalculator) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Attack button
        JButton attackButton = new JButton("Attack Monster");
        attackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get challenge based on monster difficulty
                String challenge = typingHandler.getChallenge(monster.getDifficulty());

                // Calculate time limit
                int timeLimit = Math.max(challenge.length() / 2, 5);

                // Run typing challenge
                TimedTyping.TypingResult result = typingHandler.processInputWithTimer(challenge, timeLimit);

                // Calculate damage
                DamagePerCharacter.DamageResult damageResult = damageCalculator.calculateDamage(result);
                int finalDamage = (int)(damageResult.getDamage() * (player.getDamageMultiplier() / 100.0));

                if (result.isTimedOut()) {
                    JOptionPane.showMessageDialog(monsterGUI, "You failed to cast your spell in time!");
                } else {
                    // Apply damage to monster
                    int actualDamage = monster.takeDamage(finalDamage);
                    monsterGUI.updateHealth(monster.getHealth());

                    // Show results
                    String message = String.format(
                            "Typing Accuracy: %.2f%%\n" +
                                    "You dealt %d damage!\n" +
                                    "Hit type: %s\n" +
                                    "%s HP: %d/%d",
                            result.getAccuracy(), actualDamage, damageResult.getHitType(),
                            monster.getName(), monster.getHealth(), monster.getMaxHealth()
                    );
                    JOptionPane.showMessageDialog(monsterGUI, message);

                    // Check if monster is defeated
                    if (monster.isDefeated()) {
                        JOptionPane.showMessageDialog(monsterGUI, "You defeated the " + monster.getName() + "!");
                    }
                }
            }
        });

        // Use Potion button
        JButton potionButton = new JButton("Use Potion (" + player.getPotionCount() + ")");
        potionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (player.getPotionCount() == 0) {
                    JOptionPane.showMessageDialog(monsterGUI, "You don't have any potions!");
                    return;
                }

                int healAmount = player.usePotion();
                if (healAmount > 0) {
                    JOptionPane.showMessageDialog(monsterGUI,
                            "You used a potion and restored " + healAmount + " health!\n" +
                                    "Your HP: " + player.getHealth() + "/" + player.getMaxHealth()
                    );
                    potionButton.setText("Use Potion (" + player.getPotionCount() + ")");
                } else {
                    JOptionPane.showMessageDialog(monsterGUI, "You're already at full health!");
                }
            }
        });

        // Next Monster button
        JButton nextMonsterButton = new JButton("Next Monster");
        nextMonsterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Increase difficulty
                int newDifficulty = monster.getDifficulty() + 1;

                // Create new monster
                Monster newMonster = new Monster(getRandomMonsterName(), newDifficulty);
                monsterGUI.dispose();

                // Create new GUI
                MonsterGUI newGUI = new MonsterGUI(newMonster);
                JPanel newControlPanel = createControlPanel(player, newMonster, newGUI, typingHandler, damageCalculator);
                newGUI.add(newControlPanel, BorderLayout.EAST);
                newGUI.pack();
            }
        });

        panel.add(attackButton);
        panel.add(potionButton);
        panel.add(nextMonsterButton);

        return panel;
    }

    private static String getRandomMonsterName() {
        String[] names = {
                "Goblin", "Orc", "Troll", "Skeleton", "Zombie", "Ghost",
                "Slime", "Bat", "Spider", "Wolf", "Bear", "Snake",
                "Dragon", "Demon", "Witch", "Warlock", "Banshee", "Ogre"
        };
        return names[(int)(Math.random() * names.length)];
    }
}