package cz.yakub.leave;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class IconProvider {
    public static Image getGreen() {
        return load("images/icon_green.png");
    }

    public static Image getOrange() {
        return load("images/icon_orange.png");
    }

    public static Image getRed() {
        return load("images/icon_red.png");
    }

    private static Image load(String filename) {
        URL imageURL = IconProvider.class.getResource(filename);

        if (imageURL == null) {
            throw new RuntimeException("Resource not found");
        }

        return new ImageIcon(imageURL, "tray icon").getImage();
    }
}
