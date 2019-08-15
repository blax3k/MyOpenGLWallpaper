package com.hashimapp.myopenglwallpaper.View;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.hashimapp.myopenglwallpaper.Model.GLParticleRenderer;
import com.hashimapp.myopenglwallpaper.Model.GLRenderer;

class MyGLSurfaceView extends GLSurfaceView
{

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        return super.onTouchEvent(event);
    }

    public MyGLSurfaceView(Context context) {
        super(context);
        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

    }

    public void onDestroy()
    {
        super.onDetachedFromWindow();
    }


}
