// 顶点坐标，在手机屏幕上确定一块显示的区域形状
attribute vec4 vPosition;
// 纹理坐标，采集器采样到的图片的坐标，camera
attribute vec4 vCoord;
// 正确显示 camera 需要使用其 矩阵
uniform mat4 vMatrix;
// 转换后的片元着色器的坐标，像素点
varying vec2 aCoord;
void main() {
    gl_Position = vPosition;
    aCoord = (vMatrix * vCoord).xy;
}