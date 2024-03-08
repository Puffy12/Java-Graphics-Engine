package SlRenderer;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import csc133.slWindow;
import csc133.spot;
import slKeyListener.slKeyListener;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryUtil.*;

import slGoBoard.*;

public class slSingleBatchRenderer  {
    GLFWErrorCallback errorCallback;
    GLFWKeyCallback keyCallback;
    GLFWFramebufferSizeCallback fbCallback;
    long window;
    slKeyListener listener;


    // call glCreateProgram() here - we have no gl-context here
    int shader_program;
    int vpMatLocation = 0;

    double frameRate = 0;

    long lastTime = System.nanoTime();
    long currentTime = System.nanoTime();

    boolean Delay_on = false;
    boolean displayFrameRate = false;
    boolean renderingPaused = false;
   
    private static slGoLBoard my_board;
    
    void renderObjects(){

        while (!glfwWindowShouldClose(window)) {
            
            glfwPollEvents();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            if (renderingPaused) {
                continue; // Skip rendering if paused
            }

            handleKeyInputs();

            draw_square_array();

            glfwSwapBuffers(window);
        }

    }
  
    
    private void handleKeyInputs() {

        double elapsedTime = (currentTime - lastTime) / 1_000_000_000.0; // Convert nanoseconds to seconds
        frameRate = 1 / elapsedTime;
        lastTime = currentTime;

        
        if (slKeyListener.isKeyPressed(GLFW_KEY_D)) {// D key is currently pressed
            Delay_on = !Delay_on;
        }
        if(Delay_on){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(slKeyListener.isKeyPressed(GLFW_KEY_F)){
            displayFrameRate = !displayFrameRate;
        }
        if (displayFrameRate) {
            System.out.println("FPS:" + frameRate);
        }
        if(slKeyListener.isKeyPressed(GLFW_KEY_ESCAPE)){
            glfwSetWindowShouldClose(window, true);
        }

        if (slKeyListener.isKeyPressed(GLFW_KEY_SLASH)) {
            // Print usage instructions
            //printUsage();
        }

        if (slKeyListener.isKeyPressed(GLFW_KEY_H)) {
            renderingPaused = !renderingPaused;
        }

        if(slKeyListener.isKeyPressed(GLFW_KEY_S)){
            //Save 
            //        if (!fileName.toLowerCase().endsWith(".ca")) {fileName += ".ca";
        }

        if(slKeyListener.isKeyPressed(GLFW_KEY_L)){
            //load
        }
   
    }


    private void draw_square_array() {
        


        float my_size = 3f;
        float padding = 1.2f;
        float offsetX = -30f, offsetY = -50f;
        int MAX_R = 18;
        int MAX_C = 20;

        for (int row = 0; row < MAX_R; ++row) {
            for (int col = 0; col < MAX_C; ++col) {
                float x = offsetX + col * (my_size + padding);
                float y = offsetY + row * (my_size + padding);
                
                
                Draw.drawSquare(x, y, my_size, shader_program);
            }
        }
        glfwSwapBuffers(window);

    }


    public void render() {
        try {
            initGLFWindow();
            renderLoop();
            glfwDestroyWindow(window);
            keyCallback.free();
            fbCallback.free();
        } finally {
            glfwTerminate();
            glfwSetErrorCallback(null).free();
        }
    } // void render()


    private void initGLFWindow() {

        window = slWindow.create_window(spot.WIN_WIDTH,spot.WIN_HEIGHT);

        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");
            
        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int
                    mods) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                    glfwSetWindowShouldClose(window, true);
            }
        });
        glfwSetFramebufferSizeCallback(window, fbCallback = new
                GLFWFramebufferSizeCallback() {
                    @Override
                    public void invoke(long window, int w, int h) {
                        if (w > 0 && h > 0) {
                            //spot.WIN_WIDTH = w;
                            //spot.WIN_HEIGHT = h;
                        }
                    }
                });
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(window, spot.WIN_POS_X, spot.WIN_POX_Y);
        glfwMakeContextCurrent(window);
        int VSYNC_INTERVAL = 1;
        glfwSwapInterval(VSYNC_INTERVAL);
        glfwShowWindow(window);
    } // private void initGLFWindow()


    void renderLoop() {
        glfwPollEvents();
        initOpenGL();
        renderObjects();
        /* Process window messages in the main thread */
        while (!glfwWindowShouldClose(window)) {
            glfwWaitEvents();
        }
    } // void renderLoop()


    void initOpenGL() {
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glViewport(0, 0, spot.WIN_WIDTH, spot.WIN_HEIGHT);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        this.shader_program = glCreateProgram();
        int vs = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vs,
                "uniform mat4 viewProjMatrix;" +
                        "void main(void) {" +
                        " gl_Position = viewProjMatrix * gl_Vertex;" +
                        "}");
        glCompileShader(vs);
        glAttachShader(shader_program, vs);
        int fs = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fs,
                "uniform vec3 color;" +
                        "void main(void) {" +
                        " gl_FragColor = vec4(0.0f, 1.0f, 0.0f, 1.0f);" +
                        "}");
        glCompileShader(fs);
        glAttachShader(shader_program, fs);
        glLinkProgram(shader_program);
        glUseProgram(shader_program);
        vpMatLocation = glGetUniformLocation(shader_program, "viewProjMatrix");
        return;
    } // void initOpenGL()
    
}

