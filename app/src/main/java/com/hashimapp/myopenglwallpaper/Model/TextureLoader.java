package com.hashimapp.myopenglwallpaper.Model;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.opengl.EGL14;
import android.opengl.GLES20;

import com.hashimapp.myopenglwallpaper.R;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;


public class TextureLoader extends  Thread
{
    private EGLContext textureContext;
    private EGL10 egl;
    private EGLConfig eglConfig;
    private EGLDisplay display;
    private Context androidContext;

    public TextureLoader(EGL10 egl, javax.microedition.khronos.egl.EGLContext renderContext, EGLDisplay display,
                         EGLConfig eglConfig, Context androidContext) {
        this.egl = egl;
        this.display = display;
        this.eglConfig = eglConfig;
        this.androidContext = androidContext;

        textureContext = egl.eglCreateContext(display, eglConfig, renderContext, null);
    }

    public void run() {
        int pbufferAttribs[] = { EGL10.EGL_WIDTH, 1, EGL10.EGL_HEIGHT, 1, EGL14.EGL_TEXTURE_TARGET,
                EGL14.EGL_NO_TEXTURE, EGL14.EGL_TEXTURE_FORMAT, EGL14.EGL_NO_TEXTURE,
                EGL10.EGL_NONE };

        EGLSurface localSurface = egl.eglCreatePbufferSurface(display, eglConfig, pbufferAttribs);

        egl.eglMakeCurrent(display, localSurface, localSurface, textureContext);

        sleep(); // delay loading texture to demonstrate threaded loading

        // TODO replace static variable with message passing to inform the
        // renderer about the new texture
    }

    private void sleep() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
    }

}
