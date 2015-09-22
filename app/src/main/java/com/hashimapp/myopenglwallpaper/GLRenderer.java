package com.hashimapp.myopenglwallpaper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.preference.PreferenceManager;

public class GLRenderer implements Renderer {

	// Our matrices
	private final float[] mtrxProjection = new float[16];
	private final float[] mtrxView = new float[16];
	private final float[] mMVPMatrix = new float[16];
	private float[] mModelMatrix = new float[16];
	private int mMVPMatrixHandle;

	// Geometric variables
	public static float vertices[];
	public static short indices[];
	public static float uvs[], uvs2[];
	public FloatBuffer vertexBuffer;
	public ShortBuffer drawListBuffer;
	public FloatBuffer uvBuffer, uvBuffer2;

	// Our screenresolution
	float	mScreenWidth = 1280;
	float	mScreenHeight = 768;

	// Misc
	Context mContext;
	long mLastTime;
	int mProgram;

	//set up our main_preferences
	SharedPreferences preferences;

    //set up array database
    ArrayHolder arrayHolder = new ArrayHolder();

//	Square square, square1;
	Sprite background, mountains, girl, grass;
	float offsetDifference = 1;
//	Background background;

	public void setEyeX(float offset)
	{

//			Log.d("Renderer", "renderer offsetOnce: " + this.toString());

			eyeX = -offset * offsetDifference;
			lookX = eyeX;
//		Log.d("setEyeX", "eyeX: " + eyeX);
	}

	public void setEyeY(float offset)
	{
		eyeY = offset * offsetDifference;
		lookY = eyeY;
	}

	public GLRenderer(Context c)
	{
		mContext = c;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		TimeTracker timeTracker = new TimeTracker();
		timeTracker.getDayPart();


		//Load in Preferences
		preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		// Generate Textures, if more needed, alter these numbers.
		int[] textureNames = new int[4];
		GLES20.glGenTextures(4, textureNames, 0);

        //set the scene color from preferences
		float[] sceneColor;
		if(preferences.getBoolean("activate_sunset", false))
		{
			sceneColor = arrayHolder.sunsetColor;
		}
		else
		{
			sceneColor = arrayHolder.normalColor;
		}

        //create the sprites
		grass = new Sprite(arrayHolder.grassVertices, sceneColor);
		girl = new Sprite(arrayHolder.girlVertices, sceneColor);
//		mountains = new Sprite(arrayHolder.vertices3, sceneColor);
		background = new Sprite(arrayHolder.fieldVertices, sceneColor);

		// Set the clear color to white
		GLES20.glClearColor(0.9f, 0.9f, 0.9f, 0);

		// Create the shaders, solid color
		int vertexShader = riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER, riGraphicTools.vs_SolidColor);
		int fragmentShader = riGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER, riGraphicTools.fs_SolidColor);

		riGraphicTools.sp_SolidColor = GLES20.glCreateProgram();             // create empty OpenGL ES Program
		GLES20.glAttachShader(riGraphicTools.sp_SolidColor, vertexShader);   // add the vertex shader to program
		GLES20.glAttachShader(riGraphicTools.sp_SolidColor, fragmentShader); // add the fragment shader to program
		GLES20.glLinkProgram(riGraphicTools.sp_SolidColor);                  // creates OpenGL ES program executables

		// Create the shaders, images
		vertexShader = riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER, riGraphicTools.vs_Image);
		fragmentShader = riGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER, riGraphicTools.fs_Image);

		riGraphicTools.sp_Image = GLES20.glCreateProgram();             // create empty OpenGL ES Program
		GLES20.glAttachShader(riGraphicTools.sp_Image, vertexShader);   // add the vertex shader to program
		GLES20.glAttachShader(riGraphicTools.sp_Image, fragmentShader); // add the fragment shader to program
		GLES20.glLinkProgram(riGraphicTools.sp_Image);                  // creates OpenGL ES program executables

		// Set our shader program
		GLES20.glUseProgram(riGraphicTools.sp_Image);

		setupImages();
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {

		// We need to know the current width and height.
		mScreenWidth = width;
		mScreenHeight = height;
		GLES20.glViewport(0, 0, width, height);


		float ratio;
		if(height > width)
		{
			ratio = (float) width / height;
			Matrix.frustumM(mtrxProjection,0, -ratio, ratio, -1, 1, 3, 7);
			offsetDifference = 1;
		}
		else
		{
			ratio = (float) height / width;
			Matrix.frustumM(mtrxProjection,0, -1, 1, -ratio, ratio,  3, 7);
			offsetDifference = 0.5f;
		}

	}

	// Position the eye in front of the origin.
	float eyeX = 0.0f;
	float eyeY = 0.0f;
	float eyeZ = -4.0f;
	// We are looking toward the distance
	float lookX = 0.0f;
	float lookY = 0.0f;
	float lookZ = 0.0f;
	// Set our up vector. This is where our head would be pointing were we holding the camera.
	float upX = 0.0f;
	float upY = 1.0f;
	float upZ = 0.0f;

	boolean colorIsRed;

	boolean runOnce, firstOffsetChanged = false;


	@Override
	public void onDrawFrame(GL10 unused) {

//			Log.d("Renderer", "renderer drew one frame: " + this.toString());

		// Set the camera position (View matrix)
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix");

//		Log.d("onDrawFrame", "eyeX: " + eyeX);
		Matrix.setLookAtM(mtrxView, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);


		// Calculate the projection and view transformation
//		Matrix.multiplyMM(mMVPMatrix, 0, mtrxProjection, 0, mtrxView, 0);
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.translateM(mModelMatrix, 0, eyeX * 0.2f, 0.0f, 1.0f);
		Matrix.multiplyMM(mMVPMatrix, 0, mtrxView, 0, mModelMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mtrxProjection, 0, mMVPMatrix, 0);
		background.draw(mMVPMatrix, uvBuffer, 0);

//		float[] scratch2 = new float[16];
//////		Matrix.multiplyMM(scratch, 0, mtrxProjection, 0, mtrxView, 0);
//		Matrix.setIdentityM(mModelMatrix, 0);
//		Matrix.translateM(mModelMatrix, 0, eyeX * 0.9f, 0.0f, 1.0f);
//		Matrix.multiplyMM(scratch2, 0, mtrxView, 0, mModelMatrix, 0);
//		Matrix.multiplyMM(scratch2, 0, mtrxProjection, 0, scratch2, 0);
//		mountains.draw(scratch2, uvBuffer, 3);


		float[] scratch1 = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, 1.0f);
		Matrix.multiplyMM(scratch1, 0, mtrxView, 0, mModelMatrix, 0);
		Matrix.multiplyMM(scratch1, 0, mtrxProjection, 0, scratch1, 0);
		girl.draw(scratch1, uvBuffer, 1);


		if(!preferences.getBoolean("pref_key_remove_layer", true))
		{
			float[] scratch = new float[16];
////		Matrix.multiplyMM(scratch, 0, mtrxProjection, 0, mtrxView, 0);
			Matrix.setIdentityM(mModelMatrix, 0);
			Matrix.translateM(mModelMatrix, 0, eyeX * -0.5f - 0.8f, 0.3f, 1.0f);
			Matrix.multiplyMM(scratch, 0, mtrxView, 0, mModelMatrix, 0);
			Matrix.multiplyMM(scratch, 0, mtrxProjection, 0, scratch, 0);
			grass.draw(scratch, uvBuffer, 2);
		}
	}

    public void changeColor(int colorCode)
    {
        float[] newColor = arrayHolder.normalColor;
        switch(colorCode){
            case 0: newColor = arrayHolder.normalColor;
                break;
            case 1: newColor = arrayHolder.sunsetColor;
                break;
        }

        grass.changeColor(newColor);
        girl.changeColor(newColor);
//        mountains.changeColor(newColor);
        background.changeColor(newColor);
    }

	private void setupImages()
	{
		// Create our UV coordinates.
		uvs = new float[] {
				0.0f, 0.0f,
				0.0f, 1.0f,
				1.0f, 1.0f,
				1.0f, 0.0f
		};

		uvs2 = new float[] {
				0.0f, 0.0f,
				0.0f, 0.5f,
				0.5f, 0.5f,
				0.5f, 0.0f
		};

		// The texture buffer
		ByteBuffer bb = ByteBuffer.allocateDirect(uvs.length * 4);
		bb.order(ByteOrder.nativeOrder());
		uvBuffer = bb.asFloatBuffer();
		uvBuffer.put(uvs);
		uvBuffer.position(0);

		ByteBuffer bb2 = ByteBuffer.allocateDirect(uvs2.length * 4);
		bb2.order(ByteOrder.nativeOrder());
		uvBuffer2 = bb2.asFloatBuffer();
		uvBuffer2.put(uvs2);
		uvBuffer2.position(0);

		// Generate Textures, if more needed, alter these numbers.
		int[] texturenames = new int[4];
		GLES20.glGenTextures(4, texturenames, 0);

		// Temporary create a bitmap
		Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.field);
		// Bind texture to texturename
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[0]);
		// Set filtering
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		// Load the bitmap into the bound texture.
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

		bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.girl2crop);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[1]);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

		bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.grass);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[2]);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

		bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.squaremountains);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[3]);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

		bmp.recycle();
	}
}
