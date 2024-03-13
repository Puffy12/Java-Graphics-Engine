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

import java.io.*;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;


import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4f;

import slGoBoard.slGoLBoardLive;

public class slSingleBatchRenderer {
    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback keyCallback;
    private GLFWFramebufferSizeCallback fbCallback;
    private long window;



    // call glCreateProgram() here - we have no gl-context here
    private static int shader_program;
    private static Matrix4f viewProjMatrix = new Matrix4f();
    private static FloatBuffer myFloatBuffer = BufferUtils.createFloatBuffer(spot.OGL_MATRIX_SIZE);
    private static int vpMatLocation = 0, renderColorLocation = 0;

    private double frameRate = 0;

    private long lastTime = System.nanoTime();
    private long currentTime = System.nanoTime();

    private boolean Delay_on = false;
    private boolean displayFrameRate = false;
    private boolean renderingPaused = false;
    private boolean printBoard = false;
    private boolean endRendering = false;
   
    private slGoLBoardLive my_board = new slGoLBoardLive(spot.MAX_ROW, spot.MAX_COL);

    

    private void renderObjects(){

        while (!endRendering) {

            glfwPollEvents();
            
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            glfwSetKeyCallback(window, slKeyListener::keyCallback);

            handleKeyInputs();

            handleStates();

            /* This is what updates the board using the updateNextCellArray
            if(!endRendering){
                try {
                    Thread.sleep(250);
                    int temp = my_board.updateNextCellArray();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
             */
            

            glfwSwapBuffers(window);
        }

    }

    private void handleStates(){
        
        currentTime = System.nanoTime();
        double elapsedTime = (currentTime - lastTime) / 1_000_000_000.0; // Convert nanoseconds to seconds
        frameRate = 1 / elapsedTime;
        lastTime = currentTime;

        if (renderingPaused) {
            return; // Skip rendering if paused
        }else{
            draw_square_array();
        }
        if (displayFrameRate) {
            System.out.println("FPS:" + frameRate);
        }
        if(Delay_on){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void handleKeyInputs() {


        if (slKeyListener.isKeyPressed(GLFW_KEY_D)) {
            Delay_on = !Delay_on;
            slKeyListener.resetKeypressEvent(GLFW_KEY_D);
        }

        if(slKeyListener.isKeyPressed(GLFW_KEY_F)){
            slKeyListener.resetKeypressEvent(GLFW_KEY_F);
            displayFrameRate = !displayFrameRate;
        }

        if(slKeyListener.isKeyPressed(GLFW_KEY_ESCAPE)){

            slKeyListener.resetKeypressEvent(GLFW_KEY_ESCAPE);
            //destroy_oglwindow();
            endRendering = true;

            System.out.println("Bye Bye (Press ctrl c)");
        }

        if (slKeyListener.isKeyPressed(GLFW_KEY_H)) {
            slKeyListener.resetKeypressEvent(GLFW_KEY_H);
            renderingPaused = true;
        }
        if (slKeyListener.isKeyPressed(GLFW_KEY_SPACE)) {
            slKeyListener.resetKeypressEvent(GLFW_KEY_SPACE);
            renderingPaused = false;
        }

        if (slKeyListener.isKeyPressed(GLFW.GLFW_KEY_SLASH)) {
            if (!printBoard) {
                my_board.printGoLBoard();
                System.out.println("FYI, Board is printed upside down \n");

                System.out.println("Usage:");
                System.out.println("  D: Toggle Delay");
                System.out.println("  F: Toggle Display Frame Rate");
                System.out.println("  ESCAPE: Exit the program");
                System.out.println("  H: Pause rendering");
                System.out.println("  SPACE: Restart rendering");
                System.out.println("  R: Reset the GoL Board");
                System.out.println("  S: Save the GoL Board to a file");
                System.out.println("  L: Load the GoL Board from a file");
                System.out.println("  Custom! U: Update the board given the (countLiveTwoDegreeNeighbors rule in the updateNextCellArray function) \n");
                printBoard = true;
            }
        } else {
            printBoard = false; 
        }

        if(slKeyListener.isKeyPressed(GLFW_KEY_R)){
            my_board = new slGoLBoardLive(spot.MAX_ROW, spot.MAX_COL);
            slKeyListener.resetKeypressEvent(GLFW_KEY_R);
            System.out.println("Board Reset");
        }

        if (slKeyListener.isKeyPressed(GLFW_KEY_S)) {
            saveToFile();
            slKeyListener.resetKeypressEvent(GLFW_KEY_S);
            System.out.println("File Saved");
        }

        if (slKeyListener.isKeyPressed(GLFW_KEY_L)) {
            loadFromFile();
            slKeyListener.resetKeypressEvent(GLFW_KEY_L);
            System.out.println("File Loaded");
        }

        //a custom comand by me to update the cell array 
        if (slKeyListener.isKeyPressed(GLFW_KEY_U)) {
            int temp = 0;
            try {
                Thread.sleep(250);
                temp = my_board.updateNextCellArray();
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            slKeyListener.resetKeypressEvent(GLFW_KEY_U);
            System.out.println("Board Updated (Custom Command) -> "+ temp);
        }
   
    }


    private void saveToFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Board");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CA Files (*.ca)", "ca"));
        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (FileOutputStream fileOut = new FileOutputStream(fileToSave);
                 ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {

                
                for (int row = 0; row < spot.MAX_ROW; ++row) {
                    for (int col = 0; col < spot.MAX_COL; ++col) {
                        objectOut.writeObject(my_board.return_Bool(row, col));
                    }  
                    
                } 
              
                System.out.println("Board saved successfully to " + fileToSave.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Error saving board to file: " + e.getMessage());
            }
        }
    }

    private void loadFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Board");
        
        int userSelection = fileChooser.showOpenDialog(null);

    
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            try (FileInputStream fileIn = new FileInputStream(fileToLoad);
                 ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {
                
                slGoLBoardLive loadedBoard = new slGoLBoardLive(spot.MAX_ROW, spot.MAX_COL);
                for (int row = 0; row < spot.MAX_ROW; ++row) {
                    for (int col = 0; col < spot.MAX_COL; ++col) {
                        boolean temp = (boolean) objectIn.readObject();
                        if(temp){
                            loadedBoard.setCellAlive(row,col);
                        }else{
                            loadedBoard.setCellDead(row,col);
                        }
                    }
                }
    
                // Assuming my_board is a field of the current class
                my_board = loadedBoard;
    
                System.out.println("Board loaded successfully from " + fileToLoad.getAbsolutePath());
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading board from file: " + e.getMessage());
            }
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


    private void renderLoop() {
        glfwPollEvents();
        initOpenGL();
        renderObjects();
        /* Process window messages in the main thread */
        while (!endRendering) {
            glfwWaitEvents();
        }
    } // void renderLoop()



    
}

