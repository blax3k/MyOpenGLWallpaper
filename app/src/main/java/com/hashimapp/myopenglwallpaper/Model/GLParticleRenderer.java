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

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.support.annotation.FloatRange;
import android.util.Log;


public class GLParticleRenderer
{
    private static final float DEFAULT_BIAS = -1.00f;
    private static final float MIN_BIAS_MULTIPLIER = 1.0f;
    private static final float MAX_BIAS_MULTIPLER = 6.0f;
    private float maxBias = 3.5f;

    private final int PARTICLE_COUNT = 100;
    private final int PARTICLE_SIZE = 14;


    private int mSamplerLoc;

    private int iPosition, mtrxHandle, iColor, iMove, iTimes, iLife, iAge,
            iTexCoord, iTexCoordPointSize, iSize, iAlpha, iBias;


    float xScrollOffset, xAccelOffset, yAccelOffset, yOrientationOffset, xZoomScale,
            xTouchScale, xMotionScale, yZoomScale, yTouchScale, yMotionScale;
    float motionOffsetFocalPoint;
    // Texture handle
    private int textureID;

    // Update time
    private float mTime;
    private float textureSize;
    private long mLastTime;

    // Additional member variables
    private int mWidth;
    private FloatBuffer mParticles;
    private Context mContext;
    private float spriteXPosOffset;
    private float _bias;

    private float zVertice = 0.0f;

    private ParticleData _particleData;


    ///
    // Constructor
    //
    public GLParticleRenderer(ParticleData pData)
    {
        _particleData = pData;
    }

    public int GetQueuedBitmapID()
    {
        return textureID;
    }

    public void SetTextureData(TextureData texData)
    {
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
    public void onSurfaceCreated(int currentScene)
    {
        textureID = _particleData.GetBitmapID(currentScene);
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
        iSize = GLES20.glGetAttribLocation(riGraphicTools.sp_Particle, "a_size");
        mSamplerLoc = GLES20.glGetUniformLocation(riGraphicTools.sp_Particle, "s_texture");
        iAlpha = GLES20.glGetUniformLocation(riGraphicTools.sp_Particle, "u_alpha");
        iBias = GLES20.glGetUniformLocation(riGraphicTools.sp_Particle, "u_bias");

        GLES20.glClearColor(0.3f, 0.4f, 0.6f, 0);

        // Fill in particle data array
        float[] particleData = _particleData.GetParticleData();
        mParticles = ByteBuffer.allocateDirect(particleData.length * 5)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mParticles.put(particleData).position(0);


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


    public void SetTime(int time, int phasePercentage)
    {
        float[] colors = _particleData.getColor(time, phasePercentage);
        SetColor(colors);
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


    public void SetFocalPoint(float currentFocalPoint)
    {
        //get character distance from the 'in focus' camera depth
        float distanceFromFocalPoint = Math.abs(currentFocalPoint - zVertice);
        Log.d("blur", "distance from Focal Point: " + distanceFromFocalPoint);
        //translate that depth to the bias
        //todo: adjust bias based on screen resolution
        _bias = (distanceFromFocalPoint * maxBias + DEFAULT_BIAS);
        Log.d("blur", "bias: " + _bias);
    }

    public void SetTargetFocalPoint(@FloatRange(from = 0.0f, to = 1.0f) float targetMultiplier)
    {
        maxBias = targetMultiplier * (MAX_BIAS_MULTIPLER - MIN_BIAS_MULTIPLIER) + MIN_BIAS_MULTIPLIER;
        Log.d("blur", "max bias: " + maxBias);
    }

    public void SetDefaultFocalPoint()
    {
        _bias = DEFAULT_BIAS;
    }


    private float GetZVerticeInverse(float z)
    {
        return (float) Math.abs(1.0 - zVertice);
    }

    private void update()
    {
        if (mLastTime == 0)
        {
            mLastTime = SystemClock.uptimeMillis();
        }
        long curTime = SystemClock.uptimeMillis();
        long elapsedTime = curTime - mLastTime;
        float deltaTime = elapsedTime / 2000.0f;
        mLastTime = curTime;

        mTime += 0.002f;

        textureSize = 0.5f;

        // Load uniform time variable
        GLES20.glUniform1f(iTimes, mTime);
        GLES20.glUniform1f(iTexCoordPointSize, textureSize);
        GLES20.glUniform1f(iBias, _bias);
//        GLES20.glUniform1f ( mElapsedTimeLoc, deltaTime );
    }

    ///
    // Draw a triangle using the shader pair created in onSurfaceCreated()
    //
    public void onDrawFrame(GL10 glUnused, float[] mtrxView, float[] mtrxProjection,
                            float[] mModelMatrix, float[] mMVPMatrix)
    {
        if(PARTICLE_COUNT == 0){
            return;
        }

        GLES20.glUseProgram(riGraphicTools.sp_Particle);
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
        GLES20.glVertexAttribPointer(iPosition, 3, GLES20.GL_FLOAT,
                false, PARTICLE_SIZE * 4, mParticles);

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

        mParticles.position(13);
        GLES20.glVertexAttribPointer(iSize, 1, GLES20.GL_FLOAT, false,
                PARTICLE_SIZE * 4, mParticles);
        GLES20.glEnableVertexAttribArray(iSize);


        // Blend particles
//        GLES20.glEnable ( GLES20.GL_BLEND );
//        GLES20.glBlendFunc ( GLES20.GL_SRC_ALPHA, GLES20.GL_ONE );

        // Bind the texture
        GLES20.glActiveTexture(GLTextureIndex);
        GLES20.glBindTexture(GLTextureIndex, textureName);
        GLES20.glUniform1i(mSamplerLoc, textureNameIndex);
        GLES20.glUniform1f(iAlpha, 1.0f);


        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, PARTICLE_COUNT);
    }

    ///
    // Handle surface changes
    //
    public void onSurfaceChanged(GL10 glUnused, int width, int height)
    {
//        mWidth = width;
////        mHeight = height;
    }


    private void SetColor(float[] color)
    {
        _particleData.SetColor(color);
        mParticles = ByteBuffer.allocateDirect(_particleData.GetParticleData().length * 5)
        .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mParticles.put(_particleData.GetParticleData()).position(0);

    }


}
