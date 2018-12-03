package com.hashimapp.myopenglwallpaper.View;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import com.hashimapp.myopenglwallpaper.Model.GLRenderer;

public abstract class GLWallpaperService extends WallpaperService {

    protected SensorManager sensorManager;
    protected Sensor accelerometer;
    boolean rendererSet;

    public class GLEngine extends Engine {
        class WallpaperGLSurfaceView extends GLSurfaceView {
            private static final String TAG = "WallpaperGLSurfaceView";

            WallpaperGLSurfaceView(Context context) {
                super(context);
            }

            @Override
            public SurfaceHolder getHolder() {
                return getSurfaceHolder();
            }

            public void onDestroy() {
                super.onDetachedFromWindow();
            }
        }
        public GLRenderer renderer;

        WallpaperGLSurfaceView glSurfaceView;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            glSurfaceView = new WallpaperGLSurfaceView(GLWallpaperService.this);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (rendererSet) {
                if (visible)//resume
                {
//                    sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                    glSurfaceView.onResume();
                } else //pause
                {
//                    sensorManager.unregisterListener(this);
                    glSurfaceView.onPause();
                }
            }
        }


        @Override
        public void onDestroy() {
            super.onDestroy();
            glSurfaceView.onDestroy();
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
        }

        public void setRenderer(GLSurfaceView.Renderer newRenderer) {
            this.renderer = (GLRenderer)newRenderer;
            glSurfaceView.setRenderer(newRenderer);
            rendererSet = true;
        }


        protected void setPreserveEGLContextOnPause(boolean preserve) {
            glSurfaceView.setPreserveEGLContextOnPause(preserve);
        }

        protected void setEGLContextClientVersion(int version) {
            glSurfaceView.setEGLContextClientVersion(version);
        }


    }
}
