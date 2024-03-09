package SlRenderer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import csc133.slCamera;
import csc133.slWindow;
import csc133.spot;
import slKeyListener.slKeyListener;

import static csc133.slWindow.destroy_oglwindow;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4f;

import slGoBoard.slGoLBoardLive;

public class slSingleBatchRenderer  {
    GLFWErrorCallback errorCallback;
    GLFWKeyCallback keyCallback;
    GLFWFramebufferSizeCallback fbCallback;
    long window;
    slKeyListener listener;


    // call glCreateProgram() here - we have no gl-context here
    static int shader_program;
    static Matrix4f viewProjMatrix = new Matrix4f();
    static FloatBuffer myFloatBuffer = BufferUtils.createFloatBuffer(spot.OGL_MATRIX_SIZE);
    static int vpMatLocation = 0, renderColorLocation = 0;

    double frameRate = 0;

    long lastTime = System.nanoTime();
    long currentTime = System.nanoTime();

    boolean Delay_on = false;
    boolean displayFrameRate = false;
    boolean renderingPaused = false;
   
    static slGoLBoardLive my_board = new slGoLBoardLive(spot.MAX_ROW, spot.MAX_COL);


    private void renderObjects(){

        while (!glfwWindowShouldClose(window)) {

            glfwPollEvents();
            
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
  
            handleKeyInputs();

            if (renderingPaused) {
                continue; // Skip rendering if paused
            }else{
                draw_square_array();
            }

            glfwSwapBuffers(window);
        }

    }

    private void handleKeyInputs() {

        currentTime = System.nanoTime();
        double elapsedTime = (currentTime - lastTime) / 1_000_000_000.0; // Convert nanoseconds to seconds
        frameRate = 1 / elapsedTime;
        lastTime = currentTime;

        if (slKeyListener.isKeyPressed(GLFW_KEY_A)) {
            System.out.println("The 'A' button is pressed");
        }
        if (slKeyListener.isKeyPressed(GLFW_KEY_D)) {
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
            destroy_oglwindow();
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

    private void initOpenGL() {
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glViewport(0, 0, spot.WIN_WIDTH, spot.WIN_HEIGHT);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        shader_program = glCreateProgram();
        int vs = glCreateShader(GL_VERTEX_SHADER);
        Matrix4f viewProjMatrix = new Matrix4f();
        glShaderSource(vs,
                "uniform mat4 viewProjMatrix;" +
                        "void main(void) {" +
                        " gl_Position = viewProjMatrix * gl_Vertex;" +
                        "}");
        glCompileShader(vs);
        glAttachShader(shader_program, vs);
        int fs = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fs,
                "uniform vec3 renderColorLocation;" + 
                        "void main(void) {" +
                        " gl_FragColor = vec4(renderColorLocation, 1.0);" + 
                        "}");
        glCompileShader(fs);
        glAttachShader(shader_program, fs);
        glLinkProgram(shader_program);
        glUseProgram(shader_program);
        vpMatLocation = glGetUniformLocation(shader_program, "viewProjMatrix");
        return;
    }

    private static void drawSquare(float x, float y, float my_size, boolean color_switch) {
            
            int vbo = glGenBuffers();
            int ibo = glGenBuffers();

            float[] vertices = {
                    x, y,
                    x + my_size, y,
                    x + my_size, y + my_size,
                    x, y + my_size
            };

            int[] indices = {0, 1, 2, 0, 2, 3};

            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, (FloatBuffer) BufferUtils.
                    createFloatBuffer(vertices.length).
                    put(vertices).flip(), GL_STATIC_DRAW);
            glEnableClientState(GL_VERTEX_ARRAY);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, (IntBuffer) BufferUtils.
                    createIntBuffer(indices.length).
                    put(indices).flip(), GL_STATIC_DRAW);
            glVertexPointer(2, GL_FLOAT, 0, 0L);
            
            slCamera my_cam = new slCamera();
            my_cam.setProjectionOrtho();
            viewProjMatrix = my_cam.getProjectionMatrix();
            
            //viewProjMatrix.setOrtho(spot.ORTHO_LEFT, spot.ORTHO_RIGHT, spot.ORTHO_BOTTOM, spot.ORTHO_TOP, spot.ORTHO_NEAR, spot.ORTHO_FAR);

            glUniformMatrix4fv(vpMatLocation, false,
            viewProjMatrix.get(myFloatBuffer));
            int colorLocation = glGetUniformLocation(shader_program, "renderColorLocation");

            if(color_switch){
                glUniform3f(colorLocation, spot.Alive_color.x, spot.Alive_color.y, spot.Alive_color.z);
            }else{
                glUniform3f(colorLocation, spot.Dead_color.x, spot.Dead_color.y, spot.Dead_color.z);
            }
            
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            int VTD = 6; // need to process 6 Vertices To Draw 2 triangles
            glDrawElements(GL_TRIANGLES, VTD, GL_UNSIGNED_INT, 0L);

    
        
            // Clean up
            glDeleteBuffers(vbo);
            glDeleteBuffers(ibo);
            
    }



    private void draw_square_array() {
        
        int MAX_R = spot.MAX_ROW;
        int MAX_C = spot.MAX_COL;
        
        float my_size = 5f;
        float padding = 1.5f;
        float offsetX = -50f, offsetY = -50f;

        boolean color_switch = true;

        for (int row = 0; row < MAX_R; ++row) {
            for (int col = 0; col < MAX_C; ++col) {
                float x = offsetX + col * (my_size + padding);
                float y = offsetY + row * (my_size + padding);
                

                color_switch = my_board.return_Bool(row,col);
                drawSquare(x, y, my_size,color_switch);
                
                
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

        window = slWindow.get_oglwindow(spot.WIN_WIDTH,spot.WIN_HEIGHT);

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
        glfwSetWindowPos(window, spot.WIN_POS_X, spot.WIN_POS_Y);
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



    
}

