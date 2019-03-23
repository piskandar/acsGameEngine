package com.acs.tools;

public class Sprite {

    public int width;
    public int height;
    private Pixel[] colourData;

    public Sprite(){

    }

    public Sprite(int width, int height){

        this.width = width;
        this.height = height;
        this.colourData = new Pixel[width * height];
        for (int i = 0; i < colourData.length; i++) {
            colourData[i] = new Pixel();
        }
    }

    public boolean setPixel(int x, int y, Pixel pixel)
    {

//#ifdef OLC_DBG_OVERDRAW
//        nOverdrawCount++;
//#endif

        if (x >= 0 && x < width && y >= 0 && y < height)
        {
            colourData[y*width + x] = pixel;
            return true;
        }
        else
            return false;
    }

    public Pixel[] getData() {
        return colourData;
    }
}
