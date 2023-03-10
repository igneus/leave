package cz.yakub.leave;

import java.awt.*;
import java.awt.image.BufferedImage;

public class IconProvider {
    private Dimension dimension;

    public IconProvider(Dimension dimension) {
        this.dimension = dimension;
    }

    public Image getGray() {
        return create(Color.GRAY);
    }

    public Image getGreen() {
        return create(new Color(149, 205, 65));
    }

    public Image getOrange(String minutesLeft) {
        return create(Color.orange, minutesLeft);
    }

    public Image getRed() {
        return create(Color.red);
    }

    private BufferedImage create(Color color, String text) {
        BufferedImage bufferedImage = new BufferedImage((int) dimension.getWidth(), (int) dimension.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setColor(color);
        g2d.fillRect(0, 0, (int) dimension.getWidth(), (int) dimension.getHeight());

        g2d.setColor(color.darker().darker());
        g2d.drawString(text, 1, bufferedImage.getHeight() / 2);

        g2d.dispose();

        return bufferedImage;
    }

    private BufferedImage create(Color color) {
        return create(color, "L");
    }
}
