package src.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TransitionEffects {
    public static void fadeOut(JComponent component, Runnable onComplete) {
        Timer timer = new Timer(30, null);
        final float[] opacity = {1.0f};

        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                opacity[0] -= 0.05f;
                if (opacity[0] <= 0) {
                    opacity[0] = 0;
                    timer.stop();
                    onComplete.run();
                }
                component.repaint();
            }
        });

        timer.start();
    }

    public static void fadeIn(JComponent component) {
        component.setVisible(false);
        Timer timer = new Timer(30, null);
        final float[] opacity = {0.0f};

        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                opacity[0] += 0.05f;
                if (opacity[0] >= 1) {
                    opacity[0] = 1;
                    timer.stop();
                }
                component.setVisible(true);
                component.repaint();
            }
        });

        timer.start();
    }
}