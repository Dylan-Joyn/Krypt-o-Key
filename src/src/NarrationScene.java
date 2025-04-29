package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;

public class NarrationScene extends JPanel {
    private final ImageIcon cinnaSprite;
    private final ImageIcon yesButton;
    private final ImageIcon noButton;
    private String fullText = "Hi there! I'm your family friendly Cin-narrator! You're probably confused, right?";
    private String displayedText = "";
    private int currentCharIndex = 0;
    private boolean isTyping = true;
    private boolean selectedYes = true;
    private float sceneAlpha = 0f; // For fade-in effect

    // Positions
    private final int textX = 50;
    private final int textY = 100;
    private final int buttonY = 200;
    private final int yesX = 150;
    private final int noX = 350;
    private final int spriteY = buttonY - 80;

    public NarrationScene() {
        // Load images
        cinnaSprite = loadImage("/resources/assets/cinnaSprite.png");
        yesButton = loadImage("/resources/assets/yesButton.png");
        noButton = loadImage("/resources/assets/noButton.png");

        setPreferredSize(new Dimension(600, 400));
        setupTypingAnimation();
        setupKeyListeners();
        setOpaque(false);
    }

    private ImageIcon loadImage(String path) {
        try {
            return new ImageIcon(getClass().getResource(path));
        } catch (Exception e) {
            System.err.println("Error loading image: " + path);
            return new ImageIcon(); // Return empty icon
        }
    }

    public void startFadeIn() {
        Timer fadeTimer = new Timer();
        fadeTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sceneAlpha += 0.05f;
                if (sceneAlpha >= 1f) {
                    sceneAlpha = 1f;
                    this.cancel();
                }
                repaint();
            }
        }, 0, 30);
    }

    private void setupTypingAnimation() {
        Timer typingTimer = new Timer();
        typingTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (currentCharIndex < fullText.length()) {
                    displayedText = fullText.substring(0, currentCharIndex + 1);
                    currentCharIndex++;
                    repaint();
                } else {
                    isTyping = false;
                    this.cancel();
                }
            }
        }, 0, 50);
    }

    private void setupKeyListeners() {
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!isTyping) {
                    if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        selectedYes = true;
                        repaint();
                    } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        selectedYes = false;
                        repaint();
                    } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        handleSelection();
                    }
                }
            }
        });
    }

    private void handleSelection() {
        if (selectedYes) {
            System.out.println("Player selected YES");
        } else {
            System.out.println("Player selected NO");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Apply fade-in effect
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, sceneAlpha));

        // Set retro font
        try {
            Font vt323 = Font.createFont(Font.TRUETYPE_FONT,
                            getClass().getResourceAsStream("/resources/fonts/VT323-Regular.ttf"))
                    .deriveFont(24f);
            g2d.setFont(vt323);
        } catch (Exception e) {
            g2d.setFont(new Font("Monospaced", Font.PLAIN, 24));
        }

        // Draw text with typing effect
        g2d.setColor(Color.WHITE);
        drawWrappedText(g2d, displayedText, textX, textY, getWidth() - 100);

        // Draw buttons (only after typing completes)
        if (!isTyping) {
            g2d.drawImage(yesButton.getImage(), yesX, buttonY, this);
            g2d.drawImage(noButton.getImage(), noX, buttonY, this);

            // Draw Cinna sprite over selected button
            int spriteX = selectedYes ? yesX + yesButton.getIconWidth()/2 - cinnaSprite.getIconWidth()/2
                    : noX + noButton.getIconWidth()/2 - cinnaSprite.getIconWidth()/2;
            g2d.drawImage(cinnaSprite.getImage(), spriteX, spriteY, this);
        }
    }

    private void drawWrappedText(Graphics g, String text, int x, int y, int maxWidth) {
        FontMetrics fm = g.getFontMetrics();
        String[] words = text.split(" ");
        String currentLine = "";

        for (String word : words) {
            if (fm.stringWidth(currentLine + word) <= maxWidth) {
                currentLine += word + " ";
            } else {
                g.drawString(currentLine, x, y);
                y += fm.getHeight();
                currentLine = word + " ";
            }
        }
        g.drawString(currentLine, x, y);
    }
}