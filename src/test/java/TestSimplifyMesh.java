import com.jayfella.mesh.HeightMapMesh;
import com.jayfella.mesh.MarchingCubesMeshGenerator;
import com.jayfella.mesh.SimplifyMesh;
import com.jayfella.mesh.marchingcubes.ArrayDensityVolume;
import com.jayfella.mesh.marchingcubes.DensityVolume;
import com.jayfella.mesh.MarchingSquaresMeshGenerator;
import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.system.AppSettings;
import com.simsilica.lemur.*;
import com.simsilica.lemur.core.VersionedReference;
import com.simsilica.lemur.style.BaseStyles;
import noise.GemsFractalDensityVolume;
import noise.PerlinNoise;

import java.util.Set;

import static com.jme3.material.RenderState.FaceCullMode.Off;

public class TestSimplifyMesh extends SimpleApplication {

    public static void main(String... args) {

        TestSimplifyMesh testSimplifyMesh = new TestSimplifyMesh();

        AppSettings appSettings = new AppSettings(true);
        appSettings.setResolution(1280,720);
        appSettings.setAudioRenderer(null);

        testSimplifyMesh.setSettings(appSettings);
        testSimplifyMesh.setShowSettings(false);
        testSimplifyMesh.start();

    }

    int seed = FastMath.nextRandomInt();

    private SimplifyMesh simplifyMesh = new SimplifyMesh();
    private Material material;
    private Geometry fullresGeom;
    private Geometry simplifiedGeom;

    private VersionedReference<Set<Integer>> meshTypeRef;
    private VersionedReference<Double> targetPercentRef;
    private VersionedReference<Double> aggressionRef;
    private VersionedReference<Double> iterationsRef;
    private VersionedReference<Boolean> complexNormalsRef;
    private VersionedReference<Boolean> wireframeRef;
    private Label targetPercentLabel;
    private Label aggressionLabel;
    private Label iterationsLabel;

    private Container triCountContainer;
    private Label fullResTriCount;
    private Label simplifiedTriCount;

    private int size = 64;

    private Mesh createHeightMapMesh(int loc_x, int loc_z) {

        int width = size + 3;
        int length = size + 3;

        int height = 16;

        // the coordinates to begin evaluating the noise.
        int[] coords = { loc_x, loc_z };

        float[] heightmap = new float[width * length];

        PerlinNoise perlinNoise = new PerlinNoise(seed);

        // scale the coordinates to add more detail to the mesh.
        float noiseScale = 0.05f;

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < length; z++) {

                double noise = height * perlinNoise.getNoise((coords[0] + x) * noiseScale, (coords[1] + z) * noiseScale);

                int index = x + z * length;

                heightmap[index] = (float) noise;
            }
        }

        return new HeightMapMesh(heightmap);

    }

    private Mesh createMarchingSquaresMesh(int loc_x, int loc_z) {

        // The marching squares algorithm only wants to know if a square is solid or not.
        // We'll use noise to create some cool looking shapes.

        boolean[][] map = new boolean[size + 1][size + 1];

        PerlinNoise perlinNoise = new PerlinNoise(seed);

        // scale the noise a little so we get some interesting shapes.
        float scale = 0.14f;

        for (int x = 0; x < map[0].length; x++) {
            for (int y = 0; y < map[1].length; y++) {

                double dVal = perlinNoise.getNoise((loc_x + x) * scale, (loc_z + y) * scale);
                int val = (int) Math.ceil(dVal);
                map[x][y] = val == 1;
            }
        }

        MarchingSquaresMeshGenerator marchingSquaresMeshGenerator = new MarchingSquaresMeshGenerator();
        return marchingSquaresMeshGenerator.buildMesh(map, 1.0f);

    }

    private Mesh createMarchingCubesMesh(int loc_x, int loc_z) {

        GemsFractalDensityVolume densityVolume = new GemsFractalDensityVolume(seed);

        // the coordinates of the density volume to begin extracting.
        // int[] coords = { 132, 0, 32 };

        // the size of the mesh we want to generate.
        // int[] meshSize = { 32, 32, 32 };

        MarchingCubesMeshGenerator meshGenerator = new MarchingCubesMeshGenerator(size, size, size);
        int[] requiredVolumeSize = meshGenerator.getRequiredVolumeSize();

        // Extract a section of the densityVolume that we want to visualize.
        DensityVolume chunkVolume = ArrayDensityVolume.extractVolume(densityVolume,
                loc_x, 0, loc_z,
                requiredVolumeSize[0], requiredVolumeSize[1], requiredVolumeSize[2]);

        // generate the mesh.
        return meshGenerator.buildMesh(chunkVolume);
    }

    private void simplifyMesh() {

        float targetPercent = targetPercentRef.get().floatValue();
        float aggression = aggressionRef.get().floatValue();
        int iterations = iterationsRef.get().intValue();
        boolean complexNorms = complexNormalsRef.get();

        simplifyMesh.setTargetPercent(targetPercent);
        simplifyMesh.setAggression(aggression);
        simplifyMesh.setMaxIterations(iterations);
        simplifyMesh.setComplexNormals(complexNorms);

        Mesh newMesh = simplifyMesh.simplify();
        simplifiedGeom.setMesh(newMesh);
        simplifiedGeom.updateModelBound();

        updateLabels();
    }

    private void updateLabels() {

        targetPercentLabel.setText(String.format("%.2f", targetPercentRef.get().floatValue()));
        aggressionLabel.setText(String.format("%.2f", aggressionRef.get().floatValue()));
        iterationsLabel.setText("" + iterationsRef.get().intValue());

        fullResTriCount.setText("" + fullresGeom.getMesh().getTriangleCount());
        simplifiedTriCount.setText("" + simplifiedGeom.getMesh().getTriangleCount());
    }

    private enum MeshType {
        HeightMap,
        MarchingSquares,
        MarchingCubes,
    }

    @Override
    public void simpleInitApp() {

        flyCam.setDragToRotate(true);
        flyCam.setMoveSpeed(30);

        GuiGlobals.initialize(this);
        BaseStyles.loadGlassStyle();
        GuiGlobals.getInstance().getStyles().setDefaultStyle(BaseStyles.GLASS);

        Container container = new Container();

        int row = 0;

        container.addChild(new Label("Mesh Type"), row, 0);
        ListBox<MeshType> meshTypeComboBox = container.addChild(new ListBox<>(), row, 1);

        for (MeshType meshType : MeshType.values()) {
            meshTypeComboBox.getModel().add(meshType);
        }

        meshTypeComboBox.setVisibleItems(meshTypeComboBox.getModel().size());

        meshTypeComboBox.getSelectionModel().setSelection(0);
        meshTypeRef = meshTypeComboBox.getSelectionModel().createReference();

        row++;

        container.addChild(new Label("Target Percent"), row, 0);
        Slider targetPercentSlider = container.addChild(new Slider(), row, 1);
        targetPercentSlider.getModel().setMinimum(0.01);
        targetPercentSlider.getModel().setMaximum(0.99);
        targetPercentSlider.getModel().setValue(0.5);
        targetPercentSlider.setDelta(0.01);
        targetPercentRef = targetPercentSlider.getModel().createReference();
        targetPercentLabel = container.addChild(new Label(""), row, 2);

        row++;

        container.addChild(new Label("Aggression"), row, 0);
        Slider aggressionSlider = container.addChild(new Slider(), row, 1);
        aggressionSlider.getModel().setMinimum(4);
        aggressionSlider.getModel().setMaximum(20);
        aggressionSlider.getModel().setValue(8);
        aggressionSlider.setDelta(0.01);
        aggressionRef = aggressionSlider.getModel().createReference();
        aggressionLabel = container.addChild(new Label(""), row, 2);

        row++;

        container.addChild(new Label("Max Iterations"), row, 0);
        Slider iterationsSlider = container.addChild(new Slider(), row, 1);
        iterationsSlider.getModel().setMinimum(1);
        iterationsSlider.getModel().setMaximum(10000);
        iterationsSlider.getModel().setValue(1000);
        iterationsRef = iterationsSlider.getModel().createReference();
        iterationsLabel = container.addChild(new Label(""), row, 2);

        row++;

        container.addChild(new Label("Complex Normals"), row, 0);
        Checkbox complexNormalsCheckBox = container.addChild(new Checkbox(""), row, 1);
        complexNormalsRef = complexNormalsCheckBox.getModel().createReference();

        row++;

        container.addChild(new Label("WireFrame"), row, 0);
        Checkbox wireframeCheckBox = container.addChild(new Checkbox(""), row, 1);
        wireframeRef = wireframeCheckBox.getModel().createReference();

        container.setLocalTranslation(20, cam.getHeight() - 20, 0);
        guiNode.attachChild(container);

        // tri counts
        triCountContainer = new Container();

        triCountContainer.addChild(new Label("Triangle Count"), 0, 0);
        triCountContainer.addChild(new Label("Full Res: "), 1, 0);
        fullResTriCount = triCountContainer.addChild(new Label(""), 1, 1);
        fullResTriCount.setTextHAlignment(HAlignment.Right);

        triCountContainer.addChild(new Label("Simplified: "), 2, 0);
        simplifiedTriCount = triCountContainer.addChild(new Label(""), 2, 1);
        simplifiedTriCount.setTextHAlignment(HAlignment.Right);

        guiNode.attachChild(triCountContainer);

        material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        material.getAdditionalRenderState().setFaceCullMode(Off);

        // add some light so we can see it better.
        rootNode.addLight(new DirectionalLight(new Vector3f(-1, -1, -1).normalizeLocal(), ColorRGBA.White.clone()));
        rootNode.addLight(new AmbientLight(new ColorRGBA(0.4f, 0.4f, 0.4f, 1.0f)));

        cam.setLocation(new Vector3f(32, 16, 64));
        cam.lookAt(new Vector3f(32, 0, -32), Vector3f.UNIT_Y);


        fullresGeom = new Geometry("Left Geometry");
        fullresGeom.setMaterial(material);

        simplifiedGeom = new Geometry("Simplified Geometry");
        simplifiedGeom.setMaterial(material);
        simplifiedGeom.setLocalTranslation(size, 0, 0);

        createHeightMapScene();

        rootNode.attachChild(fullresGeom);
        rootNode.attachChild(simplifiedGeom);
    }

    private void createHeightMapScene() {

        Mesh mesh_left = createHeightMapMesh(0, 0);
        fullresGeom.setMesh(mesh_left);

        Mesh mesh_right = createHeightMapMesh(size, 0);
        simplifyMesh.setMesh(mesh_right);

        simplifyMesh();


    }

    private void createMarchingSquaresScene() {

        Mesh mesh_left = createMarchingSquaresMesh(0, 0);
        fullresGeom.setMesh(mesh_left);

        Mesh mesh_right = createMarchingSquaresMesh(size, 0);
        simplifyMesh.setMesh(mesh_right);

        simplifyMesh();

    }

    private void createMarchingCubesScene() {

        Mesh mesh_left = createMarchingCubesMesh(0, 0);
        fullresGeom.setMesh(mesh_left);

        Mesh mesh_right = createMarchingCubesMesh(size, 0);
        simplifyMesh.setMesh(mesh_right);

        simplifyMesh();
    }

    @Override
    public void simpleUpdate(float tpf) {

        if (meshTypeRef.update()) {

            // List<MeshType> selectedTypes = meshTypeRef.get();
            Set<Integer> selectedIndexes = meshTypeRef.get();

            if (selectedIndexes != null && !selectedIndexes.isEmpty()) {

                // we only allow selecting a single type.
                Integer index = selectedIndexes.iterator().next();
                MeshType meshType = MeshType.values()[index];

                switch (meshType) {
                    case HeightMap: {
                        createHeightMapScene();
                        break;
                    }
                    case MarchingSquares: {
                        createMarchingSquaresScene();
                        break;
                    }
                    case MarchingCubes: {
                        createMarchingCubesScene();
                        break;
                    }
                }

            }

        }

        if (targetPercentRef.update()) {
            simplifyMesh();
        }

        if (aggressionRef.update()) {
            simplifyMesh();
        }

        if (complexNormalsRef.update()) {
            simplifyMesh();
        }

        if (iterationsRef.update()) {
            simplifyMesh();
        }

        if (wireframeRef.update()) {
            boolean value = wireframeRef.get();
            material.getAdditionalRenderState().setWireframe(value);
        }

        triCountContainer.setLocalTranslation(
                cam.getWidth() - triCountContainer.getPreferredSize().x - 10,
                cam.getHeight() - 10,
                0
        );

    }

}
