#version 430 core

layout(location = 0) in vec3 aPos;
layout(location = 1) in vec4 aCol;

out vec4 fColor;

uniform mat4 uProjMatrix;
uniform mat4 uViewMatrix;

void main() {
    fColor = aCol;
    gl_Position = uProjMatrix * uViewMatrix * vec4(aPos, 1.0);
} 