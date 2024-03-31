uniform sampler2D textureSampler;

in vec2 fTexCoords;

out vec4 fragColor;

void main()
{
    fragColor = texture(textureSampler, fTexCoords);
}
