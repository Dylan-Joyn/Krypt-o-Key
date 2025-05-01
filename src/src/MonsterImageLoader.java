package src;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Utility class for loading monster images based on type
 */
public class MonsterImageLoader {

    /**
     * Load a monster image based on monster type
     */
    public static ImageIcon getMonsterImage(String monsterName, Monster.MonsterType type) {
        String color = type.getDisplayName();  // GREEN, YELLOW, etc.

        String filename = monsterName + "_" + color + ".png";

        String[] possiblePaths = {
                "/resources/assets/" + color + "/" + filename,
                "/src/resources/assets/" + color + "/" + filename
        };

        for (String path : possiblePaths) {
            try {
                java.net.URL imgURL = MonsterImageLoader.class.getResource(path);
                if (imgURL != null) {
                    System.out.println("Found monster image at: " + path);
                    ImageIcon originalIcon = new ImageIcon(imgURL);
                    Image scaledImage = originalIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
                    return new ImageIcon(scaledImage);

                }
            } catch (Exception ignored) {}
        }

        // Fallback
        System.err.println("Couldn't find monster image for: " + monsterName + " " + color + ". Creating placeholder.");
        return createColoredPlaceholder(type);
    }


    /**
     * Create a colored placeholder based on monster type
     */
    private static ImageIcon createColoredPlaceholder(Monster.MonsterType type) {
        BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();

        // Set color based on monster type (all green with different intensities)
        Color color;
        switch (type) {
            case GREEN:
                color = new Color(100, 255, 100, 200);
                break;
            case YELLOW:
                color = new Color(200, 255, 100, 200);
                break;
            case RED:
                color = new Color(255, 100, 100, 200);
                break;
            case PURPLE:
                color = new Color(200, 100, 255, 200);
                break;
            default:
                color = new Color(100, 255, 100, 200);
        }

        // Draw a simple monster outline
        g2d.setColor(color);
        g2d.fillRoundRect(40, 40, 120, 120, 20, 20);

        // Add eyes
        g2d.setColor(Color.WHITE);
        g2d.fillOval(70, 70, 20, 20);
        g2d.fillOval(110, 70, 20, 20);

        // Add pupils
        g2d.setColor(Color.BLACK);
        g2d.fillOval(75, 75, 10, 10);
        g2d.fillOval(115, 75, 10, 10);

        // Add mouth
        g2d.drawArc(80, 100, 40, 20, 0, -180);

        g2d.dispose();
        return new ImageIcon(img);
    }
}