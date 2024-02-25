package csc133;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class slCamera {
    
    float f_left, f_right, f_bottom, f_top, f_near, f_far;

    private Matrix4f viewMatrix, projectionMatrix;
    private Vector3f defaultLookFrom, defaultLookAt, defaultUpVector;
    private Vector3f curLookFrom, curLookAt, curUpVector;

    public void setProjectionOrtho() {
        projectionMatrix.identity();
        projectionMatrix.setOrtho(spot.ORTHO_LEFT, spot.ORTHO_RIGHT, spot.ORTHO_BOTTOM, spot.ORTHO_TOP, spot.ORTHO_NEAR, spot.ORTHO_FAR);
    }

    public void setProjectionOrtho(float left, float right, float bottom, float top, float near, float far) {
        f_left = left;
        f_right = right;
        f_bottom = bottom;
        f_top = top;
        f_near = near;
        f_far = far;
        setProjectionOrtho();
    }

 
    public slCamera(Vector3f camera_position) {
        this.defaultLookFrom.set(camera_position);
        setCamera();
    }

    public slCamera() {
        this.projectionMatrix = new Matrix4f().identity();
        this.viewMatrix = new Matrix4f().identity();
        this.defaultLookFrom = new Vector3f(0f,0f,10f);
        this.defaultLookAt = new Vector3f(0f, 0f, -1.0f);
        this.defaultUpVector = new Vector3f(0f, 1.0f, 0f);
        this.curLookFrom = new Vector3f();
        this.curLookAt = new Vector3f();
        this.curUpVector = new Vector3f();
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    private void setCamera() {
        this.projectionMatrix = new Matrix4f().identity();
        this.viewMatrix = new Matrix4f().identity();
    }

}
