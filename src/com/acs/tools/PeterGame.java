package com.acs.tools;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PeterGame extends ACSGameEngine {
    private Mesh meshCube;
    private Matrix4x4 matProjection;
    private float theta = 0;

    private Vector3D vCamera = new Vector3D();

    @Override
    public boolean onUserCreate() {

        meshCube = new Mesh("objects/teapot.obj");

        //projection matrix
        matProjection = Matrix4x4.projection(90.0f, (float) getScreenHeight() / (float) getScreenWidth(),0.1f,1000.0f);

        return true;
    }


    @Override
    public boolean onUserUpdate(float elapsedTime) {

        clear(new Pixel(Color.BLACK));

        theta += 1.0f * elapsedTime;

        Matrix4x4 matRotZ = Matrix4x4.rotationZ(theta * 0.5f);
        Matrix4x4 matRotX = Matrix4x4.rotationX(theta);

        Matrix4x4 matTrans = Matrix4x4.translate(0,0,8);
        Matrix4x4 matWorld = Matrix4x4.multiply(matRotZ, matRotX);
        matWorld = Matrix4x4.multiply(matWorld, matTrans);

        List<Triangle> trianglesToRaster = new ArrayList<>();

        //Draw Triangles
        for (Triangle tri : meshCube.tris) {
            Triangle triProjected = new Triangle();
            Triangle triTransformed = new Triangle();

            triTransformed.points[0] = matrixMultiplyVector(matWorld, tri.points[0]);
            triTransformed.points[1] = matrixMultiplyVector(matWorld, tri.points[1]);
            triTransformed.points[2] = matrixMultiplyVector(matWorld, tri.points[2]);

            Vector3D line1 = subtactVector(triTransformed.points[1],triTransformed.points[0]);
            Vector3D line2 = subtactVector(triTransformed.points[2],triTransformed.points[0]);

            Vector3D normal = vectorCrossProduct(line1,line2);
            normal = vectorNormalize(normal);

            Vector3D cameraRay = subtactVector(triTransformed.points[0], vCamera);

            if (vectorDotProduct(normal,cameraRay) < 0) {

                //Illumination
                Vector3D lightDirection = new Vector3D(0, 0, -1);
                lightDirection = vectorNormalize(lightDirection);

                float dotProduct = Math.max(0.1f, vectorDotProduct(lightDirection, normal));

                triTransformed.pixel = getColour(dotProduct);

                //Project triangles from 3D --> 2D
                triProjected.points[0] = matrixMultiplyVector(matProjection,triTransformed.points[0]);
                triProjected.points[1] = matrixMultiplyVector(matProjection,triTransformed.points[1]);
                triProjected.points[2] = matrixMultiplyVector(matProjection,triTransformed.points[2]);

                triProjected.pixel = triTransformed.pixel;

                triProjected.points[0] = divideVector(triProjected.points[0], triProjected.points[0].w);
                triProjected.points[1] = divideVector(triProjected.points[1], triProjected.points[1].w);
                triProjected.points[2] = divideVector(triProjected.points[2], triProjected.points[2].w);

                Vector3D offsetView = new Vector3D(1,1,0);
                // Scale into view
                triProjected.points[0] = addVector(triProjected.points[0], offsetView);
                triProjected.points[1] = addVector(triProjected.points[1], offsetView);
                triProjected.points[2] = addVector(triProjected.points[2], offsetView);

                triProjected.points[0].x *= 0.5f * getScreenWidth();
                triProjected.points[0].y *= 0.5f * getScreenHeight();
                triProjected.points[1].x *= 0.5f * getScreenWidth();
                triProjected.points[1].y *= 0.5f * getScreenHeight();
                triProjected.points[2].x *= 0.5f * getScreenWidth();
                triProjected.points[2].y *= 0.5f * getScreenHeight();

                trianglesToRaster.add(triProjected);

//                drawTriangle(triProjected.points[0].x, triProjected.points[0].y,
//                        triProjected.points[1].x, triProjected.points[1].y,
//                        triProjected.points[2].x, triProjected.points[2].y,
//                        new Pixel(Color.BLACK));
            }
        }

        //Sort triangles back to front
        trianglesToRaster.sort((t1, t2) -> {
            float z1 = (t1.points[0].z + t1.points[1].z + t1.points[2].z) / 3.0f;
            float z2 = (t2.points[0].z + t2.points[1].z + t2.points[2].z) / 3.0f;
            return Float.compare(z2, z1);
        });

        for (Triangle triangle : trianglesToRaster) {
            fillTriangle(triangle.points[0].x, triangle.points[0].y,
                    triangle.points[1].x, triangle.points[1].y,
                    triangle.points[2].x, triangle.points[2].y,
                    triangle.pixel);
        }


        return true;
    }

    private void multiplyMatrixVector(Vector3D input, Vector3D output, Matrix4x4 matrix) {
        output.x = input.x * matrix.m[0][0] + input.y * matrix.m[1][0] + input.z * matrix.m[2][0] + matrix.m[3][0];
        output.y = input.x * matrix.m[0][1] + input.y * matrix.m[1][1] + input.z * matrix.m[2][1] + matrix.m[3][1];
        output.z = input.x * matrix.m[0][2] + input.y * matrix.m[1][2] + input.z * matrix.m[2][2] + matrix.m[3][2];
        float w = input.x * matrix.m[0][3] + input.y * matrix.m[1][3] + input.z * matrix.m[2][3] + matrix.m[3][3];

        if (w != 0.0f) {
            output.x /= w;
            output.y /= w;
            output.z /= w;
        }
    }

    private Vector3D matrixMultiplyVector(Matrix4x4 matrix, Vector3D input){
        Vector3D v = new Vector3D();
        v.x = input.x * matrix.m[0][0] + input.y * matrix.m[1][0] + input.z * matrix.m[2][0] + matrix.m[3][0];
        v.y = input.x * matrix.m[0][1] + input.y * matrix.m[1][1] + input.z * matrix.m[2][1] + matrix.m[3][1];
        v.z = input.x * matrix.m[0][2] + input.y * matrix.m[1][2] + input.z * matrix.m[2][2] + matrix.m[3][2];
        v.w = input.x * matrix.m[0][3] + input.y * matrix.m[1][3] + input.z * matrix.m[2][3] + matrix.m[3][3];
        return v;
    }

    private Pixel getColour(float luminance) {
        int value = (int) (255 * luminance);
        return new Pixel(new Color(value, value, value));
    }


    private Vector3D addVector(Vector3D v1, Vector3D v2) {
        return new Vector3D(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
    }

    private Vector3D subtactVector(Vector3D v1, Vector3D v2) {
        return new Vector3D(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
    }

    private Vector3D multiplyVector(Vector3D v1, float k) {
        return new Vector3D(v1.x * k, v1.y * k, v1.z * k);
    }

    private Vector3D divideVector(Vector3D v1, float k) {
        return new Vector3D(v1.x / k, v1.y / k, v1.z / k);
    }

    private float vectorDotProduct(Vector3D v1, Vector3D v2) {
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    }

    private float vectorLength(Vector3D v) {
        return (float) Math.sqrt(vectorDotProduct(v, v));
    }

    private Vector3D vectorNormalize(Vector3D v) {
        float l = vectorLength(v);
        return new Vector3D(v.x / l, v.y / l, v.z / l);
    }

    private Vector3D vectorCrossProduct(Vector3D v1, Vector3D v2) {
        return new Vector3D(v1.y * v2.z - v1.z * v2.y,
                v1.z * v2.x - v1.x * v2.z,
                v1.x * v2.y - v1.y * v2.x);
    }

}
