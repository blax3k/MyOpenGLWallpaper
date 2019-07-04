package com.hashimapp.myopenglwallpaper.Model;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Blake Hashimoto on 8/15/2015.
 */
public class Sprite {
    // Geometric variables
//    public static float vertices[];
    public short indices[];
    public FloatBuffer vertexBuffer;
    public ShortBuffer drawListBuffer;
    public FloatBuffer colorBuffer;
    public FloatBuffer textureVerticeBuffer;
    private TimeTracker timeTracker;
    //    private final int mProgram;
    public static float colors[];
//    private float[] scratch;

    private float spriteXPosOffset;

    private int textureIndex;
    ISpriteData spriteData;
    float xScrollOffset = 0;
    float xAccelOffset = 0;
    float yAccelOffset = 0;

    private float bias = 0.0f;


    public Sprite(ISpriteData mSpriteData) {
        spriteData = mSpriteData;
//        scratch = new float[16];
        colors = spriteData.getColor(TimeTracker.DAY, 100);
        indices = spriteData.getIndices();
        textureIndex = spriteData.getTextureIndex();

        setColor(colors);
        setVertices(spriteData.getShapeVertices(true, false));
        setTextureVertices(spriteData.getTextureVertices());
        setIndices(indices);
    }

    public void OffsetChanged(float xOffset, boolean PortraitOrientation) {
        //parallax motion determined by how "far" the sprite is from the camera
        float offsetMultiplier = (spriteData.getZVertices() + 1.0f);
        if (PortraitOrientation) {
            this.xScrollOffset = -1 //reverse movement
                    * xOffset * offsetMultiplier //screen offset position (0.0-1.0) multiplied by Z position parallax effect
                    + spriteXPosOffset; //
        } else {
            this.xScrollOffset = -1 * ((xOffset * offsetMultiplier) + (-0.15f * offsetMultiplier));
        }
    }


    public void SensorChanged(float xOffset, float yOffset) {
        float offsetMultiplier = (spriteData.getZVertices()) * 2.0f + 0.2f; // z will be a vertice between 0.0 and -1.0f
        xAccelOffset = -1 * xOffset * offsetMultiplier;
        yAccelOffset = -1 * yOffset * offsetMultiplier;
    }

    public void SetOrientation(boolean portrait, boolean motionOffset, float spriteXPosOffset) {
        spriteData.setOrientation(portrait);
        float offsetMultiplier = (spriteData.getZVertices() + 1.0f);
        this.spriteXPosOffset = spriteXPosOffset * offsetMultiplier;
        setVertices(spriteData.getShapeVertices(portrait, motionOffset));
    }

    public void SetTime(int time, int phasePercentage) {
        float[] newColor = spriteData.getColor(time, phasePercentage);
        setColor(newColor);
    }

    public void SetFocalPoint(float currentFocalPoint){
        //get character distance from the 'in focus' camera depth
        float distanceFromFocalPoint = Math.abs(currentFocalPoint - spriteData.getZVertices());
        //translate that depth to the bias
        bias = (distanceFromFocalPoint * 4 -1.0f);
    }


    public void draw(float[] mtrxView, float[] mtrxProjection, float[] mModelMatrix, int mColorHandle, int mPositionHandle, int mTexCoordLoc
            , int mtrxHandle, int textureUniformHandle, int biasHandle, float[] mMVPMatrix, int[] textureNames)
    {
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, xScrollOffset + xAccelOffset, yAccelOffset, 1.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mtrxView, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mtrxProjection, 0, mMVPMatrix, 0);

        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false, 0, colorBuffer);

        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer);

        // Get handle to texture coordinates location
        GLES20.glEnableVertexAttribArray(mTexCoordLoc);
        GLES20.glVertexAttribPointer(mTexCoordLoc, 2, GLES20.GL_FLOAT, false, 0, textureVerticeBuffer);

        GLES20.glUniformMatrix4fv(mtrxHandle, 1, false, mMVPMatrix, 0);

        // Set the sampler texture unit to x, where we have saved the texture.
        GLES20.glActiveTexture(spriteData.getTexIndex());
        GLES20.glBindTexture(spriteData.getTexIndex(), textureNames[spriteData.getTextureIndex()]);
        GLES20.glUniform1i(textureUniformHandle, textureIndex);

        GLES20.glUniform1f(biasHandle, bias);

        // Draw the triangle
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

//        // Disable vertex array
//        GLES20.glDisableVertexAttribArray(mPositionHandle);
//        GLES20.glDisableVertexAttribArray(mColorHandle);
//
//        if (textureIndex >= 0)
//            GLES20.glDisableVertexAttribArray(mTexCoordLoc);
    }


    private void setColor(float[] textureColor) {
        ByteBuffer cb = ByteBuffer.allocateDirect(textureColor.length * 4);
        cb.order(ByteOrder.nativeOrder());
        colorBuffer = cb.asFloatBuffer();
        colorBuffer.put(textureColor);
        colorBuffer.position(0);
    }

    private void setVertices(float[] vertices) {
        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
    }

    private void setIndices(short[] indices) {
        ByteBuffer dlb = ByteBuffer.allocateDirect(indices.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(indices);
        drawListBuffer.position(0);
    }

    private void setTextureVertices(float[] vertices) {
        // The texture buffer
        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        textureVerticeBuffer = bb.asFloatBuffer();
        textureVerticeBuffer.put(vertices);
        textureVerticeBuffer.position(0);
    }
}
