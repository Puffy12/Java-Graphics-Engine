
package csc133;

import org.lwjgl.glfw.GLFWErrorCallback;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

public class slWindow {

    static GLFWErrorCallback errorCallback;

    private static long win = -1;
    static int WIN_WIDTH = 1200, WIN_HEIGHT = 1200;

    void slWindow_test() {
        System.out.println("Call to slWindow:: (width, height) == ("
                        + WIN_WIDTH + ", " + WIN_HEIGHT +") received!");
    }

    public static long create_window(int win_width, int win_height){

        WIN_WIDTH = win_width;
        WIN_HEIGHT = win_height;

        glfwSetErrorCallback(errorCallback =
                GLFWErrorCallback.createPrint(System.err));
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_SAMPLES, 8);
        if(win <= 0){
            //create window
            win = glfwCreateWindow(win_width, win_height, "CSC 133", NULL, NULL);
        }
        return win;

    }


}
