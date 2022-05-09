package com.zdh.mooseopengl;

import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_MAG_FILTER;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_MIN_FILTER;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_WRAP_S;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_WRAP_T;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Size;
import android.view.Surface;

import androidx.camera.core.Preview;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.lifecycle.LifecycleOwner;

import com.zdh.mooseopengl.display.MediaRecord;
import com.zdh.mooseopengl.filter.CameraFilter;
import com.zdh.mooseopengl.filter.RenderFilter;
import com.zdh.mooseopengl.filter.ScreenFilter;
import com.zdh.mooseopengl.filter.SoulFilter;
import com.zdh.mooseopengl.filter.SplitFilter;

import java.io.IOException;
import java.util.concurrent.Executor;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ZDH
 * Date: 2022/4/8
 * Description:
 */
class CameraRenderer implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener
//    , Preview.SurfaceProvider
        , Preview.OnPreviewOutputUpdateListener
{

    private Executor executor;

    float[] mtx = new float[16];

    int[] textureIds = new int[1];
    SurfaceTexture mSurfaceTexture;
    Surface mSurface;
    Size size;

    CameraView cameraView;
    ScreenFilter screenFilter;
    CameraFilter cameraFilter;
    SplitFilter split2Filter;
//    CameraXHelper cameraXHelper;
    RenderFilter renderFilter;
    MediaRecord mediaRecord;
    SoulFilter soulFilter;
    CameraHelper cameraXHelper;
    public CameraRenderer(CameraView cameraView) {
        this.cameraView = cameraView;
        executor = CameraXExecutors.ioExecutor();
//        cameraXHelper = new CameraXHelper(cameraView.getContext(), (LifecycleOwner) cameraView.getContext(), this);
        cameraXHelper = new CameraHelper((LifecycleOwner) cameraView.getContext(), this);
    }

    private void initTexture() {
        GLES20.glGenTextures(1, textureIds, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureIds[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MIN_FILTER,GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,0);
        mSurfaceTexture = new SurfaceTexture(textureIds[0]);
        mSurfaceTexture.setOnFrameAvailableListener(this);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//        initTexture();
        Context context = cameraView.getContext();
        screenFilter = new ScreenFilter(context);
        cameraFilter = new CameraFilter(context);
        renderFilter = new RenderFilter(context);
        soulFilter = new SoulFilter(context);
        split2Filter = new SplitFilter(context);
        try {
            mediaRecord = new MediaRecord(context, EGL14.eglGetCurrentContext(),480,640);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        1
//        让 SurfaceTexture   与 Gpu  共享一个数据源  0-31
        mSurfaceTexture.attachToGLContext(textureIds[0]);
        mSurfaceTexture.setOnFrameAvailableListener(this);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        cameraFilter.setSize(width,height);
        renderFilter.setSize(width,height);
        soulFilter.setSize(width,height);
        split2Filter.setSize(width,height);
        gl.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // 更新摄像头数据
        mSurfaceTexture.updateTexImage();
        mSurfaceTexture.getTransformMatrix(mtx);
        cameraFilter.setTransformMatrix(mtx);
//        screenFilter.setMatrix(mtx);
//        screenFilter.draw(textureIds[0]);
//        GLES20.glViewport(0,0,width,height);
        int id = cameraFilter.draw(textureIds[0]);
//        id = soulFilter.draw(id);
        id = split2Filter.draw(id);
        // 将 frameBuffer 数据渲染
        id = renderFilter.draw(id);
        mediaRecord.fireRecord(id,mSurfaceTexture.getTimestamp());
    }

//    @Override
//    public void onSurfaceRequested(@NonNull SurfaceRequest request) {
//        size = request.getResolution();
//        mSurfaceTexture.setDefaultBufferSize(size.getWidth(),size.getHeight());
//        mSurface = new Surface(mSurfaceTexture);
//        request.provideSurface(mSurface, executor, result -> {
//            mSurfaceTexture.release();
//            mSurface.release();
//        });
//    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        cameraView.requestRender();
    }

    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }

    @Override
    public void onUpdated(Preview.PreviewOutput output) {
        mSurfaceTexture = output.getSurfaceTexture();
    }
}
