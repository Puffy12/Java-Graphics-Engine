package csc133;
import org.joml.Vector3f;




public class spot {
    //viewProjMatrix.setOrtho(-100, 100, -100, 100, 0, 10);
    public static final int WIN_WIDTH = 1200;
    public static final int WIN_HEIGHT = 1200;
    public static final int OGL_MATRIX_SIZE = 16;

    public static final int ORTHO_LEFT = -100;
    public static final int ORTHO_RIGHT = 100;
    public static final int ORTHO_BOTTOM = -100;
    public static final int ORTHO_TOP = 100;

    public static final int ORTHO_NEAR = 0;
    public static final int ORTHO_FAR = 10;


    public static final int WIN_POS_X = 30, WIN_POX_Y = 90;

    public static final int MAX_ROW = 20; 
    public static final int MAX_COL = 18;

    public static final Vector3f Alive_color = new Vector3f(0f, 1f, 1f);
    public static final Vector3f Dead_color = new Vector3f(1f, 0f, 0f);

}
