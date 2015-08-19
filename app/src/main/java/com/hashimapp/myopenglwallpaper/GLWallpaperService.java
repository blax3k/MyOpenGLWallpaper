package com.hashimapp.myopenglwallpaper;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

/**
 * Created by User on 8/13/2015.
 * Post apocalyptic live wallpaper Checklist

 The overall idea is to have an android live wallpaper with a bunch of images that form a scene (post apocalyptic in this case) and creates
 the (cool) illusion of a 3d environment by using the parallax effect.

 I will need a blurred out foreground layer which is basically the top of a table with various tools, a midground layer, which is the
 subject and the room itself, another layer further back, which will serve as the city backdrop outside of the room, and a background
 which will serve as the sky.

 Things I'd like to add:

 dynamic lighting so that the scene will be of different brightnesses and hues depending on the time of day.

 different objects, positions, and activities depending on the time of day (sleeping, eating, scavenging, reading etc.).

 animations for various objects. Blowing hair, flickering fires, twinkling stars, moving clouds etc.

 /Done! Settings page is a must.

 options for people without homescreens that do offsets changed.

 camera translations based on the gyroscope/acclerometer

 weather changes based on user preference or real life weather data

 donation option

 /Done! link to artists deviantart page
 */
public abstract class GLWallpaperService extends WallpaperService{
  public class GLEngine extends Engine{
        class WallpaperGLSurfaceView extends GLSurfaceView {
            private static final String TAG = "WallpaperGLSurfaceView";

            WallpaperGLSurfaceView(Context context)
            {
                super(context);
            }

            public SurfaceHolder getHolder()
            {
                return getSurfaceHolder();
            }

            public void onDestroy()
            {
                super.onDetachedFromWindow();
            }
        }

      private WallpaperGLSurfaceView glSurfaceView;
      private boolean rendererHasBeenSet;

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

          if(rendererHasBeenSet)
          {
              if(visible)
              {
                  glSurfaceView.onResume();
              }else
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

      protected void setRenderer(GLSurfaceView.Renderer renderer)
      {
          glSurfaceView.setRenderer(renderer);
          rendererHasBeenSet = true;
      }

      protected void setPreserveEGLContextOnPause(boolean preserve)
      {
          if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
          {
              glSurfaceView.setPreserveEGLContextOnPause(preserve);
          }
      }

      protected void setEGLContextClientVersion(int version)
      {
          glSurfaceView.setEGLContextClientVersion(version);
      }
    }
}
