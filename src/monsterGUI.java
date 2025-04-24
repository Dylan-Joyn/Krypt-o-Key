package src;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class MonsterGUI extends JFrame {
    private Monster monster;
    private JLabel monsterImageLabel;
    private JProgressBar healthBar;
    private JLabel statsLabel;

    public MonsterGUI(Monster monster) {
        this.monster = monster;
        setTitle("Monster Battle");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLayout(new BorderLayout());

        // Create components
        createComponents();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void createComponents() {
        // Panel for monster image
        JPanel imagePanel = new JPanel();
        monsterImageLabel = new JLabel();
        try {
            // Try to load the image (use a placeholder if not found)
            BufferedImage monsterImage = ImageIO.read(getClass().getResource("monster.png"));
            monsterImageLabel.setIcon(new ImageIcon(monsterImage));
        } catch (IOException | IllegalArgumentException e) {
            // If image not found, create a colored placeholder based on monster type
            Color color;
            switch (monster.getType()) {
                case GREEN: color = Color.GREEN; break;
                case YELLOW: color = Color.YELLOW; break;
                case RED: color = Color.RED; break;
                case PURPLE: color = new Color(128, 0, 128); break; // Purple
                default: color = Color.GRAY;
            }

            BufferedImage placeholder = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = placeholder.createGraphics();
            g2d.setColor(color);
            g2d.fillOval(0, 0, 200, 200);
            g2d.dispose();
            monsterImageLabel.setIcon(new ImageIcon(placeholder));
        }
        imagePanel.add(monsterImageLabel);
        add(imagePanel, BorderLayout.CENTER);

        // Health bar
        healthBar = new JProgressBar(0, monster.getMaxHealth());
        healthBar.setValue(monster.getHealth());
        healthBar.setStringPainted(true);
        healthBar.setForeground(getHealthBarColor(monster.getHealth(), monster.getMaxHealth()));
        healthBar.setString(monster.getHealth() + "/" + monster.getMaxHealth());

        // Customize health bar appearance
        healthBar.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        healthBar.setPreferredSize(new Dimension(300, 30));
        healthBar.setFont(new Font("Arial", Font.BOLD, 12));

        JPanel healthPanel = new JPanel();
        healthPanel.add(healthBar);
        add(healthPanel, BorderLayout.NORTH);

        // Stats label
        statsLabel = new JLabel("<html><center>" +
                monster.getType().getDisplayName() + " " + monster.getName() +
                "<br>Level: " + monster.getDifficulty() +
                "<br>Attack: ~" + monster.getAttackDamage() +
                "</center></html>");
        statsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(statsLabel, BorderLayout.SOUTH);
    }

    private Color getHealthBarColor(int currentHealth, int maxHealth) {
        double ratio = (double) currentHealth / maxHealth;
        if (ratio > 0.6) return Color.GREEN;
        if (ratio > 0.3) return Color.YELLOW;
        return Color.RED;
    }

    public void updateHealth(int newHealth) {
        monster.takeDamage(monster.getHealth() - newHealth); // Adjust health
        healthBar.setValue(monster.getHealth());
        healthBar.setForeground(getHealthBarColor(monster.getHealth(), monster.getMaxHealth()));
        healthBar.setString(monster.getHealth() + "/" + monster.getMaxHealth());
    }

    public static void main(String[] args) {
        // Example usage
        SwingUtilities.invokeLater(() -> {
            Monster monster = new Monster("Goblin", 3); // Create a level 3 monster
            new MonsterGUI(monster);
        });
    }
}