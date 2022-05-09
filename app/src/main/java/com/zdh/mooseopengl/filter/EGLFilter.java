package com.zdh.mooseopengl.filter;

import android.content.Context;

import com.zdh.mooseopengl.R;

/**
 * author: ZDH
 * Date: 2022/4/11
 * Description:
 */
public class EGLFilter extends AbstractFilter{
    public EGLFilter(Context context) {
        super(context, R.raw.base_vert,  R.raw.base_frag);
    }

    @Override
    public void beforDraw() {

    }
}
