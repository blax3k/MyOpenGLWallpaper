package com.hashimapp.myopenglwallpaper.Model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.preference.PreferenceManager;
import android.util.Log;

import com.hashimapp.myopenglwallpaper.SceneData.DataHolder;

public class GLRenderer implements Renderer {

    // Geometric variables
    public float uvs[], uvs2[][];
    public FloatBuffer uvBuffer;
    //set up our settings_activity
//    public SharedPreferences preferences;
    public boolean portraitOrientation, simScroll;
    //	private int frameBuffer;
    int[] texturenames;
    SceneSetter sceneSetter;
    // Our screen resolution
    float mScreenWidth = 1280;
    float mScreenHeight = 768;
    // Misc
    //	Square square, square1;
    Sprite bld1, bld0, bld2, bld3, sky, template;
    Date dateCreated;


    // Our matrices
    private int NUMBEROFCLOUDS = 16;
    public final float[][] cloudMVPMatrix = new float[NUMBEROFCLOUDS][16];
    public FloatBuffer[] cloudUVBuffer = new FloatBuffer[NUMBEROFCLOUDS];
    private float[] mModelMatrix = new float[16];

    public GLCamera camera;

    public GLRenderer() {
        dateCreated = new Date();
        camera = new GLCamera();
    }

    public void OnOffsetChanged(float xOffset, float yOffset) {
        camera.OffsetChanged(xOffset);
    }

    public void OnSensorChanged(float[] accelValues, int rotation) {
        camera.SensorChanged(accelValues, rotation);
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        sceneSetter = new SceneSetter();
        //create the sprites
        bld0 = new Sprite(sceneSetter.getSpriteVertices(SceneSetter.BLD0), sceneSetter.getSpriteColor(SceneSetter.BLD0), DataHolder.indices, sceneSetter.getTextureIndex(SceneSetter.BLD0));
        bld1 = new Sprite(sceneSetter.getSpriteVertices(SceneSetter.BLD1), sceneSetter.getSpriteColor(SceneSetter.BLD1), DataHolder.indices, sceneSetter.getTextureIndex(SceneSetter.BLD1));
        bld2 = new Sprite(sceneSetter.getSpriteVertices(SceneSetter.BLD2), sceneSetter.getSpriteColor(SceneSetter.BLD2), DataHolder.indices, sceneSetter.getTextureIndex(SceneSetter.BLD2));
        bld3 = new Sprite(sceneSetter.getSpriteVertices(SceneSetter.BLD3), sceneSetter.getSpriteColor(SceneSetter.BLD3), DataHolder.indices, sceneSetter.getTextureIndex(SceneSetter.BLD3));
        sky = new Sprite(sceneSetter.getSpriteVertices(SceneSetter.SKY), sceneSetter.getSpriteColor(SceneSetter.SKY), DataHolder.indices, sceneSetter.getTextureIndex(SceneSetter.SKY));
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


//        String temp = preferences.getString("camera_blur", "none");
//        sceneSetter.setBlur(temp);

        //load the images
        loadTextures();

    }

    float currentRatio;

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // We need to know the current width and height.
            mScreenWidth = width;
            mScreenHeight = height;
            GLES20.glViewport(0, 0, width, height);

//            String temp = preferences.getString("camera_blur", "none");
//            sceneSetter.setBlur(temp);

            camera.OnSurfaceChanged(width, height);


    }


    public void refreshColors() {
        try {
            bld0.setColor(sceneSetter.getSpriteColor(SceneSetter.BLD1));
            bld1.setColor(sceneSetter.getSpriteColor(SceneSetter.BLD0));
            bld3.setColor(sceneSetter.getSpriteColor(SceneSetter.BLD3));
            sky.setColor(sceneSetter.getSpriteColor(SceneSetter.SKY));
        } catch (Exception e) {

        }
    }

    public void changeColor(int sprite) {
        if (sprite == SceneSetter.BLD0) {
//			grassMid.setColor(sceneSetter.getSpriteColor("grassMid"));
            bld1.setColor(sceneSetter.getSpriteColor(SceneSetter.BLD0));
        } else if (sprite == SceneSetter.BLD1) {
            bld0.setColor(sceneSetter.getSpriteColor(SceneSetter.BLD1));
        } else if (sprite == SceneSetter.BLD3) {
            bld3.setColor(sceneSetter.getSpriteColor(SceneSetter.BLD3));
        } else if (sprite == SceneSetter.SKY) {
//            sky.setColor(sceneSetter.getSpriteColor(SceneModel.SKY));
        } else if (sprite == SceneSetter.BLD2) {
            bld2.setColor(sceneSetter.getSpriteColor(SceneSetter.BLD2));
        }
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
        Random rnd = new Random();

        // 30 image objects times 4 vertices times (u and v)
        uvs2 = new float[NUMBEROFCLOUDS][4 * 2];

        // We will make 30 randomly textures objects
        for (int i = 0; i < uvs2.length; i++) {
            int random_u_offset = rnd.nextInt(2);
            int random_v_offset = rnd.nextInt(2);

            // Adding the UV's using the offsets
            uvs2[i][0] = random_u_offset * 0.5f;
            uvs2[i][1] = random_v_offset * 0.5f;
            uvs2[i][2] = random_u_offset * 0.5f;
            uvs2[i][3] = (random_v_offset + 1) * 0.5f;
            uvs2[i][4] = (random_u_offset + 1) * 0.5f;
            uvs2[i][5] = (random_v_offset + 1) * 0.5f;
            uvs2[i][6] = (random_u_offset + 1) * 0.5f;
            uvs2[i][7] = random_v_offset * 0.5f;

            ByteBuffer bb2 = ByteBuffer.allocateDirect(uvs2.length * 4);
            bb2.order(ByteOrder.nativeOrder());
            cloudUVBuffer[i] = bb2.asFloatBuffer();
            cloudUVBuffer[i].put(uvs2[i]);
            cloudUVBuffer[i].position(0);
        }

        // Generate Textures, if more needed, alter these numbers.
        texturenames = new int[20];
        GLES20.glGenTextures(20, texturenames, 0);

        //texture 0
        // Temporary create a bitmap
        Bitmap bmp = sceneSetter.getTexture(SceneSetter.BLD0, SceneSetter.FOUR_EIGHTY_P, SceneSetter.BLUR_NONE);

        bld0.SetTexture(bmp, GLES20.GL_TEXTURE0);

        //texture 1 bld1
        bmp = sceneSetter.getTexture(SceneSetter.BLD1, SceneSetter.FOUR_EIGHTY_P, SceneSetter.BLUR_NONE);
//        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[1]);
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
//        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
        bld1.SetTexture(bmp, GLES20.GL_TEXTURE1);


        //texture 2 bld2
        bmp = sceneSetter.getTexture(SceneSetter.BLD2, SceneSetter.FOUR_EIGHTY_P, SceneSetter.BLUR_NONE);
        bld2.SetTexture(bmp, GLES20.GL_TEXTURE2);

        //texture 3 bld3
        bmp = sceneSetter.getTexture(SceneSetter.BLD3, SceneSetter.FOUR_EIGHTY_P, SceneSetter.BLUR_NONE);
        bld3.SetTexture(bmp, GLES20.GL_TEXTURE3);

        //Texture 4 Sky
        bmp = sceneSetter.getTexture(SceneSetter.SKY, SceneSetter.FOUR_EIGHTY_P, SceneSetter.BLUR_NONE);
        sky.SetTexture(bmp,GLES20.GL_TEXTURE4);

        bmp.recycle();
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
        Matrix.setIdentityM(mModelMatrix, 0);


        sky.draw(uvBuffer, camera.mtrxView, camera.mtrxProjection, mModelMatrix);
        //change the program being used

        //draw the clouds
//		Matrix.setIdentityM(mModelMatrix, 0);
//		Matrix.translateM(mModelMatrix, 0, xOffset * 0.1f, yOffset * 0.1f, 1.0f);
//		Matrix.multiplyMM(scratch1, 0, mtrxView, 0, mModelMatrix, 0);
//		Matrix.multiplyMM(scratch1, 0, mtrxProjection, 0, scratch1, 0);
//		clouds.draw(cloudMVPMatrix, cloudUVBuffer, sceneSetter.getTextureIndex(SceneModel.CLOUDS), mModelMatrix,
//				mtrxView, mtrxProjection, xOffset, yOffset);


//
        //Draw the bld3
        bld3.draw(uvBuffer, camera.mtrxView, camera.mtrxProjection, mModelMatrix);

        //draw the bld2
        bld2.draw(uvBuffer, camera.mtrxView, camera.mtrxProjection, mModelMatrix);


        //draw the bld1
        bld1.draw(uvBuffer, camera.mtrxView, camera.mtrxProjection, mModelMatrix);
//
        //draw the bld0
//        Matrix.translateM(mModelMatrix, 0, xOffset * 0.7f, yOffset, 1.0f);
        bld0.draw(uvBuffer, camera.mtrxView, camera.mtrxProjection, mModelMatrix);
    }

}
