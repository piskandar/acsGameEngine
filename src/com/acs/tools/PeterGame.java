package com.acs.tools;

import java.awt.*;

public class PeterGame extends ACSGameEngine {
    private Mesh meshCube;
    private Matrix4x4 matProjection;
    private float theta = 0;

    Vector3D vCamera = new Vector3D();

    @Override
    public boolean onUserCreate() {
        meshCube = new Mesh();

        // SOUTH
        meshCube.tris.add(new Triangle(new Vector3D(0.0f, 0.0f, 0.0f), new Vector3D(0.0f, 1.0f, 0.0f), new Vector3D(1.0f, 1.0f, 0.0f)));
        meshCube.tris.add(new Triangle(new Vector3D(0.0f, 0.0f, 0.0f), new Vector3D(1.0f, 1.0f, 0.0f), new Vector3D(1.0f, 0.0f, 0.0f)));

        // EAST
        meshCube.tris.add(new Triangle(new Vector3D(1.0f, 0.0f, 0.0f), new Vector3D(1.0f, 1.0f, 0.0f), new Vector3D(1.0f, 1.0f, 1.0f)));
        meshCube.tris.add(new Triangle(new Vector3D(1.0f, 0.0f, 0.0f), new Vector3D(1.0f, 1.0f, 1.0f), new Vector3D(1.0f, 0.0f, 1.0f)));

        // NORTH
        meshCube.tris.add(new Triangle(new Vector3D(1.0f, 0.0f, 1.0f), new Vector3D(1.0f, 1.0f, 1.0f), new Vector3D(0.0f, 1.0f, 1.0f)));
        meshCube.tris.add(new Triangle(new Vector3D(1.0f, 0.0f, 1.0f), new Vector3D(0.0f, 1.0f, 1.0f), new Vector3D(0.0f, 0.0f, 1.0f)));

        // WEST
        meshCube.tris.add(new Triangle(new Vector3D(0.0f, 0.0f, 1.0f), new Vector3D(0.0f, 1.0f, 1.0f), new Vector3D(0.0f, 1.0f, 0.0f)));
        meshCube.tris.add(new Triangle(new Vector3D(0.0f, 0.0f, 1.0f), new Vector3D(0.0f, 1.0f, 0.0f), new Vector3D(0.0f, 0.0f, 0.0f)));

        // TOP
        meshCube.tris.add(new Triangle(new Vector3D(0.0f, 1.0f, 0.0f), new Vector3D(0.0f, 1.0f, 1.0f), new Vector3D(1.0f, 1.0f, 1.0f)));
        meshCube.tris.add(new Triangle(new Vector3D(0.0f, 1.0f, 0.0f), new Vector3D(1.0f, 1.0f, 1.0f), new Vector3D(1.0f, 1.0f, 0.0f)));

        // BOTTOM
        meshCube.tris.add(new Triangle(new Vector3D(1.0f, 0.0f, 1.0f), new Vector3D(0.0f, 0.0f, 1.0f), new Vector3D(0.0f, 0.0f, 0.0f)));
        meshCube.tris.add(new Triangle(new Vector3D(1.0f, 0.0f, 1.0f), new Vector3D(0.0f, 0.0f, 0.0f), new Vector3D(1.0f, 0.0f, 0.0f)));

        //projection matrix

        float near = 0.1f;
        float far = 1000.0f;
        float fov = 90f;
        float aspectRatio = (float) getScreenHeight() / (float) getScreenWidth();
        float fovRad =  1.0f / (float)Math.tan(fov * 0.5f / 180.0f * 3.14159f);

        matProjection = new Matrix4x4();
        matProjection.m[0][0] = aspectRatio * fovRad;
        matProjection.m[1][1] = fovRad;
        matProjection.m[2][2] = far/ (far - near);
        matProjection.m[3][2] = (-far * near) / (far - near);
        matProjection.m[2][3] = 1.0f;
        matProjection.m[3][3] = 0;

        return true;
    }

    @Override
    public boolean onUserUpdate(float elapsedTime) {

        clear(new Pixel(Color.BLACK));

        Matrix4x4 matRotZ = new Matrix4x4();
        Matrix4x4 matRotX = new Matrix4x4();

        theta += 1.0f * elapsedTime;

        // Rotation Z
        matRotZ.m[0][0] = (float) Math.cos(theta);
        matRotZ.m[0][1] = (float) Math.sin(theta);
        matRotZ.m[1][0] = (float) Math.sin(theta) * -1.0f;
        matRotZ.m[1][1] = (float) Math.cos(theta);
        matRotZ.m[2][2] = 1;
        matRotZ.m[3][3] = 1;

        // Rotation X
        matRotX.m[0][0] = 1;
        matRotX.m[1][1] = (float) Math.cos(theta * 0.5f);
        matRotX.m[1][2] = (float) Math.sin(theta * 0.5f);
        matRotX.m[2][1] = (float) Math.sin(theta * 0.5f) * -1.0f;
        matRotX.m[2][2] = (float) Math.cos(theta * 0.5f);
        matRotX.m[3][3] = 1;


        //Draw Triangles
        for (Triangle tri : meshCube.tris) {
            Triangle triProjected = new Triangle();
            Triangle triRotatedZ = new Triangle();
            Triangle triRotatedZX = new Triangle();


            // Rotate in Z-Axis
            multipleMatrixVector(tri.points[0],triRotatedZ.points[0], matRotZ);
            multipleMatrixVector(tri.points[1],triRotatedZ.points[1], matRotZ);
            multipleMatrixVector(tri.points[2],triRotatedZ.points[2], matRotZ);

            multipleMatrixVector(triRotatedZ.points[0],triRotatedZX.points[0], matRotX);
            multipleMatrixVector(triRotatedZ.points[1],triRotatedZX.points[1], matRotX);
            multipleMatrixVector(triRotatedZ.points[2],triRotatedZX.points[2], matRotX);

            // Offset into the screen
            Triangle triTranslated = Triangle.copy(triRotatedZX);

            triTranslated.points[0].z = triRotatedZX.points[0].z + 3.0f;
            triTranslated.points[1].z = triRotatedZX.points[1].z + 3.0f;
            triTranslated.points[2].z = triRotatedZX.points[2].z + 3.0f;

            Vector3D normal = new Vector3D();
            Vector3D line1 = new Vector3D();
            Vector3D line2 = new Vector3D();

            line1.x = triTranslated.points[1].x - triTranslated.points[0].x;
            line1.y = triTranslated.points[1].y - triTranslated.points[0].y;
            line1.z = triTranslated.points[1].z - triTranslated.points[0].z;

            line2.x = triTranslated.points[2].x - triTranslated.points[0].x;
            line2.y = triTranslated.points[2].y - triTranslated.points[0].y;
            line2.z = triTranslated.points[2].z - triTranslated.points[0].z;

            normal.x = line1.y * line2.z - line1.z * line2.y;
            normal.y = line1.z * line2.x - line1.x * line2.z;
            normal.z = line1.x * line2.y - line1.y * line2.x;

            double length = Math.sqrt(normal.x * normal.x + normal.y * normal.y + normal.z * normal.z);
            normal.x /= length;
            normal.y /= length;
            normal.z /= length;

            if( normal.x * (triTranslated.points[0].x - vCamera.x)+
                normal.y * (triTranslated.points[0].y - vCamera.y)+
                normal.z * (triTranslated.points[0].z - vCamera.z) < 0)
            {
                multipleMatrixVector(triTranslated.points[0], triProjected.points[0], matProjection);
                multipleMatrixVector(triTranslated.points[1], triProjected.points[1], matProjection);
                multipleMatrixVector(triTranslated.points[2], triProjected.points[2], matProjection);

                // Scale into view
                triProjected.points[0].x += 1.0f;
                triProjected.points[0].y += 1.0f;
                triProjected.points[1].x += 1.0f;
                triProjected.points[1].y += 1.0f;
                triProjected.points[2].x += 1.0f;
                triProjected.points[2].y += 1.0f;
                triProjected.points[0].x *= 0.5f * getScreenWidth();
                triProjected.points[0].y *= 0.5f * getScreenHeight();
                triProjected.points[1].x *= 0.5f * getScreenWidth();
                triProjected.points[1].y *= 0.5f * getScreenHeight();
                triProjected.points[2].x *= 0.5f * getScreenWidth();
                triProjected.points[2].y *= 0.5f * getScreenHeight();

                fillTriangle(triProjected.points[0].x, triProjected.points[0].y,
                        triProjected.points[1].x, triProjected.points[1].y,
                        triProjected.points[2].x, triProjected.points[2].y,
                        Pixel.WHITE);
            }
        }

        return true;
    }

    private Triangle multipleMatrixVector(Triangle input, Matrix4x4 matrix) {
        Triangle output = new Triangle();
        for(int i = 0; i < 3; i ++){
            output.points[i].x = input.points[i].x * matrix.m[0][0] + input.points[i].y * matrix.m[1][0] + input.points[i].z * matrix.m[2][0] + matrix.m[3][0];
            output.points[i].y = input.points[i].x * matrix.m[0][1] + input.points[i].y * matrix.m[1][1] + input.points[i].z * matrix.m[2][1] + matrix.m[3][1];
            output.points[i].z = input.points[i].x * matrix.m[0][2] + input.points[i].y * matrix.m[1][2] + input.points[i].z * matrix.m[2][2] + matrix.m[3][2];
            float w = input.points[i].x * matrix.m[0][3] + input.points[i].y * matrix.m[1][3] + input.points[i].z * matrix.m[2][3] + matrix.m[3][3];

            if (w != 0.0f)
            {
                output.points[i].x /= w; output.points[i].y /= w; output.points[i].z /= w;
            }
        }

        return output;
    }


    void multipleMatrixVector(Vector3D input, Vector3D output, Matrix4x4 matrix){
        output.x = input.x * matrix.m[0][0] + input.y * matrix.m[1][0] + input.z * matrix.m[2][0] + matrix.m[3][0];
        output.y = input.x * matrix.m[0][1] + input.y * matrix.m[1][1] + input.z * matrix.m[2][1] + matrix.m[3][1];
        output.z = input.x * matrix.m[0][2] + input.y * matrix.m[1][2] + input.z * matrix.m[2][2] + matrix.m[3][2];
        float w = input.x * matrix.m[0][3] + input.y * matrix.m[1][3] + input.z * matrix.m[2][3] + matrix.m[3][3];

        if (w != 0.0f)
        {
            output.x /= w; output.y /= w; output.z /= w;
        }
    }


}
