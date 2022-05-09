attribute vec4 vPosition;
// 采样坐标系
attribute vec2 vCoord;
// 传递给片元着色器的采样坐标
varying vec2 aCoord;
void main() {
    gl_Position = vPosition;
    aCoord = vCoord;
}
