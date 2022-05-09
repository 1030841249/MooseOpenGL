precision mediump float;
// 采样坐标
varying vec2 aCoord;
// 采样器
uniform sampler2D vTexture;
void main() {
    float y = aCoord.y;
    // 采取中间 0.25 - 0.75 之间的 0.5 的屏幕数据
    if(y < 0.5) {
        y += 0.25;
    } else  {
        y -= 0.25;
    }
    gl_FragColor = texture2D(vTexture,vec2(aCoord.x,y));
}
