precision mediump float;
// 采样坐标
varying vec2 aCoord;
// 采样器
uniform sampler2D vTexture;
uniform highp float scalePercent;
uniform highp float mixPercent;
void main() {
    highp vec2 center = vec2(0.5f,0.5f);
    highp vec2 tmpCoord = aCoord;
    tmpCoord = aCoord - center;
    tmpCoord = tmpCoord / scalePercent;
    tmpCoord += center;
    highp vec4 origin = texture2D(vTexture,aCoord);
    highp vec4 soul = texture2D(vTexture,tmpCoord);
    gl_FragColor = mix(origin,soul,mixPercent);
}
