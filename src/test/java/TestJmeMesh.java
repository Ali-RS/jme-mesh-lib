import com.jayfella.mesh.JmeMesh;
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.material.Materials;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.VertexBuffer;
import com.jme3.system.AppSettings;

public class TestJmeMesh extends SimpleApplication {

    public static void main(String... args) {

        TestJmeMesh testJmeMesh = new TestJmeMesh();

        AppSettings appSettings = new AppSettings(true);
        appSettings.setResolution(1280, 720);
        appSettings.setAudioRenderer(null);

        testJmeMesh.setSettings(appSettings);
        testJmeMesh.setShowSettings(false);
        testJmeMesh.start();

    }

    @Override
    public void simpleInitApp() {

        flyCam.setMoveSpeed(30);

        Vector3f[] verts = {
                new Vector3f(0, 0, 0),
                new Vector3f(1, 0, 0),
                new Vector3f(0, 1, 0),
                new Vector3f(1, 1, 0),
        };

        Integer[] indices = {
                2,0,1,  1,3,2
        };

        JmeMesh mesh = new JmeMesh();
        mesh.set(VertexBuffer.Type.Position, verts);
        mesh.set(VertexBuffer.Type.Index, indices);

        Material material = new Material(assetManager, Materials.UNSHADED);

        Geometry geometry = new Geometry("Jme Mesh Test", mesh);
        geometry.setMaterial(material);

        rootNode.attachChild(geometry);

    }

}
