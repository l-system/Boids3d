package com.libgdxdemo;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;

public class InputHandler extends InputAdapter implements InputProcessor {

    private Camera camera;
    private CameraInputController cameraController;

    // Flags to track key states
    private boolean moveForward, moveBackward, moveLeft, moveRight;

    public InputHandler(Camera camera) {
        this.camera = camera;
        this.cameraController = new CameraInputController(camera);  // Setup the default camera controls
    }

    public void initialize() {
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(cameraController);  // Add the camera controller first
        inputMultiplexer.addProcessor(this);  // Add custom input handling (for WASD movement)
        Gdx.input.setInputProcessor(inputMultiplexer);  // Set up the input system
    }

    @Override
    public boolean keyDown(int keycode) {
        // Handle the WASD keys for movement
        if (keycode == Input.Keys.W) {
            moveForward = true;  // Start moving forward
        } else if (keycode == Input.Keys.S) {
            moveBackward = true;  // Start moving backward
        } else if (keycode == Input.Keys.A) {
            moveLeft = true;  // Start moving left
        } else if (keycode == Input.Keys.D) {
            moveRight = true;  // Start moving right
        }
        return false;  // Let other processors handle the event
    }

    @Override
    public boolean keyUp(int keycode) {
        // Stop movement when key is released
        if (keycode == Input.Keys.W) {
            moveForward = false;
        } else if (keycode == Input.Keys.S) {
            moveBackward = false;
        } else if (keycode == Input.Keys.A) {
            moveLeft = false;
        } else if (keycode == Input.Keys.D) {
            moveRight = false;
        }
        return false;
    }

    public void update() {
        float speed = 100f * Gdx.graphics.getDeltaTime(); // frame rate-independent movement
        Vector3 movement = new Vector3();

        if (moveForward) {
            movement.add(camera.direction.cpy().scl(speed));
        }
        if (moveBackward) {
            movement.add(camera.direction.cpy().scl(-speed));
        }

        // For strafing left/right, we use the camera's right vector (crossed with up)
        Vector3 right = camera.direction.cpy().crs(camera.up).nor();

        if (moveLeft) {
            movement.add(right.cpy().scl(-speed));
        }
        if (moveRight) {
            movement.add(right.cpy().scl(speed));
        }

        camera.position.add(movement);
    }


    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(float v, float v1) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }
}
