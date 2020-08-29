import com.jayfella.mesh.BlobTile;
import com.jayfella.mesh.JmeMesh;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.MaterialKey;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.system.AppSettings;
import jdk.nashorn.internal.ir.Block;
import noise.PerlinNoise;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestBlobTileSet extends SimpleApplication {

    public static void main(String... args) {

        TestBlobTileSet testBlobTileSet = new TestBlobTileSet();

        AppSettings appSettings = new AppSettings(true);
        appSettings.setResolution(1280, 720);
        appSettings.setAudioRenderer(null);

        testBlobTileSet.setSettings(appSettings);
        testBlobTileSet.setShowSettings(false);
        testBlobTileSet.start();

    }

    PerlinNoise perlinNoise = new PerlinNoise();

    @Override
    public void simpleInitApp() {

        flyCam.setMoveSpeed(50);
        flyCam.setDragToRotate(true);

        // determines the size of each cell and the bits for our grid system.
        int cellSize = 16;
        int cellBitshift = 4;

        int tilesX = 16;
        int tilesY = 8;

        boolean[][] tiles = new boolean[cellSize][cellSize];
        byte[][] configurations = new byte[cellSize][cellSize];

        perlinNoise.setSeed(FastMath.nextRandomInt());

        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setTexture("ColorMap", assetManager.loadTexture("Textures/dirt-spritesheet.png"));

        Node worldNode = new Node("Game World");

        // populate

        // for each cell in our grid
        for (int xCell = 0; xCell < tilesX; xCell++) {
            for (int yCell = 0; yCell < tilesY; yCell++) {

                // cellSize * xCell
                int worldX = xCell << cellBitshift;
                int worldY = yCell << cellBitshift;

                // when we determine this is air or not from a noise value.
                float cutoff = 0.2f;

                float noiseScale = 0.06f;

                // for each block in our cell.
                // - populate the cells with a solid/air value.
                // - determine the configuration of each block.
                // - generate the mesh.

                for (int x = 0; x < cellSize; x++) {
                    for (int y = 0; y < cellSize; y++) {

                        double noise = scaleNoise(worldX + x, worldY + y, noiseScale);
                        boolean solid = noise > cutoff;

                        tiles[x][y] = solid;

                    }
                }

                for (int x = 0; x < cellSize; x++) {
                    for (int y = 0; y < cellSize; y++) {

                        int posX = worldX + x;
                        int posY = worldY + y;

                        // determine if there are any blocks on any sides.
                        // Use the noise so we can query the sides.
                        // In your game you may want to query existing tiles first
                        // for modified values (pre-generation) first.
                        // Any new changes to the block configuration (outside interference)
                        // will be displayed instead.

                        boolean n = scaleNoise(posX + 0, posY + 1, noiseScale) > cutoff;
                        boolean e = scaleNoise(posX + 1, posY + 0, noiseScale) > cutoff;
                        boolean s = scaleNoise(posX + 0, posY - 1, noiseScale) > cutoff;
                        boolean w = scaleNoise(posX - 1, posY + 0, noiseScale) > cutoff;

                        boolean ne = scaleNoise(posX + 1, posY + 1, noiseScale) > cutoff;
                        boolean se = scaleNoise(posX + 1, posY - 1, noiseScale) > cutoff;
                        boolean sw = scaleNoise(posX - 1, posY - 1, noiseScale) > cutoff;
                        boolean nw = scaleNoise(posX- 1, posY + 1, noiseScale) > cutoff;

                        byte configuration = BlobTile.getConfiguration(n, e, s, w, nw, ne, sw, se);

                        configurations[x][y] = configuration;

                    }
                }

                // generate the mesh
                Mesh mesh = generateSpriteSheetMesh(tiles, configurations);
                Geometry geometry = new Geometry("Cell " + xCell + "," + yCell, mesh);
                geometry.setMaterial(material);

                geometry.setLocalTranslation(worldX, worldY, 0);

                worldNode.attachChild(geometry);
            }
        }

        cam.setLocation(new Vector3f(
                ( (tilesX * cellSize) * 0.5f),
                ( (tilesY * cellSize) * 0.5f),
                60
        ));

        rootNode.attachChild(worldNode);

    }

    private double scaleNoise(float x, float y, float scale) {
        return perlinNoise.getNoise(x * scale, y * scale);
    }

    public Mesh generateSpriteSheetMesh(boolean[][] cell, byte[][] configuration) {

        List<Vector3f> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        List<Vector2f> texCoords = new ArrayList<>();

        // add the lookup int to "voxel data".
        // x = which texture variant to show (top-left, etc.. one of the 48 variants.).
        // y = which texture to show (grass, dirt, etc)
        // z = lightValue (for now)
        // right now we only have one type, so it's just zero
        List<Vector3f> blockData = new ArrayList<>();

        int indexStart = 0;

        for (int y = 0; y < cell[0].length; y++) {
            for (int x = 0; x < cell.length; x++) {

                boolean solid = cell[x][y];

                if (solid) {

                    Collections.addAll(vertices,
                            new Vector3f(x + 0, y + 0, 0),
                            new Vector3f(x + 1, y + 0, 0),
                            new Vector3f(x + 1, y + 1, 0),
                            new Vector3f(x + 0, y + 1, 0));

                    Collections.addAll(indices,
                            indexStart + 0,
                            indexStart + 1,
                            indexStart + 2,
                            indexStart + 0,
                            indexStart + 2,
                            indexStart + 3);


                    // calculate the row and column from the texture index.
                    int textureId = BlobTile.getTextureIndex(configuration[x][y]);
                    int lookupId = BlobTile.toArtistsLayout(textureId);

                    // row    = (int)(index / width)
                    // column = index % width
                    int row = lookupId / 7;
                    int col = lookupId % 7;

                    float size = 1.0f / 7.0f;

                    float bl_x = size * col;
                    float bl_y = size * row;

                    // bl, br, tr, tl
                    Collections.addAll(texCoords,
                            new Vector2f(bl_x, bl_y),
                            new Vector2f(bl_x + size, bl_y),
                            new Vector2f(bl_x + size, bl_y + size),
                            new Vector2f(bl_x, bl_y + size)
                    );

                    indexStart += 4;
                }
            }
        }

        JmeMesh mesh = new JmeMesh();
        mesh.set(VertexBuffer.Type.Position, vertices);
        mesh.set(VertexBuffer.Type.Index, indices);
        mesh.set(VertexBuffer.Type.TexCoord, texCoords);
        mesh.set(VertexBuffer.Type.TexCoord2, blockData);

        return mesh;
    }

}
