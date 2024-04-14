package SlRenderer;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class slShaderManager {
    private String vsSource;
    private String fsSource;
    private int VSID = 0, FSID = 0;
    private int csProgram = 0;

    slShaderManager(String vs_filename, String fs_filename) {
        
        vs_filename = System.getProperty("user.dir") + "/src/assets/shaders/" + vs_filename;
        fs_filename = System.getProperty("user.dir") + "/src/assets/shaders/" + fs_filename;
        System.out.println(vs_filename + " : " + fs_filename);
        try {
            vsSource = new String(Files.readAllBytes(Paths.get(vs_filename)));
            fsSource = new String(Files.readAllBytes(Paths.get(fs_filename)));
        } catch (IOException e) {
            e.printStackTrace();
            assert false : "Error opening shader files: " + vs_filename + ", " + fs_filename;
        }
    }  // slShaderManager(String vs_filename, String fs_filename)

    public  int compile_shader() {
        csProgram = glCreateProgram();
        int VSID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(VSID, vsSource);
        glCompileShader(VSID);

        int FSID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(FSID, fsSource);
        glCompileShader(FSID);

        glAttachShader(csProgram, VSID);
        glAttachShader(csProgram, FSID);

        glLinkProgram(csProgram);

        return csProgram;
    }  // public boolean compile_shaders()

    public void set_shader_program() {
        glUseProgram(csProgram);
    }  // public void set_shader_program()

    public static void detach_shader() {
        glUseProgram(0);
    }  // public static void detach_shader()

    public void loadTexture(String texName, int texSlot) {
        int texLocation = glGetUniformLocation(csProgram, texName);
        set_shader_program();
        glUniform1i(texLocation, texSlot);
    }  // public void loadTexture(String texName, int texSlot)

    public void loadMatrix4f(String strMatrixName, Matrix4f my_mat4) {
        int var_location = glGetUniformLocation(csProgram, strMatrixName);
        final int OGL_MATRIX_SIZE = 16;
        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(OGL_MATRIX_SIZE);
        my_mat4.get(matrixBuffer);
        glUniformMatrix4fv(var_location, false, matrixBuffer);
    }  // private static void loadMatrix4f(String strMatrixName, Matrix4f my_mat4)


}
