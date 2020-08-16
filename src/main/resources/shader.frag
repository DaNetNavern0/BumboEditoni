#version 330 core

layout (location = 0) out vec4 output;

uniform sampler2DArray in_texture;
in vec3 vertex_uv_br;

void main()
{
    output = texture(in_texture, vertex_uv_br);
}