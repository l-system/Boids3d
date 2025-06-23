package com.libgdxdemo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.assets.AssetManager;


import java.util.ArrayList;

public class BoidsApp extends ApplicationAdapter {
    private float time;
    private float normalizedValue;  // The value to oscillate

    private ShaderProgram shader;
    private Mesh boidMesh;
    private PerspectiveCamera camera;
    private InputHandler inputHandler;
    private AssetManager assetManager;


    private final int MAX_BOIDS = 1000;
    float boidSize = 0.5f;
    private final ArrayList<Boid> boids = new ArrayList<>();

    // Temporary vectors for calculations, reducing object creation in render loop
    private final Vector3 tempVector = new Vector3();
    private final Quaternion tempQuaternion = new Quaternion();
    private final Vector3 forward = new Vector3(0, 0, 1); // The initial forward direction of the boid mesh

    @Override
    public void create() {
        time = 0;

        // First, create the camera since other components depend on it
        float viewportWidth = Gdx.graphics.getWidth();
        float viewportHeight = Gdx.graphics.getHeight();

        camera = new PerspectiveCamera(67f, viewportWidth, viewportHeight);
        camera.position.set(0f, 0f, 600f);
        camera.lookAt(0f, 0f, 0f);
        camera.up.set(0f, 1f, 0f);
        camera.near = 0.001f;
        camera.far = 10e3f;
        camera.update();

        // Initialize input handler
        inputHandler = new InputHandler(camera);
        inputHandler.initialize();

        // Initialize boids
        for (int i = 0; i < MAX_BOIDS; i++) {
            Boid b = new Boid(
                    (float) Math.random() * 10f - 5f,
                    (float) Math.random() * 10f - 5f,
                    (float) Math.random() * 10f - 5f
            );
            boids.add(b);
        }

        // Set up the shader
        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(
                Gdx.files.internal("shaders/vertex_shader.glsl"),
                Gdx.files.internal("shaders/fragment_shader.glsl")
        );

        if (!shader.isCompiled()) {
            throw new GdxRuntimeException("Shader compile error: " + shader.getLog());
        }

        // Create the boid mesh
        boidMesh = createBoidMesh();

        // Set up the particle system
        assetManager = new AssetManager();
    }

    private Mesh createBoidMesh() {
        // Define vertices of an octahedron with an elongated front
        Vector3[] vertexPositions = {
                new Vector3(0, boidSize, 0),    // Top vertex
                new Vector3(boidSize, 0, 0),    // Right vertex
                new Vector3(0, 0, boidSize * 3),    // Front vertex (elongated)
                new Vector3(-boidSize, 0, 0),   // Left vertex
                new Vector3(0, 0, -boidSize),   // Back vertex
                new Vector3(0, -boidSize, 0)    // Bottom vertex
        };

        int numVertices = 24;
        int numIndices = 24;

        Mesh mesh = new Mesh(true, numVertices, numIndices,
                new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"),
                new VertexAttribute(VertexAttributes.Usage.Normal, 3, "a_normal"));

        float[] vertexData = new float[numVertices * 6];
        short[] indices = new short[numIndices];

        int[][] faceIndices = {
                {0, 1, 2}, {0, 2, 3}, {0, 3, 4}, {0, 4, 1},
                {5, 2, 1}, {5, 3, 2}, {5, 4, 3}, {5, 1, 4}
        };

        int vertexOffset = 0;
        int indexOffset = 0;

        for (int face = 0; face < 8; face++) {
            int v1 = faceIndices[face][0];
            int v2 = faceIndices[face][1];
            int v3 = faceIndices[face][2];

            Vector3 p1 = vertexPositions[v1];
            Vector3 p2 = vertexPositions[v2];
            Vector3 p3 = vertexPositions[v3];

            tempVector.set(p2).sub(p1);
            Vector3 edge2 = new Vector3(p3).sub(p1);
            Vector3 normal = new Vector3().set(tempVector).crs(edge2).nor();

            for (int i = 0; i < 3; i++) {
                Vector3 position = vertexPositions[faceIndices[face][i]];
                vertexData[vertexOffset++] = position.x;
                vertexData[vertexOffset++] = position.y;
                vertexData[vertexOffset++] = position.z;
                vertexData[vertexOffset++] = normal.x;
                vertexData[vertexOffset++] = normal.y;
                vertexData[vertexOffset++] = normal.z;
                indices[indexOffset++] = (short) (face * 3 + i);
            }
        }

        mesh.setVertices(vertexData);
        mesh.setIndices(indices);

        return mesh;
    }


    @Override
    public void render() {
        inputHandler.update();
        camera.update();

        float delta = Gdx.graphics.getDeltaTime();
        for (Boid b : boids) {
            b.update(delta, boids);
        }

        time += delta;
        normalizedValue = (MathUtils.sin(time * 0.01f) + 1) / 2;

        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL30.GL_DEPTH_TEST);
        Gdx.gl.glEnable(GL30.GL_BLEND);
        Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE);


        shader.bind();
        shader.setUniformf("u_time", normalizedValue);
        shader.setUniformf("u_resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shader.setUniformf("u_cameraPos", camera.position);
        shader.setUniformMatrix("u_viewMatrix", camera.view);
        shader.setUniformMatrix("u_projMatrix", camera.projection);
        shader.setUniformf("u_lightPos", new Vector3(0.0f, 0.0f, 0.0f));

        for (Boid b : boids) {
            // Calculate the rotation needed to align the boid's forward axis with its velocity
            tempVector.set(b.velocity).nor(); // Get the normalized velocity direction
            tempQuaternion.setFromCross(forward, tempVector); // Calculate the rotation

            // Create the model matrix with translation and rotation
            Matrix4 model = new Matrix4()
                    .setToTranslation(b.position)
                    .rotate(tempQuaternion); // Apply the rotation

            shader.setUniformMatrix("u_modelMatrix", model);
            boidMesh.render(shader, GL30.GL_TRIANGLES);
        }
        shader.end();

        Gdx.gl.glDisable(GL30.GL_BLEND);
    }


    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    @Override
    public void dispose() {
        boidMesh.dispose();
        shader.dispose();
        assetManager.dispose();
    }
}