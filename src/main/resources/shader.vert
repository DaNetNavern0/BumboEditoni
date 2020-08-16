#version 330 core

out vec3 vertex_uv_br;

layout(location = 0) in vec3 vertex_pos;
layout(location = 1) in vec3 vertex_uv;

uniform mat4 proj_n_view;

void main()
{
    vertex_uv_br = vertex_uv;
    gl_Position = proj_n_view * vec4(vertex_pos, 1);
}