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
import com.jme3.texture.Texture;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Checkbox;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.core.VersionedReference;
import com.simsilica.lemur.style.BaseStyles;
import noise.PerlinNoise;


public class TestMarchingSquares extends SimpleApplication {

    public static void main(String... args) {

        TestMarchingSquares testMarchingSquares = new TestMarchingSquares();

        AppSettings appSettings = new AppSettings(true);
        appSettings.setResolution(1280,720);
        appSettings.setAudioRenderer(null);

        testMarchingSquares.setSettings(appSettings);
        testMarchingSquares.setShowSettings(false);
        testMarchingSquares.start();

    }

    private int seed = FastMath.nextRandomInt();
    private VersionedReference<Boolean> wireframeRef;
    private Geometry geometry;
    private Material material;

    private int sizeX = 64;
    private int sizeY = 64;

    private void buildMesh(int sizeX, int sizeY) {

        // The marching squares algorithm only wants to know if a square is solid or not.
        // We'll use noise to create some cool looking shapes.

        boolean[][] map = new boolean[sizeX][sizeY];

        PerlinNoise perlinNoise = new PerlinNoise(seed);

        // scale the noise a little so we get some interesting shapes.
        float scale = 0.14f;

        for (int x = 0; x < map[0].length; x++) {
            for (int y = 0; y < map[1].length; y++) {

                double dVal = perlinNoise.getNoise(x * scale, y * scale);
                int val = (int) Math.ceil(dVal);
                map[x][y] = val == 1;
            }
        }

        MarchingSquaresMeshGenerator marchingSquaresMeshGenerator = new MarchingSquaresMeshGenerator();
        Mesh mesh = marchingSquaresMeshGenerator.buildMesh(map, 1.0f);
        geometry.setMesh(mesh);
    }

    @Override
    public void simpleInitApp() {

        flyCam.setDragToRotate(true);
        flyCam.setMoveSpeed(30);

        GuiGlobals.initialize(this);
        BaseStyles.loadGlassStyle();
        GuiGlobals.getInstance().getStyles().setDefaultStyle(BaseStyles.GLASS);

        Container container = new Container();
        Button newSeedButton = container.addChild(new Button("New Seed"));
        newSeedButton.addClickCommands(source -> {
            seed = FastMath.nextRandomInt();
            buildMesh(sizeX, sizeY);
        });

        Checkbox wireframeCheckbox = container.addChild(new Checkbox("Wireframe"));
        wireframeRef = wireframeCheckbox.getModel().createReference();

        container.setLocalTranslation(20, cam.getHeight() - 20, 0);
        guiNode.attachChild(container);


        geometry = new Geometry("Marching Squares");
        buildMesh(sizeX, sizeY);

        material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        Texture colorMap = assetManager.loadTexture("Textures/stone_wall.jpg");
        colorMap.setWrap(Texture.WrapMode.Repeat);
        material.setTexture("DiffuseMap", colorMap);

        geometry.setMaterial(material);

        // add some light so we can see it better.
        rootNode.addLight(new DirectionalLight(new Vector3f(-1, -1, -1).normalizeLocal(), ColorRGBA.White.clone()));
        rootNode.addLight(new AmbientLight(new ColorRGBA(0.4f, 0.4f, 0.4f, 1.0f)));

        geometry.rotate(FastMath.HALF_PI, 0, 0);

        rootNode.attachChild(geometry);
        cam.setLocation(new Vector3f(0, 0, 80));
    }

    @Override
    public void simpleUpdate(float tpf) {

        if (wireframeRef.update()) {
            boolean value = wireframeRef.get();
            material.getAdditionalRenderState().setWireframe(value);
        }

    }
}
