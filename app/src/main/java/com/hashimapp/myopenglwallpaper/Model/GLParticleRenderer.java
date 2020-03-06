package com.hashimapp.myopenglwallpaper.Model;

// Book:      OpenGL(R) ES 2.0 Programming Guide
// Authors:   Aaftab Munshi, Dan Ginsburg, Dave Shreiner
// ISBN-10:   0321502795
// ISBN-13:   9780321502797
// Publisher: Addison-Wesley Professional
// URLs:      http://safari.informit.com/9780321563835
//            http://www.opengles-book.com
//

// ParticleSystem
//
//    This is an example that demonstrates rendering a particle system
//    using a vertex shader and point sprites.
//


        import java.nio.ByteBuffer;
        import java.nio.ByteOrder;
        import java.nio.FloatBuffer;
        import java.util.Random;

        import javax.microedition.khronos.egl.EGLConfig;
        import javax.microedition.khronos.opengles.GL10;

        import android.content.Context;
        import android.opengl.GLES20;
        import android.opengl.Matrix;
        import android.os.SystemClock;
        import android.util.Log;

        import com.hashimapp.myopenglwallpaper.R;


public class GLParticleRenderer
{
    private int mSamplerLoc;

    private int iPosition, mtrxHandle, iColor, iMove, iTimes, iLife, iAge, iTexCoord, iTexCoordPointSize;


    float xScrollOffset, xAccelOffset, yAccelOffset, yOrientationOffset, xZoomScale,
            xTouchScale, xMotionScale, yZoomScale, yTouchScale, yMotionScale;
    float motionOffsetFocalPoint;
    // Texture handle
    private int mTextureId;

    // Update time
    private float mTime;
    private float textureSize;
    private long mLastTime;

    // Additional member variables
    private int mWidth;
    private FloatBuffer mParticles;
    private Context mContext;
    private float spriteXPosOffset;

    private final int NUM_PARTICLES = 5000;
    private final int PARTICLE_SIZE = 13;
    private float zVertice = 0.0f;

    private final float[] fVertices = new float[NUM_PARTICLES * PARTICLE_SIZE];

    ///
    // Constructor
    //
    public GLParticleRenderer(Context context)
    {
        mContext = context;
    }

    public int GetQueuedBitmapID(){
        return R.drawable.layer00;
    }

    public void SetTextureData(TextureData texData){
        GLTextureIndex = texData.GLTextureIndex;
        textureName = texData.textureName;
        textureNameIndex = texData.textureNameIndex;
    }

    int GLTextureIndex;
    int textureName;
    int textureNameIndex;

    ///
    // Initialize the shader and program object
    //
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        // Load the shaders and get a linked program object

        riGraphicTools.sp_Particle = GLES20.glCreateProgram();             // create empty OpenGL ES Program

        riGraphicTools.vsParticleID = riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER, riGraphicTools.vs_Particle);
        GLES20.glAttachShader(riGraphicTools.sp_Particle, riGraphicTools.vsParticleID);   // add the vertex shader to program

        riGraphicTools.fsParticleID = riGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER, riGraphicTools.fs_Particle);
        GLES20.glAttachShader(riGraphicTools.sp_Particle, riGraphicTools.fsParticleID); // add the fragment shader to program

        GLES20.glLinkProgram(riGraphicTools.sp_Particle);                  // creates OpenGL ES program executrees

        // Set our shader program
        GLES20.glUseProgram(riGraphicTools.sp_Particle);

        iPosition = GLES20.glGetAttribLocation(riGraphicTools.sp_Particle, "a_Position");
        mtrxHandle = GLES20.glGetUniformLocation(riGraphicTools.sp_Particle, "uMVPMatrix");
        iColor = GLES20.glGetAttribLocation(riGraphicTools.sp_Particle, "a_color");
        iMove = GLES20.glGetAttribLocation(riGraphicTools.sp_Particle, "a_move");
        iTimes = GLES20.glGetUniformLocation(riGraphicTools.sp_Particle, "a_time");
        iLife = GLES20.glGetAttribLocation(riGraphicTools.sp_Particle, "a_life");
        iAge = GLES20.glGetAttribLocation(riGraphicTools.sp_Particle, "a_age");
        iTexCoord = GLES20.glGetAttribLocation(riGraphicTools.sp_Particle, "TextureCoordIn");
        iTexCoordPointSize = GLES20.glGetUniformLocation(riGraphicTools.sp_Particle, "a_stufff");


//        // Get the attribute locations
//        mLifetimeLoc = GLES20.glGetAttribLocation(riGraphicTools.sp_Particle, "a_lifetime");
//        mStartPositionLoc = GLES20.glGetAttribLocation(riGraphicTools.sp_Particle, "a_startPosition" );
//        mEndPositionLoc = GLES20.glGetAttribLocation(riGraphicTools.sp_Particle, "a_endPosition" );
//
//        // Get the uniform locations
//        mTimeLoc = GLES20.glGetUniformLocation ( riGraphicTools.sp_Particle, "u_time" );
//        mElapsedTimeLoc = GLES20.glGetUniformLocation ( riGraphicTools.sp_Particle, "v_elapsedTime" );
//        mCenterPositionLoc = GLES20.glGetUniformLocation ( riGraphicTools.sp_Particle, "u_centerPosition" );
//        mColorLoc = GLES20.glGetUniformLocation ( riGraphicTools.sp_Particle, "u_color" );
        mSamplerLoc = GLES20.glGetUniformLocation ( riGraphicTools.sp_Particle, "s_texture" );

        GLES20.glClearColor(0.3f, 0.4f, 0.6f, 0);

        // Fill in particle data array
        Random gen = new Random();

        float inc = 1.0f/NUM_PARTICLES;
        float vel = 4.0f;
        int angle;
        float z = 0.0f;
        for ( int i = 0; i < NUM_PARTICLES; i++ )
        {
            vel += inc;
            angle = (int) (gen.nextFloat() * 360f);
            z -= inc;
            //x,y,z
            fVertices[i*PARTICLE_SIZE + 0] = 0;
            fVertices[i*PARTICLE_SIZE + 1] = 0;
            fVertices[i*PARTICLE_SIZE + 2] = z;
            //r,g,b
            fVertices[i*PARTICLE_SIZE + 3] = gen.nextFloat();
            fVertices[i*PARTICLE_SIZE + 4] = gen.nextFloat();
            fVertices[i*PARTICLE_SIZE + 5] = gen.nextFloat();
            //dx,dy,dz
            fVertices[i*PARTICLE_SIZE + 6] = (float) (Math.cos(Math.toRadians(angle)) * vel);
            fVertices[i*PARTICLE_SIZE + 7] = (float) (Math.sin(Math.toRadians(angle)) * vel);
            fVertices[i*PARTICLE_SIZE + 8] =0.0f;

            //life
            fVertices[i*PARTICLE_SIZE + 9] = rnd(1.0f, 2.0f);
            //age
            fVertices[i*PARTICLE_SIZE + 10] = rnd(0.01f, 0.1f);

            //texCoord
            fVertices[i*PARTICLE_SIZE + 11] = 0.5f;
            fVertices[i*PARTICLE_SIZE + 12] = 0.0f;

//            fVertices[i * 7 + 6] = -1.0f;// generator.nextFloat() * 0.25f - 0.125f;
        }
        mParticles = ByteBuffer.allocateDirect(fVertices.length * 5)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mParticles.put(fVertices).position(0);



        // Initialize time to cause reset on first update
        mTime = 1.0f;
    }

    public void SetXOffset(float xOffset)
    {
        //parallax motion determined by how "far" the sprite is from the camera
        float offsetMultiplier = (GetZVerticeInverse(zVertice));// * 0.9f + 0.1f; //range between 0.1 and 1.0f
        this.xScrollOffset = -1 //reverse movement
                * (xOffset) * offsetMultiplier  //screen offset position (0.0-1.0) multiplied by Z position parallax effect
                + spriteXPosOffset; //centers the image depending on whether it's landscape or portrait

        Log.d("Set offset", "x: " + xScrollOffset);

    }

    public void SetOrientation(float spriteXPosOffset, float touchScale, float motionScale)
    {
        float offsetMultiplier = (GetZVerticeInverse(zVertice)) * 0.9f + 0.1f;
        this.spriteXPosOffset = spriteXPosOffset * offsetMultiplier;
        SetTouchScale(touchScale);
        SetMotionScale(motionScale);
    }

    public void SetTouchScale(float touchScale)
    {
        xTouchScale = yTouchScale = touchScale * GetZVerticeInverse(zVertice);
    }

    public void SetMotionScale(float motionScale)
    {
        xMotionScale = yMotionScale = motionScale * Math.abs(motionOffsetFocalPoint - zVertice);
    }

    public static float rnd(float min, float max) {
        float fRandNum = (float)Math.random();
        return min + (max - min) * fRandNum;
    }

    private float GetZVerticeInverse(float z)
    {
        return (float) Math.abs(1.0 - zVertice);
    }

    private void update()
    {
        if (mLastTime == 0)
            mLastTime = SystemClock.uptimeMillis();
        long curTime = SystemClock.uptimeMillis();
        long elapsedTime = curTime - mLastTime;
        float deltaTime = elapsedTime / 2000.0f;
        mLastTime = curTime;

        mTime += 0.002f;

        textureSize = 0.5f;
//        if ( mTime >= 1.0f )
//        {
//            Random generator = new Random();
//            float[] centerPos = new float[3];
//            float[] color = new float[4];
//
//            mTime = 0.1f;
//
//            // Pick a new start location and color
//            centerPos[0] = 0.0f;// generator.nextFloat() * 1.0f - 0.5f;
//            centerPos[1] = 0.0f;// generator.nextFloat() * 1.0f - 0.5f;
//            centerPos[2] = 0.0f;//generator.nextFloat() * 1.0f - 0.5f;
//
//            GLES20.glUniform3f ( mCenterPositionLoc, centerPos[0], centerPos[1], centerPos[2]);
//
//            // Random color
//            color[0] = generator.nextFloat() * 0.5f + 0.5f;
//            color[1] = generator.nextFloat() * 0.5f + 0.5f;
//            color[2] = generator.nextFloat() * 0.5f + 0.5f;
//            color[3] = 0.5f;
//
//            GLES20.glUniform4f ( mColorLoc, color[0], color[1], color[2], color[3] );
//        }

        // Load uniform time variable
        GLES20.glUniform1f ( iTimes, mTime );
        GLES20.glUniform1f(iTexCoordPointSize, textureSize);
//        GLES20.glUniform1f ( mElapsedTimeLoc, deltaTime );
    }

    ///
    // Draw a triangle using the shader pair created in onSurfaceCreated()
    //
    public void onDrawFrame(GL10 glUnused, float[] mtrxView, float[] mtrxProjection,
                            float[] mModelMatrix, float[] mMVPMatrix)
    {
        update();
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, xScrollOffset + xAccelOffset,
                yAccelOffset + yOrientationOffset, 1.0f);
//        Matrix.scaleM(mModelMatrix, 0, xZoomScale + xTouchScale + xMotionScale,
//                yZoomScale + yTouchScale + yMotionScale, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mtrxView, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mtrxProjection, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(mtrxHandle, 1, false, mMVPMatrix, 0);
        // Set the viewport
//        GLES20.glViewport ( 0, 0, mWidth, mHeight );

        // Clear the color buffer
//        GLES20.glClear ( GLES20.GL_COLOR_BUFFER_BIT );

        // Load the vertex attributes
        mParticles.position(0);
        GLES20.glVertexAttribPointer ( iPosition, 3, GLES20.GL_FLOAT,
                false, PARTICLE_SIZE * 4, mParticles );

        mParticles.position(3);
        GLES20.glVertexAttribPointer(iColor, 3, GLES20.GL_FLOAT, false,
                PARTICLE_SIZE * 4, mParticles);
        GLES20.glEnableVertexAttribArray(iColor);

        mParticles.position(6);
        GLES20.glVertexAttribPointer(iMove, 3, GLES20.GL_FLOAT, false,
                PARTICLE_SIZE * 4, mParticles);
        GLES20.glEnableVertexAttribArray(iMove);

        mParticles.position(9);
        GLES20.glVertexAttribPointer(iLife, 1, GLES20.GL_FLOAT, false,
                PARTICLE_SIZE * 4, mParticles);
        GLES20.glEnableVertexAttribArray(iLife);

        mParticles.position(10);
        GLES20.glVertexAttribPointer(iAge, 1, GLES20.GL_FLOAT, false,
                PARTICLE_SIZE * 4, mParticles);
        GLES20.glEnableVertexAttribArray(iAge);

        mParticles.position(11);
        GLES20.glVertexAttribPointer(iTexCoord, 2, GLES20.GL_FLOAT, false,
                PARTICLE_SIZE * 4, mParticles);
        GLES20.glEnableVertexAttribArray(iTexCoord);




        // Blend particles
//        GLES20.glEnable ( GLES20.GL_BLEND );
//        GLES20.glBlendFunc ( GLES20.GL_SRC_ALPHA, GLES20.GL_ONE );

        // Bind the texture
        GLES20.glActiveTexture(GLTextureIndex);
        GLES20.glBindTexture(GLTextureIndex, textureName);
        GLES20.glUniform1i(mSamplerLoc, textureNameIndex);


        GLES20.glDrawArrays( GLES20.GL_POINTS, 0, NUM_PARTICLES );
    }

    ///
    // Handle surface changes
    //
    public void onSurfaceChanged(GL10 glUnused, int width, int height)
    {
//        mWidth = width;
////        mHeight = height;
    }



}
