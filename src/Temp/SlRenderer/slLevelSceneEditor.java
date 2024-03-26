package SlRenderer;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import csc133.spot;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray;
import static org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.*;
import static csc133.spot.*;

public class slLevelSceneEditor {

    private final Vector3f my_camera_location = new Vector3f(0, 0, 0.0f);
    private slShaderManager testShader;
    private slTextureManager testTexture;

    private float xmin = POLY_OFFSET, ymin = POLY_OFFSET, zmin = 0.0f, xmax = xmin+ POLYGON_LENGTH,
                            ymax = ymin+ POLYGON_LENGTH, zmax = 0.0f;

    private final float uvmin = 0.0f, uvmax = 1.0f;

    private static final float VFactor = spot.alpha; // Speed of the polygon across the window;
    
    // TODO: Add the vertices here: you need to add the UV Coordinates for the textures here -
    // colors will be discarded by the fragment texture with the texels. Nevertheless, we do send
    // them in.
    private final float [] vertexArray = {
            // vertices -        colors                   UV coordinates
            

    };

    private final int[] rgElements = {2, 1, 0, //top triangle
                                      0, 1, 3 // bottom triangle
    };
    int positionStride = 3;
    int colorStride = 4;
    int textureStride = 2;
    int vertexStride = (positionStride + colorStride + textureStride) * Float.BYTES;

    private int vaoID, vboID, eboID;
    final private int  vpoIndex = 0, vcoIndex = 1, vtoIndex = 2;

    slCamera my_camera;

    public slLevelSceneEditor() {

    }

    public void init() {
        my_camera = new slCamera(my_camera_location);
        my_camera.setOrthoProjection();

        try {
            testShader =
                    new slShaderManager("vs_texture_1.glsl", "fs_texture_1.glsl");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        testShader.compile_shader();
        // TODO: Add texture manager object here:

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

        glVertexAttribPointer(vcoIndex, colorStride, GL_FLOAT, false, vertexStride,
                                                            positionStride * Float.BYTES);
        glEnableVertexAttribArray(vcoIndex);

        // TODO: Add the vtoIndex --> "Vertex Texture Object Index" here via glVertexAttribPointer and enable it -
        // similar to other AttribPointers above.

    }

    public void update(float dt) {

        //TODO: Add camera motion here:
        my_camera.relativeMoveCamera(dt*VFactor, dt*VFactor);

        if (my_camera.getCurLookFrom().x < -FRUSTUM_RIGHT) {
            my_camera.restoreCamera();
            my_camera.setOrthoProjection();
        }
        testShader.set_shader_program();


        slShaderManager.loadMatrix4f("uProjMatrix", my_camera.getProjectionMatrix());
        slShaderManager.loadMatrix4f("uViewMatrix", my_camera.getViewMatrix());
        glBindVertexArray(vaoID);

        glEnableVertexAttribArray(vpoIndex);
        glEnableVertexAttribArray(vcoIndex);
        glEnableVertexAttribArray(vtoIndex);
        glDrawElements(GL_TRIANGLES, rgElements.length, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(vpoIndex);
        glDisableVertexAttribArray(vcoIndex);
        glDisableVertexAttribArray(vtoIndex);

        glBindVertexArray(0);
        slShaderManager.detach_shader();
    }

}
