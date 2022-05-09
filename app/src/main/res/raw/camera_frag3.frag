#extension GL_OES_EGL_image_external : require
varying vec2 aCoords;
// 采样器，相机
uniform samplerExternalOES vTexture;
void main() {
    // 对坐标采样
    vec4 rgba = texture2D(vTexture,aCoords);
    gl_FragColor = rgba + vec4(0,0,0.3,0);
}