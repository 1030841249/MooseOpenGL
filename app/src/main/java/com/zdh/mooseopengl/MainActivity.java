package com.zdh.mooseopengl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioGroup;

import com.zdh.mooseopengl.databinding.ActivityMainBinding;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    ActivityMainBinding mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);

//        gl1();
        gl2();
    }

    private void gl1() {

        mBinding.glSv.setEGLContextClientVersion(2);
        MyRenderer renderer = new MyRenderer();
        mBinding.glSv.setRenderer(renderer);
        // 被动渲染
        mBinding.glSv.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mBinding.glSv.requestRender();
    }

    private void gl2() {
//        CameraXHelper cameraXHelper = new CameraXHelper(this,this,mBinding.pvv);
        mBinding.rgSpeed.setOnCheckedChangeListener(this);
        mBinding.btnRecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mBinding.cameraView.startRecord();
                        break;
                    case MotionEvent.ACTION_UP:
                        mBinding.cameraView.stop();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mBinding.glSv.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        mBinding.glSv.onPause();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.btn_extra_slow:
                mBinding.cameraView.setSpeed(CameraView.Speed.MODE_EXTRA_SLOW);
                break;
            case R.id.btn_slow:
                mBinding.cameraView.setSpeed(CameraView.Speed.MODE_SLOW);
                break;
            case R.id.btn_normal:
                mBinding.cameraView.setSpeed(CameraView.Speed.MODE_NORMAL);
                break;
            case R.id.btn_fast:
                mBinding.cameraView.setSpeed(CameraView.Speed.MODE_FAST);
                break;
            case R.id.btn_extra_fast:
                mBinding.cameraView.setSpeed(CameraView.Speed.MODE_EXTRA_FAST);
                break;
        }
    }
}