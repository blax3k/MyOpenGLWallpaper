package com.hashimapp.myopenglwallpaper;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

/**
 * Created by Blake Hashimoto on 8/14/2015.
 */
public class OpenGLES2WallpaperService extends GLWallpaperService
{
    GLRenderer renderer;

    @Override
    public Engine onCreateEngine()
    {
        return new OpenGLES2Engine();
    }

    class OpenGLES2Engine extends GLWallpaperService.GLEngine
    {
        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xStep,
                                     float yStep, int xPixels, int yPixels)
        {
            renderer.setEyeX(xOffset);
        }
        private android.view.GestureDetector.OnGestureListener gestureListener = new android.view.GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
//                doTap(e);
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//                renderer.setEyeX(renderer.eyeX - distanceX * 0.2f);
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        };

        GestureDetector mGestureDetector = new GestureDetector(OpenGLES2WallpaperService.this, gestureListener);





        private float mPreviousX;
        @Override
        public void onTouchEvent(MotionEvent event)
        {
            mGestureDetector.onTouchEvent(event);
//            float x = event.getX();
//            if(event.getAction() == MotionEvent.ACTION_MOVE)
//            {
//                float dx = (x - mPreviousX) * 0.003f;
//                renderer.setEyeX(dx);
//
//            }
//            super.onTouchEvent(event);
//
//            mPreviousX = x;
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder)
        {
            super.onCreate(surfaceHolder);

            final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
            final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

            if (supportsEs2)
            {
                setEGLContextClientVersion(2);

                setPreserveEGLContextOnPause(true);

                setRenderer(getNewRenderer());
//                mSurfaceView = new OpenGLES2WallpaperGLSurfaceView(OpenGLES2WallpaperService.this);

            }
        }


    }

    GLSurfaceView.Renderer getNewRenderer()
    {

        renderer = new GLRenderer(this);
        return renderer;
    }
}
