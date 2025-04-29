package src;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;

public class FontLoader {
    private static Font vt323Font;

    // Call this once at game startup
    public static void loadFonts() {
        try {
            // Load from resources folder
            InputStream fontStream = FontLoader.class.getResourceAsStream("/fonts/VT323-Regular.ttf");
            vt323Font = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(24f);

            // Register the font system-wide
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(vt323Font);

        } catch (Exception e) {
            System.err.println("Failed to load VT323 font. Using fallback.");
            vt323Font = new Font("Monospaced", Font.PLAIN, 24); // Fallback
        }
    }

    // Use this to get the font anywhere
    public static Font getVT323(float size) {
        return vt323Font.deriveFont(size);
    }
}