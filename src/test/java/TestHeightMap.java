import com.jayfella.mesh.HeightMapMesh;
import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.system.AppSettings;
import noise.PerlinNoise;

public class TestHeightMap extends SimpleApplication {

    public static void main(String... args) {

        TestHeightMap testHeightMap = new TestHeightMap();

        AppSettings appSettings = new AppSettings(true);
        appSettings.setResolution(1280, 720);
        appSettings.setAudioRenderer(null);

        testHeightMap.setSettings(appSettings);
        testHeightMap.setShowSettings(false);
        testHeightMap.start();

    }

    @Override
    public void simpleInitApp() {

        flyCam.setDragToRotate(true);
        flyCam.setMoveSpeed(30);

        // The size of the mesh.
        // the mesh size is +3 greater than the generated size. This accounts for the extra row and column required
        // to create the edge of the mesh, and an additional +1 on each side to allow us to calculate normals correctly
        // on the edges.
        int width = 35;
        int length = 35;

        // The height. We multiply the noise value by this value to create height greater than 1.
        int height = 4;

        // the coordinates to begin evaluating the noise.
        int[] coords = { 12, 43 };

        float[] heightmap = new float[width * length];

        PerlinNoise perlinNoise = new PerlinNoise("my seed".hashCode());

        // scale the coordinates to add more detail to the mesh.
        float noiseScale = 0.3f;

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < length; z++) {

                double noise = height * perlinNoise.getNoise(coords[0] + x * noiseScale, coords[1] + z * noiseScale);

                int index = x + z * length;

                heightmap[index] = (float) noise;
            }
        }

        HeightMapMesh heightMapMesh = new HeightMapMesh(heightmap);

        Geometry geometry = new Geometry("HeightMap", heightMapMesh);
        geometry.setMaterial(new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md"));

        // add some light so we can see it better.
        rootNode.addLight(new DirectionalLight(new Vector3f(-1, -1, -1).normalizeLocal(), ColorRGBA.White.clone()));
        rootNode.addLight(new AmbientLight(new ColorRGBA(0.4f, 0.4f, 0.4f, 1.0f)));

        rootNode.attachChild(geometry);

        // look at it.
        cam.setLocation(new Vector3f(width / 2f, 8, 0));
        cam.lookAt(new Vector3f(width / 2f, 0, length / 2f), Vector3f.UNIT_Y);

    }

}
