package com.zdh.mooseopengl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ZDH
 * Date: 2022/4/7
 * Description:
 */
public class MyRenderer implements GLSurfaceView.Renderer {
    final String vertexCode =
            "attribute vec4 vPosition;" +
            "void main() {" +
            "gl_Position = vPosition;" +
            "}";
    final String fragmentCode =
            "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main() {" +
            "  gl_FragColor = vColor;" +
            "}";
    float[] vertexCoords = new float[] {
            -1,1,0,
            -1,-1,0,
            1,1,0,
            1,-1,0,

    };
    //设置颜色，依次为红绿蓝和透明通道
    float color[] = { 1.0f, 0f, 0f, 1.0f };
    FloatBuffer mVertexBuffer;

    int mProgram;

    public MyRenderer() {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertexCoords.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        mVertexBuffer = byteBuffer.asFloatBuffer();
        mVertexBuffer.put(vertexCoords);
        mVertexBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        // 清除旧数据
        gl10.glClearColor(0,0,0,1);


        // 创建定点程序
        int vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        // 链接代码
        GLES20.glShaderSource(vertexShader,vertexCode);
        // 编译
        GLES20.glCompileShader(vertexShader);
        // 创建定点程序
        int fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        // 链接代码
        GLES20.glShaderSource(fragmentShader,fragmentCode);
        // 编译
        GLES20.glCompileShader(fragmentShader);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        gl10.glViewport(0,0,width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glUseProgram(mProgram);
        // 获取变量引用，并赋值
        int vPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        int vColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        // 允许 cpu 往 gpu 写入数据
        GLES20.glEnableVertexAttribArray(vPositionHandle);
        GLES20.glVertexAttribPointer(vPositionHandle, vertexCoords.length / 3, GLES20.GL_FLOAT, false, 3*4, mVertexBuffer);
        GLES20.glUniform4fv(vColorHandle,0,color,0);
        // 绘制
        gl10.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCoords.length / 3);
    }
}
