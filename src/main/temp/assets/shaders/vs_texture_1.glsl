#version 430 core

layout(location = 0) in vec3 aPos;
layout(location = 1) in vec4 aCol;
layout(location = 2) in vec2 aTc; 

out vec4 fColor;
out vec2 fTexCoords; 

uniform mat4 uProjMatrix;
uniform mat4 uViewMatrix;

void main() {
    fColor = aCol;
    fTexCoords = aTc; 

    gl_Position = uProjMatrix * uViewMatrix * vec4(aPos, 1.0);
}

