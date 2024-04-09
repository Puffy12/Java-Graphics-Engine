package SlRenderer;


import org.joml.Matrix4f;
import org.joml.Vector3f;

import static csc133.spot.*;

interface CameraInterface {
    public void setOrthoProjection();
    public void relativeMoveCamera(float deltaX, float deltaY);
    public Vector3f getCurLookFrom();
    public void setCurLookFrom(Vector3f new_lf);
    public Vector3f getCurLookAt();
    public Matrix4f getViewMatrix();
    public Matrix4f getProjectionMatrix();
}


class slCamera implements CameraInterface {
    private Matrix4f projectionMatrix, viewMatrix;
    public Vector3f defaultLookFrom;
    public Vector3f defaultLookAt;
    public Vector3f defaultUpVector;

    // Inside getViewMatrix() or elsewhere, don't update the above: will be needed
    // if we reset the camera to starting position. Instead mutate the following where needed:
    public Vector3f curLookAt;
    public Vector3f curLookFrom;
    public Vector3f curUpVector;

    private float screen_left = FRUSTUM_LEFT, screen_right = FRUSTUM_RIGHT,
                    screen_bottom = FRUSTUM_BOTTOM, screen_top = FRUSTUM_TOP;
    private float zNear = Z_NEAR, zFar = Z_FAR;  // these are NOT pixels!

    private void init_camera() {
        defaultLookFrom = new Vector3f(0.0f, 0.0f, 00.0f);
        defaultLookAt = new Vector3f(0.0f, 0.0f, -1.0f);
        defaultUpVector = new Vector3f(0.0f, 1.0f, 0.0f);

        curLookFrom = new Vector3f(defaultLookFrom);
        curLookAt   = new Vector3f(defaultLookAt);
        curUpVector = new Vector3f(defaultUpVector);
    }

    public slCamera() {
        init_camera();
    }

    public slCamera(Vector3f look_from, Vector3f look_at, Vector3f view_up) {
        defaultLookFrom.set(look_from);
        defaultLookAt.set(look_at);
        defaultUpVector.set(view_up);

        curLookFrom = new Vector3f(defaultLookFrom);
        curLookAt   = new Vector3f(defaultLookAt);
        curUpVector = new Vector3f(defaultUpVector);
    }

    // camera_position.z > 0 as (0, 0, 0) is at the center of the screen; e.g: (0, 0, 20):
    public slCamera(Vector3f camera_position) {
        init_camera();
        defaultLookFrom.set(camera_position);
        curLookFrom.set(camera_position);
        projectionMatrix = new Matrix4f();
        projectionMatrix.identity();
        viewMatrix = new Matrix4f();
        viewMatrix.identity();
        setOrthoProjection();
    }

    public void setOrthoProjection() {
        projectionMatrix.identity();
        projectionMatrix.ortho(screen_left, screen_right,
                                    screen_bottom, screen_top, zNear, zFar);
    }

    // Careful: this is camera movement to imidate an object moving right. So
    // positive arguments are interpreted as negative increment to CURRENT position
    public void relativeMoveCamera(float deltaX, float deltaY) {
        curLookFrom.x -= deltaX;
        curLookFrom.y -= deltaY;
    }  // public void relativeMoveCamera(...)

    void restoreCamera() {
        init_camera();
        curLookFrom.set(defaultLookFrom);
        projectionMatrix = new Matrix4f();
        projectionMatrix.identity();
        viewMatrix = new Matrix4f();
        viewMatrix.identity();
        setOrthoProjection();
    }

    public Vector3f getCurLookFrom() {
        return curLookFrom;
    }

    public void setCurLookFrom(Vector3f new_lf) {
        curLookFrom.set(new_lf);
    }

    public Vector3f getCurLookAt() {
        return curLookAt;
    }

    public void setCurLookAt(Vector3f new_la) {
        curLookAt.set(new_la);
    }

    public Matrix4f getViewMatrix() {
        curLookAt.set(defaultLookAt);
        viewMatrix.identity();
        viewMatrix.lookAt(curLookFrom, curLookAt.add(curLookFrom), curUpVector);

        return viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }
}
