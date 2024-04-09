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

    public slDrawablesManager(int num_mines) {
        


    }

    private void initRendering() {
       



    }  // void initRendering()

    public void update(int row, int col) {
        // first check if row, col >= 0 and if so, get the cell status. If status is GE --> call glDrawElements(...)
        // Else, update the polygon status change in the TilesManager, and get updated cell status.
        // If total Gold tiles == 0, expose the entire board
        // if the vertex data changed; needs updating to GPU that the vertices have changed --> need to call:
        //    glBufferData(GL_ARRAY_BUFFER, vertexArray, GL_DYNAMIC_DRAW);
        }

        
        glDrawElements(GL_TRIANGLES, vertexIndexArray.length, GL_UNSIGNED_INT, 0);
    }  //  public void update(int row, int col)

}
