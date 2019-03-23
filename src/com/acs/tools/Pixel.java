package com.acs.tools;

import java.awt.*;

public class Pixel {

    public static final Pixel WHITE = new Pixel(Color.WHITE);
    public int red;
    public int green;
    public int blue;
    public int alpha = 255;

    public Pixel() {

    }

    public Pixel(Color color){
        this.red = color.getRed();
        this.green = color.getGreen();
        this.blue = color.getBlue();
        this.alpha = color.getAlpha();
    }

    public Pixel(int red, int green, int blue, int alpha) {

        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public enum Mode {NORMAL, MASK, ALPHA, CUSTOM}

    public Color getColor() {
        return new Color(red/255.0f, green/255.0f, blue/255.0f, alpha/255.0f);
    }

}
