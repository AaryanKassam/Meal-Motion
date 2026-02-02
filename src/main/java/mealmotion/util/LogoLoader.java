package mealmotion.util;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public final class LogoLoader {
    private LogoLoader() {}

    /**
     * Loads logo icon in this order:
     * - `resources/logo.png` from disk (easy to swap during development)
     * - `logo.png` from classpath (packaged via src/main/resources/logo.png)
     */
    public static ImageIcon loadLogoIcon(int maxWidthPx) {
        // 1) Dev override
        File disk = new File("resources/logo.png");
        if (disk.exists() && disk.isFile()) {
            return scale(new ImageIcon(disk.getPath()), maxWidthPx);
        }

        // 2) Packaged resource
        try {
            java.net.URL url = LogoLoader.class.getResource("/logo.png");
            if (url != null) return scale(new ImageIcon(url), maxWidthPx);
        } catch (Exception ignored) { }

        return null;
    }

    public static Image loadWindowIconImage() {
        ImageIcon icon = loadLogoIcon(256);
        return icon == null ? null : icon.getImage();
    }

    private static ImageIcon scale(ImageIcon icon, int maxWidthPx) {
        if (icon == null) return null;
        int w0 = icon.getIconWidth();
        int h0 = icon.getIconHeight();
        if (w0 <= 0 || h0 <= 0) return icon;
        int w = Math.min(maxWidthPx, w0);
        int h = (int) ((double) h0 / (double) w0 * (double) w);
        Image scaled = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }
}

