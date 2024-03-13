package SlRenderer;


import csc133.slWindow;
import org.joml.Matrix4f;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;


import static SlUtils.slTime.getTime;
import static csc133.spot.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.glClear;
import static org.lwjgl.opengl.GL11C.glClearColor;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glUniform3f;

public class slSingleBatchRenderer {
    private static long glfw_window = 0;
    private static final int renderColorLocation = 0;

    private static final float ccRed = 0.3f, ccGreen = 0.6f, ccBlue = 0.9f, ccAlpha = 1.0f; // Clear Colors
    private static final float[] vertices = getVertexArray(WIN_WIDTH, WIN_HEIGHT);
    private static final int[] indices = getIndexArrayForSquares(NUM_POLY_ROWS, NUM_POLY_COLS);
    private static slCamera my_camera;
    private static final Vector3f camera_start = new Vector3f(SQUARE_SIDE, SQUARE_SIDE, 0f);

    private static slShaderManager mysm0;

    private static final float alpha = 200.0f; // Speed of the polygon across the window;

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

    private static void initOpenGL() {
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        glViewport(0, 0, WIN_WIDTH, WIN_HEIGHT);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(ccRed, ccGreen, ccBlue, ccAlpha);

         int vbo = glGenBuffers();
         int ibo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, (FloatBuffer) BufferUtils.
                createFloatBuffer(vertices.length).
                put(vertices).flip(), GL_STATIC_DRAW);
        glEnableClientState(GL_VERTEX_ARRAY);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, (IntBuffer) BufferUtils.
                createIntBuffer(indices.length).
                put(indices).flip(), GL_STATIC_DRAW);

        int coords_per_vertex = 2, vertex_stride = 0;
        long first_vertex_ptr = 0L;
        glVertexPointer(coords_per_vertex, GL_FLOAT,
                vertex_stride, first_vertex_ptr);

        my_camera = new slCamera(new Vector3f(camera_start));
        my_camera.setProjection();
        mysm0 = new slShaderManager("vs_0.glsl", "fs_0.glsl");
        mysm0.compile_shader();
        glUniform3f(renderColorLocation, liveColor.x, liveColor.y, liveColor.z);

        return;
    } // void initOpenGL()

    private static float[] getVertexArray(int win_width, int win_height) {
        // Fill in this function:  you need four vertices. 

        return ret_array;
    }  // float[] getVertexArray(...)

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

    private static void renderObjects() {
        // Initialize begin, end times here!

        Matrix4f uProjMatrix = my_camera.getProjectionMatrix();
        Matrix4f uViewMatrix = my_camera.getViewMatrix();

        while (!glfwWindowShouldClose(glfw_window)) {
            glfwPollEvents();
            glClearColor(ccRed, ccGreen, ccBlue, ccAlpha);
            // Not sure why this order is important - but don't change it:
            glClear(GL_DEPTH_BUFFER_BIT|GL_COLOR_BUFFER_BIT);

            // compute dt here
            if (dt > 0) {
                // Update the two cooridinates of the defaultLookFrom of my_camera here
                // by a scaled amount of dt (dt will be too small + good to have tunable parameter)
                // Also, if the object is out of the window, reset the camera position
                
                // Update "endTime", "beginTime" - else dt will diverge quickly!
            }
            // Your slShaderManager class has to support these functions:
            mysm0.set_shader_program();
            mysm0.loadMatrix4f("uProjMatrix", my_camera.getProjectionMatrix());
            mysm0.loadMatrix4f("uViewMatrix", my_camera.getViewMatrix());

            int verts_per_triangle = 3, tris_per_square = 2;  // Vertices Per Vertex
            int dvps = verts_per_triangle * tris_per_square; // Drawn Vertices Per Square
            glDrawElements(GL_TRIANGLES, dvps, GL_UNSIGNED_INT, 0);
            
            slShaderManager.detach_shader();

            glfwSwapBuffers(glfw_window);
        }  //   while (!glfwWindowShouldClose(glfw_window))
    } // renderObjects

}  //  public class slSingleBatchRenderer
