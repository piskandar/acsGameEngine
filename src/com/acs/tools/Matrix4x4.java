package com.acs.tools;

public class Matrix4x4 {
    float[][] m = new float[4][4];

    public Matrix4x4() {
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[i].length; j++) {
                m[i][j] = 0;
            }
        }
    }
}
