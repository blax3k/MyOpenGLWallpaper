package com.hashimapp.myopenglwallpaper.View;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationProvider;
import android.opengl.GLSurfaceView;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

import com.hashimapp.myopenglwallpaper.Model.GLParticleRenderer;
import com.hashimapp.myopenglwallpaper.Model.GLRenderer;
import com.hashimapp.myopenglwallpaper.Model.TextureLoader;

import java.util.Date;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

public abstract class GLWallpaperService extends WallpaperService
{

    protected SensorManager sensorManager;
    protected Sensor sensor;
    boolean rendererSet;

    private LocationProvider locationProvider;


    public class GLEngine extends Engine
    {
        class WallpaperGLSurfaceView extends GLSurfaceView
        {
            WallpaperGLSurfaceView(Context context)
            {
                super(context);
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

    }
}
