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

    public static Matrix4x4 newIdenity(){
        Matrix4x4 matrix4x4 = new Matrix4x4();
        matrix4x4.m[0][0] = 1.0f;
        matrix4x4.m[1][1] = 1.0f;
        matrix4x4.m[2][2] = 1.0f;
        matrix4x4.m[3][3] = 1.0f;
        return matrix4x4;
    }

    public static Matrix4x4 rotationZ(float angleRad){
        // Rotation Z
        Matrix4x4 matrix = new Matrix4x4();
        matrix.m[0][0] = (float) Math.cos(angleRad);
        matrix.m[0][1] = (float) Math.sin(angleRad);
        matrix.m[1][0] = (float) Math.sin(angleRad) * -1.0f;
        matrix.m[1][1] = (float) Math.cos(angleRad);
        matrix.m[2][2] = 1;
        matrix.m[3][3] = 1;
        return matrix;
    }

    public static Matrix4x4 rotationX(float angleRad){
        Matrix4x4 matrix = new Matrix4x4();
        matrix.m[0][0] = 1;
        matrix.m[1][1] = (float) Math.cos(angleRad);
        matrix.m[1][2] = (float) Math.sin(angleRad);
        matrix.m[2][1] = (float) Math.sin(angleRad) * -1.0f;
        matrix.m[2][2] = (float) Math.cos(angleRad);
        matrix.m[3][3] = 1;
        return matrix;
    }

    public static Matrix4x4 rotationY(float angleRad){
        Matrix4x4 matrix = new Matrix4x4();
        matrix.m[0][0] = (float) Math.cos(angleRad);
        matrix.m[0][2] = (float) Math.sin(angleRad);
        matrix.m[2][0] = (float) Math.sin(angleRad) * -1.0f;
        matrix.m[1][1] = 1.0f;
        matrix.m[2][2] = (float) Math.cos(angleRad);
        matrix.m[3][3] = 1.0f;
        return matrix;
    }

    public static Matrix4x4 translate(float x, float y, float z){
        Matrix4x4 matrix = new Matrix4x4();
        matrix.m[0][0] = 1.0f;
        matrix.m[1][1] = 1.0f;
        matrix.m[2][2] = 1.0f;
        matrix.m[3][3] = 1.0f;
        matrix.m[3][0] = x;
        matrix.m[3][1] = y;
        matrix.m[3][2] = z;
        return matrix;
    }

    public static Matrix4x4 projection(float fov, float aspectRatio, float near, float far){

        float fovRad = 1.0f / (float) Math.tan(fov * 0.5f / 180.0f * 3.14159f);

        Matrix4x4 matrix = new Matrix4x4();
        matrix.m[0][0] = aspectRatio * fovRad;
        matrix.m[1][1] = fovRad;
        matrix.m[2][2] = far / (far - near);
        matrix.m[3][2] = (-far * near) / (far - near);
        matrix.m[2][3] = 1.0f;
        matrix.m[3][3] = 0;

        return matrix;
    }

    public static Matrix4x4 multiply(Matrix4x4 m1, Matrix4x4 m2){
        Matrix4x4 matrix = new Matrix4x4();
        for (int c = 0; c < 4; c++)
            for (int r = 0; r < 4; r++)
                matrix.m[r][c] = m1.m[r][0] * m2.m[0][c] + m1.m[r][1] * m2.m[1][c] + m1.m[r][2] * m2.m[2][c] + m1.m[r][3] * m2.m[3][c];
        return matrix;
    }
    /*

        // Rotation X
        matRotX.m[0][0] = 1;
        matRotX.m[1][1] = (float) Math.cos(theta * 0.5f);
        matRotX.m[1][2] = (float) Math.sin(theta * 0.5f);
        matRotX.m[2][1] = (float) Math.sin(theta * 0.5f) * -1.0f;
        matRotX.m[2][2] = (float) Math.cos(theta * 0.5f);
        matRotX.m[3][3] = 1;
     */
}
