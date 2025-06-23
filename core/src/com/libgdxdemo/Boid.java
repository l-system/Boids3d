package com.libgdxdemo;

import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;

public class Boid {
    public Vector3 position;
    public Vector3 velocity;
    public Vector3 acceleration;

    private float maxforce;    // Maximum steering force
    private float maxspeed;    // Maximum speed

    // Constructor
    public Boid(float x, float y, float z) {
        position = new Vector3(x, y, z);
        velocity = new Vector3((float) Math.random() * 2 - 1, (float) Math.random() * 2 - 1, (float) Math.random() * 2 - 1);
        velocity.nor().scl(2); // Normalize and set speed

        acceleration = new Vector3(0, 0, 0);
        maxspeed = 400;          // max speed
        maxforce = 4f;      // max steering force
    }

    // Update boid position
    public void update(float delta, ArrayList<Boid> boids) {
        flock(boids);  // Apply flocking behavior

        // Update velocity based on acceleration
        velocity.add(acceleration);
        velocity.limit(maxspeed); // Limit velocity to maxspeed

        // Update position based on velocity, including delta time
        // Use a temporary vector to avoid permanently scaling the velocity
        position.add(new Vector3(velocity).scl(delta)); // Scale by delta for frame rate independence

        // Reset acceleration for next frame
        acceleration.setZero();

        // Apply boundary conditions (wrapping around the screen)
        borders(); // Call after position update to apply boundary logic
    }

    // Apply a force to the boid
    public void applyForce(Vector3 force) {
        acceleration.add(force);
    }

    // Flocking behavior: Separation, Alignment, Cohesion
    public void flock(ArrayList<Boid> boids) {
        Vector3 sep = separate(boids);
        Vector3 ali = align(boids);
        Vector3 coh = cohesion(boids);

        sep.scl(1.5f);
        ali.scl(1.0f);
        coh.scl(0.5f);


        applyForce(sep);
        applyForce(ali);
        applyForce(coh);
    }

    // Separation: steer to avoid crowding nearby boids
    public Vector3 separate(ArrayList<Boid> boids) {
        float desiredseparation = 10.0f;
        Vector3 steer = new Vector3();
        int count = 0;

        for (Boid other : boids) {
            float d = position.dst(other.position);
            if (d > 0 && d < desiredseparation) {
                Vector3 diff = new Vector3(position).sub(other.position);
                diff.nor().scl(1 / d); // Weight by distance
                steer.add(diff);
                count++;
            }
        }

        if (count > 0) {
            steer.scl(1f / count);  // Average steering force
        }

        if (steer.len() > 0) {
            steer.nor().scl(maxspeed); // Match desired velocity
            steer.sub(velocity);       // Steering = desired - current velocity
            steer.limit(maxforce);     // Limit the steering force
        }

        return steer;
    }

    // Alignment: steer to match the average velocity of neighbors
    public Vector3 align(ArrayList<Boid> boids) {
        float neighbordist = 30;
        Vector3 sum = new Vector3();
        int count = 0;

        for (Boid other : boids) {
            float d = position.dst(other.position);
            if (d > 0 && d < neighbordist) {
                sum.add(other.velocity);
                count++;
            }
        }

        if (count > 0) {
            sum.scl(1f / count);  // Average velocity
            sum.nor().scl(maxspeed); // Match desired speed
            Vector3 steer = new Vector3(sum).sub(velocity);  // Steering = desired - current velocity
            steer.limit(maxforce);     // Limit the steering force
            return steer;
        }
        return new Vector3();
    }

    // Cohesion: steer to move toward the average position of neighbors
    public Vector3 cohesion(ArrayList<Boid> boids) {
        float neighbordist = 40;
        Vector3 sum = new Vector3();
        int count = 0;

        for (Boid other : boids) {
            float d = position.dst(other.position);
            if (d > 0 && d < neighbordist) {
                sum.add(other.position);
                count++;
            }
        }

        if (count > 0) {
            sum.scl(1f / count);  // Average position
            return seek(sum);     // Seek towards average position
        }
        return new Vector3();
    }

    // Seek towards a target
    public Vector3 seek(Vector3 target) {
        Vector3 desired = new Vector3(target).sub(position);
        desired.nor().scl(maxspeed);  // Desired velocity
        Vector3 steer = new Vector3(desired).sub(velocity);  // Steering force
        steer.limit(maxforce);         // Limit the steering force
        return steer;
    }


    // Spherical border
    private void borders() {
        float boundaryThreshold = 500.0f;  // Radius of spherical boundary
        float bufferZone = 100.0f;         // How close to the edge before reacting
        float bounceFactor = 0.5f;        // Damping factor for soft bounce
        float r = 2.0f;                  // Radius of the boid/object

        float x = position.x;
        float y = position.y;
        float z = position.z;

        float distanceFromCenter = (float)Math.sqrt(x * x + y * y + z * z);

        if (distanceFromCenter + r > boundaryThreshold - bufferZone) {
            // Normalize position vector to get direction
            float nx = x / distanceFromCenter;
            float ny = y / distanceFromCenter;
            float nz = z / distanceFromCenter;

            // Reflect velocity vector away from the boundary direction and apply bounce
            float dot = velocity.x * nx + velocity.y * ny + velocity.z * nz;
            velocity.x = velocity.x - 2 * dot * nx;
            velocity.y = velocity.y - 2 * dot * ny;
            velocity.z = velocity.z - 2 * dot * nz;

            // Apply bounce damping
            velocity.x *= bounceFactor;
            velocity.y *= bounceFactor;
            velocity.z *= bounceFactor;

            // Push the position back inside the sphere just within the boundary
            float newPosRadius = boundaryThreshold - bufferZone - r;
            position.x = nx * newPosRadius;
            position.y = ny * newPosRadius;
            position.z = nz * newPosRadius;
        }
    }
}