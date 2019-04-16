package com.acs.tools;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class PeterGame extends ACSGameEngine {
    private Mesh meshCube;
    private Matrix4x4 matProjection;
    private float theta = 0.0f;

    private Vector3D vCamera = new Vector3D();
    private Vector3D vLookAt = new Vector3D();
    float yaw;

    @Override
    public boolean onUserCreate() {

        meshCube = new Mesh("objects/axis.obj");

        //projection matrix
        matProjection = Matrix4x4.projection(90.0f, (float) getScreenHeight() / (float) getScreenWidth(), 0.1f, 1000.0f);

        return true;
    }


    @Override
    public boolean onUserUpdate(float elapsedTime) {

        if (getKey(KeyEvent.VK_UP)) {
            vCamera.y += 8.0f * elapsedTime;
        }

        if (getKey(KeyEvent.VK_DOWN)) {
            vCamera.y -= 8.0f * elapsedTime;
        }

        if (getKey(KeyEvent.VK_LEFT)) {
            vCamera.x -= 8.0f * elapsedTime;
        }

        if (getKey(KeyEvent.VK_RIGHT)) {
            vCamera.x += 8.0f * elapsedTime;
        }

        Vector3D forward = multiplyVector(vLookAt, 8.0f * elapsedTime);

        if (getKey(KeyEvent.VK_W)) {
            vCamera = addVector(vCamera, forward);
        }

        if (getKey(KeyEvent.VK_S)) {
            vCamera = subtactVector(vCamera, forward);
        }

        if (getKey(KeyEvent.VK_A)) {
            yaw -= 2.0f * elapsedTime;
        }

        if (getKey(KeyEvent.VK_D)) {
            yaw += 2.0f * elapsedTime;
        }


        clear(new Pixel(Color.BLACK));

//        theta += 1.0f * elapsedTime;

        Matrix4x4 matRotZ = Matrix4x4.rotationZ(theta * 0.5f);
        Matrix4x4 matRotX = Matrix4x4.rotationX(theta);

        Matrix4x4 matTrans = Matrix4x4.translate(0, 0, 16);
        Matrix4x4 matWorld = Matrix4x4.multiply(matRotZ, matRotX);
        matWorld = Matrix4x4.multiply(matWorld, matTrans);

        Vector3D up = new Vector3D(0, 1, 0);

        Vector3D target = new Vector3D(0, 0, 1);
        Matrix4x4 matCameraRot = Matrix4x4.rotationY(yaw);
        vLookAt = matrixMultiplyVector(matCameraRot, target);
        target = addVector(vCamera, vLookAt);

        Matrix4x4 matCamera = pointAt(vCamera, target, up);
        Matrix4x4 matView = quickInverse(matCamera);

        List<Triangle> trianglesToRaster = new ArrayList<>();

        //Draw Triangles
        for (Triangle tri : meshCube.tris) {
            Triangle triProjected = new Triangle();
            Triangle triTransformed = new Triangle();
            Triangle triViewed = new Triangle();


            triTransformed.points[0] = matrixMultiplyVector(matWorld, tri.points[0]);
            triTransformed.points[1] = matrixMultiplyVector(matWorld, tri.points[1]);
            triTransformed.points[2] = matrixMultiplyVector(matWorld, tri.points[2]);

            Vector3D line1 = subtactVector(triTransformed.points[1], triTransformed.points[0]);
            Vector3D line2 = subtactVector(triTransformed.points[2], triTransformed.points[0]);

            Vector3D normal = vectorCrossProduct(line1, line2);
            normal = vectorNormalize(normal);

            Vector3D cameraRay = subtactVector(triTransformed.points[0], vCamera);

            if (vectorDotProduct(normal, cameraRay) < 0) {

                //Illumination
                Vector3D lightDirection = new Vector3D(0, 0, -1);
                lightDirection = vectorNormalize(lightDirection);

                float dotProduct = Math.max(0.1f, vectorDotProduct(lightDirection, normal));

                triTransformed.pixel = getColour(dotProduct);


                triViewed.points[0] = matrixMultiplyVector(matView, triTransformed.points[0]);
                triViewed.points[1] = matrixMultiplyVector(matView, triTransformed.points[1]);
                triViewed.points[2] = matrixMultiplyVector(matView, triTransformed.points[2]);

                int clippedTriangles = 0;
                Triangle[] clipped = new Triangle[2];
                clipped[0] = new Triangle();
                clipped[1] = new Triangle();

                clippedTriangles = Triangle_ClipAgainstPlane(new Vector3D(0.0f, 0.0f, 0.1f),
                        new Vector3D(0.0f, 0.0f, 1.0f), triViewed, clipped[0], clipped[1]);
                for (int n = 0; n < clippedTriangles; n++) {


                    //Project triangles from 3D --> 2D
                    triProjected.points[0] = matrixMultiplyVector(matProjection, triViewed.points[0]);
                    triProjected.points[1] = matrixMultiplyVector(matProjection, triViewed.points[1]);
                    triProjected.points[2] = matrixMultiplyVector(matProjection, triViewed.points[2]);

                    triProjected.pixel = triTransformed.pixel;

                    triProjected.points[0] = divideVector(triProjected.points[0], triProjected.points[0].w);
                    triProjected.points[1] = divideVector(triProjected.points[1], triProjected.points[1].w);
                    triProjected.points[2] = divideVector(triProjected.points[2], triProjected.points[2].w);

                    Vector3D offsetView = new Vector3D(1, 1, 0);
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
                }

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

                            drawTriangle(triangle.points[0].x, triangle.points[0].y,
                        triangle.points[1].x, triangle.points[1].y,
                        triangle.points[2].x, triangle.points[2].y,
                        new Pixel(Color.BLACK));
        }


        return true;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        keyboardState[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keyboardState[e.getKeyCode()] = false;
    }

    private Vector3D matrixMultiplyVector(Matrix4x4 matrix, Vector3D input) {
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


    public Matrix4x4 pointAt(Vector3D pos, Vector3D target, Vector3D up) {
        Vector3D newForward = subtactVector(target, pos);
        newForward = vectorNormalize(newForward);

        Vector3D a = multiplyVector(newForward, vectorDotProduct(up, newForward));
        Vector3D newUp = subtactVector(up, a);
        newUp = vectorNormalize(newUp);

        Vector3D newRight = vectorCrossProduct(newUp, newForward);

        Matrix4x4 matrix = new Matrix4x4();
        matrix.m[0][0] = newRight.x;
        matrix.m[0][1] = newRight.y;
        matrix.m[0][2] = newRight.z;
        matrix.m[0][3] = 0.0f;
        matrix.m[1][0] = newUp.x;
        matrix.m[1][1] = newUp.y;
        matrix.m[1][2] = newUp.z;
        matrix.m[1][3] = 0.0f;
        matrix.m[2][0] = newForward.x;
        matrix.m[2][1] = newForward.y;
        matrix.m[2][2] = newForward.z;
        matrix.m[2][3] = 0.0f;
        matrix.m[3][0] = pos.x;
        matrix.m[3][1] = pos.y;
        matrix.m[3][2] = pos.z;

        return matrix;
    }

    public Matrix4x4 quickInverse(Matrix4x4 m) // Only for Rotation/Translation Matrices
    {
        Matrix4x4 matrix = new Matrix4x4();
        matrix.m[0][0] = m.m[0][0];
        matrix.m[0][1] = m.m[1][0];
        matrix.m[0][2] = m.m[2][0];
        matrix.m[0][3] = 0.0f;
        matrix.m[1][0] = m.m[0][1];
        matrix.m[1][1] = m.m[1][1];
        matrix.m[1][2] = m.m[2][1];
        matrix.m[1][3] = 0.0f;
        matrix.m[2][0] = m.m[0][2];
        matrix.m[2][1] = m.m[1][2];
        matrix.m[2][2] = m.m[2][2];
        matrix.m[2][3] = 0.0f;
        matrix.m[3][0] = -(m.m[3][0] * matrix.m[0][0] + m.m[3][1] * matrix.m[1][0] + m.m[3][2] * matrix.m[2][0]);
        matrix.m[3][1] = -(m.m[3][0] * matrix.m[0][1] + m.m[3][1] * matrix.m[1][1] + m.m[3][2] * matrix.m[2][1]);
        matrix.m[3][2] = -(m.m[3][0] * matrix.m[0][2] + m.m[3][1] * matrix.m[1][2] + m.m[3][2] * matrix.m[2][2]);
        matrix.m[3][3] = 1.0f;
        return matrix;
    }

    private Vector3D vectorIntersecPlane(Vector3D planeP, Vector3D planeN, Vector3D lineStart, Vector3D lineEnd) {

        planeN = vectorNormalize(planeN);
        float plane_d = -vectorDotProduct(planeN, planeP);
        float ad = vectorDotProduct(lineStart, planeN);
        float bd = vectorDotProduct(lineEnd, planeN);
        float t = (-plane_d - ad) / (bd - ad);
        Vector3D lineStartToEnd = subtactVector(lineEnd, lineStart);
        Vector3D lineToIntersect = multiplyVector(lineStartToEnd, t);
        return addVector(lineStart, lineToIntersect);
    }

    private int Triangle_ClipAgainstPlane(Vector3D plane_p, Vector3D plane_n, Triangle in_tri, Triangle out_tri1, Triangle out_tri2) {
        plane_n = vectorNormalize(plane_n);

        Vector3D[] insidePoints = new Vector3D[3];
        Vector3D[] outsidePoints = new Vector3D[3];

        for (int i = 0; i < insidePoints.length; i++) {
            insidePoints[i] = new Vector3D();
            outsidePoints[i] = new Vector3D();
        }

        int insidePointCount = 0;
        int outsidePointCount = 0;

        float d0 = dist(in_tri.points[0], plane_p, plane_n);
        float d1 = dist(in_tri.points[1], plane_p, plane_n);
        float d2 = dist(in_tri.points[2], plane_p, plane_n);

        if (d0 >= 0) {
            insidePoints[insidePointCount++] = in_tri.points[0];
        } else {
            outsidePoints[outsidePointCount++] = in_tri.points[0];
        }
        if (d1 >= 0) {
            insidePoints[insidePointCount++] = in_tri.points[1];
        } else {
            outsidePoints[outsidePointCount++] = in_tri.points[1];
        }
        if (d2 >= 0) {
            insidePoints[insidePointCount++] = in_tri.points[2];
        } else {
            outsidePoints[outsidePointCount++] = in_tri.points[2];
        }

        if (insidePointCount == 0) {
            return 0;
        }

        if (insidePointCount == 3) {
            out_tri1 = in_tri;

            return 1;
        }

        if (insidePointCount == 1 && outsidePointCount == 2) {
            out_tri1.pixel = new Pixel(Color.BLUE);
            out_tri1.points[0] = insidePoints[0];
            out_tri1.points[1] = vectorIntersecPlane(plane_p, plane_n, insidePoints[0], outsidePoints[0]);
            out_tri1.points[2] = vectorIntersecPlane(plane_p, plane_n, insidePoints[1], outsidePoints[1]);

            return 1;
        }

        if (insidePointCount == 2 && outsidePointCount == 1) {
            out_tri1.pixel = new Pixel(Color.GREEN);
            out_tri2.pixel = new Pixel(Color.RED);

            out_tri1.points[0] = insidePoints[0];
            out_tri1.points[1] = insidePoints[1];
            out_tri1.points[2] = vectorIntersecPlane(plane_p, plane_n, insidePoints[0], outsidePoints[0]);

            out_tri2.points[0] = insidePoints[1];
            out_tri2.points[1] = out_tri1.points[2];
            out_tri2.points[2] = vectorIntersecPlane(plane_p, plane_n, insidePoints[1], outsidePoints[0]);

            return 2;
        }

        return 0;
    }

    private float dist(Vector3D p, Vector3D plane_p, Vector3D plane_n) {
        Vector3D n = vectorNormalize(p);
        return (plane_n.x * p.x + plane_n.y * p.y + plane_n.z * p.z - vectorDotProduct(plane_n, plane_p));
    }

}
