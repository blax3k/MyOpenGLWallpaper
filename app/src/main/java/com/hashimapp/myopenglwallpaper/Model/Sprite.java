package com.hashimapp.myopenglwallpaper.Model;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.support.annotation.FloatRange;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

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

    private static final float ZOOM_MAX = 1.30f;
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

    private float motionOffsetPivotPoint;

    private boolean fadeOutRequired;

    private int textureName;
    private int textureNameIndex;
    private int currentBitmapID;
    private float[] currentTextureVertices;
    private float[] currentVertices;
    public SpriteData spriteData;
    float xScrollOffset = 0;
    float yOrientationOffset = 0;
    float xAccelOffset = 0;
    float yAccelOffset = 0;

    float xZoomScale, xMotionScale, xTouchScale;
    float yZoomScale, yMotionScale, yTouchScale;

    private SpriteData queuedSpriteData;


    private float bias = 0.0f;


    public Sprite(SpriteData mSpriteData)
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
        setTextureVertices(spriteData.GetTextureVertices());
        setIndices(indices);
    }


    public void SensorChanged(float xOffset, float yOffset, boolean inverted)
    {
        float inversion = 1.0f;
        if (inverted)
        {
            inversion = -1.0f;
        }
        float offsetMultiplier = ((motionOffsetPivotPoint - spriteData.ZVertice())) * 2.0f; // z will be a vertice between 0.0 and -1.0f
        xAccelOffset = xOffset * offsetMultiplier * inversion;
        yAccelOffset = yOffset * offsetMultiplier * inversion;
    }


    //region Set Methods

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

    public void SetXOffset(float xOffset)
    {
        //parallax motion determined by how "far" the sprite is from the camera
        float offsetMultiplier = (spriteData.ZVerticeInverse());// * 0.9f + 0.1f; //range between 0.1 and 1.0f
//        if (PortraitOrientation)
//        {
        this.xScrollOffset = -1 //reverse movement
                * (xOffset) * offsetMultiplier //screen offset position (0.0-1.0) multiplied by Z position parallax effect
                + spriteXPosOffset; //centers the image depending on whether it's landscape or portrait
//        } else
//        {
//            this.xScrollOffset = -1 * ((xOffset * offsetMultiplier) + (-0.15f * offsetMultiplier));
//        }
    }

    public void SetYOffset(float yOffset)
    {
        float offsetMultiplier = (spriteData.ZVerticeInverse());
        yOrientationOffset = yOffset * offsetMultiplier;
    }

    public void SetTextureData(TextureData textureData)
    {
        GLTextureIndex = textureData.GLTextureIndex;
        textureName = textureData.textureName;
        textureNameIndex = textureData.TextureIndex;
    }


    public void SetOrientation(float spriteXPosOffset, float touchScale, float motionScale)
    {
        float offsetMultiplier = (spriteData.ZVerticeInverse() * 0.9f + 0.1f);
        this.spriteXPosOffset = spriteXPosOffset * offsetMultiplier;
        SetTouchScale(touchScale);
        SetMotionScale(motionScale);
    }

    public void SetTouchScale(float touchScale)
    {
        xTouchScale = yTouchScale = touchScale * spriteData.ZVerticeInverse();
    }

    public void SetMotionScale(float motionScale)
    {
        xMotionScale = yMotionScale = motionScale * Math.abs(motionOffsetPivotPoint - spriteData.ZVertice());
    }

    public void SetTime(int time, int phasePercentage)
    {
        colors = spriteData.getColor(time, phasePercentage);
        setColor(colors);
    }

    public void SetFocalPoint(float currentFocalPoint)
    {
        //get character distance from the 'in focus' camera depth
        float distanceFromFocalPoint = Math.abs(currentFocalPoint - spriteData.ZVertice());
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

    public void SetDefaultFocalPoint()
    {
        bias = DEFAULT_BIAS;
    }

    public void SetMotionOffsetPivotPoint(float point)
    {
        motionOffsetPivotPoint = point;
    }

    ///zoom percent is some value between 0.0 and 1.0
    public void SetZoomPoint(float zoomPercent)
    {
        //figure out how much the size is going to be modified
        Log.d("zoom", "zoomPercent: " + zoomPercent);

        if (zoomPercent > 1.0f)
        {
            zoomPercent = 1.0f;
        } else if (zoomPercent < 0.0f)
        {
            zoomPercent = 0.0f;
        }

        float zPositionModifier = (spriteData.ZVerticeInverse()) * 0.9f + 0.1f; //zoom less if further from camera
        float maxZoomPercent = (ZOOM_MAX - ZOOM_MIN) * zPositionModifier;

        float sineZoomPercentInverse = 1.0f - (float) Math.sin((zoomPercent * Math.PI / 2)); //convert to sine curve
        xZoomScale = yZoomScale = (ZOOM_MIN + maxZoomPercent * sineZoomPercentInverse);
        Log.d("zoom", "xscale: " + xZoomScale + " " + yZoomScale);
    }

    public void SetFade(float progress, boolean fadingIn)
    {
        float startPoint;
        if (fadingIn)
        {
            startPoint = Math.abs(spriteData.ZVertice()) * (1.0f - FADE_DURATION);
        } else
        {
            startPoint = Math.abs(spriteData.ZVerticeInverse()) * (1.0f - FADE_DURATION);
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
            newAlpha = 1.0f - spriteProgress;
        }
        Log.d("alpha", "new alpha: " + newAlpha);

        alpha = newAlpha;
    }

    //endregion



    /*
    Queues the next values for the next scene, and returns the 'severity' of the transition
     */
    public int QueueSceneData(SpriteData nSpriteData, int timePhase, int percentage, int weather)
    {
        if(nSpriteData == null)
        {
            queuedSpriteData = spriteData;
        }
        else
        {
            queuedSpriteData = nSpriteData;
        }

        if (queuedSpriteData.BitmapID != currentBitmapID && queuedSpriteData.BitmapID > 0 && currentBitmapID > 0)
        {
            return SceneSetter.FULL_FADE_TRANSITION;
        }
        if (!Arrays.equals(queuedSpriteData.ShapeVertices, currentVertices) ||
            !Arrays.equals(queuedSpriteData.TextureVertices, currentTextureVertices) ||
             queuedSpriteData.ZVertice() != spriteData.ZVertice())
        {
            //todo: apply local z vertice
            return SceneSetter.PARTIAL_FADE_TRANSITION;
        }
//        else if (!Arrays.equals(queuedSpriteSceneData.SpriteColorData.SetColor();, colors))
//        {
//            return SceneSetter.INSTANT_TRANSITION;
//        }
        return SceneSetter.NO_TRANSITION;
    }

    public int GetQueuedBitmapID()
    {
        if (queuedSpriteData != null &&
                queuedSpriteData.BitmapID != currentBitmapID &&
                queuedSpriteData.BitmapID != 0)
        {
            return queuedSpriteData.GetBitmapID();
        }
        return -1;
    }

    public String GetQueuedBitmapName()
    {
        if (queuedSpriteData != null &&
                queuedSpriteData.BitmapID != currentBitmapID &&
                queuedSpriteData.BitmapID != 0)
        {
            return queuedSpriteData.BitmapName;
        }
        return "";
    }

    public void DequeueSceneData()
    {
        if (queuedSpriteData != null)
        {
            spriteData = queuedSpriteData;
            currentBitmapID = spriteData.BitmapID;
            this.setVertices(queuedSpriteData.ShapeVertices);
            this.setTextureVertices(queuedSpriteData.TextureVertices);


            queuedSpriteData = null;
        }
    }

    public void DequeueColor(){
//        if(queuedSpriteSceneData.Colors != null){
//            this.setColor(queuedSpriteSceneData.Colors);
//        }
    }


    public int getTextureName()
    {
        return textureName;
    }


    public boolean TextureVerticeChange()
    {
        if(currentTextureVertices == null || queuedSpriteData == null || queuedSpriteData.TextureVertices == null){
            return false;
        }
        return !Arrays.equals(queuedSpriteData.TextureVertices, currentTextureVertices);
    }

    /*
    set texture vertices to currentTextureVertices. May have been changed by TextureVerticeChange
    */
    public void SetNextTextureVertices()
    {
        setTextureVertices(currentTextureVertices);
    }


    public boolean TextureSwapRequired()
    {
        if(currentBitmapID == 0 || queuedSpriteData == null || queuedSpriteData.BitmapID == 0){
            return false;
        }
        return currentBitmapID != queuedSpriteData.BitmapID;
    }

    public void SetFadeOutRequired(boolean swapping)
    {
        fadeOutRequired = swapping;
    }

    public boolean IsFadeOutRequired(){
        return fadeOutRequired;
    }

    public boolean IsEssentialLayer()
    {
        return spriteData.IsEssentialLayer();
    }

    public void draw(float[] mtrxView, float[] mtrxProjection, float[] mModelMatrix, float[] mMVPMatrix)
    {
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, xScrollOffset + xAccelOffset, yAccelOffset + yOrientationOffset, 1.0f);
        Matrix.scaleM(mModelMatrix, 0, xZoomScale + xTouchScale + xMotionScale, yZoomScale + yTouchScale + yMotionScale, 1.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mtrxView, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mtrxProjection, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(mtrxHandle, 1, false, mMVPMatrix, 0);

        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, true, 0, colorBuffer);

        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer);

        // Get handle to texture coordinates location
        GLES20.glEnableVertexAttribArray(mTexCoordLoc);
        GLES20.glVertexAttribPointer(mTexCoordLoc, 2, GLES20.GL_FLOAT, false, 0, textureVerticeBuffer);

        // Set the sampler texture unit to x, where we have saved the texture.
        GLES20.glActiveTexture(GLTextureIndex);
        GLES20.glBindTexture(GLTextureIndex, textureName);
        GLES20.glUniform1i(mSamplerLoc, textureNameIndex);

        GLES20.glUniform1f(biasHandle, bias);
        GLES20.glUniform1f(alphaHandle, alpha);

        // Draw the triangle
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
    }

    //region Private
    private void setColor(float[] textureColor)
    {
        if(textureColor != null){
            ByteBuffer cb = ByteBuffer.allocateDirect(textureColor.length * 4);
            cb.order(ByteOrder.nativeOrder());
            colorBuffer = cb.asFloatBuffer();
            colorBuffer.put(textureColor);
            colorBuffer.position(0);
        }
    }

    private void setVertices(float[] vertices)
    {
        if(vertices != null)
        {
            currentVertices = vertices;
            ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
            bb.order(ByteOrder.nativeOrder());
            vertexBuffer = bb.asFloatBuffer();
            vertexBuffer.put(vertices);
            vertexBuffer.position(0);
        }
    }

    private void setIndices(short[] indices)
    {
        if(indices != null)
        {
            ByteBuffer dlb = ByteBuffer.allocateDirect(indices.length * 2);
            dlb.order(ByteOrder.nativeOrder());
            drawListBuffer = dlb.asShortBuffer();
            drawListBuffer.put(indices);
            drawListBuffer.position(0);
        }
    }

    private void setTextureVertices(float[] vertices)
    {
        if(vertices != null)
        {
            currentTextureVertices = vertices;
            // The texture buffer
            ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
            bb.order(ByteOrder.nativeOrder());
            textureVerticeBuffer = bb.asFloatBuffer();
            textureVerticeBuffer.put(vertices);
            textureVerticeBuffer.position(0);
        }
    }


    //endregion
}
