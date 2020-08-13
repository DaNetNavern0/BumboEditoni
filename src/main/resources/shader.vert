#version 330 compatibility

out vec3 v_texcoord;
in vec3 in_Position;
uniform mat4 proj_view;

void main()
{
    //Yes, these are absurdly outdates features from early 00s.
    //Replacing this with modern OpenGL at some point would be good, but for now I'd rather focus on making the app usable
    v_texcoord = vec3(gl_MultiTexCoord0);
    gl_Position = proj_view * vec4(in_Position, 1);
}