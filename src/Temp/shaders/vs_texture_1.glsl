#version 430 core

layout(location = 0) in vec3 aPos;
layout(location = 1) in vec4 aColor;
layout(location = 2) in vec2 aTexCoords; 

out vec4 bColor;
out vec2 bTexCoords; 

uniform mat4 uProjMatrix;
uniform mat4 uViewMatrix;

void main() {
    bColor = aColor;
    bTexCoords = aTexCoords; 

    gl_Position = uProjMatrix * uViewMatrix * vec4(aPos, 1.0);
}


