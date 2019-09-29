package com.hashimapp.myopenglwallpaper.Model;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.support.annotation.FloatRange;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL;

/**
 * Created by Blake Hashimoto on 8/15/2015.
 */
public class Sprite
{
    // Geometric variables
//    public static float vertices[];
    private static final float DEFAULT_BIAS = -1.00f;
    private static final float MIN_BIAS_MULTIPLIER = 1.0f;
    private static final float MAX_BIAS_MULTIPLER = 6.0f;
    private float maxBias = 3.5f;


    private static final float ZOOM_MAX = 1.20f;
    private static final float ZOOM_MIN = 1.0f;
    private static final float FADE_DURATION = 0.4f; //duration of fade in percentage

    public short indices[];
    public FloatBuffer vertexBuffer;
    public ShortBuffer drawListBuffer;
    public FloatBuffer colorBuffer;
    public FloatBuffer textureVerticeBuffer;
    private int GLTextureIndex;
    public static float colors[];
    private float alpha;

    private int mColorHandle;
    private int mPositionHandle;
    private int mTexCoordLoc;
    private int mtrxHandle;
    private int mSamplerLoc;
    private int biasHandle;
    private int alphaHandle;

    private float spriteXPosOffset;

    private boolean textureSwapRequired;
    private boolean textureVerticeSwapRequired;

    private int textureName;
    private int textureNameIndex;
    private int currentBitmapID;
    public int changeTextureVertices = SpriteData.NO_CHANGE;
    private float[] currentTextureVertices;
    public SpriteData spriteData;
    float xScrollOffset = 0;
    float xAccelOffset = 0;
    float yAccelOffset = 0;

    float xZoomScale, xMotionScale, xTouchScale;
    float yZoomScale, yMotionScale, yTouchScale = 1.0f;



    private int spriteKey;

    private float bias = 0.0f;


    public Sprite(SpriteData mSpriteData, int spriteKey, int scene)
    {
        xZoomScale = yZoomScale = 1.0f;
        alpha = 1.0f;
        spriteData = mSpriteData;
//        mvpMatrix = new float[16];
        colors = spriteData.getColor(TimeTracker.DAY, 100);
        indices = spriteData.GetIndices();
//        currentBitmapID = spriteData.GetBitmapID();

        setColor(colors);
        setVertices(spriteData.getShapeVertices(true, false));
        setTextureVertices(spriteData.GetTextureVertices(scene));
        setIndices(indices);
        this.spriteKey = spriteKey;
    }

    public void OffsetChanged(float xOffset, boolean PortraitOrientation)
    {
        //parallax motion determined by how "far" the sprite is from the camera
        float offsetMultiplier = (spriteData.GetZVerticeInverse());// * 0.9f + 0.1f; //range between 0.1 and 1.0f
//        if (PortraitOrientation)
//        {
        this.xScrollOffset = -1 //reverse movement
                * xOffset * offsetMultiplier //screen offset position (0.0-1.0) multiplied by Z position parallax effect
                + spriteXPosOffset; //centers the image depending on whether it's landscape or portrait
//        } else
//        {
//            this.xScrollOffset = -1 * ((xOffset * offsetMultiplier) + (-0.15f * offsetMultiplier));
//        }
    }

    public void SetSpriteMembers(int colorHandle, int positionHandle, int texCoordLoc, int mtrxHandle,
                                 int samplerLoc, int biasHandle, int alphaHandle)
    {
        mColorHandle = colorHandle;
        mPositionHandle = positionHandle;
        mTexCoordLoc = texCoordLoc;
        this.mtrxHandle = mtrxHandle;
        mSamplerLoc = samplerLoc;
        this.biasHandle = biasHandle;
        this.alphaHandle = alphaHandle;
    }


    public void SensorChanged(float xOffset, float yOffset)
    {
        float offsetMultiplier = (spriteData.GetZVertice()) * 2.0f; // z will be a vertice between 0.0 and -1.0f
        xAccelOffset = xOffset * offsetMultiplier;
        yAccelOffset = yOffset * offsetMultiplier;
    }

    public void SetOrientation(boolean portrait, boolean motionOffset, float spriteXPosOffset, float touchScale, float motionScale)
    {
        float offsetMultiplier = (spriteData.GetZVerticeInverse()) * 0.9f + 0.1f;
        this.spriteXPosOffset = spriteXPosOffset * offsetMultiplier;
        setVertices(spriteData.getShapeVertices(portrait, motionOffset));
        xTouchScale = yTouchScale = touchScale * spriteData.GetZVerticeInverse();
        xMotionScale = yMotionScale = motionScale * spriteData.GetZVertice();


    }


    public void SetTime(int time, int phasePercentage)
    {
        colors = spriteData.getColor(time, phasePercentage);
        setColor(colors);
    }

    public void SetFocalPoint(float currentFocalPoint)
    {
        //get character distance from the 'in focus' camera depth
        float distanceFromFocalPoint = Math.abs(currentFocalPoint - spriteData.GetZVertice());
        //translate that depth to the bias
        //todo: adjust bias based on screen resolution
        Log.d("blur", "max bias while setting: " + maxBias);
        bias = (distanceFromFocalPoint * maxBias + DEFAULT_BIAS);
    }

    public void SetTargetFocalPoint(@FloatRange(from = 0.0f, to = 1.0f) float targetMultiplier)
    {
        maxBias = targetMultiplier * (MAX_BIAS_MULTIPLER - MIN_BIAS_MULTIPLIER) + MIN_BIAS_MULTIPLIER;
        Log.d("blur", "max bias: " + maxBias);
    }

    ///zoom percent is some value between 0.0 and 1.0
    public void SetZoomPoint(float zoomPercent)
    {
        //figure out how much the size is going to be modified
        Log.d("zoom", "zoomPercent: " + zoomPercent);

        float sineZoomPercent = (float) Math.sin((zoomPercent * Math.PI / 2)); //convert to sine curve
        float zPositionModifier = (spriteData.GetZVerticeInverse()) * 0.9f + 0.1f; //zoom less if further from camera
        xZoomScale = yZoomScale = ZOOM_MAX - (sineZoomPercent * zPositionModifier * (ZOOM_MAX - ZOOM_MIN));
        Log.d("zoom", "xscale: " + xZoomScale);

    }


    public void turnOffBlur()
    {
        bias = DEFAULT_BIAS;
    }


    public void SetFade(float progress, boolean fadingIn)
    {
        int length = colors.length;
        float startPoint;
        if (fadingIn)
        {
            startPoint = Math.abs(spriteData.GetZVertice()) * (1.0f - FADE_DURATION);
        } else
        {
            startPoint = Math.abs(spriteData.GetZVerticeInverse()) * (1.0f - FADE_DURATION);
        }
        float endPoint = startPoint + FADE_DURATION;

        float spriteProgress;
        if (progress <= startPoint)
        {
            spriteProgress = 0.0f;
        } else if (progress >= endPoint)
        {
            spriteProgress = 1.0f;
        } else
        {
            spriteProgress = (progress - startPoint) / FADE_DURATION;
        }

        //fade in closer sprites first
        float newAlpha;
        if (fadingIn)
        {
            newAlpha = spriteProgress;
        } else
        {
            newAlpha = 1.0f - spriteProgress ;
        }
        Log.d("alpha", "new alpha: " + newAlpha);

        alpha = newAlpha;
    }


    public int GetNextBitmapID(int bitmapSize, int scene)
    {
        int newBitmapID = spriteData.GetBitmapID(bitmapSize, scene);
        if (newBitmapID != currentBitmapID)
        {
            currentBitmapID = newBitmapID;
            textureSwapRequired = true;
            return currentBitmapID;
        }
        return -1;
    }

    public boolean PrepNextTextureVertices(int scene){

        float[] newTextureVertices = spriteData.GetTextureVertices(scene);

        if (newTextureVertices != currentTextureVertices)
        {
            currentTextureVertices = newTextureVertices;
            textureSwapRequired = true;
            return true;
        }
        return  false;
    }

    /*
    set texture vertices to currentTextureVertices. May have been changed by PrepNextTextureVertices
    */
    public void SetNextTextureVertices()
    {
        setTextureVertices(currentTextureVertices);
    }

    public void SetTextureData(TextureData textureData)
    {
        GLTextureIndex = textureData.GLTextureIndex;
        textureName = textureData.textureName;
        textureNameIndex = textureData.textureNameIndex;
    }


    public int getTextureName()
    {
        return textureName;
    }

    public boolean TextureSwapRequired()
    {
        return textureSwapRequired;
    }

    public void SetTextureSwapRequired(boolean swapping)
    {
        textureSwapRequired = swapping;
    }

    public boolean IsEssentialLayer(){
        return spriteData.IsEssentialLayer();
    }


    public void draw(float[] mtrxView, float[] mtrxProjection, float[] mModelMatrix, float[] mMVPMatrix)
    {
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, xScrollOffset + xAccelOffset, yAccelOffset, 1.0f);
        Matrix.scaleM(mModelMatrix, 0, xZoomScale + xTouchScale + xMotionScale, yZoomScale + yTouchScale + xMotionScale, 1.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mtrxView, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mtrxProjection, 0, mMVPMatrix, 0);

        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, true, 0, colorBuffer);

        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer);

        // Get handle to texture coordinates location
        GLES20.glEnableVertexAttribArray(mTexCoordLoc);
        GLES20.glVertexAttribPointer(mTexCoordLoc, 2, GLES20.GL_FLOAT, false, 0, textureVerticeBuffer);

        GLES20.glUniformMatrix4fv(mtrxHandle, 1, false, mMVPMatrix, 0);

        // Set the sampler texture unit to x, where we have saved the texture.
        GLES20.glActiveTexture(GLTextureIndex);
        GLES20.glBindTexture(GLTextureIndex, textureName);
        GLES20.glUniform1i(mSamplerLoc, textureNameIndex);

        GLES20.glUniform1f(biasHandle, bias);
        GLES20.glUniform1f(alphaHandle, alpha);

        // Draw the triangle
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

//        // Disable vertex array
//        GLES20.glDisableVertexAttribArray(mPositionHandle);
//        GLES20.glDisableVertexAttribArray(mColorHandle);
//
//        if (textureName >= 0)
//            GLES20.glDisableVertexAttribArray(mTexCoordLoc);
    }


    private void setColor(float[] textureColor)
    {
        ByteBuffer cb = ByteBuffer.allocateDirect(textureColor.length * 4);
        cb.order(ByteOrder.nativeOrder());
        colorBuffer = cb.asFloatBuffer();
        colorBuffer.put(textureColor);
        colorBuffer.position(0);
    }

    private void setVertices(float[] vertices)
    {
        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
    }

    private void setIndices(short[] indices)
    {
        ByteBuffer dlb = ByteBuffer.allocateDirect(indices.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(indices);
        drawListBuffer.position(0);
    }

    private void setTextureVertices(float[] vertices)
    {
        // The texture buffer
        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        textureVerticeBuffer = bb.asFloatBuffer();
        textureVerticeBuffer.put(vertices);
        textureVerticeBuffer.position(0);
    }
}
