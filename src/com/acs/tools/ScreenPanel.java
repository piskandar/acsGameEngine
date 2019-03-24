package com.acs.tools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ScreenPanel extends JPanel {

    private final int screenWidth;
    private final int screenHeight;
    private final int pixelHeight;
    private final int pixelWidth;
    private Pixel[] data;

    public ScreenPanel(int screenWidth, int screenHeight, int pixelWidth, int pixelHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        this.pixelHeight = pixelHeight;
        this.pixelWidth = pixelWidth;

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (data != null) {
            for (int y = 0; y < screenHeight; y++) {
                for (int x = 0; x < screenWidth; x++) {

                    g.setColor(data[getIndex(x, y)].getColor());

                    int actualX = x * pixelWidth;
                    int actualY = y * pixelHeight;

                    g.fillRect(actualX, actualY, pixelWidth, pixelHeight);
                }
            }
        }
    }


    private int getIndex(int x, int y) {
        return x + screenWidth * y;
    }

    public void draw(Pixel[] data) {
        this.data = data;
        repaint();
    }

}
