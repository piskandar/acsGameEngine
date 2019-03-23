package com.acs.tools;

import java.awt.*;

public class PeterGame extends ACSGameEngine {
    private Mesh meshCube;
    private Matrix4x4 matProjection;

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

        //Draw Triangles
        for (Triangle tri : meshCube.tris) {
            Triangle triProjected = new Triangle();
            Triangle triTranslated = Triangle.copy(tri);

            triTranslated.points[0].z = tri.points[0].z + 3.0f;
            triTranslated.points[1].z = tri.points[1].z + 3.0f;
            triTranslated.points[2].z = tri.points[2].z + 3.0f;

            multipleMatrixVector(triTranslated.points[0],triProjected.points[0], matProjection);
            multipleMatrixVector(triTranslated.points[1],triProjected.points[1], matProjection);
            multipleMatrixVector(triTranslated.points[2],triProjected.points[2], matProjection);

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

            drawTriangle(triProjected.points[0].x, triProjected.points[0].y,
                    triProjected.points[1].x, triProjected.points[1].y,
                    triProjected.points[2].x, triProjected.points[2].y,
                    Pixel.WHITE);
        }

        return true;
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
