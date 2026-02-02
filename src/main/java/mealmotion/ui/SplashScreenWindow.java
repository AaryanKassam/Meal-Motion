package mealmotion.ui;

import mealmotion.util.LogoLoader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.concurrent.CountDownLatch;

public final class SplashScreenWindow {
    private final CountDownLatch latch = new CountDownLatch(1);

    public SplashScreenWindow() {
        SwingUtilities.invokeLater(this::createAndShow);
    }

    public void waitForClose() {
        try { latch.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    private void createAndShow() {
        JWindow window = new JWindow();

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(18, 18, 18, 18));
        root.setBackground(new Color(250, 250, 250));

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel();
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        ImageIcon icon = LogoLoader.loadLogoIcon(220);
        if (icon != null) {
            logo.setIcon(icon);
        } else {
            logo.setText("MealMotion");
            logo.setFont(new Font("SansSerif", Font.BOLD, 34));
            logo.setForeground(new Color(35, 35, 35));
        }

        JLabel subtitle = new JLabel("Preparing your personalized plan…");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(new Color(80, 80, 80));

        center.add(Box.createVerticalStrut(8));
        center.add(logo);
        center.add(Box.createVerticalStrut(10));
        center.add(subtitle);
        center.add(Box.createVerticalStrut(8));

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setStringPainted(false);
        bar.setForeground(new Color(60, 179, 113));
        bar.setBorderPainted(false);
        bar.setPreferredSize(new Dimension(0, 18));
        root.add(center, BorderLayout.CENTER);
        root.add(bar, BorderLayout.SOUTH);

        window.setContentPane(root);
        window.setSize(440, 340);
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        Timer t = new Timer(18, null);
        t.addActionListener(e -> {
            int v = bar.getValue();
            if (v < 100) bar.setValue(v + 1);
            else {
                ((Timer) e.getSource()).stop();
                window.dispose();
                latch.countDown();
            }
        });
        t.start();
    }
}

