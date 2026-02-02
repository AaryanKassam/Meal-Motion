package mealmotion;

import mealmotion.ui.SplashScreenWindow;
import mealmotion.ui.WizardFrame;

import javax.swing.*;

public final class MealMotionApp {
    public static void main(String[] args) {
        // Consistent native-ish look
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }

        SplashScreenWindow splash = new SplashScreenWindow();
        splash.waitForClose();

        SwingUtilities.invokeLater(() -> new WizardFrame().show());
    }
}

