precision mediump float;
// 采样坐标
varying vec2 aCoord;
// 采样器
uniform sampler2D vTexture;
void main() {
    vec4 origin = texture2D(vTexture,aCoord);


}