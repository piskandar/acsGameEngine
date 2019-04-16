package com.acs.tools;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class Frogger extends ACSGameEngine {

    private List<Pair<Float, String>> vecLanes;
    private float totalElapsed;
    private int cellSize = 8;
    private Sprite spriteCar2;

    @Override
    public boolean onUserCreate() {
        vecLanes = new ArrayList<>();

        vecLanes.add(new Pair<>(0.0f,  "xxx..xxx..xxx..xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"));
        vecLanes.add(new Pair<>(-3.0f, ",,,xxxx,,xxxxxx,,,,,,,xxxx,,,,,xx,,,xxxxx,,,,xxxxxx,,,,xxxxx,,,,"));
        vecLanes.add(new Pair<>(3.0f,  ",,,,xxxx,,,,,xxxx,,,,xxxx,,,,,,,,,xxxx,,,,,xx,,,,,,xxxxxx,,,,,,,"));
        vecLanes.add(new Pair<>(2.0f,  ",,xxx,,,,,xxx,,,,,xx,,,,,xxx,,,xxx,,,,xx,,,,xxxx,,,,xx,,,,,,xx,,"));
        vecLanes.add(new Pair<>(0.0f,  "................................................................"));
        vecLanes.add(new Pair<>(-3.0f, "....xxxx.......xxxx....xxxx..........xxxx........xxxx....xxxx..."));
        vecLanes.add(new Pair<>( 3.0f, ".....xx..xx....xx....xx.....xx........xx..xx.xx......xx.......xx"));
        vecLanes.add(new Pair<>(-4.0f, "..zx.....zx.........zx..zx........zx...zx...zx....zx...zx...zx.."));
        vecLanes.add(new Pair<>(2.0f,  "..xx.....xx.......xx.....xx......xx..xx.xx.......xx....xx......."));
        vecLanes.add(new Pair<>(0.0f,  "................................................................"));

        spriteCar2 = Sprite.load("/Users/piskandar/Development/java/game-engine/objects/car2.spr");
        return true;
    }

    @Override
    public boolean onUserUpdate(float elapsedTime) {
        totalElapsed += elapsedTime;
        clear(Pixel.RED);
        //draw Lans
        int x=-1;
        int y=0;
        for (Pair<Float, String> lane : vecLanes) {
            //find offset
            int startPos = (int)(totalElapsed * lane.first) % 64;
            if(startPos < 0){
                startPos = 64 - (Math.abs(startPos) % 64);
            }

            int cellOffset = (int)(((float)cellSize) * totalElapsed * lane.first)% cellSize;

            for (int i = 0; i < (getScreenWidth() / cellSize)+2; i++) {
                char graphic = lane.second.charAt((startPos + i) % 64);
                switch (graphic){
                    case 'z':
                        drawPartialSprite((x + i) * cellSize - cellOffset, y * cellSize, spriteCar2,0,0,8,8);
                        break;

                    case 'x':
                        drawPartialSprite((x + i) * cellSize - cellOffset, y * cellSize, spriteCar2,8,0,8,8);
                        break;
                    default:
                        fill((x + i) * cellSize - cellOffset, y * cellSize, (x + i + 1) * cellSize - cellOffset, (y + 1) * cellSize, Pixel.BLACK);
                }
            }
            y++;
        }
        return true;
    }


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public static void main(String[] args) {
        int pixelDim = 8;
        Frogger acsGameEngine = new Frogger();
        if(acsGameEngine.construct(128,80,pixelDim,pixelDim) == RCode.OK){
            acsGameEngine.start();
        }
    }
}
