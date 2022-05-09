precision mediump float; // 数据精度
// 采样坐标
varying vec2 aCoord;
// 采样器，对 aCoord 坐标进行采样
uniform sampler2D vTexture;

void main() {
    vec4 rgba = texture2D(vTexture,aCoord);
    gl_FragColor = rgba;
}