# 3D Boids Simulation

> **Note**: This is a hobbyist project in active development. The code is provided for educational reference and experimentation purposes.

A mesmerizing 3D flocking simulation built with Java libGDX and OpenGL, featuring 1000 autonomous boid agents that exhibit emergent flocking behavior within a spherical boundary.

## Features

- **Realistic Flocking Behavior**: Implements the classic boids algorithm with three core behaviors:
  - **Separation**: Boids steer away from crowded neighbors
  - **Alignment**: Boids align with the average velocity of nearby flockmates
  - **Cohesion**: Boids move toward the center of mass of local neighbors

- **3D Visualization**: 
  - Custom octahedral boid meshes with elongated front for directional clarity
  - Dynamic color-shifting shader effects that cycle through the spectrum
  - Sophisticated lighting with ambient, diffuse, and glow effects

- **Interactive Camera**: Mouse controls for adjusting viewing direction and exploring the simulation from different angles

- **Spherical Boundary**: Invisible spherical containment with soft bouncing physics keeps boids within the viewing area

- **Performance Optimized**: Handles 1000+ boids with smooth 60fps performance through efficient OpenGL rendering

## Technical Details

- **Engine**: libGDX (Java game development framework)
- **Graphics**: OpenGL with custom GLSL shaders
- **Physics**: Real-time flocking simulation with frame-rate independent movement
- **Rendering**: Instanced mesh rendering for optimal performance

## Controls

- **Mouse**: Click and drag to rotate the camera around the flock
- **Scroll**: Zoom in/out (if zoom controls are implemented)

## Flocking Parameters

The simulation uses carefully tuned parameters for realistic flocking:
- Maximum speed: 400 units/second
- Maximum steering force: 4.0
- Separation distance: 10 units
- Alignment neighbor distance: 30 units  
- Cohesion neighbor distance: 40 units
- Spherical boundary radius: 500 units

## Getting Started

**Disclaimer**: This is an ongoing hobby project and may contain bugs or incomplete features. Use as a reference for learning and experimentation.

1. Ensure you have Java and libGDX set up in your development environment
2. Include the required shader files (`vertex_shader.glsl` and `fragment_shader.glsl`)
3. Run the `BoidsApp` class to launch the simulation
4. Watch as the boids naturally form flocks, split apart, and rejoin in beautiful emergent patterns

## Development Status

This project is continuously evolving as a learning exercise in:
- 3D graphics programming with OpenGL
- Flocking algorithms and emergent behavior
- Shader development and visual effects
- Performance optimization techniques

Feel free to experiment with the parameters and code - that's what it's here for!

## Visual Effects

The simulation features a dynamic color-shifting shader that cycles through the color spectrum over time, creating an ethereal, ever-changing visual experience. Each boid glows with complementary colors and fresnel effects that make the flock appear alive and organic.

Perfect for demonstrating emergent behavior, swarm intelligence, or simply enjoying a relaxing algorithmic art piece!# Boids3d
