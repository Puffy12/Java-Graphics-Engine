package SlRenderer;


import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import csc133.slCamera;
import csc133.spot;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.*;


public class Draw {

        int shader_program;
        static Matrix4f viewProjMatrix = new Matrix4f();
        static FloatBuffer myFloatBuffer = BufferUtils.createFloatBuffer(spot.OGL_MATRIX_SIZE);
        static int vpMatLocation = 0;
        static int renderColorLocation = 0;

        public static void drawSquare(float x, float y, float my_size) {
                int vbo = glGenBuffers();
                int ibo = glGenBuffers();

                float[] vertices = {
                        x, y,
                        x + my_size, y,
                        x + my_size, y + my_size,
                        x, y + my_size
                };

                int[] indices = {0, 1, 2, 0, 2, 3};

                glBindBuffer(GL_ARRAY_BUFFER, vbo);
                glBufferData(GL_ARRAY_BUFFER, (FloatBuffer) BufferUtils.
                        createFloatBuffer(vertices.length).
                        put(vertices).flip(), GL_STATIC_DRAW);
                glEnableClientState(GL_VERTEX_ARRAY);
                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
                glBufferData(GL_ELEMENT_ARRAY_BUFFER, (IntBuffer) BufferUtils.
                        createIntBuffer(indices.length).
                        put(indices).flip(), GL_STATIC_DRAW);
                glVertexPointer(2, GL_FLOAT, 0, 0L);
                
                slCamera my_cam = new slCamera();
                my_cam.setProjectionOrtho();
                viewProjMatrix = my_cam.getProjectionMatrix();
                
                //viewProjMatrix.setOrtho(spot.ORTHO_LEFT, spot.ORTHO_RIGHT, spot.ORTHO_BOTTOM, spot.ORTHO_TOP, spot.ORTHO_NEAR, spot.ORTHO_FAR);

                glUniformMatrix4fv(vpMatLocation, false,
                viewProjMatrix.get(myFloatBuffer));
                glUniform3f(renderColorLocation, 1.0f, 0.498f, 0.153f);
                glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
                int VTD = 6; // need to process 6 Vertices To Draw 2 triangles
                glDrawElements(GL_TRIANGLES, VTD, GL_UNSIGNED_INT, 0L);

                // Clean up
                glDeleteBuffers(vbo);
                glDeleteBuffers(ibo);
                
        }
}
