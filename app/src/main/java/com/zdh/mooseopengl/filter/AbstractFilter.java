package com.zdh.mooseopengl.filter;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;

import android.content.Context;
import android.opengl.GLES20;

import com.zdh.mooseopengl.display.GLUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * author: ZDH
 * Date: 2022/4/9
 * Description:
 */
public abstract class AbstractFilter {
    // 正方形
    float[] vertexCoords = {
            -1,1,
            -1,-1,
            1,1,
            1,-1
    };
    // 纹理坐标
    float[] textureCoords = {
            0,1,
            0,0,
            1,1,
            1,0
    };

    FloatBuffer vertexBuffer;
    FloatBuffer textureBuffer;
//    float[] matrix;


    int vertexShader;
    int fragShader;
    int program;

    /* 变量 */
    int vPosition,vCoords,vMatrix,vTexture;

    int mWidth,mHeight;

    public AbstractFilter(Context context,int vertexId,int fragId) {
        // 申请 gpu 内存
        ByteBuffer bb = ByteBuffer.allocateDirect(vertexCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertexCoords);
        vertexBuffer.position(0);

        textureBuffer = ByteBuffer.allocateDirect(vertexCoords.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        textureBuffer.put(textureCoords);
        textureBuffer.position(0);
        // 创建着色器
        vertexShader = GLUtil.loadShaderAssets(context, GLES20.GL_VERTEX_SHADER, vertexId);
        fragShader = GLUtil.loadShaderAssets(context, GLES20.GL_FRAGMENT_SHADER, fragId);
        // 链接
        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program,vertexShader);
        GLES20.glAttachShader(program,fragShader);
        GLES20.glLinkProgram(program);
        // 使用程序
        GLES20.glUseProgram(program);

        // 获取变量索引
        vPosition = GLES20.glGetAttribLocation(program, "vPosition");
        vCoords = GLES20.glGetAttribLocation(program, "vCoord");
//        vMatrix = GLES20.glGetUniformLocation(program, "vMatrix");
        vTexture = GLES20.glGetUniformLocation(program, "vTexture");

    }

//    public void setMatrix(float[] mat) {
//        matrix = mat;
//    }

    public int draw(int texture) {

        GLES20.glViewport(0, 0, mWidth, mHeight);
        GLES20.glUseProgram(program);
        // 给变量赋值
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glEnableVertexAttribArray(vCoords);
//
//        // 每次使用完重置使用位置
        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(vPosition,2,
                GLES20.GL_FLOAT,false,0,vertexBuffer);
//
        textureBuffer.position(0);
        GLES20.glVertexAttribPointer(vCoords,2,
                GLES20.GL_FLOAT,false,0,textureBuffer);
//
//        // 纹理赋值
//        // 激活纹理，总共有32纹理可以使用，我们这里只创建了一个，所以使用 0
        GLES20.glActiveTexture(GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture);
        GLES20.glUniform1i(vTexture,0);
////        GLES20.glUniformMatrix4fv(vMatrix, 1, false, matrix, 0);
        beforDraw();
//        // 绘制，按照给定数组的值
        GLES20.glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

        return texture;
    }

    public void setSize(int width, int height) {
        mWidth = width;
        mHeight = height;

    }

    public abstract void beforDraw();

    public void release() {
        GLES20.glDeleteProgram(program);
    }
}
