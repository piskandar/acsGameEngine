package com.acs.tools;

public class Triangle {
    Vector3D[] points ;

    public Triangle() {
        points = new Vector3D[3];
        points[0] = new Vector3D(0,0,0);
        points[1] = new Vector3D(0,0,0);
        points[2] = new Vector3D(0,0,0);
    }

    public Triangle(Vector3D[] points) {
        this.points = points;
    }

    public Triangle(Vector3D v1, Vector3D v2, Vector3D v3) {
        this();
        points[0] = v1;
        points[1] = v2;
        points[2] = v3;
    }

    public static Triangle copy(Triangle original){
        Triangle triangle = new Triangle();
        triangle.points = new Vector3D[3];
        triangle.points[0] = new Vector3D(original.points[0].x, original.points[0].y, original.points[0].z);
        triangle.points[1] = new Vector3D(original.points[1].x, original.points[1].y, original.points[1].z);
        triangle.points[2] = new Vector3D(original.points[2].x, original.points[2].y, original.points[2].z);

        return triangle;
    }
}
