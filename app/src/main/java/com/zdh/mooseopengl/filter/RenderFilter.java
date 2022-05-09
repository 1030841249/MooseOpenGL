package com.zdh.mooseopengl.filter;

import android.content.Context;

import com.zdh.mooseopengl.R;

/**
 * author: ZDH
 * Date: 2022/4/11
 * Description: 渲染fbo数据到纹理
 */
public class RenderFilter extends AbstractFilter {
    public RenderFilter(Context context) {
        super(context, R.raw.base_vert,R.raw.base_frag);
    }

    @Override
    public void beforDraw() {

    }
}
