package com.acs.tools;

public class Main {

    public static void main(String[] args) {
	    PeterGame acsGameEngine = new PeterGame();
	    if(acsGameEngine.construct(256,240,4,4) == RCode.OK){
            acsGameEngine.start();
        }

    }
}
