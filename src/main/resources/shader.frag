#version 330 compatibility

uniform sampler2DArray in_texture;

in vec3 v_texcoord;

void main()
{
    gl_FragColor = texture(in_texture, v_texcoord);
}