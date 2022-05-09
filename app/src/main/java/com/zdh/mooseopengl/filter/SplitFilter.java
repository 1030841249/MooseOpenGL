package com.zdh.mooseopengl.filter;

import android.content.Context;

import com.zdh.mooseopengl.R;

/**
 * author: ZDH
 * Date: 2022/4/13
 * Description:
 */
public class SplitFilter extends AbstractFBOFilter{

    public SplitFilter(Context context) {
        super(context, R.raw.base_vert, R.raw.split2_chunck_frag);
    }

    @Override
    public int draw(int texture) {
        super.draw(texture);
        return frameBuffer[0];
    }

    @Override
    public void beforDraw() {

    }
}
