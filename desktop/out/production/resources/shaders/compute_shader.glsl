#version 310 es

layout (local_size_x = 64, local_size_y = 1, local_size_z = 1) in;

// Define structure for circle data
struct Circle {
    vec2 position;
    float radius;
};

layout(binding = 0) buffer CirclesBuffer {
    Circle circles[];
};

void main() {
    uint index = gl_GlobalInvocationID.x;

    Circle circleA = circles[index];

    for (uint i = 0; i < circles.length(); ++i) {
        if (i == index) continue; // Skip self-collision

        Circle circleB = circles[i];

        // Perform collision detection (e.g., circle-circle collision)
        vec2 distance = circleB.position - circleA.position;
        float minDistance = circleA.radius + circleB.radius;

        if (length(distance) < minDistance) {
            // Handle collision (e.g., update velocities or positions)
            circles[index].position += distance * 0.5;

        }
    }
}