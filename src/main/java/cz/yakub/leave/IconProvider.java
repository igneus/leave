package cz.yakub.leave;

import java.awt.*;
import java.awt.image.BufferedImage;

public class IconProvider {
    private Dimension dimension;

    public IconProvider(Dimension dimension) {
        this.dimension = dimension;
    }

    public Image getGreen() {
        return create(Color.green);
    }

    public Image getOrange() {
        return create(Color.orange);
    }

    public Image getRed() {
        return create(Color.red);
    }

    private Image create(Color color) {
        BufferedImage bufferedImage = new BufferedImage((int) dimension.getWidth(), (int) dimension.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setColor(color);
        g2d.fillRect(0, 0, (int) dimension.getWidth(), (int) dimension.getHeight());
        g2d.dispose();

        return bufferedImage;
    }
}
