package com.hashimapp.myopenglwallpaper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;

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
	public static float uvs[];
	public FloatBuffer vertexBuffer;
	public ShortBuffer drawListBuffer;
	public FloatBuffer uvBuffer;
	
	// Our screenresolution
	float	mScreenWidth = 1280;
	float	mScreenHeight = 768;

	// Misc
	Context mContext;
	long mLastTime;
	int mProgram;

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

	Square square, square1;
	Sprite sprite1, sprite2;
	Background background;

	public void setEyeX(float offset)
	{
		eyeX = -offset;
		lookX = eyeX;
	}
	
	public GLRenderer(Context c)
	{
		mContext = c;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {

		// Generate Textures, if more needed, alter these numbers.
		int[] textureNames = new int[4];
		GLES20.glGenTextures(3, textureNames, 0);

		float[] vertices1 = new float[]{
				-1.1f,  1.1f, -2.0f,   // top left
				-1.1f, -1.1f, -2.0f,   // bottom left
				1.1f, -1.1f, -2.0f,   // bottom right
				1.1f,  1.1f, -2.0f }; // top right


		float[] vertices2 = new float[]{
				-2.5f,  2.5f, 2.0f,   // top left
				-2.5f, -2.5f, 2.0f,   // bottom left
				2.5f, -2.5f, 2.0f,   // bottom right
				2.5f,  2.5f, 2.0f }; // top right

		float[] vertices3 = new float[]{
				-2.5f,  2.5f, 2.0f,   // top left
				-2.5f, -2.5f, 2.0f,   // bottom left
				2.5f, -2.5f, 2.0f,   // bottom right
				2.5f,  2.5f, 2.0f }; // top right

		float[] vertices4 = new float[]{
				-2.5f,  2.5f, 2.0f,   // top left
				-2.5f, -2.5f, 2.0f,   // bottom left
				2.5f, -2.5f, 2.0f,   // bottom right
				2.5f,  2.5f, 2.0f }; // top right

//		square1 = new Square(mContext, "drawable/girl2", vertices2, textureNames);
		square = new Square(mContext, "drawable/squareperson", vertices1, textureNames);
		background = new Background(mContext, "drawable/squaresky", vertices2, textureNames);
		sprite1 = new Sprite(mContext, "drawable/squareground", vertices3, textureNames);
//		sprite2 = new Sprite(mContext, "drawable/squaremountains.png.png", vertices3, textureNames);


		// Set the clear color to black
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
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {

		// We need to know the current width and height.
		mScreenWidth = width;
		mScreenHeight = height;
		GLES20.glViewport(0, 0, width, height);

		float ratio = (float) width / height;

		Matrix.frustumM(mtrxProjection,0, -ratio, ratio, -1, 1, 3, 7);
	}

	@Override
	public void onDrawFrame(GL10 unused) {
		// Setup our screen width and height for normal sprite translation.
//	    Matrix.orthoM(mtrxProjection, 0, 0f, mScreenWidth, 0.0f, mScreenHeight, 0, 50);

		// Set the camera position (View matrix)
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix");

		Matrix.setLookAtM(mtrxView, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);


		// Calculate the projection and view transformation
//		Matrix.multiplyMM(mMVPMatrix, 0, mtrxProjection, 0, mtrxView, 0);
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.translateM(mModelMatrix, 0, eyeX, 0.0f, 1.0f);
		Matrix.multiplyMM(mMVPMatrix, 0, mtrxView, 0, mModelMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mtrxProjection, 0, mMVPMatrix, 0);
//		square.draw(mMVPMatrix);
		background.draw(mMVPMatrix);


		float[] scratch1 = new float[16];
////		Matrix.multiplyMM(scratch, 0, mtrxProjection, 0, mtrxView, 0);
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.translateM(mModelMatrix, 0, eyeX * 0.5f, 0.0f, 1.0f);
		Matrix.multiplyMM(scratch1, 0, mtrxView, 0, mModelMatrix, 0);
		Matrix.multiplyMM(scratch1, 0, mtrxProjection, 0, scratch1, 0);
		sprite1.draw(scratch1);


		float[] scratch = new float[16];
////		Matrix.multiplyMM(scratch, 0, mtrxProjection, 0, mtrxView, 0);
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.translateM(mModelMatrix, 0, -0.5f, 0.0f, 1.0f);
		Matrix.multiplyMM(scratch, 0, mtrxView, 0, mModelMatrix, 0);
		Matrix.multiplyMM(scratch, 0, mtrxProjection, 0, scratch, 0);
		square.draw(scratch);
	}

	private void setupImage()
	{
		// Create our UV coordinates.
		uvs = new float[] {
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

		// Generate Textures, if more needed, alter these numbers.
		int[] texturenames = new int[2];
		GLES20.glGenTextures(2, texturenames, 0);

		// Retrieve our image from resources.
//		int id = mContext.getResources().getIdentifier("drawable/girl.png", null, mContext.getPackageName());
//		int id2 = mContext.getResources().getIdentifier("drawable/girl2.png", null, mContext.getPackageName());
//
//		// Temporary create a bitmap
//		Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.girl);
//		Bitmap bmp2 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.girl2);

		// Bind texture to texturename
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[0]);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[1]);

		// Set filtering
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
//
//		// Load the bitmap into the bound texture.
//		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
//		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 1, bmp2, 0);
//
//		// We are done using the bitmap so we should recycle it.
//		bmp.recycle();
//		bmp2.recycle();
	}

	private void Render(float[] m)
	{

		// clear Screen and Depth Buffer, we have set the clear color as black.
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		// get handle to vertex shader's vPosition member
		int mPositionHandle = GLES20.glGetAttribLocation(riGraphicTools.sp_Image, "vPosition");

		// Enable generic vertex attribute array
		GLES20.glEnableVertexAttribArray(mPositionHandle);

		// Prepare the triangle coordinate data
		GLES20.glVertexAttribPointer(mPositionHandle, 3,
				GLES20.GL_FLOAT, false,
				12, vertexBuffer);

		// Get handle to texture coordinates location
		int mTexCoordLoc = GLES20.glGetAttribLocation(riGraphicTools.sp_Image, "a_texCoord" );

		// Enable generic vertex attribute array
		GLES20.glEnableVertexAttribArray ( mTexCoordLoc );

		// Prepare the texturecoordinates
		GLES20.glVertexAttribPointer ( mTexCoordLoc, 2, GLES20.GL_FLOAT,
				false,
				0, uvBuffer);

		// Get handle to shape's transformation matrix
		int mtrxhandle = GLES20.glGetUniformLocation(riGraphicTools.sp_Image, "uMVPMatrix");

		// Apply the projection and view transformation
		GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, m, 0);

		// Get handle to textures locations
		int mSamplerLoc = GLES20.glGetUniformLocation (riGraphicTools.sp_Image, "s_texture" );

		// Set the sampler texture unit to 0, where we have saved the texture.
		GLES20.glUniform1i(mSamplerLoc, 0);


//		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
//		GLES20.glEnable(GLES20.GL_BLEND);


		// Draw the triangle
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length,
				GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

//		GLES20.glDisable(GLES20.GL_BLEND);
		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);
		GLES20.glDisableVertexAttribArray(mTexCoordLoc);

	}

	public void SetupImage()
	{
		// Create our UV coordinates.
		uvs = new float[] {
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
		
		// Generate Textures, if more needed, alter these numbers.
		int[] texturenames = new int[1];
		GLES20.glGenTextures(1, texturenames, 0);
		
		// Retrieve our image from resources.
		int id = mContext.getResources().getIdentifier("drawable/girl.png", null, mContext.getPackageName());
		
		// Temporary create a bitmap
		Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.girl);
		
		// Bind texture to texturename
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[0]);
		
		// Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        
        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
        
        // We are done using the bitmap so we should recycle it.
		bmp.recycle();

	}
	
	public void SetupTriangle()
	{
		// We have to create the vertices of our triangle.
		vertices = new float[]{
				-0.5f,  0.5f, 0.0f,   // top left
				-0.5f, -0.5f, 0.0f,   // bottom left
				0.5f, -0.5f, 0.0f,   // bottom right
				0.5f,  0.5f, 0.0f }; // top right

		indices = new short[] {0, 1, 2, 0, 2, 3}; // The order of vertexrendering.

		// The vertex buffer.
		ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
		bb.order(ByteOrder.nativeOrder());
		vertexBuffer = bb.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);
		
		// initialize byte buffer for the draw list
		ByteBuffer dlb = ByteBuffer.allocateDirect(indices.length * 2);
		dlb.order(ByteOrder.nativeOrder());
		drawListBuffer = dlb.asShortBuffer();
		drawListBuffer.put(indices);
		drawListBuffer.position(0);
		
		
	}
}
