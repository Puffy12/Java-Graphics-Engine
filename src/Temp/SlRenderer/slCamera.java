package SlRenderer;


import org.joml.Matrix4f;
import org.joml.Vector3f;

import static csc133.spot.*;

   
public class slCamera implements CameraInterface{
    private final Matrix4f projectionMatrix, viewMatrix;
    public Vector3f defaultLookFrom = new Vector3f(0.0f, 0.0f, 00.0f);
    public Vector3f defaultLookAt = new Vector3f(0.0f, 0.0f, -1.0f);
    public Vector3f defaultUpVector = new Vector3f(0.0f, 1.0f, 0.0f);

    // Inside getViewMatrix() or elsewhere, don't update the above: will be needed
    // if we reset the camera to starting position. Instead mutate the following where needed:
    private final Vector3f curLookFrom = new Vector3f(defaultLookFrom);
    private final Vector3f curLookAt   = new Vector3f(defaultLookAt);
    private final Vector3f curUpVector = new Vector3f(defaultUpVector);

    // camera_position.z > 0 as (0, 0, 0) is at the center of the screen; e.g: (0, 0, 20):
    public slCamera(Vector3f camera_position) {
        this.defaultLookFrom = camera_position;
        this.projectionMatrix = new Matrix4f();
        this.projectionMatrix.identity();
        this.viewMatrix = new Matrix4f();
        this.viewMatrix.identity();
        setProjection();
    }

    public void setProjection() {
        projectionMatrix.identity();
        projectionMatrix.ortho(FRUSTUM_LEFT, FRUSTUM_RIGHT,
                FRUSTUM_BOTTOM, FRUSTUM_TOP, Z_NEAR, Z_FAR);
    }

    @Override
    public void setOrthoProjection() {
        setProjection();
    }

    @Override
    public void relativeMoveCamera(float deltaX, float deltaY) {
        // Modify current lookFrom position based on relative movement
        curLookFrom.add(deltaX, deltaY, 0.0f);
    }

    @Override
    public Vector3f getCurLookFrom() {
        return new Vector3f(curLookFrom);
    }

    @Override
    public void setCurLookFrom(Vector3f new_lf) {
        curLookFrom.set(new_lf);
    }

    @Override
    public Vector3f getCurLookAt() {
        return new Vector3f(curLookAt);
    }

    @Override
    public Matrix4f getViewMatrix() {
        curLookFrom.set(defaultLookFrom);
        curLookAt.set(defaultLookAt);
        this.viewMatrix.identity();
        this.viewMatrix.lookAt(curLookFrom, curLookAt.add(defaultLookFrom), curUpVector);

        return this.viewMatrix;
    }

    @Override
    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

    public void restoreCamera() {
        curLookFrom.set(defaultLookFrom); // Restore camera position to initial state
    }

}