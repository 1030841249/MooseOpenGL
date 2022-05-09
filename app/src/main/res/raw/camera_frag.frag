#extension GL_OES_EGL_image_external : require
varying vec2 aCoord;
// 采样器，相机
uniform samplerExternalOES vTexture;
void main() {
    // 对坐标采样
    vec4 rgba = texture2D(vTexture,aCoord);
    gl_FragColor = rgba;
}