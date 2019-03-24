package com.acs.tools;

public class Main {

    public static void main(String[] args) {
        int pixelDim = 4;
	    PeterGame acsGameEngine = new PeterGame();
	    if(acsGameEngine.construct(256,240,pixelDim,pixelDim) == RCode.OK){
            acsGameEngine.start();
        }

    }
}
