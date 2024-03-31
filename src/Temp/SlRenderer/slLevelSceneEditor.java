package SlRenderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;

import csc133.slWindow;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static csc133.spot.*;

public class slLevelSceneEditor {

    private final Vector3f my_camera_location = new Vector3f(0, 0, 0.0f);
    private slShaderManager testShader;
    private slTextureManager testTexture;
    private static long glfw_window;

    private float xmin = POLY_OFFSET, ymin = POLY_OFFSET, zmin = 0.0f, xmax = xmin + POLYGON_LENGTH,
            ymax = ymin + POLYGON_LENGTH, zmax = 0.0f;

    private final float uvmin = 0.0f, uvmax = 1.0f;

    private static final float VFactor = alpha; // Speed of the polygon across the window;

    // Vertices and UV coordinates for textures
    private final float[] vertexArray = {
            // Vertices        // UV coordinates
            xmax, ymax, zmax, uvmax, uvmax, // Top right
            xmax, ymin, zmax, uvmax, uvmin, // Bottom right
            xmin, ymin, zmax, uvmin, uvmin, // Bottom left
            xmin, ymax, zmax, uvmin, uvmax // Top left
    };

    private final int[] rgElements = {2, 1, 0, // Top triangle
            0, 1, 3 // Bottom triangle
    };
    int positionStride = 3;
    int textureStride = 2;
    int vertexStride = (positionStride + textureStride) * Float.BYTES;

    private int vaoID, vboID, eboID;
    final private int vpoIndex = 0, vtoIndex = 1;

    private slCamera my_camera;

    public slLevelSceneEditor() {
        
    }

    private static final String path = "src/assets/shaders/";

    public void init() {
        my_camera = new slCamera(my_camera_location);
        my_camera.setOrthoProjection();

        try {
            testShader = new slShaderManager("vs_texture_1.glsl", "fs_texture_1.glsl");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Exiting renderLoop");
        testShader.compile_shader();
        
        // Bind texture
        testTexture = new slTextureManager(path+"texture.png");
        testTexture.bind_texture();
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        IntBuffer elementBuffer = BufferUtils.createIntBuffer(rgElements.length);
        elementBuffer.put(rgElements).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        glVertexAttribPointer(vpoIndex, positionStride, GL_FLOAT, false, vertexStride, 0);
        glEnableVertexAttribArray(vpoIndex);

        glVertexAttribPointer(vtoIndex, textureStride, GL_FLOAT, false, vertexStride,
                positionStride * Float.BYTES);
        glEnableVertexAttribArray(vtoIndex);
    }

    public void update(float dt) {
        // Update camera motion
        my_camera.relativeMoveCamera(dt * VFactor, dt * VFactor);

        // Check if camera position is out of bounds
        if (my_camera.getCurLookFrom().x < -FRUSTUM_RIGHT) {
            my_camera.restoreCamera();
            my_camera.setOrthoProjection();
        }

        // Use shader program
        testShader.set_shader_program();

        // Load projection and view matrices
        slShaderManager.loadMatrix4f("uProjMatrix", my_camera.getProjectionMatrix());
        slShaderManager.loadMatrix4f("uViewMatrix", my_camera.getViewMatrix());

        // Bind VAO
        glBindVertexArray(vaoID);

        // Draw elements
        glDrawElements(GL_TRIANGLES, rgElements.length, GL_UNSIGNED_INT, 0);

        // Unbind VAO and shader program
        glBindVertexArray(0);
        slShaderManager.detach_shader();

        // Unbind texture
        testTexture.unbind_texture();
    }

    public void render() {
        glfw_window = slWindow.get_oglwindow(WIN_WIDTH, WIN_HEIGHT);
        try {
            glfwWaitEvents();
            renderLoop();
            slWindow.destroy_oglwindow();
        } finally {
            glfwTerminate();
            glfwSetErrorCallback(null).free();
        }
    }

    private void renderLoop() {
        try {
            glfwPollEvents();
            init();
            long lastFrameTime = System.nanoTime();
            
            while(!glfwWindowShouldClose(glfw_window)){
                // Measure delta time
                long currentFrameTime = System.nanoTime();
                float dt = (float) ((currentFrameTime - lastFrameTime) / 1e9); // Convert nanoseconds to seconds
                lastFrameTime = currentFrameTime;
    
                update(dt);
                glfwWaitEvents();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Exiting renderLoop");
        }
    }
}
