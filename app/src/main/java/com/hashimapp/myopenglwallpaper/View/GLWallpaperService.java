package com.hashimapp.myopenglwallpaper.View;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

import com.hashimapp.myopenglwallpaper.Model.GLRenderer;

import java.util.Date;

public abstract class GLWallpaperService extends WallpaperService
{

    protected SensorManager sensorManager;
    protected Sensor sensor;
    boolean rendererSet;

    public class GLEngine extends Engine
    {


        class WallpaperGLSurfaceView extends GLSurfaceView
        {
            private static final String TAG = "WallpaperGLSurfaceView";

            public Date startDate;

            WallpaperGLSurfaceView(Context context)
            {
                super(context);
                startDate = new Date();
                Log.d("create", "wallpaperGLSurfaceView " + startDate + " created");

            }

            @Override
            public SurfaceHolder getHolder()
            {
                return getSurfaceHolder();
            }

            public void onDestroy()
            {
                super.onDetachedFromWindow();
            }
        }

        WallpaperGLSurfaceView glSurfaceView;
        public GLRenderer renderer;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder)
        {
            super.onCreate(surfaceHolder);
            glSurfaceView = new WallpaperGLSurfaceView(GLWallpaperService.this);
        }

        @Override
        public void onVisibilityChanged(boolean visible)
        {
            super.onVisibilityChanged(visible);

            if (rendererSet)
            {
                if (visible)//resume
                {
                    glSurfaceView.onResume();
                    glSurfaceView.requestRender();
                } else //pause
                {
                    glSurfaceView.onPause();
                }
            }
        }


        @Override
        public void onDestroy()
        {
            super.onDestroy();
            glSurfaceView.onDestroy();
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder)
        {
            super.onSurfaceDestroyed(holder);
        }

        public void setRenderer(GLSurfaceView.Renderer newRenderer)
        {
            renderer = (GLRenderer) newRenderer;
            glSurfaceView.setRenderer(newRenderer);
            rendererSet = true;
            glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        }


        protected void setPreserveEGLContextOnPause(boolean preserve)
        {
            glSurfaceView.setPreserveEGLContextOnPause(preserve);
        }

        protected void setEGLContextClientVersion(int version)
        {
            glSurfaceView.setEGLContextClientVersion(version);
        }


    }
}
