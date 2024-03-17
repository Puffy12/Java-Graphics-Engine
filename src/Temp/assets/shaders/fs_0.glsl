

uniform vec3 renderColorLocation;

void main(void )
{
    gl_FragColor = vec4(renderColorLocation, 1.0);
}
