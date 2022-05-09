package com.zdh.mooseopengl.filter;

import android.content.Context;
import android.opengl.GLES20;

import com.zdh.mooseopengl.R;

/**
 * author: ZDH
 * Date: 2022/4/10
 * Description:
 */
public class CameraFilter extends AbstractFBOFilter{
    private float[] mtx;
    private int vMatrix;
    public CameraFilter(Context context) {
        super(context, R.raw.camera_vert, R.raw.camera_frag);
        vMatrix = GLES20.glGetUniformLocation(program, "vMatrix");
    }

    @Override
    public void beforDraw() {
        GLES20.glUniformMatrix4fv(vMatrix, 1, false, mtx, 0);
    }

    public void setTransformMatrix(float[] mtx) {
        this.mtx = mtx;
    }
}
