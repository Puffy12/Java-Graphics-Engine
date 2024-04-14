


uniform mat4 uProjMatrix;
uniform mat4 uViewMatrix;

void main()
{
    gl_Position = uProjMatrix * uViewMatrix * gl_Vertex;
}
