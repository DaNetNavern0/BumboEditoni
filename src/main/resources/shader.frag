#version 440 compatibility

uniform sampler2DArray a_texture;

in vec3 v_texcoord;

void main()
{
    gl_FragColor = texture(a_texture, v_texcoord);
}