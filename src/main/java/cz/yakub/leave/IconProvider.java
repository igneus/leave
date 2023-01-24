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

    public Image getOrange(String minutesLeft) {
        BufferedImage image = create(Color.orange);
        addText(image, minutesLeft);
        return image;
    }

    public Image getRed() {
        return create(Color.red);
    }

    private BufferedImage create(Color color) {
        BufferedImage bufferedImage = new BufferedImage((int) dimension.getWidth(), (int) dimension.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setColor(color);
        g2d.fillRect(0, 0, (int) dimension.getWidth(), (int) dimension.getHeight());
        g2d.dispose();

        return bufferedImage;
    }

    private void addText(BufferedImage bufferedImage, String text) {
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setColor(Color.black);
        g2d.drawString(text, 1, bufferedImage.getHeight() / 2);
        g2d.dispose();
    }
}
