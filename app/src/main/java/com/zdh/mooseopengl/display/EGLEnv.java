package com.zdh.mooseopengl.display;

import android.content.Context;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.view.Surface;

import com.zdh.mooseopengl.filter.EGLFilter;

/**
 * author: ZDH
 * Date: 2022/4/10
 * Description:
 */
public class EGLEnv {
    // 要想使用OpenGL 操作，只能在 EGL 环境下
    private EGLDisplay mEglDisplay;
    private EGLConfig mEglConfig;
    private EGLContext mEglContext;
    private EGLSurface mEglSurface;

    private EGLFilter eglFilter;

    public EGLEnv(Context context, EGLContext glContext, Surface surface,int width,int height) {
        // 获取默认显示窗口

        mEglDisplay= EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);

        if (mEglDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("eglGetDisplay failed");
        }

//        100%  固定代码
        // 初始化顯示窗口
        int[] version = new int[2];
        if(!EGL14.eglInitialize(mEglDisplay, version,0,version,1)) {
            throw new RuntimeException("eglInitialize failed");
        }


        // 配置 属性选项
        int[] configAttribs = {
                EGL14.EGL_RED_SIZE, 8, //颜色缓冲区中红色位数
                EGL14.EGL_GREEN_SIZE, 8,//颜色缓冲区中绿色位数
                EGL14.EGL_BLUE_SIZE, 8, //
                EGL14.EGL_ALPHA_SIZE, 8,//
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT, //opengl es 2.0
                EGL14.EGL_NONE
        };
        int[] numConfigs = new int[1];
        EGLConfig[] configs = new EGLConfig[1];
        //EGL 根据属性选择一个配置
        if (!EGL14.eglChooseConfig(mEglDisplay, configAttribs, 0, configs, 0, configs.length,
                numConfigs, 0)) {
            throw new RuntimeException("EGL error " + EGL14.eglGetError());
        }
        mEglConfig = configs[0];
        int[] context_attrib_list = {
                EGL14.EGL_CONTEXT_CLIENT_VERSION,2,
                EGL14.EGL_NONE
        };
//        创建好了 之后   你是可以读取到数据
        mEglContext=EGL14.eglCreateContext(mEglDisplay, mEglConfig, glContext, context_attrib_list,0);


        if (mEglContext == EGL14.EGL_NO_CONTEXT){
            throw new RuntimeException("EGL error " + EGL14.eglGetError());
        }
        /**
         * 创建EGLSurface
         */
        int[] surface_attrib_list = {
                EGL14.EGL_NONE
        };
//        还没完
//        MediaProjection mediaProjection;
//        mediaProjection.createVirtualDisplay()

//        录屏推流
        mEglSurface = EGL14.eglCreateWindowSurface(mEglDisplay, mEglConfig, surface, surface_attrib_list, 0);
        // mEglSurface == null
        if (mEglSurface == null){
            throw new RuntimeException("EGL error " + EGL14.eglGetError());
        }
        /**
         * 绑定当前线程的显示器display mEglDisplay  虚拟 物理设备
         */
        if (!EGL14.eglMakeCurrent(mEglDisplay,mEglSurface,mEglSurface,mEglContext)){
            throw new RuntimeException("EGL error " + EGL14.eglGetError());
        }

        // 当前egl线程的绘制者
        eglFilter = new EGLFilter(context);
        eglFilter.setSize(width,height);
    }

    public void draw(int textureId,long timeStamp) {
        eglFilter.draw(textureId);
        // 给帧缓冲区设置时间戳
        EGLExt.eglPresentationTimeANDROID(mEglDisplay, mEglSurface, timeStamp);
        EGL14.eglSwapBuffers(mEglDisplay, mEglSurface);
    }

    public void release() {
        EGL14.eglDestroyContext(mEglDisplay, mEglContext);
        EGL14.eglMakeCurrent(mEglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, mEglContext);
        EGL14.eglDestroySurface(mEglDisplay, mEglSurface);
        EGL14.eglReleaseThread();
        EGL14.eglTerminate(mEglDisplay);
        eglFilter.release();
    }
}
