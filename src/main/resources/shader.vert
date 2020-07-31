#version 440 compatibility

out vec3 v_texcoord;

void main()
{
    v_texcoord = vec3(gl_MultiTexCoord0);
    gl_Position = ftransform();
}