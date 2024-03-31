layout(location = 0) in vec3 aPos;
layout(location = 1) in vec4 aColor;
layout(location = 2) in vec2 aTC;

uniform mat4 uProjMatrix;
uniform mat4 uViewMatrix;

out vec4 bColor;
out vec2 bTC;

void main()
{
    bColor = aColor;
    bTC = aTC;

    gl_Position = uProjMatrix * uViewMatrix * vec4(aPos, 1.0);
}

