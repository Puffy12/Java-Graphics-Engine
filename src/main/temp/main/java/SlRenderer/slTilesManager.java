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
        for (int row = 0; row < NUM_POLY_ROWS; row++) {
            for (int col = 0; col < NUM_POLY_COLS; col++) {
                int index = (row * NUM_POLY_COLS + col) * vps * fpv;
                fillSquareCoordinates(index, row, col, verticesArray);
            }
        }

    }  // float[] setVertexArray(...)

    // Given a index, row, column, fill up the vertices of the square. "index" is the index
    // to the vert_array, a multiple of vps * fpv
    private void fillSquareCoordinates(int index, int row, int col, float[] vert_array) {
        float x = col * SQUARE_SIZE + POLY_OFFSET + col * POLY_PADDING;
        float y = row * SQUARE_SIZE + POLY_OFFSET + row * POLY_PADDING;
        float z = ZMIN;
    
        // Vertex 1 (Bottom Left)
        vert_array[index++] = x;
        vert_array[index++] = y;
        vert_array[index++] = z;
        // Set color to red (you can adjust the color as needed)
        vert_array[index++] = 1.0f; // Red
        vert_array[index++] = 0.0f; // Green
        vert_array[index++] = 0.0f; // Blue
        vert_array[index++] = 1.0f; // Alpha
    
        // Vertex 2 (Bottom Right)
        vert_array[index++] = x + POLYGON_LENGTH;
        vert_array[index++] = y;
        vert_array[index++] = z;
        // Set color to red
        vert_array[index++] = 1.0f; 
        vert_array[index++] = 0.0f; 
        vert_array[index++] = 0.0f; 
        vert_array[index++] = 1.0f; 
    
        // Vertex 3 (Top Right)
        vert_array[index++] = x + POLYGON_LENGTH;
        vert_array[index++] = y + POLYGON_LENGTH;
        vert_array[index++] = z;
        // Set color to red
        vert_array[index++] = 1.0f; 
        vert_array[index++] = 0.0f; 
        vert_array[index++] = 0.0f;
        vert_array[index++] = 1.0f; 
    
        // Vertex 4 (Top Left)
        vert_array[index++] = x;
        vert_array[index++] = y + POLYGON_LENGTH;
        vert_array[index++] = z;
        // Set color to red
        vert_array[index++] = 1.0f;
        vert_array[index++] = 0.0f;
        vert_array[index++] = 0.0f;
        vert_array[index++] = 1.0f; 
    }//  private void fillSquareCoordinates(int indx, int row, int col, float[] vert_array)

    public void setVertexIndicesArray() {
        int numSquares = NUM_POLY_COLS * NUM_POLY_ROWS;
        int numIndicesPerSquare = 6; // 2 triangles per square
        vertexIndicesArray = new int[numSquares * numIndicesPerSquare];
        int index = 0;
        for (int i = 0; i < numSquares; i++) {
            int baseVertexIndex = i * vps; // vps = vertices per square
            // Triangle 1
            vertexIndicesArray[index++] = baseVertexIndex;
            vertexIndicesArray[index++] = baseVertexIndex + 1;
            vertexIndicesArray[index++] = baseVertexIndex + 2;
            // Triangle 2
            vertexIndicesArray[index++] = baseVertexIndex;
            vertexIndicesArray[index++] = baseVertexIndex + 2;
            vertexIndicesArray[index++] = baseVertexIndex + 3;
        }
    } //  public int[] setVertexIndicesArray(...)

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
        System.out.println("Exposed Gold Tiles: " + cellStats[0]);
        System.out.println("Unexposed Gold Tiles: " + cellStats[1]);
        System.out.println("Remaining Mines: " + cellStats[2]);
    }

    // status can be GE, ME, GU, MU
    public void setCellStatus(int row, int col, int status) {
    
        cellStatusArray[row * NUM_POLY_COLS + col] = status;

    }  //  public void setCellStatus(int row, int col, int status)

    public int getCellStatus(int row, int col) {
        return cellStatusArray[row * NUM_POLY_COLS + col];
    }

    public void updateStatusArrayToDisplayAll() {
        
        for (int row = 0; row < NUM_POLY_ROWS; row++) {
            for (int col = 0; col < NUM_POLY_COLS; col++) {
                int index = row * NUM_POLY_COLS + col;
                int status = cellStatusArray[index];
                if (status == GU) {
                    // If it's unexposed gold, update to exposed gold
                    cellStatusArray[index] = GE;
                } else if (status == MU) {
                    // If it's a mine, update to exposed mine
                    cellStatusArray[index] = ME;
                }
                // For GE (exposed gold), no change needed
            }
        }



    }

    public float[] getVertexArray() {
        return verticesArray;
    }

    public int[] getVertexIndicesArray() {
        return vertexIndicesArray;
    }

    public void printMineSweeperArray() {
        for (int row = 0; row < NUM_POLY_ROWS; row++) {
            for (int col = 0; col < NUM_POLY_COLS; col++) {
                int status = cellStatusArray[row * NUM_POLY_COLS + col];
                System.out.print(status + " ");
            }
            System.out.println(); // Move to the next row after printing each row
        }

    }  //  public void printMineSweeperArray()

    public int[] getCellStats() {
        return cellStats;
    }

    // returns (-1, -1) if a cell was not selected; else computes the row and column 
    // corresponding to the window coordinates and returns the updated retVec
    public static Vector2i getRowColFromXY(float xpos, float ypos) {
        Vector2i retVec = new Vector2i(-1, -1);

        // Calculate column
        int col = (int) ((xpos - POLY_OFFSET) / (POLYGON_LENGTH + POLY_PADDING));
        // Calculate row
        int row = (int) ((ypos - POLY_OFFSET) / (POLYGON_LENGTH + POLY_PADDING));
    
        // Check if the calculated row and column are within valid bounds
        if (row >= 0 && row < NUM_POLY_ROWS && col >= 0 && col < NUM_POLY_COLS) {
            retVec.set(row, col);
        }
    
        return retVec;
    }

}  //  public class slGeometryManager
