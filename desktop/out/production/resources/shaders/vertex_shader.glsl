#version 460 core

in vec3 a_position;
in vec3 a_normal;
in vec4 a_color;

uniform mat4 u_projMatrix;
uniform mat4 u_viewMatrix;
uniform mat4 u_modelMatrix;

out vec3 v_worldPos;
out vec3 v_normal;
out vec4 v_color;

void main() {
    // Transform the vertex position to world space
    vec4 worldPos = u_modelMatrix * vec4(a_position, 1.0);
    v_worldPos = worldPos.xyz;

    // Transform the normal to world space (excluding translation)
    mat3 normalMatrix = mat3(u_modelMatrix);
    v_normal = normalize(normalMatrix * a_normal);

    // Pass the color to the fragment shader
    v_color = a_color;

    // Set the final position
    gl_Position = u_projMatrix * u_viewMatrix * worldPos;
}