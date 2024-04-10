package SlRenderer;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import javax.swing.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;


import static SlRenderer.slTilesManager.MU;
import static org.lwjgl.opengl.ARBVertexArrayObject.*;
import static org.lwjgl.opengl.GL20.*;

public class slDrawablesManager {

    private final Vector3f my_camera_location = new Vector3f(0, 0, 0.0f);
    private final slTilesManager board_manager;
    private final float [] vertexArray;
    private final int[] vertexIndexArray;
    
    private slShaderManager shaderManager;

    private int vaoID;
    private int vboID;
    private int eboID;

    public slDrawablesManager(int num_mines) {
        board_manager = new slTilesManager(num_mines);
        vertexArray = board_manager.getVertexArray();
        vertexIndexArray = board_manager.getVertexIndicesArray();

        initRendering();
      
        shaderManager = new slShaderManager("vs_texture_1.glsl", "fs_texture_1.glsl");
        int temp = shaderManager.compile_shader();
        System.out.println("Shader Status: " + temp);
        shaderManager.set_shader_program();
    }

    private void initRendering() {
        // Generate and bind Vertex Array Object (VAO)
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Generate Vertex Buffer Object (VBO) and Element Buffer Object (EBO)
        vboID = glGenBuffers();
        eboID = glGenBuffers();

        // Bind and set vertex buffer data
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, createBuffer(vertexArray), GL_STATIC_DRAW);

        // Bind and set element buffer data
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, createBuffer(vertexIndexArray), GL_STATIC_DRAW);

        // Set vertex attribute pointers
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);

        // Unbind VAO, VBO, and EBO
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }  // void initRendering()

    private FloatBuffer createBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private IntBuffer createBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    public void update(int row, int col) {
        // first check if row, col >= 0 and if so, get the cell status. If status is GE --> call glDrawElements(...)
        // Else, update the polygon status change in the TilesManager, and get updated cell status.
        // If total Gold tiles == 0, expose the entire board
        // if the vertex data changed; needs updating to GPU that the vertices have changed --> need to call:
        //    glBufferData(GL_ARRAY_BUFFER, vertexArray, GL_DYNAMIC_DRAW);
        
        // Check if row and col are valid
        if (row < 0 || col < 0) {
            return;
        }

        // Get cell status
        int status = board_manager.getCellStatus(row, col);

        // Update tile status if it's mine unexposed
        if (status == MU) {
            board_manager.updateForPolygonStatusChange(row, col, true);
        }

        // Get updated vertex array
        float[] updatedVertexArray = board_manager.getVertexArray();

        // Check if vertex data has changed
        if (!areArraysEqual(vertexArray, updatedVertexArray)) {
            // Update vertex buffer data
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferData(GL_ARRAY_BUFFER, createBuffer(updatedVertexArray), GL_DYNAMIC_DRAW);
            glBindBuffer(GL_ARRAY_BUFFER, 0);

            // Update vertex array reference
            System.arraycopy(updatedVertexArray, 0, vertexArray, 0, updatedVertexArray.length);
        }

        // Set shader program
        shaderManager.set_shader_program();
        
        // Draw elements
        glBindVertexArray(vaoID);
        glDrawElements(GL_TRIANGLES, vertexIndexArray.length, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }  //  public void update(int row, int col)


    private boolean areArraysEqual(float[] array1, float[] array2) {
        if (array1.length != array2.length) {
            return false;
        }
        for (int i = 0; i < array1.length; i++) {
            if (array1[i] != array2[i]) {
                return false;
            }
        }
        return true;
    }
}
