package com.acs.tools;

import javax.swing.*;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.abs;

public abstract class ACSGameEngine {

    private int screenWidth = 256;
    private int screenHeight = 240;
    private int pixelWidth = 4;
    private int pixelHeight = 4;
    private float pixelX = 1.0f;
    private float pixelY = 1.0f;
    private Sprite defaultDrawTarget = null;
    private Sprite drawTarget = null;
    private Pixel.Mode pixelMode = Pixel.Mode.NORMAL;
    private boolean atomActive;
    private JFrame jFrame;
    private ThreadPoolExecutor threadPoolExecutor;
    private ScreenPanel contentPane;

    public RCode construct(int screen_w, int screen_h, int pixel_w, int pixel_h) {

        screenWidth = screen_w;
        screenHeight = screen_h;
        pixelWidth = pixel_w;
        pixelHeight = pixel_h;
        pixelX = 2.0f / (float) (screenWidth);
        pixelY = 2.0f / (float) (screenHeight);

        if (pixelWidth == 0 || pixelHeight == 0 || screenWidth == 0 || screenHeight == 0)
            return RCode.FAIL;

        defaultDrawTarget = new Sprite(screenWidth, screenHeight);
        setDrawTarget(null);
        threadPoolExecutor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.NANOSECONDS, new LinkedBlockingDeque<>());
        return RCode.OK;
    }

    public RCode start() {
        contentPane = new ScreenPanel(screenWidth, screenHeight, pixelWidth, pixelHeight);
        contentPane.setLayout(null);
        jFrame = new JFrame();

        jFrame.setSize(screenWidth * pixelWidth, screenHeight * pixelHeight);
        jFrame.setContentPane(contentPane);
        jFrame.setVisible(true);

        atomActive = true;
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                engineThread();
            }
        });
        return RCode.OK;
    }

    private void setDrawTarget(Sprite target) {
        if (target != null)
            drawTarget = target;
        else
            drawTarget = defaultDrawTarget;
    }

    private Sprite getDrawTarget() {
        return drawTarget;
    }

    private void engineThread() {
        if (!onUserCreate())
            atomActive = false;

        long tp1 = System.currentTimeMillis();
        long tp2 = System.currentTimeMillis();

        while (atomActive) {
            while (atomActive) {
                tp2 = System.currentTimeMillis();
                long elapsedTime = tp2 - tp1;
                tp1 = tp2;

                float fElapsedTime = elapsedTime / 1000.0f;

                if (!onUserUpdate(fElapsedTime))
                    atomActive = false;


                contentPane.draw(defaultDrawTarget.getData());
                try {
                    Thread.sleep(33); //30FPS
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public abstract boolean onUserCreate();

    public abstract boolean onUserUpdate(float elapsedTime);

    public boolean draw(int x, int y, Pixel p){
        defaultDrawTarget.setPixel(x, y, p);
        return true;
    }

    public void drawLine(int x1, int y1, int x2, int y2, Pixel p){
        int x, y, dx, dy, dx1, dy1, px, py, xe, ye, i;
        dx = x2 - x1; dy = y2 - y1;

        // Line is vertical
        if(dx == 0) {
            if(y2 < y1) {
                int temp = y2;
                y2 = y1;
                y1 = temp;
            }

            for(y = y1; y <= y2; y ++){
                draw(x1, y, p);
            }

            return;
        }

        if (dy == 0) // Line is horizontal
        {
            if (x2 < x1) {
                int temp = x2;
                x2 = x1;
                x1 = temp;
            }

            for (x = x1; x <= x2; x++)
                draw(x, y1, p);
            return;
        }

        dx1 = abs(dx); dy1 = abs(dy);

        px = 2 * dy1 - dx1;	py = 2 * dx1 - dy1;

        if (dy1 <= dx1)
        {
            if (dx >= 0)
            {
                x = x1; y = y1; xe = x2;
            }
            else
            {
                x = x2; y = y2; xe = x1;
            }

            draw(x, y, p);

            for (i = 0; x<xe; i++)
            {
                x = x + 1;
                if (px<0)
                    px = px + 2 * dy1;
                else
                {
                    if ((dx<0 && dy<0) || (dx>0 && dy>0)) y = y + 1; else y = y - 1;
                    px = px + 2 * (dy1 - dx1);
                }
                draw(x, y, p);
            }
        }
        else
        {
            if (dy >= 0)
            {
                x = x1; y = y1; ye = y2;
            }
            else
            {
                x = x2; y = y2; ye = y1;
            }

            draw(x, y, p);

            for (i = 0; y<ye; i++)
            {
                y = y + 1;
                if (py <= 0)
                    py = py + 2 * dx1;
                else
                {
                    if ((dx<0 && dy<0) || (dx>0 && dy>0)) x = x + 1; else x = x - 1;
                    py = py + 2 * (dx1 - dy1);
                }
                draw(x, y, p);
            }
        }
    }

    public void drawTriangle(int x1, int y1, int x2, int y2, int x3, int y3, Pixel p){
        drawLine(x1, y1, x2, y2, p);
        drawLine(x2, y2, x3, y3, p);
        drawLine(x3, y3, x1, y1, p);

    }

    public void drawTriangle(float x1, float y1, float x2, float y2, float x3, float y3, Pixel p){
        drawTriangle((int)x1, (int)y1, (int)x2, (int)y2, (int)x3, (int)y3, p);
    }
    public void fillTriangle(float x1, float y1, float x2, float y2, float x3, float y3, Pixel p){
        fillTriangle((int)x1, (int)y1, (int)x2, (int)y2, (int)x3, (int)y3, p);
    }
//    http://www.sunshine2k.de/coding/java/TriangleRasterization/TriangleRasterization.html
    public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3, Pixel p){
        if(y1 > y2){
            int temp1 = y1;
            y1 = y2;
            y2 = temp1;

            int temp2 = x1;
            x1 = x2;
            x2 = temp2;
        }

        if(y1 > y3){
            int temp1 = y1;
            y1 = y3;
            y3 = temp1;

            int temp2 = x1;
            x1 = x3;
            x3 = temp2;
        }

        if(y2 > y3){
            int temp1 = y2;
            y2 = y3;
            y3 = temp1;

            int temp2 = x2;
            x2 = x3;
            x3 = temp2;
        }
        if (y2 == y3) {
            fillBottomFlatTriangle(x1, y1, x2, y2, x3, y3, p);
        } else if (y1 == y2){
            fillTopFlatTriangle(x1, y1, x2, y2, x3, y3, p);
        }else {

            int x4 = (int)(x1 + ((float)(y2 - y1) / (float)(y3 - y1)) * (x3 - x1));
            int y4 = y2;

            fillBottomFlatTriangle(x1,y1, x2,y2, x4,y4, p);
            fillTopFlatTriangle(x2,y2, x4,y4, x3,y3,p);
        }

    }

    private void fillBottomFlatTriangle(int x1, int y1, int x2, int y2, int x3, int y3, Pixel p){
        float invslope1 = (float)(x2 - x1) / (float)(y2 - y1);
        float invslope2 = (float)(x3 - x1) / (float)(y3 - y1);

        float curx1 = x1;
        float curx2 = x1;

        for (int scanlineY = y1; scanlineY <= y2; scanlineY++) {
            drawLine((int) curx1, scanlineY, (int) curx2, scanlineY, p);
            curx1 += invslope1;
            curx2 += invslope2;
        }
    }

    private void fillTopFlatTriangle(int x1, int y1, int x2, int y2, int x3, int y3, Pixel p){
        float invslope1 = (float)(x3 - x1) / (float)(y3 - y1);
        float invslope2 = (float)(x3 - x2) / (float)(y3 - y2);

        float curx1 = x3;
        float curx2 = x3;

        for (int scanlineY = y3; scanlineY > y1; scanlineY--)
        {
            drawLine((int)curx1, scanlineY, (int)curx2, scanlineY, p);
            curx1 -= invslope1;
            curx2 -= invslope2;
        }
    }


    public void clear(Pixel p)
    {
        int pixels = getDrawTargetWidth() * getDrawTargetHeight();
        Pixel[] m = getDrawTarget().getData();
        for (int i = 0; i < pixels; i++)
            m[i] = p;
    }


    private int getDrawTargetWidth()
    {
        if (drawTarget != null)
            return drawTarget.width;
        else
            return 0;
    }

    private int getDrawTargetHeight()
    {
        if (drawTarget != null)
            return drawTarget.height;
        else
            return 0;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }
}
