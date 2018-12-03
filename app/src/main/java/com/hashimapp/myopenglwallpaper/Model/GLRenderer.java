package com.hashimapp.myopenglwallpaper.Model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Date;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.usage.UsageEvents;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.SensorEvent;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;

import com.hashimapp.myopenglwallpaper.SceneData.TestImage;
import com.hashimapp.myopenglwallpaper.View.OpenGLES2WallpaperService;

public class GLRenderer implements Renderer {

    // Geometric variables
    public float uvs[], uvs2[][];
    public FloatBuffer uvBuffer;
    int[] texturenames;
    SceneSetter sceneSetter;
    // Our screen resolution
    float mScreenWidth = 1280;
    float mScreenHeight = 768;
//    Sprite sky, template;
    Date dateCreated;

    // Our matrices
    private int NUMBEROFCLOUDS = 16;
    public final float[][] cloudMVPMatrix = new float[NUMBEROFCLOUDS][16];
    public FloatBuffer[] cloudUVBuffer = new FloatBuffer[NUMBEROFCLOUDS];
    private float[] mModelMatrix = new float[16];
    private int mProgram;

    public GLCamera camera;

    public GLRenderer() {
        dateCreated = new Date();
        camera = new GLCamera();
        sceneSetter = new SceneSetter();
    }

    public void OnOffsetChanged(float xOffset, float yOffset) {
        float newXOffset = camera.getxOffsetStepPortrait(xOffset);
        sceneSetter.OffsetChanged(newXOffset, camera.IsPortraitOrientation());
    }

    public void OnSensorChanged(SensorEvent event, int rotation) {
        float[] newSensorValues = camera.SensorChanged(event, rotation);
        sceneSetter.SensorChanged(newSensorValues[0], newSensorValues[1]);
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        sceneSetter = new SceneSetter();
        // Set the clear color to white
        GLES20.glClearColor(0.9f, 0.9f, 0.9f, 0);


//		// Create the shaders for solid color
//		int vertexShader = riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER, riGraphicTools.vs_SolidColor);
//		int fragmentShader = riGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER, riGraphicTools.fs_SolidColor);
//
////		riGraphicTools.sp_SolidColor = GLES20.glCreateProgram();             // create empty OpenGL ES Program
////		GLES20.glAttachShader(riGraphicTools.sp_SolidColor, vertexShader);   // add the vertex shader to program
////		GLES20.glAttachShader(riGraphicTools.sp_SolidColor, fragmentShader); // add the fragment shader to program
////		GLES20.glLinkProgram(riGraphicTools.sp_SolidColor);                  // creates OpenGL ES program executrees


        // Create the shaders, images
        int vertexShader = riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER, riGraphicTools.vs_Image);
        int fragmentShader = riGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER, riGraphicTools.fs_Image);

        riGraphicTools.sp_Image = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        GLES20.glAttachShader(riGraphicTools.sp_Image, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(riGraphicTools.sp_Image, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(riGraphicTools.sp_Image);                  // creates OpenGL ES program executrees

        // Set our shader program
        GLES20.glUseProgram(riGraphicTools.sp_Image);
        //load the images
        loadTextures();

    }

    public void ResetMotionOffset(){
        camera.ResetSensorOffset();
        sceneSetter.SensorChanged(0, 0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // We need to know the current width and height.
            mScreenWidth = width;
            mScreenHeight = height;
            GLES20.glViewport(0, 0, width, height);

//            String temp = preferences.getString("camera_blur", "none");
//            sceneSetter.setBlur(temp);

            camera.OnSurfaceChanged(width, height);
            sceneSetter.SetOrientation(camera.IsPortrait());
    }


    private void loadTextures() {
        // Create our UV coordinates.
        uvs = new float[]{
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f
        };

        // The texture buffer
        ByteBuffer bb = ByteBuffer.allocateDirect(uvs.length * 4);
        bb.order(ByteOrder.nativeOrder());
        uvBuffer = bb.asFloatBuffer();
        uvBuffer.put(uvs);
        uvBuffer.position(0);

        //cloud texture buffer
//        Random rnd = new Random();
//
//        // 30 image objects times 4 portraitVertices times (u and v)
//        uvs2 = new float[NUMBEROFCLOUDS][4 * 2];
//
//        // We will make 30 randomly textures objects
//        for (int i = 0; i < uvs2.length; i++) {
//            int random_u_offset = rnd.nextInt(2);
//            int random_v_offset = rnd.nextInt(2);
//
//            // Adding the UV's using the offsets
//            uvs2[i][0] = random_u_offset * 0.5f;
//            uvs2[i][1] = random_v_offset * 0.5f;
//            uvs2[i][2] = random_u_offset * 0.5f;
//            uvs2[i][3] = (random_v_offset + 1) * 0.5f;
//            uvs2[i][4] = (random_u_offset + 1) * 0.5f;
//            uvs2[i][5] = (random_v_offset + 1) * 0.5f;
//            uvs2[i][6] = (random_u_offset + 1) * 0.5f;
//            uvs2[i][7] = random_v_offset * 0.5f;
//
//            ByteBuffer bb2 = ByteBuffer.allocateDirect(uvs2.length * 4);
//            bb2.order(ByteOrder.nativeOrder());
//            cloudUVBuffer[i] = bb2.asFloatBuffer();
//            cloudUVBuffer[i].put(uvs2[i]);
//            cloudUVBuffer[i].position(0);
//        }

        // Generate Textures, if more needed, alter these numbers.
        texturenames = new int[20];
        GLES20.glGenTextures(20, texturenames, 0);

        //texture 0
        // Temporary create a bitmap
    }

    @Override
    public void onDrawFrame(GL10 unused) {

        GLES20.glFlush();
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
//        // Enable alpha blending.
//        GLES20.glEnable(GLES20.GL_BLEND);
//        // Blend based on the fragment's alpha value.
//        GLES20.glBlendFunc(GLES20.GL_ONE /*GL_SRC_ALPHA*/, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        //increase opawater from zero

//		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix");

//		Log.d("onDrawFrame", "eyeX: " + eyeX);
//		Matrix.setLookAtM(mtrxView, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

//		GLES20.glUseProgram(riGraphicTools.sp_SolidColor);


        GLES20.glUseProgram(riGraphicTools.sp_Image);
        // Calculate the projection and view

        sceneSetter.DrawSprites(uvBuffer, camera.mtrxView, camera.mtrxProjection, mModelMatrix);

//        sky.draw(uvBuffer, camera.mtrxView, camera.mtrxProjection, mModelMatrix);
        //change the program being used

        //draw the clouds
//		Matrix.setIdentityM(mModelMatrix, 0);
//		Matrix.translateM(mModelMatrix, 0, xScrollOffset * 0.1f, yOffset * 0.1f, 1.0f);
//		Matrix.multiplyMM(scratch1, 0, mtrxView, 0, mModelMatrix, 0);
//		Matrix.multiplyMM(scratch1, 0, mtrxProjection, 0, scratch1, 0);
//		clouds.draw(cloudMVPMatrix, cloudUVBuffer, sceneSetter.getTextureIndex(SceneModel.CLOUDS), mModelMatrix,
//				mtrxView, mtrxProjection, xScrollOffset, yOffset);

    }

}
