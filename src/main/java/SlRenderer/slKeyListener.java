package SlRenderer;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;


public class slKeyListener {
    private static slKeyListener my_instance;

    private static final int MAX_KEYS = 400;
    private boolean keyPressed[] = new boolean[MAX_KEYS]; // some "reasonable" number!

    private slKeyListener() {

    }

    public static slKeyListener get() {
        if (my_instance == null) {
            my_instance = new slKeyListener();
        }
        return my_instance;
    }

    public static void keyCallback(long my_window, int key, int scancode,
                                   int action, int modifier_key) {
        if (action == GLFW_PRESS) {
            get().keyPressed[key] = true;
        } else if (action == GLFW_RELEASE){
            get().keyPressed[key] = false;
        }
    }

    public static boolean isKeyPressed(int keyCode){
        if (keyCode < get().keyPressed.length) {
            return get().keyPressed[keyCode];
        } else {
            return false;
        }
    }
}
