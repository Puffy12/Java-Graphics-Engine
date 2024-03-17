package SlRenderer;


import csc133.slWindow;
import csc133.spot;

import org.joml.Matrix4f;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;


import static SlUtils.slTime.getTime;
import static csc133.spot.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.glClear;
import static org.lwjgl.opengl.GL11C.glClearColor;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.*;

public class slSingleBatchRenderer {
    
    private static long glfw_window = 0;
    private static final int renderColorLocation = 0;

    private static final float ccRed = 0.3f, ccGreen = 0.6f, ccBlue = 0.9f, ccAlpha = 1.0f; // Clear Colors
    private static final float[] vertices = getVertexArray(WIN_WIDTH, WIN_HEIGHT);
    private static final int[] indices = getIndexArrayForSquares(NUM_POLY_ROWS, NUM_POLY_COLS);
    private static slCamera my_camera;
    private static int SQUARE_SIDE = 1;
    private static final Vector3f camera_start = new Vector3f(SQUARE_SIDE, SQUARE_SIDE, 0f);

    private static slShaderManager mysm0;

    private static final float alpha = spot.alpha; // Speed of the polygon across the window;

    public slSingleBatchRenderer() {

    }

    public static void render() {
        glfw_window = slWindow.get_oglwindow(WIN_WIDTH, WIN_HEIGHT);
        try {
            renderLoop();
            slWindow.destroy_oglwindow();
        } finally {
            glfwTerminate();
            glfwSetErrorCallback(null).free();
        }
    } // void render()

    private static void renderLoop() {
        glfwPollEvents();
        initOpenGL();
        renderObjects();
        /* Process window messages in the main thread */
        while (!glfwWindowShouldClose(glfw_window)) {
            glfwWaitEvents();
        }
    } // void renderLoop()


    private static int[] getIndexArrayForSquares(int num_rows, int num_cols) {
        int indices_per_square = 6, verts_per_square = 4;
        int[] indx_array =
                new int[num_rows* num_cols * indices_per_square];
        int my_i = 0, v_indx = 0;
        while (my_i < indx_array.length) {
            indx_array[my_i++] = v_indx;
            indx_array[my_i++] = v_indx + 1;
            indx_array[my_i++] = v_indx + 2;

            indx_array[my_i++] = v_indx;
            indx_array[my_i++] = v_indx + 2;
            indx_array[my_i++] = v_indx + 3;

            v_indx += verts_per_square;
        }
        return indx_array;
    }  //  public int[] getIndexArrayForSquares(...)

    private static float[] getVertexArray(int win_width, int win_height) {
        float squareSize = 100f; // Change this value according to your requirements
        float x_buffer = 300, y_buffer = 100;
        // Calculate the bottom-left corner coordinates
        float x = (WIN_POS_X -win_width / 2f) + x_buffer;
        float y = (WIN_POS_Y -win_height / 2f) + y_buffer;

        // Define the vertices of the square
        float[] vertices = {
            x, y,                   // Bottom-left corner
            x + squareSize, y,      // Bottom-right corner
            x + squareSize, y + squareSize,  // Top-right corner
            x, y + squareSize       // Top-left corner
        };
    
        return vertices;
    }

    private static void initOpenGL() {
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        glViewport(0, 0, WIN_WIDTH, WIN_HEIGHT);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(ccRed, ccGreen, ccBlue, ccAlpha);
    

        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);
        verticesBuffer.put(vertices).flip();
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);

        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
    
        int ibo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
    
        IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
        indicesBuffer.put(indices).flip();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
    
        // Initialize camera and shader manager
        my_camera = new slCamera(new Vector3f(camera_start));
        my_camera.setProjection();
        try {
            mysm0 = new slShaderManager("vs_0.glsl", "fs_0.glsl");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        mysm0.compile_shader();
        glUniform3f(renderColorLocation, VEC_RC.x, VEC_RC.y, VEC_RC.z);
    }


    private static void renderObjects() {
        while (!glfwWindowShouldClose(glfw_window)) {
            glfwPollEvents();
            glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
    
            // Update camera position
            my_camera.defaultLookFrom.x -= alpha;
            my_camera.defaultLookFrom.y -= alpha;
            if (my_camera.defaultLookFrom.x < -(WIN_WIDTH + SQUARE_SIDE)) {
                my_camera.defaultLookFrom.x = camera_start.x;
                my_camera.defaultLookFrom.y = camera_start.y;
            }
    
            // Set shader program and matrices
            mysm0.set_shader_program();
            slShaderManager.loadMatrix4f("uProjMatrix", my_camera.getProjectionMatrix());
            slShaderManager.loadMatrix4f("uViewMatrix", my_camera.getViewMatrix());
    
            // Draw square
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
            slShaderManager.detach_shader();
    
            glfwSwapBuffers(glfw_window);
        }
    }

}  //  public class slSingleBatchRenderer
