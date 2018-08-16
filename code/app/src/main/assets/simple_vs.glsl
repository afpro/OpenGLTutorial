#version 100
precision mediump float;

uniform mat4 u_mvp;

attribute vec3 a_pos;
attribute vec4 a_color;

varying vec4 v_frag_color;

void main() {
    gl_Position = u_mvp * vec4(a_pos, 1);
    v_frag_color = a_color;
}
