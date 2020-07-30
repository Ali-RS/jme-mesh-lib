package com.jayfella.mesh;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Derived from Sebastian Lague's Youtube Series: Procedural Cave Generation
 * License: MIT
 * https://github.com/SebLague/Procedural-Cave-Generation
 *
 * Converted to Java by @jayfella - a.k.a James Khan
 */
public class MarchingSquaresMeshGenerator {

    public SquareGrid squareGrid;
    List<Vector3f> vertices;
    List<Integer> triangles;

    public Mesh buildMesh(boolean[][] map, float squareSize) {

        squareGrid = new SquareGrid(map, squareSize);

        vertices = new ArrayList<>();
        triangles = new ArrayList<>();

        for (int x = 0; x < squareGrid.squares[0].length; x ++) {
            for (int y = 0; y < squareGrid.squares[1].length; y ++) {
                TriangulateSquare(squareGrid.squares[x][y]);
            }
        }

        Mesh mesh = new Mesh();

        Vector3f[] vertArray = vertices.toArray(new Vector3f[0]);
        FloatBuffer pb = BufferUtils.createFloatBuffer(vertArray);
        mesh.setBuffer(VertexBuffer.Type.Position, 3, pb);

        Vector3f[] normArray = new Vector3f[vertArray.length];
        Arrays.fill(normArray, new Vector3f(0, 1, 0));
        FloatBuffer nb = BufferUtils.createFloatBuffer(normArray);
        mesh.setBuffer(VertexBuffer.Type.Normal, 3, nb);

        int[] triIndexes = new int[triangles.size()];
        for (int i = 0; i < triIndexes.length; i++) {
            triIndexes[i] = triangles.get(i);
        }

        IntBuffer ib = BufferUtils.createIntBuffer(triIndexes);
        mesh.setBuffer(VertexBuffer.Type.Index, 3, ib);

        Vector2f[] uvs = new Vector2f[vertArray.length];

        int tileCountX = map[0].length;
        int tileCountY = map[0].length;

        for (int i =0; i < uvs.length; i ++) {

            float percentX = (vertArray[i].x / map[0].length) * tileCountX;
            float percentY = (vertArray[i].z / map[1].length) * tileCountY;

            uvs[i] = new Vector2f(percentX,percentY);
        }
        FloatBuffer uvb = BufferUtils.createFloatBuffer(uvs);
        mesh.setBuffer(VertexBuffer.Type.TexCoord, 2, uvb);

        // we don't need these anymore.
        vertices.clear();
        triangles.clear();
        squareGrid = null;

        mesh.updateBound();

        return mesh;
    }

    void TriangulateSquare(Square square) {
        switch (square.configuration) {
            case 0:
                break;

            // 1 points:
            case 1:
                MeshFromPoints(square.centreBottom, square.bottomLeft, square.centreLeft);
                break;
            case 2:
                MeshFromPoints(square.centreRight, square.bottomRight, square.centreBottom);
                break;
            case 4:
                MeshFromPoints(square.centreTop, square.topRight, square.centreRight);
                break;
            case 8:
                MeshFromPoints(square.topLeft, square.centreTop, square.centreLeft);
                break;

            // 2 points:
            case 3:
                MeshFromPoints(square.centreRight, square.bottomRight, square.bottomLeft, square.centreLeft);
                break;
            case 6:
                MeshFromPoints(square.centreTop, square.topRight, square.bottomRight, square.centreBottom);
                break;
            case 9:
                MeshFromPoints(square.topLeft, square.centreTop, square.centreBottom, square.bottomLeft);
                break;
            case 12:
                MeshFromPoints(square.topLeft, square.topRight, square.centreRight, square.centreLeft);
                break;
            case 5:
                MeshFromPoints(square.centreTop, square.topRight, square.centreRight, square.centreBottom, square.bottomLeft, square.centreLeft);
                break;
            case 10:
                MeshFromPoints(square.topLeft, square.centreTop, square.centreRight, square.bottomRight, square.centreBottom, square.centreLeft);
                break;

            // 3 point:
            case 7:
                MeshFromPoints(square.centreTop, square.topRight, square.bottomRight, square.bottomLeft, square.centreLeft);
                break;
            case 11:
                MeshFromPoints(square.topLeft, square.centreTop, square.centreRight, square.bottomRight, square.bottomLeft);
                break;
            case 13:
                MeshFromPoints(square.topLeft, square.topRight, square.centreRight, square.centreBottom, square.bottomLeft);
                break;
            case 14:
                MeshFromPoints(square.topLeft, square.topRight, square.bottomRight, square.centreBottom, square.centreLeft);
                break;

            // 4 point:
            case 15:
                MeshFromPoints(square.topLeft, square.topRight, square.bottomRight, square.bottomLeft);
                break;
        }

    }

    void MeshFromPoints(Node... points) {
        AssignVertices(points);

        if (points.length >= 3)
            CreateTriangle(points[0], points[1], points[2]);
        if (points.length >= 4)
            CreateTriangle(points[0], points[2], points[3]);
        if (points.length >= 5)
            CreateTriangle(points[0], points[3], points[4]);
        if (points.length >= 6)
            CreateTriangle(points[0], points[4], points[5]);

    }

    void AssignVertices(Node[] points) {
        for (Node point : points) {
            if (point.vertexIndex == -1) {
                point.vertexIndex = vertices.size();
                vertices.add(point.position);
            }
        }
    }

    void CreateTriangle(Node a, Node b, Node c) {
        triangles.add(a.vertexIndex);
        triangles.add(b.vertexIndex);
        triangles.add(c.vertexIndex);
    }

    private static class SquareGrid {
        private final Square[][] squares;

        public SquareGrid(boolean[][] map, float squareSize) {
            int nodeCountX = map[0].length;
            int nodeCountY = map[1].length;
            float mapWidth = nodeCountX * squareSize;
            float mapHeight = nodeCountY * squareSize;

            ControlNode[][] controlNodes = new ControlNode[nodeCountX][nodeCountY];

            for (int x = 0; x < nodeCountX; x ++) {
                for (int y = 0; y < nodeCountY; y ++) {
                    Vector3f pos = new Vector3f(-mapWidth/2 + x * squareSize + squareSize/2, 0, -mapHeight/2 + y * squareSize + squareSize/2);
                    controlNodes[x][y] = new ControlNode(pos,map[x][y], squareSize);
                }
            }

            squares = new Square[nodeCountX -1][nodeCountY -1];
            for (int x = 0; x < nodeCountX-1; x ++) {
                for (int y = 0; y < nodeCountY-1; y ++) {
                    squares[x][y] = new Square(controlNodes[x][y+1], controlNodes[x+1][y+1], controlNodes[x+1][y], controlNodes[x][y]);
                }
            }

        }
    }

    private static class Square {

        private final ControlNode topLeft, topRight, bottomRight, bottomLeft;
        private final Node centreTop, centreRight, centreBottom, centreLeft;
        private int configuration;

        public Square (ControlNode _topLeft, ControlNode _topRight, ControlNode _bottomRight, ControlNode _bottomLeft) {
            topLeft = _topLeft;
            topRight = _topRight;
            bottomRight = _bottomRight;
            bottomLeft = _bottomLeft;

            centreTop = topLeft.right;
            centreRight = bottomRight.above;
            centreBottom = bottomLeft.right;
            centreLeft = bottomLeft.above;

            // we could probably use bitmasking instead of math here.
            // we can also use this data to determine which part of a tilemap to show.
            // if each vertex has a material index in its mesh, we can also set "materials".

            if (topLeft.active)
                configuration += 8;
            if (topRight.active)
                configuration += 4;
            if (bottomRight.active)
                configuration += 2;
            if (bottomLeft.active)
                configuration += 1;
        }

    }

    private static class Node {
        protected final Vector3f position;
        private int vertexIndex = -1;

        public Node(Vector3f _pos) {
            position = _pos;
        }
    }

    private static class ControlNode extends Node {

        private final boolean active;
        private final Node above, right;

		public ControlNode(Vector3f _pos, boolean _active, float squareSize) {
		    super(_pos);
            active = _active;
            above = new Node(position.add(Vector3f.UNIT_Z.mult(squareSize / 2f)));
            right = new Node(position.add(Vector3f.UNIT_X.mult(squareSize / 2f)));
        }

    }

}
