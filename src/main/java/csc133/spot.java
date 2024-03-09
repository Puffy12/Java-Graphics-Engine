package csc133;
import org.joml.Vector3f;
import org.joml.Vector4f;




public class spot {
    //viewProjMatrix.setOrtho(-100, 100, -100, 100, 0, 10);
    public static final int WIN_WIDTH = 900;
    public static final int WIN_HEIGHT = 900;
    public static final int OGL_MATRIX_SIZE = 16;
    

    public static final int ORTHO_LEFT = -100;
    public static final int ORTHO_RIGHT = 100;
    public static final int ORTHO_BOTTOM = -100;
    public static final int ORTHO_TOP = 100;

    public static final int ORTHO_NEAR = 0;
    public static final int ORTHO_FAR = 10;


    public static final int WIN_POS_X = 30, WIN_POS_Y = 90;

    public static final int MAX_ROW = 20; 
    public static final int MAX_COL = 18;

    public static final Vector4f Alive_color = new Vector4f(0.0f, 1.0f, 0.0f, 1.0f);
    public static final Vector4f Dead_color = new Vector4f(1.0f, 0f, 0f, 1.0f);

    public static final Vector3f VEC_RC = new Vector3f(0.0f, 1.0f, 0.0f); // "vector render color" for square


}
