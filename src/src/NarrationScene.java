package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JTextField;
import java.awt.image.BufferedImage;

public class NarrationScene extends JPanel {
    // Image assets
    private final ImageIcon cinnaSprite;
    private final ImageIcon yesButton;
    private final ImageIcon noButton;
    private final ImageIcon inputBox;

    // Game state
    private String displayedText = "";
    private int currentCharIndex = 0;
    private boolean isTyping = true;
    private boolean selectedYes = true;
    private boolean showButtons = false;
    private boolean showInput = false;
    private String playerName = "";
    private float sceneAlpha = 0f;
    private float fadeOutAlpha = 0f;
    private boolean fadingOut = false;
    private boolean buttonsActive = false;
    private boolean gotPotions = false;

    // Dialogue sequence
    private int dialogueStage = 0;
    private final String[] dialogue = {
            "Hi there! I'm your family friendly Cin-narrator! You're probably confused, right?\n" +
                    "\n (Use the arrow keys to select between yes and no]",
            "No worries! I'm here to explain our game!",
            "We were supposed to be on our way to CSCI-3381, but we got sidetracked!",
            "Also, I'm really bad at directions so instead of turning left I accidentally warped us into a fantasy land. Whoopsie! :3",
            "In my defense, I am a fictional sky puppy and I don't have a license. Why would you let me drive??!?!",
            "So really, this is on you! What's with that face? Okay, FINE! It was a team effort you big bully...",
            "Woah! That's some pretty scary monsters up ahead... I only have a bottle of Gatorade and some Bath and Body works perfume",
            "[You got 2 potions in your inventory now!]",
            "Heyyy... so uhm! Since I gave you something to help you out... what d'ya say you uhhh...",
            "Protect lil ol' me... especially bc my character design makes me too fat and small to protect myself.",
            "Oh? I'll take your silence as a yes! \n(*You sweat a little. Cinna didn't even give you a chance to respond??*)",
            "Ah, okay then! Straight to point I see... well then...",
            "What's my new friend's name?"
    };

    // Positions
    private final int textX = 200;
    private final int textY = 150;
    private final int buttonY = 450;
    private final int yesX = 400;
    private final int noX = 700;
    private final int spriteY = buttonY - 100;
    private final int inputX = 450;
    private final int inputY = 400;
    private JTextField nameInputField;
    private boolean nameEntered = false;
    private String greeting = "";

    public NarrationScene() {
        // Load all assets with error handling
        cinnaSprite = loadImage("/resources/assets/cinnaSprite.png");
        yesButton = loadImage("/resources/assets/yesButton.png");
        noButton = loadImage("/resources/assets/noButton.png");
        inputBox = loadImage("/resources/assets/inputBox.png");
        System.out.println("Input box loaded: " + (inputBox.getImageLoadStatus() == MediaTracker.COMPLETE));
        nameInputField = new JTextField();
        nameInputField.setFont(getVT323Font(36f));
        nameInputField.setBounds(inputX + 50, inputY + 20, 300, 40);
        nameInputField.setVisible(false);
        nameInputField.setOpaque(false);
        nameInputField.setBorder(BorderFactory.createEmptyBorder());
        nameInputField.setForeground(Color.BLACK);
        add(nameInputField);

        setPreferredSize(new Dimension(1280, 720));
        setOpaque(false);

        setupTypingAnimation();
        startFadeIn();

        // Debug output
        System.out.println("Cinna sprite loaded: " + (cinnaSprite.getImageLoadStatus() == MediaTracker.COMPLETE));
        System.out.println("Yes button loaded: " + (yesButton.getImageLoadStatus() == MediaTracker.COMPLETE));
        System.out.println("No button loaded: " + (noButton.getImageLoadStatus() == MediaTracker.COMPLETE));
    }

    private ImageIcon loadImage(String path) {
        try {
            // Get the resource URL first
            java.net.URL imgURL = getClass().getResource(path);
            if (imgURL == null) {
                System.err.println("Couldn't find file: " + path);
                return createMissingImagePlaceholder(path);
            }
            return new ImageIcon(imgURL);
        } catch (Exception e) {
            System.err.println("Error loading image: " + path);
            e.printStackTrace();
            return createMissingImagePlaceholder(path);
        }
    }

    private ImageIcon createMissingImagePlaceholder(String path) {
        // Create a red placeholder with error message
        BufferedImage img = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(Color.RED);
        g2d.fillRect(0, 0, 200, 100);
        g2d.setColor(Color.WHITE);
        g2d.drawString("Missing: " + path, 10, 50);
        g2d.dispose();
        return new ImageIcon(img);
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
                if (currentCharIndex < dialogue[dialogueStage].length()) {
                    displayedText = dialogue[dialogueStage].substring(0, currentCharIndex + 1);
                    currentCharIndex++;
                    repaint();
                } else {
                    isTyping = false;
                    this.cancel();
                    handleDialogueComplete();
                }
            }
        }, 0, 30);
    }

    private void handleDialogueComplete() {
        switch (dialogueStage) {
            case 0: // Initial question
                showButtons = true;
                buttonsActive = true;
                setupKeyListeners();
                break;

            case 6: // After monster warning
                if (selectedYes) { // Only give potions if player was confused
                    dialogueStage = 6; // Skip to potions
                    gotPotions = true;
                } else {
                    dialogueStage = 10; // Skip to not confused path
                }
                advanceDialogue();
                break;

            case 12: // Name prompt
                showInput = true;
                nameInputField.setVisible(true);
                nameInputField.requestFocusInWindow();
                setupInputListener();
                break;

            default:
                // Continue automatically after short delay
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        advanceDialogue();
                    }
                }, 1500);
                break;
        }
    }

    private void advanceDialogue() {
        dialogueStage++;
        currentCharIndex = 0;
        displayedText = "";
        isTyping = true;
        setupTypingAnimation();
        repaint();
    }

    private void setupKeyListeners() {
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (buttonsActive) {
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

                if (showInput && e.getKeyCode() != KeyEvent.VK_ENTER) {
                    // Handle name input
                    if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && playerName.length() > 0) {
                        playerName = playerName.substring(0, playerName.length() - 1);
                    } else if (Character.isLetterOrDigit(e.getKeyChar())) {
                        playerName += e.getKeyChar();
                    }
                    repaint();
                }
            }
        });
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

    private void setupInputListener() {
        nameInputField.addActionListener(e -> {
            playerName = nameInputField.getText().trim();
            if (!playerName.isEmpty()) {
                greeting = "Nice to meet ya, " + playerName + "!";
                nameEntered = true;
                nameInputField.setVisible(false);
                showInput = false;
                startFadeOut();
                repaint();
            }
        });
    }

    private void handleSelection() {
        showButtons = false;
        buttonsActive = false;

        if (selectedYes) {
            // Confused path
            dialogueStage = 0; // Start explanation
        } else {
            // Not confused path
            dialogueStage = 10; // Skip to "straight to the point"
        }

        advanceDialogue();
    }

    private void startFadeOut() {
        fadingOut = true;
        Timer fadeTimer = new Timer();
        fadeTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                fadeOutAlpha += 0.05f;
                if (fadeOutAlpha >= 1f) {
                    fadeOutAlpha = 1f;
                    this.cancel();
                    transitionToGame();
                }
                repaint();
            }
        }, 0, 30);
    }

    private void transitionToGame() {
        JFrame topFrame = (JFrame)SwingUtilities.getWindowAncestor(this);
        topFrame.dispose();

        JFrame gameFrame = new JFrame("Fantasy Adventure");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ImageIcon overlay = new ImageIcon(getClass().getResource("/resources/screenOverlay.png"));
        JLabel overlayLabel = new JLabel(overlay);

        gameFrame.setContentPane(overlayLabel);
        gameFrame.pack();
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setVisible(true);

        // Show greeting in game window
        JOptionPane.showMessageDialog(gameFrame, greeting, "Cin-narrator", JOptionPane.PLAIN_MESSAGE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Apply scene fade-in/out effect
        if (fadingOut) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f - fadeOutAlpha));
        } else {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, sceneAlpha));
        }

        // Set retro font
        g2d.setFont(getVT323Font(36f));

        // Draw current dialogue text
        g2d.setColor(Color.WHITE);
        drawWrappedText(g2d, displayedText, textX, textY, 880);

        // Draw buttons if visible
        if (showButtons) {
            if (yesButton.getImage() != null) {
                g2d.drawImage(yesButton.getImage(), yesX, buttonY, this);
            }
            if (noButton.getImage() != null) {
                g2d.drawImage(noButton.getImage(), noX, buttonY, this);
            }

            if (cinnaSprite.getImage() != null) {
                int spriteX = selectedYes ?
                        yesX + yesButton.getIconWidth()/2 - cinnaSprite.getIconWidth()/2 :
                        noX + noButton.getIconWidth()/2 - cinnaSprite.getIconWidth()/2;
                g2d.drawImage(cinnaSprite.getImage(), spriteX, spriteY, this);
            }
        }

        // Draw input box if visible
        if (showInput) {
            if (inputBox.getImage() != null) {
                g2d.drawImage(inputBox.getImage(), inputX, inputY, 400, 100, this);
            }

            // Draw instructions
            g2d.setColor(Color.WHITE);
            g2d.drawString("Enter your name:", inputX + 50, inputY + 30);

            // Name will be drawn by the JTextField component
        }

        // Draw greeting if name was entered
        if (nameEntered) {
            g2d.setColor(Color.WHITE);
            g2d.drawString(greeting, inputX + 50, inputY + 150);
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
                y += fm.getHeight() * 1.2;
                currentLine = word + " ";
            }
        }
        g.drawString(currentLine, x, y);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }
}