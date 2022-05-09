//package com.zdh.mooseopengl;
//
//import android.content.Context;
//import android.graphics.SurfaceTexture;
//import android.opengl.GLSurfaceView;
//import android.os.Handler;
//import android.os.HandlerThread;
//import android.util.Size;
//import android.view.SurfaceView;
//
//import androidx.annotation.NonNull;
//import androidx.camera.core.Camera;
//import androidx.camera.core.CameraSelector;
//import androidx.camera.core.ImageAnalysis;
//import androidx.camera.core.ImageProxy;
//import androidx.camera.core.Preview;
//import androidx.camera.lifecycle.ProcessCameraProvider;
//import androidx.camera.view.PreviewView;
//import androidx.core.content.ContextCompat;
//import androidx.core.os.ExecutorCompat;
//import androidx.lifecycle.LifecycleOwner;
//
//import com.google.common.util.concurrent.ListenableFuture;
//
//import java.util.concurrent.ExecutionException;
//
///**
// * author: ZDH
// * Date: 2022/4/6
// * Description:
// */
//public class CameraXHelper {
//    LifecycleOwner lifecycleOwner;
//    CameraRenderer cameraRenderer;
//    PreviewView previewView;
//    ProcessCameraProvider mCameraProvider;
//    ListenableFuture<ProcessCameraProvider> mListenableFuture;
//    Camera mCamera;
//    int width = 640;
//    int height = 480;
//
//    public CameraXHelper(Context context, LifecycleOwner lifecycleOwner, CameraRenderer cameraRenderer) {
//        this.lifecycleOwner = lifecycleOwner;
//        this.cameraRenderer = cameraRenderer;
//        mListenableFuture = ProcessCameraProvider.getInstance(context);
//        mListenableFuture.addListener((Runnable) () -> {
//            try {
//                mCameraProvider = mListenableFuture.get();
//                bindPreView();
//            } catch (ExecutionException | InterruptedException e) {
//                e.printStackTrace();
//            }
//        }, ContextCompat.getMainExecutor(context));
//
//    }
//    private void bindPreView() {
//        mCamera = mCameraProvider.bindToLifecycle(lifecycleOwner, getSelector(), getPreview());
//    }
//
//    private CameraSelector getSelector() {
//        CameraSelector cameraSelector = new CameraSelector.Builder()
//                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
//                .build();
//        return cameraSelector;
//    }
//
//    private Preview getPreview() {
//        Preview preview = new Preview.Builder()
//                .setTargetResolution(new Size(width, height))
//                .build();
////        preview.setSurfaceProvider(previewView.getSurfaceProvider());
//        preview.setSurfaceProvider(cameraRenderer);
//        return preview;
//    }
//}
