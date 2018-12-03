package com.hashimapp.myopenglwallpaper.Model;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.hashimapp.myopenglwallpaper.Model.riGraphicTools;

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
    private final int mProgram;
    float color[] = {0.63671875f, 0.76953125f, 0.22265625f, 1.0f};
    public static float colors[];
    private float[] scratch;
    private int textureIndex;


    public Sprite(float[] mVertices, float[] textureColor, short[] mIndices, int textureIndex) {
        vertices = mVertices;
        colors = textureColor;
        indices = mIndices;
        scratch = new float[16];
        this.textureIndex = textureIndex;

        ByteBuffer cb = ByteBuffer.allocateDirect(colors.length * 4);
        cb.order(ByteOrder.nativeOrder());
        colorBuffer = cb.asFloatBuffer();
        colorBuffer.put(colors);
        colorBuffer.position(0);

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

        int vertexShader = riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER,
                riGraphicTools.vs_Image);
        int fragmentShader = riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER,
                riGraphicTools.fs_Image);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables

//        setupImage(context, bitmapAddress, texturenames);
    }



    public void SetTexture(Bitmap bmp, int activeTexture){
        GLES20.glActiveTexture(activeTexture);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIndex);
        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        //clamp texture to edge of shape
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

    }

    public void setColor(float[] textureColor) {
        ByteBuffer cb = ByteBuffer.allocateDirect(colors.length * 4);
        cb.order(ByteOrder.nativeOrder());
        colorBuffer = cb.asFloatBuffer();
        colorBuffer.put(textureColor);
        colorBuffer.position(0);
    }

    public void setVertices(float[] vertices) {
        this.vertices = vertices;
        vertexBuffer.put(this.vertices);
        vertexBuffer.position(0);
    }

    public void draw(FloatBuffer uvBuffer, float[] mtrxView, float[] mtrxProjection, float[] mModelMatrix) {
        //set the program
        GLES20.glUseProgram(mProgram);
        GLES20.glFlush();

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
