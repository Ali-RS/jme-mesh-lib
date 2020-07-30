import com.jayfella.mesh.marchingcubes.ArrayDensityVolume;
import com.jayfella.mesh.marchingcubes.DensityVolume;
import com.jayfella.mesh.MarchingCubesMeshGenerator;
import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.system.AppSettings;
import noise.GemsFractalDensityVolume;

public class TestMarchingCubes extends SimpleApplication {

    public static void main(String... args) {

        TestMarchingCubes testMarchingCubes = new TestMarchingCubes();

        AppSettings appSettings = new AppSettings(true);
        appSettings.setResolution(1280, 720);
        appSettings.setAudioRenderer(null);

        testMarchingCubes.setSettings(appSettings);
        testMarchingCubes.setShowSettings(false);
        testMarchingCubes.start();
    }

    @Override
    public void simpleInitApp() {

        flyCam.setDragToRotate(true);
        flyCam.setMoveSpeed(50);

        // a 3D noise generator.
        // https://developer.nvidia.com/gpugems/gpugems3/part-i-geometry/chapter-1-generating-complex-procedural-terrains-using-gpu
        GemsFractalDensityVolume densityVolume = new GemsFractalDensityVolume("my seed".hashCode());

        // the coordinates of the density volume to begin extracting.
        int[] coords = { 132, 0, 32 };

        // the size of the mesh we want to generate.
        int[] meshSize = { 32, 32, 32 };

        MarchingCubesMeshGenerator meshGenerator = new MarchingCubesMeshGenerator(meshSize[0], meshSize[1], meshSize[2]);
        int[] requiredVolumeSize = meshGenerator.getRequiredVolumeSize();

        // Extract a section of the densityVolume that we want to visualize.
        DensityVolume chunkVolume = ArrayDensityVolume.extractVolume(densityVolume,
                coords[0], coords[1], coords[2],
                requiredVolumeSize[0], (int)requiredVolumeSize[1], (int)requiredVolumeSize[2]);

        // generate the mesh.
        Mesh mesh = meshGenerator.buildMesh(chunkVolume);

        // standard JME scene stuff.
        Geometry geometry = new Geometry("Marching Cubes", mesh);

        // hint: IsoSurface meshes generally require some sort of texture mapping algorithm such as TriPlanar Mapping.
        geometry.setMaterial(new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md"));

        // add some light so we can see it better.
        rootNode.addLight(new DirectionalLight(new Vector3f(-1, -1, -1).normalizeLocal(), ColorRGBA.White.clone()));
        rootNode.addLight(new AmbientLight(new ColorRGBA(0.4f, 0.4f, 0.4f, 1.0f)));

        rootNode.attachChild(geometry);

        // look at it.
        cam.setLocation(new Vector3f(0, meshSize[1] / 4f, 0));
        cam.lookAt(new Vector3f(meshSize[0] / 2f, 0, meshSize[2] / 2f), Vector3f.UNIT_Y);

    }

}
