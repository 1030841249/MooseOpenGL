package com.zdh.mooseopengl.filter;

import android.content.Context;
import android.opengl.GLES20;

import com.zdh.mooseopengl.R;

/**
 * author: ZDH
 * Date: 2022/4/12
 * Description:
 */
public class SoulFilter extends AbstractFBOFilter {

    float scale,mix;
    int scalePercent;
    int mixPercent;

    public SoulFilter(Context context) {
        super(context, R.raw.base_vert,R.raw.soul_frag);
        scalePercent = GLES20.glGetUniformLocation(program, "scalePercent");
        mixPercent = GLES20.glGetUniformLocation(program, "mixPercent");
    }

    @Override
    public void beforDraw() {
        GLES20.glUniform1f(scalePercent, scale + 1.0f);
        GLES20.glUniform1f(mixPercent, mix);
        scale+=0.08f;
        mix+=0.08f;
        if (scale > 1) {
            scale = 0;
        }
        if (mix > 1) {
            mix = 0;
        }
    }
}
