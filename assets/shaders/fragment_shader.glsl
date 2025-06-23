#version 460 core

in vec3 v_worldPos;
in vec3 v_normal;
in vec4 v_color;

out vec4 fragColor;

uniform vec3 u_cameraPos;
uniform vec3 u_lightPos;
uniform float u_time;

// Constants for lighting and materials - defined directly in shader
const vec3 LIGHT_COLOR = vec3(1.0, 1.0, 1.0);
const float LIGHT_INTENSITY = 0.50;
const float AMBIENT_STRENGTH = 0.2;
const vec3 AMBIENT_COLOR = vec3(1.0, 1.0, 1.0);

const float FRESNEL_POWER = 0.60;
const float SPEC_POWER = 0.3;
const float GLOSSINESS = 0.1;
const float REFLECTIVITY = 0.5;
const float GLOW_POWER = 0.1;


// Function to convert HSV to RGB for smooth ROYGBIV transitions
vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0/3.0, 1.0/3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main() {
    vec3 N = normalize(v_normal);
    vec3 V = normalize(u_cameraPos - v_worldPos);

    // === Use time-based colors ===
    float hue = mod(u_time, 1.0); // cycle hue
    float saturation = 1.0;
    float value = 1.0;
    vec3 baseColor = hsv2rgb(vec3(hue, saturation, value));

    // Generate complementary colors for highlights and fresnel
    vec3 highlightColor = hsv2rgb(vec3(fract(hue + 0.5), saturation, value));
    vec3 fresnelColor = hsv2rgb(vec3(fract(hue + 0.33), saturation * 0.8, value * 1.2));
    vec3 glowColor = hsv2rgb(vec3(fract(hue + 0.1), saturation * 1.0, value * 1.3));

    // === Primary light calculations ===
    vec3 L = normalize(u_lightPos - v_worldPos);
    vec3 H = normalize(L + V);
    float diff = max(dot(N, L), 0.0);
    float spec = pow(max(dot(N, H), 0.0), SPEC_POWER);

    vec3 diffuse = diff * LIGHT_COLOR * LIGHT_INTENSITY;
    vec3 specular = spec * highlightColor * GLOSSINESS * LIGHT_INTENSITY;
    specular *= step(0.05, length(specular)); // Avoid small values

    vec3 ambient = AMBIENT_COLOR * AMBIENT_STRENGTH * baseColor;

    // === Fresnel (view angle-based rim light) ===
    float fresnel = pow(1.0 - max(dot(N, V), 0.0), FRESNEL_POWER);
    vec3 fresnelEffect = fresnel * fresnelColor;
    vec3 rimLight = fresnelColor * fresnel * REFLECTIVITY;

    // === Omni glow effect ===
    float glowIntensity = pow(1.0 - abs(dot(N, V)), GLOW_POWER);
    vec3 glowEffect = glowColor * glowIntensity;

    // Add all components to final color
    //vec3 finalColor = baseColor + fresnelEffect + diffuse + specular + glowEffect;
    vec3 finalColor = baseColor + diffuse + glowEffect;

    // Apply a slight boost to brightness in areas with glow
    finalColor += glowColor * glowIntensity * 0.3;

    // Ensure we don't exceed maximum intensity
    finalColor = min(finalColor, vec3(1.0));

    // Slight transparency for glow effect
    fragColor = vec4(finalColor, 0.9);

}