#version 100
precision mediump float;

varying vec4 v_frag_color;

void main() {
    gl_FragColor = v_frag_color;
}
