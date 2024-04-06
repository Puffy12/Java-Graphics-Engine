package csc133;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class spot {

        public static long my_oglwindow = 0;
        public static int WIN_WIDTH = 800, WIN_HEIGHT = 800;
        public static int WIN_POS_X = 50, WIN_POS_Y = 150;
        public static String WINDOW_TITLE = "CSC 133";
        public static final int OGL_MATRIX_SIZE = 16;
    
        public static int NUM_POLY_ROWS = 1, NUM_POLY_COLS = 1, POLYGON_LENGTH = 200;
    
        public static final float POLY_OFFSET = 20.0f, POLY_PADDING = 10.0f, SQUARE_LENGTH = 200;
    
        public static final float FRUSTUM_LEFT = 0.0f,   
                FRUSTUM_RIGHT = (float)WIN_WIDTH,
                FRUSTUM_BOTTOM = 0.0f, 
                FRUSTUM_TOP = (float)WIN_HEIGHT,
                Z_NEAR = 0.0f, Z_FAR = 10.0f;
    
        public static final Vector3f VEC_RC =
                new Vector3f(0.0f, 0.498f, 0.0153f); // "vector render color" for square
    
        public static final Vector4f liveColor = new Vector4f(1.0f, 1.0f, 0.0f, 1.0f);
        public static final Vector4f deadColor = new Vector4f(1.0f, 0.0f, 0.0f, 1.0f);
    
        //-----------------------------------------------------------------------------------
        public static final float defaultSL = 0.0f, defaultSR = WIN_WIDTH,
                defaultSB = 0.0f, defaultST = WIN_HEIGHT;
        public static final float defaultZNear = 0.0f, defaultZFar = 10.0f;

        public static final float alpha = 200f;
    
    }