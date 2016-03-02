package com.hashimapp.myopenglwallpaper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

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
import android.provider.ContactsContract;
import android.util.Log;

public class GLRenderer implements Renderer {

	// Our matrices
	private final float[] mtrxProjection = new float[16];
	private final float[] mtrxView = new float[16];
	private final float[] mMVPMatrix = new float[16],
			scratch0 = new float[16],
			scratch1 = new float[16],
			scratch2 = new float[16],
			scratch3 = new float[16];
	private float[] mModelMatrix = new float[16];
	private int mMVPMatrixHandle;

	// Geometric variables
	public static float uvs[];
	public FloatBuffer uvBuffer;
//	private int frameBuffer;
	int[] texturenames;

	SceneSetter sceneSetter;

	// Our screenresolution
	float	mScreenWidth = 1280;
	float	mScreenHeight = 768;

	// Misc
	Context mContext;
	int mProgram;

	long startTime = System.currentTimeMillis();

	//set up our main_preferences
	SharedPreferences preferences;
	private SharedPreferences.OnSharedPreferenceChangeListener prefListener;

    //set up array database
//    DataHolder dataHolder = new DataHolder();

//	Square square, square1;
	Sprite girl, table, room, city, building, sky;//, girlMid, girlFront, girlBack;
	float offsetDifferenceX = 1;
	float offsetDifferenceY = 1;
//	Background room;
	float xOffset;
	public void setEyeX(float offset)
	{
			if(portraitOrientation)
			{
				xOffset = offset * offsetDifferenceX;
				if(simScroll)
					xOffset -= 1.5f;
//				lookX = xOffset;
			}
		else //landscape orientation
			{
				xOffset = offset * offsetDifferenceX + 1.2f;
//				lookX = xOffset;
			}
	}

	public void resetEyeY(float offset)
	{
		eyeY = offset;
		lookY = eyeY;
	}

	public void setEyeY(float offset)
	{
		if(portraitOrientation)
		{
			eyeY = offset * offsetDifferenceY;
			if(simScroll)
				eyeY += 0.25f;
			lookY = eyeY;
//			lookY = offset* offsetDifferenceX *4;
		}
		else
		{
			eyeY = offset * offsetDifferenceY; // - 0.3f;
			lookY = eyeY;
//			lookY = offset * offsetDifferenceX * 4;
		}
	}

	public GLRenderer(Context c)
	{
		mContext = c;
		//Load in Preferences
		preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		sceneSetter = new SceneSetter(preferences, c);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		initializePreferences();
		if(preferences.getBoolean("pref_key_sim_scroll", true))
					setSimScroll(true);
		else		setSimScroll(false);

		TimeTracker timeTracker = new TimeTracker();
		timeTracker.getDayHour();

		// Generate Textures, if more needed, alter these numbers.
//		int[] textureNames = new int[9];
//		GLES20.glGenTextures(9, textureNames, 0);

        //create the sprites
		table = new Sprite(sceneSetter.getSpriteVertices(DataHolder.TABLE), sceneSetter.getSpriteColor("table"));
//		girlFront = new Sprite(sceneSetter.getSpriteVertices("girlFront"), sceneSetter.getSpriteColor("girlFront"));
//		girlMid = new Sprite(sceneSetter.getSpriteVertices("girlMid"), sceneSetter.getSpriteColor("girlMid"));
//		girlBack = new Sprite(sceneSetter.getSpriteVertices("girlBack"), sceneSetter.getSpriteColor("girlBack"));
		girl = new Sprite(sceneSetter.getSpriteVertices(DataHolder.GIRL), sceneSetter.getSpriteColor("girl"));
		room = new Sprite(sceneSetter.getSpriteVertices(DataHolder.ROOM), sceneSetter.getSpriteColor("field"));
		building = new Sprite(sceneSetter.getSpriteVertices(DataHolder.BUILDING), sceneSetter.getSpriteColor("building"));
		city = new Sprite(sceneSetter.getSpriteVertices(DataHolder.CITY), sceneSetter.getSpriteColor("city"));
		sky = new Sprite(sceneSetter.getSpriteVertices(DataHolder.SKY), sceneSetter.getSpriteColor("sky"));

		// Set the clear color to white
//		GLES20.glClearColor(0.9f, 0.9f, 0.9f, 0);

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
		//load the images
		loadTextures();
	}

	private void initializePreferences()
	{
		//set up preference listener
		SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		//set up the colors
		prefListener = new SharedPreferences.OnSharedPreferenceChangeListener()
		{
			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPrefs, String key)
			{
				Log.d("preferences", "The preferences were changed");
				if(key.equals("activate_sunset"))
				{
					refreshColors();
				}
				if(key.equals("texture_model"))
				{
					refreshTexture(DataHolder.GIRL);
					refreshVertices(DataHolder.GIRL);
				}
				if(key.equals("pref_key_sim_scroll"))
				{
					if(sharedPrefs.getBoolean("pref_key_sim_scroll", true))
						setSimScroll(true);
					else
						setSimScroll(false);
				}
				if(key.equals("camera_blur"))
				{
					String temp = preferences.getString("camera_blur", "1");
					sceneSetter.setBlur(temp);
//                    refreshTexture(DataHolder.GIRL);
//                    refreshTexture(DataHolder.ROOM);
//                    refreshTexture(DataHolder.TABLE);
                    refreshTexture(DataHolder.CITY);
//                    refreshTexture(DataHolder.SKY);
//                    refreshTexture(DataHolder.BUILDING);
					//reload images
				}
			}
		};
		mPrefs.registerOnSharedPreferenceChangeListener(prefListener);
	}

	public void setSimScroll(boolean mSimScroll)
	{
		simScroll = mSimScroll;
		if(simScroll)
		{
			if(portraitOrientation)
			{
				eyeZ = -2.7f;
				offsetDifferenceX = getOffsetDifference(0);
			}
			else
			{
				eyeZ = -3.4f;
			}
		}
		else
		{
			if(portraitOrientation)
			{
				eyeZ = -2.7f;
			}
			else
			{
				eyeZ = -3.4f;
			}

		}
	}

	public boolean portraitOrientation, simScroll;

	float ratio;
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {

		// We need to know the current width and height.
		mScreenWidth = width;
		mScreenHeight = height;
		GLES20.glViewport(0, 0, width, height);

		if(height > width) //portrait
		{
			portraitOrientation = true;
			ratio = (float) width / height;
//			Matrix.frustumM(mtrxProjection, 0, -ratio, ratio, -1, 1, 2, 10);
			Matrix.perspectiveM(mtrxProjection, 0, 53.0f, ratio, 2, 10);
			eyeZ = -3.0f;
			eyeX = 0.0f;
			lookX = eyeX;
			Matrix.setLookAtM(mtrxView, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
			offsetDifferenceX = getOffsetDifference(0);
			offsetDifferenceY = getOffsetDifference(1);
			setEyeY(0);
		}
		else //landscape
		{
			portraitOrientation = false;
			ratio = (float) width / height;
//			Matrix.frustumM(mtrxProjection, 0, -1, 1, -ratio, ratio, 2, 10);
			Matrix.perspectiveM(mtrxProjection, 0, 43.0f, ratio, 2, 10);
			eyeZ = -3.0f;
			eyeX = 0.0f;
			lookX = eyeX;
			Matrix.setLookAtM(mtrxView, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
			offsetDifferenceX = getOffsetDifference(0);
			offsetDifferenceY = getOffsetDifference(1);
			setEyeY(0);
//			resetEyeY(-0.2f);
		}
	}

	private void setVertices(int rotation)
	{

	}

	private float getOffsetDifference(int choice)
	{
		if (choice == 0) //x
		{
			if (portraitOrientation)
			{
				if (simScroll)
				{
					return 3.0f;
				} else
				{
					return 3.8f;
				}
			} else //landscape orientation
			{
				if (simScroll)
				{
					return 0.3f;
				} else
				{
					return 1.0f;
				}
			}
		} else //y
		{
			if (portraitOrientation)
			{
					if (simScroll)
					{
						return 0.1f;
					} else
					{
						return 0.8f;
					}
			} else //landscape orientation
			{
					if (simScroll)
					{
						return 0.3f;
					} else
					{
						return 0.5f;
					}
			}
		}
	}



	// Position the eye in front of the origin.
	float eyeX = 0.0f;
	float eyeY = 0.0f;
	float eyeZ = -3.0f;
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

	public void setOpacity(float newOpacity)
	{
		sceneSetter.setOpacity(newOpacity);
//		if(girlMid != null)
		if(girl != null)
			changeColor("girl");
	}

	float skyXOffset = 0.0f;

	public void refreshVertices(int sprite)
	{
		if(sprite == DataHolder.GIRL)
		{
			girl.setVertices(sceneSetter.getSpriteVertices(DataHolder.GIRL));
		}
	}

	public void refreshColors()
	{
			table.setColor(sceneSetter.getSpriteColor("table"));
//			girlMid.setColor(sceneSetter.getSpriteColor("girlMid"));
			girl.setColor(sceneSetter.getSpriteColor("girl"));
			room.setColor(sceneSetter.getSpriteColor("room"));
//			city.setColor(sceneSetter.getSpriteColor("city"));
//			sky.setColor(sceneSetter.getSpriteColor("sky"));
	}

	public void changeColor(String sprite)
	{
		if(sprite.equals("girl"))
		{
//			girlMid.setColor(sceneSetter.getSpriteColor("girlMid"));
			girl.setColor(sceneSetter.getSpriteColor("girl"));
		}
		else if(sprite.equals("table"))
		{
			table.setColor(sceneSetter.getSpriteColor("table"));
		}else
		{
			room.setColor(sceneSetter.getSpriteColor("room"));
		}
	}


	public void refreshTexture(int texture)
	{
		if(preferences != null && girl != null)// girlFront != null && girlMid != null && girlBack != null)
		{
            Bitmap bmp = sceneSetter.getTexture(DataHolder.GIRL);
			//Texture 0 The Room
//            if(texture == DataHolder.ROOM)
//            {
//                bmp = sceneSetter.getTexture(DataHolder.ROOM);
//                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[0]);
//                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
//                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
//                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
//                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
//            }

			//texture 1 The Girl
            if(texture == DataHolder.GIRL)
            {
                bmp = sceneSetter.getTexture(DataHolder.GIRL);
                GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[1]);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
            }

//			//texture 2 The Table
//            else if(texture == DataHolder.TABLE)
//            {
//                bmp = sceneSetter.getTexture(DataHolder.TABLE);//BitmapFactory.decodeResource(mContext.getResources(), R.drawable.table);
//                GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
//                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[2]);
//                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
//                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
//                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
//                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
//            }

			//texture 3 The City
//            else if(texture == DataHolder.CITY)
//            {
//                bmp = sceneSetter.getTexture(DataHolder.CITY);
//                GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
//                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[3]);
//                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
//                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
//                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
//                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
//            }
//
//			//texture 4 The Sky
//            else if(texture == DataHolder.SKY)
//            {
//                bmp = sceneSetter.getTexture(DataHolder.SKY);
//                GLES20.glActiveTexture(GLES20.GL_TEXTURE4);
//                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[4]);
//                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
//                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
//                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
//                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
//            }
//
//			//texture 7 The Building
//            else if(texture == DataHolder.BUILDING)
//            {
//                bmp = sceneSetter.getTexture(DataHolder.BUILDING);
//                GLES20.glActiveTexture(GLES20.GL_TEXTURE7);
//                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[7]);
//                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
//                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
//                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
//                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
//            }


			bmp.recycle();
		}
	}

	private void loadTextures()
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
		texturenames = new int[20];
		GLES20.glGenTextures(20, texturenames, 0);

		//texture 0
		// Temporary create a bitmap
		Bitmap bmp = sceneSetter.getTexture(DataHolder.ROOM);
		// Bind texture to texturename
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[0]);
		// Set filtering
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		//clamp texture to edge of shape
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		// Load the bitmap into the bound texture.
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

		//texture 1
		bmp = sceneSetter.getTexture(DataHolder.GIRL);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[1]);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

		//texture 2 Table
		bmp = sceneSetter.getTexture(DataHolder.TABLE);//BitmapFactory.decodeResource(mContext.getResources(), R.drawable.table);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[2]);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

		bmp = BlurBuilder.blur(mContext, bmp, 10.5f, 0.2f);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE12);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[12]);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

		//texture 3 City
		bmp = sceneSetter.getTexture(DataHolder.CITY);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[3]);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

		bmp = BlurBuilder.blur(mContext, bmp, 10.5f, 0.4f);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE13);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[13]);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

		//Texture 4 Sky
		bmp = sceneSetter.getTexture(DataHolder.SKY);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE4);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[4]);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
		//Texture 14 Blurred Sky
		bmp = BlurBuilder.blur(mContext, bmp, 10.5f, 0.2f);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE14);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[14]);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

		//Texture 7 Building
		bmp = sceneSetter.getTexture(DataHolder.BUILDING);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE7);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[7]);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

		bmp = BlurBuilder.blur(mContext, bmp, 9.5f, 0.4f);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE17);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[17]);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

		bmp.recycle();
	}

	@Override
	public void onDrawFrame(GL10 unused) {
		skyXOffset += 0.001f;
		if(skyXOffset > 3.0f)
			skyXOffset = 0.0f;

		long endTime = System.currentTimeMillis();
		long dt = endTime - startTime;
		if(dt < 10)
		{
			try
			{
				Thread.sleep(10 - dt);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		startTime = System.currentTimeMillis();


		// Set the camera position (View matrix)
//		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		//increase opacity from zero
		if (sceneSetter.getOpacity() < 1.0f) {
			sceneSetter.setOpacity(sceneSetter.getOpacity() + 0.04f);
			changeColor("girl");
		}

//		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix");

//		Log.d("onDrawFrame", "eyeX: " + eyeX);
//		Matrix.setLookAtM(mtrxView, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);


		// Calculate the projection and view transformation
		//Draw the sky
		Matrix.setIdentityM(mModelMatrix, 0);
//		Matrix.translateM(mModelMatrix, 0, eyeX * 0.7f + skyXOffset, 0.0f, 1.0f);
		Matrix.translateM(mModelMatrix, 0, xOffset * 0.1f + skyXOffset, 0.0f, 1.0f);
		Matrix.multiplyMM(scratch0, 0, mtrxView, 0, mModelMatrix, 0);
		Matrix.multiplyMM(scratch0, 0, mtrxProjection, 0, scratch0, 0);
		sky.draw(scratch0, uvBuffer, getTextureIndex(DataHolder.SKY));
		//Draw the City
		Matrix.setIdentityM(mModelMatrix, 0);
//		Matrix.translateM(mModelMatrix, 0, eyeX * 0.7f, 0.0f, 1.0f);
		Matrix.translateM(mModelMatrix, 0, xOffset * 0.1f, 0.0f, 1.0f);
		Matrix.multiplyMM(scratch1, 0, mtrxView, 0, mModelMatrix, 0);
		Matrix.multiplyMM(scratch1, 0, mtrxProjection, 0, scratch1, 0);
		city.draw(scratch1, uvBuffer, getTextureIndex(DataHolder.CITY));
		//draw the building
		Matrix.setIdentityM(mModelMatrix, 0);
//		Matrix.translateM(mModelMatrix, 0, 0.0f, 0.3f, 1.0f);
		Matrix.translateM(mModelMatrix, 0, xOffset * 0.3f, 0.3f, 1.0f);
		Matrix.multiplyMM(scratch2, 0, mtrxView, 0, mModelMatrix, 0);
		Matrix.multiplyMM(scratch2, 0, mtrxProjection, 0, scratch2, 0);
		building.draw(scratch2, uvBuffer, getTextureIndex(DataHolder.BUILDING));
		//draw the room
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.translateM(mModelMatrix, 0, xOffset * 0.95f, 0.0f, 1.0f);
		Matrix.multiplyMM(mMVPMatrix, 0, mtrxView, 0, mModelMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mtrxProjection, 0, mMVPMatrix, 0);
		room.draw(mMVPMatrix, uvBuffer, 0);
		//draw the girl
		float[] scratch3 = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.translateM(mModelMatrix, 0, xOffset * sceneSetter.getGirlOffset(), 0.0f, 1.0f);
		Matrix.multiplyMM(scratch3, 0, mtrxView, 0, mModelMatrix, 0);
		Matrix.multiplyMM(scratch3, 0, mtrxProjection, 0, scratch3, 0);
//		girl.draw(scratch1, uvBuffer, 1);
		if(sceneSetter.getGirlRender() == 0)
		{            //don't render
		}else
			girl.draw(scratch3, uvBuffer, 1);
//		}else if(sceneSetter.getGirlRender() == 1)
//		{	girlFront.draw(scratch3, uvBuffer, 1);
//		}else if(sceneSetter.getGirlRender() == 2)
//		{	girlMid.draw(scratch3, uvBuffer, 5);
//		}else if(sceneSetter.getGirlRender() == 3)
//		{	girlBack.draw(scratch3, uvBuffer, 6);
//		}

		//draw the table
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.translateM(mModelMatrix, 0, xOffset, 0.3f, 1.0f);
		Matrix.multiplyMM(this.scratch3, 0, mtrxView, 0, mModelMatrix, 0);
		Matrix.multiplyMM(this.scratch3, 0, mtrxProjection, 0, this.scratch3, 0);
		table.draw(this.scratch3, uvBuffer, getTextureIndex(DataHolder.TABLE));

	}


	private int getTextureIndex(int texture)
	{
		if(texture == DataHolder.CITY)
		{
			if(preferences.getString("camera_blur", "none").equals("none"))
			{
				return 3;
			}
			return 13;
		}
		else if(texture == DataHolder.BUILDING)
		{
			if(preferences.getString("camera_blur", "none").equals("none"))
			{
				return 7;
			}
			return 17;
		}
		else if(texture == DataHolder.SKY)
		{
			if(preferences.getString("camera_blur", "none").equals("none"))
			{
				return 4;
			}
			return 14;
		}
		else if(texture == DataHolder.TABLE)
		{
			if(preferences.getString("camera_blur", "none").equals("none"))
			{
				return 2;
			}
			return 12;
		}
		return 0;
	}
}
