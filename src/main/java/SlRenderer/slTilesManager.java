package SlRenderer;

import org.joml.Vector2i;

import javax.swing.*;
import java.util.Random;

import static csc133.spot.*;

public class slTilesManager {
    private float[] verticesArray;
    private int[] vertexIndicesArray;

    private final int vps = 4; // Vertices Per Square
    private final int fpv = 9;  // Vertices Per Vertex

    // 0 <-- gold & unexposed; GU, 1 <-- gold & exposed; GE,
    // 2 <-- mine & unexposed; MU, 3 <-- mine & exposed; ME.
    public static final int GU = 0;
    public static final int GE = 1;
    public static final int MU = 2;
    public static final int ME = 3;
    // Precomputed Texture Coordinates for the above states - tied to how textures are tiled:
                                       //umin, vmin, umax, vmax
    private static final float[] GUTC = {0.5f, 0.5f, 1.0f, 0.0f};
    private static final float[] MUTC = GUTC;
    private static final float[] GETC = {0.0f, 1.0f, 0.5f, 0.5f};
    private static final float[] METC = {0.5f, 1.0f, 1.0f, 0.5f};

    private int[] cellStatusArray;
    private int[] cellStats;  // {exposed gold - unexposed gold - mines} - in that order.
    // < 0 --> game in progress, 0 --> ended on gold, 1 --> ended on a mine, 2 --> game ended.
    private int boardDisplayStatus = -1;
    private int num_mines;

    float ZMIN = 0.0f; // z coordinate for all polygons

    public slTilesManager(int total_mines) {
        num_mines = total_mines;
        cellStatusArray = new int[NUM_POLY_COLS * NUM_POLY_ROWS];
        cellStats = new int[] {0, NUM_POLY_COLS * NUM_POLY_ROWS - total_mines, total_mines};

        for (int ii = 0; ii < cellStatusArray.length; ++ii) {
            cellStatusArray[ii] = GU;
        }

        int cur_mines = 0, cur_index = -1;
        Random my_rand = new Random();
        while (cur_mines < num_mines) {
            cur_index = my_rand.nextInt(cellStatusArray.length);
            if (cellStatusArray[cur_index] != MU) {
                cellStatusArray[cur_index] = MU;
                ++cur_mines;
            }
        }

        setVertexArray();
        setVertexIndicesArray();
    }  //  public slGeometryManager(int num_mines)

    // Call fillSquarecoordinates for each cell array
    private void setVertexArray() {
        verticesArray = new float[(NUM_POLY_ROWS * NUM_POLY_COLS) * vps * fpv];
       





    }  // float[] setVertexArray(...)

    // Given a index, row, column, fill up the vertices of the square. "index" is the index
    // to the vert_array, a multiple of vps * fpv
    private void fillSquareCoordinates(int indx, int row, int col, float[] vert_array) {
        

    }  //  private void fillSquareCoordinates(int indx, int row, int col, float[] vert_array)

    public void setVertexIndicesArray() {
        



    }  //  public int[] setVertexIndicesArray(...)

    public void updateForPolygonStatusChange(int row, int col, boolean printStats) {
        //locate the index to the verticesArray:
        int fps = vps * fpv; //Floats Per Square
        int first_offset = 7; // first texture coord offset;
        int tc_offset = 8;  // subsequent texture coord offsets;
        int indx = (row * NUM_POLY_COLS + col) * fps + first_offset;
        float umin = GETC[0], vmin = GETC[1], umax = GETC[2], vmax = GETC[3];
        int old_state = cellStatusArray[row * NUM_POLY_COLS + col];
        int new_state = -1;
        if (old_state == GU ) {
            new_state = GE;
            ++cellStats[0];
            --cellStats[1];
            if (printStats && boardDisplayStatus < 0) {
                printStats();
            }
            // tex coords set to this by default - no need to update
        } else if (old_state == MU) {
            new_state = ME;
        } else {
            return;
        }

        if (new_state == ME) {
            umin = METC[0]; umax = METC[1]; vmin = METC[2]; vmax = METC[3];
        }
        cellStatusArray[row * NUM_POLY_COLS + col] = new_state;

        verticesArray[indx++] = umin;
        verticesArray[indx] = vmin;
        indx += tc_offset;
        verticesArray[indx++] = umax;
        verticesArray[indx] = vmin;
        indx += tc_offset;
        verticesArray[indx++] = umax;
        verticesArray[indx] = vmax;
        indx += tc_offset;
        verticesArray[indx++] = umin;
        verticesArray[indx] = vmax;
    }

    private void printStats() {
        
    }

    // status can be GE, ME, GU, MU
    public void setCellStatus(int row, int col, int status) {
        



    }  //  public void setCellStatus(int row, int col, int status)

    public int getCellStatus(int row, int col) {
        return cellStatusArray[row * NUM_POLY_COLS + col];
    }

    public void updateStatusArrayToDisplayAll() {
        




    }

    public float[] getVertexArray() {
        return verticesArray;
    }

    public int[] getVertexIndicesArray() {
        return vertexIndicesArray;
    }

    public void printMineSweeperArray() {
        



    }  //  public void printMineSweeperArray()

    public int[] getCellStats() {
        return cellStats;
    }

    // returns (-1, -1) if a cell was not selected; else computes the row and column 
    // corresponding to the window coordinates and returns the updated retVec
    public static Vector2i getRowColFromXY(float xpos, float ypos) {
        Vector2i retVec = new Vector2i(-1, -1);
     
     



        return retVec.set(NUM_POLY_ROWS-1-row, col);
    }

}  //  public class slGeometryManager
