package com.zdh.mooseopengl.display;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.opengl.EGLContext;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * author: ZDH
 * Date: 2022/4/11
 * Description:
 */
public class MediaRecord {

    private EGLEnv eglEnv;
    private HandlerThread mEGLThread;
    private Handler handler;
    MediaCodec mediaCodec;
    MediaMuxer mediaMuxer;
    MediaFormat mediaFormat;
    Surface surface;
    int track;
    private boolean isStart = false;
    String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/output.mp4";

    Context context;
    EGLContext eglContext;
    int width;
    int height;
    float mSpeed;
    private long mLastTimeStamp;
    public MediaRecord(Context context, EGLContext eglContext,int width, int height) throws IOException {
        this.context = context;
        this.eglContext = eglContext;
        this.width = width;
        this.height = height;

        mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
        mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, width * height);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 25);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 10);
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        surface = mediaCodec.createInputSurface();
        mediaCodec.start();

        mediaMuxer = new MediaMuxer(path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

        mEGLThread = new HandlerThread("codec-egl");
        mEGLThread.start();
        handler = new Handler(mEGLThread.getLooper());


    }

    public void start(float speed) {
        mSpeed = speed;
        handler.post(() -> {
            // 将当前线程和EGL线程绑定，之后可以再该线程进行opengl操作
            eglEnv = new EGLEnv(context, eglContext, surface, width, height);
            isStart = true;
        });
    }

    public void fireRecord(int textureId,long timeStamp) {
        if (!isStart) {
            return;
        }
        handler.post(() -> {
            eglEnv.draw(textureId,timeStamp);
            codec(false);
        });
    }

    private void codec(boolean endOfStream) {
        if (endOfStream) {
            mediaCodec.signalEndOfInputStream();
        }
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        boolean state = true;
        while(state) {
            int index = mediaCodec.dequeueOutputBuffer(bufferInfo, 10_000);
            switch (index) {
                case MediaCodec.INFO_TRY_AGAIN_LATER:
                    if (!endOfStream) {
                        state = false;
                    }
                    break;
                case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                    track = mediaMuxer.addTrack(mediaCodec.getOutputFormat());
                    mediaMuxer.start();
                    break;
                case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:

                    break;
                default:
                    //调整时间戳
                    bufferInfo.presentationTimeUs = (long) (bufferInfo.presentationTimeUs / mSpeed);
                    //有时候会出现异常 ： timestampUs xxx < lastTimestampUs yyy for Video track
                    if (bufferInfo.presentationTimeUs <= mLastTimeStamp) {
                        bufferInfo.presentationTimeUs = (long) (mLastTimeStamp + 1_000_000 / 25 / mSpeed);
                    }
                    mLastTimeStamp = bufferInfo.presentationTimeUs;

                    ByteBuffer byteBuffer = mediaCodec.getOutputBuffer(index);
                    // 过滤配置信息
                    if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                        bufferInfo.size = 0;
                    }
                    if (bufferInfo.size != 0) {
                        byteBuffer.position(bufferInfo.offset);
                        byteBuffer.limit(bufferInfo.offset + bufferInfo.size);
                        mediaMuxer.writeSampleData(track,byteBuffer,bufferInfo);
                    }
                    mediaCodec.releaseOutputBuffer(index,false);
                    // 如果给了结束信号 signalEndOfInputStream
                    if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        state = false;
                    }
                    break;
            }
        }
    }

    public void release() {
        handler.post(()->{
           codec(true);
           mediaCodec.release();
           mediaMuxer.release();
           eglEnv.release();
           mediaCodec = null;
           mediaMuxer = null;
            handler.getLooper().quitSafely();
        });
    }
}
