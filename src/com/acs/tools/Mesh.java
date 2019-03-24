package com.acs.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Mesh {
    List<Triangle> tris = new ArrayList<>();

    public Mesh() {

    }
    public Mesh(String filename) {
        loadMesh(filename);
    }

    private void loadMesh(String filename) {
        Path path = Paths.get(filename);

        try {
            List<String> lines = Files.readAllLines(path);
            List<Vector3D> vector3DList = new ArrayList<>();
            for (String line : lines) {
                if (line.startsWith("v")) {
                    Vector3D vector3D = new Vector3D();
                    StringTokenizer stringTokenizer = new StringTokenizer(line);
                    stringTokenizer.nextToken(); //ignore v;

                    String vertexString1 = stringTokenizer.nextToken();
                    String vertexString2 = stringTokenizer.nextToken();
                    String vertexString3 = stringTokenizer.nextToken();

                    vector3D.x = Float.parseFloat(vertexString1);
                    vector3D.y = Float.parseFloat(vertexString2);
                    vector3D.z = Float.parseFloat(vertexString3);

                    vector3DList.add(vector3D);
                }

                if (line.startsWith("f")) {
                    StringTokenizer stringTokenizer = new StringTokenizer(line);
                    stringTokenizer.nextToken(); //ignore f;

                    String faceString1 = stringTokenizer.nextToken();
                    String faceString2 = stringTokenizer.nextToken();
                    String faceString3 = stringTokenizer.nextToken();

                    int face1 = Integer.parseInt(faceString1);
                    int face2 = Integer.parseInt(faceString2);
                    int face3 = Integer.parseInt(faceString3);

                    tris.add(new Triangle(vector3DList.get(face1 - 1), vector3DList.get(face2 - 1), vector3DList.get(face3 - 1)));
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
