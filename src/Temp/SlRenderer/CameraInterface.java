package SlRenderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

interface CameraInterface {
    public void setOrthoProjection();
    public void relativeMoveCamera(float deltaX, float deltaY);
    public Vector3f getCurLookFrom();
    public void setCurLookFrom(Vector3f new_lf);
    public Vector3f getCurLookAt();
    public Matrix4f getViewMatrix();
    public Matrix4f getProjectionMatrix();
   }
