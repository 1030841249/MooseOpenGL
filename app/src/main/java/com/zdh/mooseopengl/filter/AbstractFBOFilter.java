package com.zdh.mooseopengl.filter;

import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_MAG_FILTER;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_MIN_FILTER;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

/**
 * author: ZDH
 * Date: 2022/4/10
 * Description:
 */
public abstract class AbstractFBOFilter extends AbstractFilter {

    // fbo
    int[] frameBuffer;
    // 纹理
    int[] frameTexture;

    public AbstractFBOFilter(Context context,int vertexId,int fragId) {
        super(context,vertexId,fragId);
    }


    private void releaseFrame() {
        if (frameBuffer != null) {
            GLES20.glDeleteFramebuffers(1,frameBuffer,0);
            frameBuffer = null;
        }
        if (frameTexture != null) {
            GLES20.glDeleteTextures(1, frameTexture, 0);
            frameTexture = null;
        }
    }

    @Override
    public void setSize(int width,int height) {
        super.setSize(width,height);
        releaseFrame();
        frameBuffer = new int[1];
        frameTexture = new int[1];
        // 生成fbo，存放数据
        GLES20.glGenFramebuffers(1, frameBuffer, 0);
        // 生成纹理，一个图层
        GLES20.glGenTextures(1, frameTexture, 0);
        // 将一个命名的纹理绑定到一个纹理目标（GL_TEXTURE_2D）上
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, frameTexture[0]);
        // 设置纹理环绕参数,设置 s/t 方向的缩放，对应x，y
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
        // 纹理过滤,放大或者缩小的时候 取最近值
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        // 使用完解绑
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);

        // 纹理和fbo绑定
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, frameTexture[0]);
        /**
         * 指定一个二维的纹理图片
         * level
         *     指定细节级别，0级表示基本图像，n级则表示Mipmap缩小n级之后的图像（缩小2^n）
         * internalformat
         *     指定纹理内部格式，必须是下列符号常量之一：GL_ALPHA，GL_LUMINANCE，GL_LUMINANCE_ALPHA，GL_RGB，GL_RGBA。
         * width height
         *     指定纹理图像的宽高，所有实现都支持宽高至少为64 纹素的2D纹理图像和宽高至少为16 纹素的立方体贴图纹理图像 。
         * border
         *     指定边框的宽度。必须为0。
         * format
         *     指定纹理数据的格式。必须匹配internalformat。下面的符号值被接受：GL_ALPHA，GL_RGB，GL_RGBA，GL_LUMINANCE，和GL_LUMINANCE_ALPHA。
         * type
         *     指定纹理数据的数据类型。下面的符号值被接受：GL_UNSIGNED_BYTE，GL_UNSIGNED_SHORT_5_6_5，GL_UNSIGNED_SHORT_4_4_4_4，和GL_UNSIGNED_SHORT_5_5_5_1。
         * data
         *     指定一个指向内存中图像数据的指针。
         */

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height,
                0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        // 绑定，使用 gpu 数据区域
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer[0]);  //綁定FBO
        // fbo和texture 正式绑定
        // 将纹理图像附加到帧缓冲对象
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D,
                frameTexture[0],
                0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,0);
    }

    @Override
    public int draw(int texture) {
        // 将数据渲染到 fbo 上，这样就不会推送到物理设备上进行显示
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,frameBuffer[0]);
         super.draw(texture);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,0);
        return frameBuffer[0];
    }
}
