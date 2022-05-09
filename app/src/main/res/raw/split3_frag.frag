precision mediump float;
// 采样坐标
varying vec2 aCoord;
// 采样器
uniform sampler2D vTexture;
void main() {
    float y = aCoord.y;
    float a = 1.0/3.0;
    // 采取中间 0.33-0.66 之间的 0.33 的屏幕数据
    if(y < a) {
        y += a;
    } else if(y > 2.0*a)  {
        y -= a;
    }
    gl_FragColor = texture2D(vTexture,vec2(aCoord.x,y));
}
