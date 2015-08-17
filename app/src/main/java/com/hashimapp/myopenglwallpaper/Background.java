package com.hashimapp.myopenglwallpaper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.transition.Scene;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by User on 8/15/2015.
 */
public class Background {
           // Geometric variables
        public static float vertices[];
        public static short indices[];
        public static float uvs[];
        public FloatBuffer vertexBuffer;
        public ShortBuffer drawListBuffer;
        public FloatBuffer uvBuffer;
        private final int mProgram;
        private int mMVPMatrixHandle;

        public Background(Context context, String bitmapAddress, float[] mVertices, int[] textureNames)
        {
            this.vertices = mVertices;
            // We have to create the vertices of our triangle.
//        vertices = new float[]{
//                -0.5f,  0.5f, 0.0f,   // top left
//                -0.5f, -0.5f, 0.0f,   // bottom left
//                0.5f, -0.5f, 0.0f,   // bottom right
//                0.5f,  0.5f, 0.0f }; // top right

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

            int vertexShader = riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER,
                    riGraphicTools.vs_Image);
            int fragmentShader = riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER,
                    riGraphicTools.fs_Image);

            mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
            GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
            GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
            GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables

            setupImage(context, bitmapAddress, textureNames);
        }

//    private void setupTriangle()
//    {
//        // We have to create the vertices of our triangle.
////        vertices = new float[]{
////                -0.5f,  0.5f, 0.0f,   // top left
////                -0.5f, -0.5f, 0.0f,   // bottom left
////                0.5f, -0.5f, 0.0f,   // bottom right
////                0.5f,  0.5f, 0.0f }; // top right
//
//        indices = new short[] {0, 1, 2, 0, 2, 3}; // The order of vertexrendering.
//
//        // The vertex buffer.
//        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
//        bb.order(ByteOrder.nativeOrder());
//        vertexBuffer = bb.asFloatBuffer();
//        vertexBuffer.put(vertices);
//        vertexBuffer.position(0);
//
//        // initialize byte buffer for the draw list
//        ByteBuffer dlb = ByteBuffer.allocateDirect(indices.length * 2);
//        dlb.order(ByteOrder.nativeOrder());
//        drawListBuffer = dlb.asShortBuffer();
//        drawListBuffer.put(indices);
//        drawListBuffer.position(0);
//
//        int vertexShader = riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER,
//                riGraphicTools.vs_Image);
//        int fragmentShader = riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER,
//                riGraphicTools.fs_Image);
//
//        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
//        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
//        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
//        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables
//    }

        public void setupImage(Context context, String bitmapAddress, int[] texturenames)
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

//        int id = context.getResources().getIdentifier(bitmapAddress, null, context.getPackageName());
//        // Temporary create a bitmap
//        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), id);
//
//        // Load the bitmap into the bound texture.
//        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
//
//            // Generate Textures, if more needed, alter these numbers.
//            int[] texturenames = new int[2];
//            GLES20.glGenTextures(2, texturenames, 0);

            // Retrieve our image from resources.
            int id = context.getResources().getIdentifier(bitmapAddress, null, context.getPackageName());

            // Temporary create a bitmap
            Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), id);

            // Bind texture to texturename
            GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[2]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

            // We are done using the bitmap so we should recycle it.
            bmp.recycle();
        }

        public void draw(float[] m)
        {
            GLES20.glUseProgram(mProgram);
//            // clear Screen and Depth Buffer, we have set the clear color as black.
//            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

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
            GLES20.glVertexAttribPointer ( mTexCoordLoc, 2, GLES20.GL_FLOAT, false, 0, uvBuffer);

            // Get handle to shape's transformation matrix
            int mtrxhandle = GLES20.glGetUniformLocation(riGraphicTools.sp_Image, "uMVPMatrix");


            // Apply the projection and view transformation
            GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, m, 0);

            // Get handle to textures locations
            int mSamplerLoc = GLES20.glGetUniformLocation(riGraphicTools.sp_Image, "s_texture");

            // Set the sampler texture unit to 0, where we have saved the texture.
            GLES20.glUniform1i(mSamplerLoc, 2);


            GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, m, 0);



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
}