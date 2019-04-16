package com.acs.tools;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Sprite {

    public int width;
    public int height;
    private Pixel[] colourData;
    private short[] glyphs = null;
    private short[] colours = null;

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

    public Sprite(int width, int height, Pixel[] colourData) {

        this.width = width;
        this.height = height;
        this.colourData = colourData;
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

    public Pixel getColor(int x, int y)
    {
        if (x <0 || x >= width || y < 0 || y >= height)
            return Pixel.BLACK;
		else
            return colourData[y * width + x];
    }

    public static Sprite load(String file){
        File file1 = new File(file);
        if(!file1.exists())
            return null;
        try {
            DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
            int t = dataInputStream.readInt();
            int t2 = dataInputStream.readInt();

            int width = (0x000000ff & (t>>24)) |
                    (0x0000ff00 & (t>> 8)) |
                    (0x00ff0000 & (t<< 8)) |
                    (0xff000000 & (t<<24));

            int height = (0x000000ff & (t2>>24)) |
                    (0x0000ff00 & (t2>> 8)) |
                    (0x00ff0000 & (t2<< 8)) |
                    (0xff000000 & (t2<<24));

            byte[] bytes = new byte[width * height * 2];
            for (int i = 0; i < width * height * 2; i++) {
                byte s = dataInputStream.readByte();
                bytes[i] = s;

            }
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            Pixel[] colourData = new Pixel[width * height];
            int index = 0;
            while (byteBuffer.hasRemaining()) {
                short aShort = byteBuffer.getShort();
                colourData[index++] = hexToPixel(aShort);
            }


            dataInputStream.close();

            return new Sprite(width, height,colourData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static Pixel hexToPixel(short aShort) {
        switch (aShort) {
            case FG_DARK_BLUE:
            case BG_DARK_BLUE:
                return Pixel.DARK_BLUE;

            case FG_DARK_GREEN:
            case BG_DARK_GREEN:
                return Pixel.DARK_GREEN;
            case FG_DARK_CYAN:
            case BG_DARK_CYAN:
                return Pixel.DARK_CYAN;

            case FG_DARK_RED:
            case BG_DARK_RED:
                return Pixel.DARK_RED;

            case FG_DARK_MAGENTA:
            case BG_DARK_MAGENTA:
                return Pixel.DARK_MAGENTA;

            case FG_DARK_YELLOW:
            case BG_DARK_YELLOW:
                return Pixel.DARK_YELLOW;

            case FG_GREY:
            case BG_GREY:
                return Pixel.GREY;

            case FG_DARK_GREY:
            case BG_DARK_GREY:
                return Pixel.DARK_GREY;

            case FG_BLUE:
            case BG_BLUE:
                return Pixel.BLUE;

            case FG_GREEN:
            case BG_GREEN:
                return Pixel.GREEN;

            case FG_CYAN:
            case BG_CYAN:
                return Pixel.CYAN;

            case FG_RED:
            case BG_RED:
                return Pixel.RED;

            case FG_MAGENTA:
            case BG_MAGENTA:
                return Pixel.MAGENTA;

            case FG_YELLOW:
            case BG_YELLOW:
                return Pixel.YELLOW;

            case FG_WHITE:
            case BG_WHITE:
                return Pixel.WHITE;

        }
        return Pixel.BLACK;
    }

    private static final short FG_DARK_BLUE = 0x01;
    private static final short FG_DARK_GREEN = 0x02;
    private static final short FG_DARK_CYAN = 0x03;
    private static final short FG_DARK_RED = 0x04;
    private static final short FG_DARK_MAGENTA = 0x05;
    private static final short FG_DARK_YELLOW = 0x06;
    private static final short FG_GREY = 0x07;
    private static final short FG_DARK_GREY = 0x08;
    private static final short FG_BLUE = 0x09;
    private static final short FG_GREEN = 0x0A;
    private static final short FG_CYAN = 0x0B;
    private static final short FG_RED = 0x0C;
    private static final short FG_MAGENTA = 0x0D;
    private static final short FG_YELLOW = 0x0E;
    private static final short FG_WHITE = 0x0F;
    private static final short BLACK = 0x00;
    private static final short BG_DARK_BLUE = 0x10;
    private static final short BG_DARK_GREEN = 0x20;
    private static final short BG_DARK_CYAN = 0x30;
    private static final short BG_DARK_RED = 0x40;
    private static final short BG_DARK_MAGENTA = 0x50;
    private static final short BG_DARK_YELLOW = 0x60;
    private static final short BG_GREY = 0x70;
    private static final short BG_DARK_GREY = 0x80;
    private static final short BG_BLUE = 0x90;
    private static final short BG_GREEN = 0xA0;
    private static final short BG_CYAN = 0xB0;
    private static final short BG_RED = 0xC0;
    private static final short BG_MAGENTA = 0xD0;
    private static final short BG_YELLOW = 0xE0;
    private static final short BG_WHITE = 0xF0;
}
