#version 430 core

in vec4 fColor;

out vec4 color;

uniform sampler2D uTexture;

void main() {

    vec4 texColor = texture(uTexture, fColor.zw); // Assuming fColor.zw holds texture coordinates
    
    color = vec4(fColor.xyz * texColor.xyz, 1.0); // Adjust alpha as needed
}