package SlRenderer;


import static org.lwjgl.opengl.GL20.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import csc133.spot;

public class slShaderManager {
    private static String file1, file2;
    private static String fragmentShaderSource, vertexShaderSource;
    private static final String path = "src/assets/shaders/";
    private static int shader_program;
    private static final int OGL_MATRIX_SIZE = spot.OGL_MATRIX_SIZE;

    public slShaderManager(String import_file_1, String import_file_2) throws FileNotFoundException, IOException {

        file1 = path + import_file_1;
        file2 = path + import_file_2;

        StringBuilder shaderSource = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file1))) {
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line);
            }
        }
        vertexShaderSource =  shaderSource.toString();

        
        StringBuilder shaderSource2 = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file2))) {
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource2.append(line);
            }
        }
        fragmentShaderSource =  shaderSource2.toString();
    }

    public int compile_shader() {
        shader_program = glCreateProgram(); // Assign to shader_program field
        int VSID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(VSID,vertexShaderSource);
        glCompileShader(VSID);
        glAttachShader(shader_program, VSID); // Attach to shader_program
        int FSID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(FSID, fragmentShaderSource);
        glCompileShader(FSID);
        glAttachShader(shader_program, FSID); // Attach to shader_program
        glLinkProgram(shader_program); // Link shader program
        glUseProgram(shader_program); // Use the shader program
        return shader_program;
    }

    public void set_shader_program(){

        glUseProgram(shader_program);

    }

    public static void loadMatrix4f(String strMatrixName, Matrix4f my_mat4) {
        int var_location = glGetUniformLocation(shader_program, strMatrixName);
        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(OGL_MATRIX_SIZE);
        my_mat4.get(matrixBuffer);
        glUniformMatrix4fv(var_location, false, matrixBuffer);
    }

    public static void detach_shader() {
        glUseProgram(0);
    }
}
