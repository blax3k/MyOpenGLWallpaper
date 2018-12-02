package com.hashimapp.myopenglwallpaper.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Blake Hashimoto on 8/15/2015.
 */
public class Sprite {
    // Geometric variables
    public static float vertices[];
    public short indices[];
    public FloatBuffer vertexBuffer;
    public ShortBuffer drawListBuffer;
    public FloatBuffer colorBuffer;
//    private final int mProgram;
    public static float colors[];
    private float[] scratch;
    private int textureIndex;
    ISpriteData spriteData;
    float xScrollOffset = 0;
    float xAccelOffset = 0;
    float yAccelOffset = 0;
    float landscapeOffset;



    public Sprite(ISpriteData mSpriteData) {
        spriteData = mSpriteData;
        scratch = new float[16];
        vertices = spriteData.getVertices();
        colors = spriteData.getColor();
        indices = spriteData.getIndices();
        textureIndex = spriteData.getTextureIndex();

        setColor(colors);
        setVertices(vertices);
        setIndices(indices);
    }



    public void SetTexture(Context context){
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), spriteData.getBitmapID());
        GLES20.glActiveTexture(spriteData.getGLImageID());
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIndex);
        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        //clamp texture to edge of shape
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
    }

    private void setColor(float[] textureColor) {
        ByteBuffer cb = ByteBuffer.allocateDirect(colors.length * 4);
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

    private void setIndices(short[] indices){
        ByteBuffer dlb = ByteBuffer.allocateDirect(indices.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(indices);
        drawListBuffer.position(0);

    }

    public void OffsetChanged(float xOffset, boolean PortraitOrientation){
        float offsetMultiplier = (spriteData.getZVertices() + 2.0f)/2;
        if(PortraitOrientation){
            this.xScrollOffset = -1 * xOffset * offsetMultiplier + (1.05f * offsetMultiplier);
        }
        else{
            this.xScrollOffset = -1 *( (xOffset * offsetMultiplier) +  (-0.15f * offsetMultiplier));
        }
    }

    public void SensorChanged(float xOffset, float yOffset){
        float offsetMultiplier = (spriteData.getZVertices() - 2.0f)/2; // z will be a vertice between 0.0 and 2.0f
        xAccelOffset = -1 * xOffset * offsetMultiplier;
        yAccelOffset = -1 * yOffset * offsetMultiplier;


    }

    public void SetOrientation(boolean portrait){
        spriteData.setOrientation(portrait);
        setVertices(spriteData.getVertices());
    }


    public void draw(FloatBuffer uvBuffer, float[] mtrxView, float[] mtrxProjection, float[] mModelMatrix) {

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, xScrollOffset + xAccelOffset, yAccelOffset, 1.0f);
        Matrix.multiplyMM(scratch, 0, mtrxView, 0, mModelMatrix, 0);
        Matrix.multiplyMM(scratch, 0, mtrxProjection, 0, scratch, 0);

        // get handle to vertex shader's vPosition member
        int mColorHandle = GLES20.glGetAttribLocation(riGraphicTools.sp_Image, "a_Color");
        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false, 0, colorBuffer);

        // get handle to vertex shader's vPosition member
        int mPositionHandle = GLES20.glGetAttribLocation(riGraphicTools.sp_Image, "vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer);

        // Get handle to texture coordinates location
        int mTexCoordLoc = GLES20.glGetAttribLocation(riGraphicTools.sp_Image, "a_texCoord");
        GLES20.glEnableVertexAttribArray(mTexCoordLoc);
        GLES20.glVertexAttribPointer(mTexCoordLoc, 2, GLES20.GL_FLOAT, false, 0, uvBuffer);

        // Get handle to shape's transformation matrix and apply it
        int mtrxhandle = GLES20.glGetUniformLocation(riGraphicTools.sp_Image, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, scratch, 0);

        // Get handle to textures locations
        int mSamplerLoc = GLES20.glGetUniformLocation(riGraphicTools.sp_Image, "s_texture");
        // Set the sampler texture unit to x, where we have saved the texture.
        GLES20.glUniform1i(mSamplerLoc, textureIndex);
        GLES20.glUniform4fv(mColorHandle, 1, colors, 0);

        //enable transparency
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        // Draw the triangle
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
        GLES20.glDisable(GLES20.GL_BLEND);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mColorHandle);

        if (textureIndex >= 0)
            GLES20.glDisableVertexAttribArray(mTexCoordLoc);
    }
}
