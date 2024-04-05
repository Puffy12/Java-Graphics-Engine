package SlRenderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static csc133.spot.*;

public class slLevelSceneEditor {

    private slCamera my_camera;
    private slShaderManager testShader;
    private slTextureManager testTexture;

    private float xmin = POLY_OFFSET, ymin = POLY_OFFSET, zmin = 0.0f, xmax = xmin+ POLYGON_LENGTH,
                            ymax = ymin+ POLYGON_LENGTH, zmax = 0.0f;

    private final float uvmin = 0.0f, uvmax = 1.0f;

    private final float[] vertexArray = {
        // Vertices           // Colors          // UV coordinates
        xmax, ymax, zmax,    1.0f, 1.0f, 1.0f, 1.0f,  uvmax, uvmin, // Top right
        xmax, ymin, zmax,    1.0f, 1.0f, 1.0f, 1.0f,  uvmax, uvmax, // Bottom right
        xmin, ymin, zmin,    1.0f, 1.0f, 1.0f, 1.0f,  uvmin, uvmax, // Bottom left
        xmin, ymax, zmin,    1.0f, 1.0f, 1.0f, 1.0f,  uvmin, uvmin  // Top left
    };
    
    

    private final int[] rgElements = {0, 1, 2, 0, 2, 3};

    
    int positionStride = 3;
    int colorStride = 4;
    int textureStride = 2;
    private final int vertexStride =(positionStride + colorStride + textureStride) * Float.BYTES;

    private int vaoID, vboID, eboID;
    final private int vpoIndex = 0, vcoIndex = 1, vtoIndex = 2;

    private final float VFactor = alpha;

    public slLevelSceneEditor() {

    
    }

    public void init() throws FileNotFoundException, IOException {
        my_camera = new slCamera(new Vector3f(0, 0, 0f));
        my_camera.setOrthoProjection();

        testShader = new slShaderManager("vs_texture_1.glsl", "fs_texture_1.glsl");

        int temp = testShader.compile_shader();
        testShader.set_shader_program();
        System.out.println(temp);

        testTexture = new slTextureManager("src/assets/shaders/texture.png");

        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        // GL_STATIC_DRAW good for now; we can later change to dynamic vertices:
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        IntBuffer elementBuffer = BufferUtils.createIntBuffer(rgElements.length);
        elementBuffer.put(rgElements).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        glVertexAttribPointer(vpoIndex, positionStride, GL_FLOAT, false, vertexStride, 0);
        glEnableVertexAttribArray(vpoIndex);

        glVertexAttribPointer(vcoIndex, colorStride, GL_FLOAT, false, vertexStride, positionStride * Float.BYTES);
        glEnableVertexAttribArray(vcoIndex);

        glVertexAttribPointer(vtoIndex, textureStride, GL_FLOAT, false, vertexStride, (positionStride + colorStride) * Float.BYTES);
        glEnableVertexAttribArray(vtoIndex);

        // Unbind VAO
        glBindVertexArray(0);
    }

    public void update(float dt) {
        //my_camera.relativeMoveCamera(VFactor, VFactor);
       
        if (dt > 0) {
            my_camera.defaultLookFrom.x -= dt * VFactor;
            my_camera.defaultLookFrom.y -= dt * VFactor;
            if (my_camera.getCurLookFrom().x < -FRUSTUM_RIGHT ) {
                my_camera.restoreCamera();  // Restore camera to initial position
                my_camera.setOrthoProjection();  // Reset projection matrix
            }
        }
        // Set the clear color to blue (R=0, G=0, B=1, Alpha=1)
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);

        testShader.set_shader_program();
        slShaderManager.loadMatrix4f("uProjMatrix", my_camera.getProjectionMatrix());
        slShaderManager.loadMatrix4f("uViewMatrix", my_camera.getViewMatrix());
        glBindVertexArray(vaoID);

        glEnableVertexAttribArray(vpoIndex);
        glEnableVertexAttribArray(vcoIndex);
        glEnableVertexAttribArray(vtoIndex);

        testTexture.bind_texture(); // Bind the texture before drawing

        glDrawElements(GL_TRIANGLES, rgElements.length, GL_UNSIGNED_INT, 0);

        testTexture.unbind_texture(); // Unbind the texture after drawing

        glDisableVertexAttribArray(vpoIndex);
        glDisableVertexAttribArray(vcoIndex);
        glDisableVertexAttribArray(vtoIndex);

        glBindVertexArray(0);
        slShaderManager.detach_shader();
    }
}