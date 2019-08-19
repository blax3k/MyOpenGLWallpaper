package com.hashimapp.myopenglwallpaper.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class Textures {

    class TextureUploadData
    {
        Bitmap _bitmap;
        int _textureLevel;
        int _textureName;

        public TextureUploadData(Bitmap bitmap, int name, int level){
            _bitmap = bitmap;
            _textureName = name;
            _textureLevel = level;
        }

        public void SetBitmap(Bitmap bitmap){
            _bitmap = bitmap;
        }
        public Bitmap GetBitmap(){
            return _bitmap;
        }

        public void SetTextureName(int textureName){
            _textureName = textureName;
        }
        public int GetTextureName(){
            return _textureName;
        }

        public void SetTextureLevel(int level){
            _textureLevel = level;
        }
        public int GetTextureLevel(){
            return _textureLevel;
        }
    }

    private boolean InterruptLoading = false;

    private static final int MAX_OPEN_TEXTURE_SLOTS = 4;
    private static final int MAX_USABLE_TEXTURE_SLOTS = 28;

    public static final int GL_TEXTURE_ID_INDEX = 0;
    public static final int TEXTURE_NAME_INDEX = 1;
    public static final int TEXTURE_NAME_INDEX_INDEX = 2;

    private boolean Loaded;


    private Deque<TextureUploadData> textureDataDeque;


    public static final int[] GL_TEXTURE_IDS = new int[]{
            GLES20.GL_TEXTURE0, GLES20.GL_TEXTURE1, GLES20.GL_TEXTURE2, GLES20.GL_TEXTURE3,
            GLES20.GL_TEXTURE4, GLES20.GL_TEXTURE5, GLES20.GL_TEXTURE6, GLES20.GL_TEXTURE7,
            GLES20.GL_TEXTURE8, GLES20.GL_TEXTURE9, GLES20.GL_TEXTURE10, GLES20.GL_TEXTURE11,
            GLES20.GL_TEXTURE12, GLES20.GL_TEXTURE13,  GLES20.GL_TEXTURE14, GLES20.GL_TEXTURE15,
            GLES20.GL_TEXTURE16, GLES20.GL_TEXTURE17, GLES20.GL_TEXTURE18, GLES20.GL_TEXTURE19,
            GLES20.GL_TEXTURE20, GLES20.GL_TEXTURE21, GLES20.GL_TEXTURE22, GLES20.GL_TEXTURE23,
            GLES20.GL_TEXTURE24,  GLES20.GL_TEXTURE25, GLES20.GL_TEXTURE26, GLES20.GL_TEXTURE27,
            GLES20.GL_TEXTURE28, GLES20.GL_TEXTURE29, GLES20.GL_TEXTURE30, GLES20.GL_TEXTURE31,
    };


    private ArrayList<Integer> UsedTextureSlots;
    private ArrayList<Integer> OpenTextureSlots;


    Context context;

    Date startDate;

    public Textures(Context inContext) {
        startDate = new Date();

        context = inContext;

        textureNames = new int[20];
        GLES20.glGenTextures(20, textureNames, 0);
        textureDataDeque = new ArrayDeque<>();

        OpenTextureSlots = new ArrayList<>();
        UsedTextureSlots = new ArrayList<>();

        for(int i = 0; i < GL_TEXTURE_IDS.length; i++){
            OpenTextureSlots.add(i);
        }
    }

    public void InitTextures()
    {
        textureNames = new int[32];
        GLES20.glGenTextures(32, textureNames, 0);
    }

    public TextureData AddTexture(int bitmapID, int textureNameIndex){
        int glTextureID = GL_TEXTURE_IDS[GetNextGLTextureIDIndex()];

        TextureData textureData = new TextureData( glTextureID, textureNames[textureNameIndex], textureNameIndex);

        SetTexture(bitmapID, textureData.GLTextureIndex, textureData.textureName);

        return new TextureData( glTextureID, textureNames[textureNameIndex], textureNameIndex);

    }

    private int GetNextGLTextureIDIndex(){
        int nextIndex = OpenTextureSlots.remove(0);
        UsedTextureSlots.add(nextIndex);

        return  nextIndex;
    }


    public int[] textureNames;



    public boolean UploadComplete(){
        if(textureDataDeque.size() > 0 || !Loaded){
            return false;
        }
        return true;
    }


    ///load textures from storage into memory
    public void LoadTextures(HashMap<Integer, Integer> bitmapIdTextureNameHashMap){
        Loaded = false;
        for(Map.Entry<Integer, Integer> data : bitmapIdTextureNameHashMap.entrySet()){

            int bitmapId = data.getKey();
            int texName = data.getValue();

            Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), bitmapId);
            TextureUploadData uploadData = new TextureUploadData(bmp, texName, 0);

            textureDataDeque.add(uploadData);
            if(InterruptLoading){
                textureDataDeque.clear();
                Loaded = true;
                InterruptLoading = false;
                return;
            }

            //blur textures
            int originalWidth, bmpWidth;
            originalWidth = bmpWidth = bmp.getWidth();
            int originalHeight, bmpHeight;
            originalHeight = bmpHeight = bmp.getHeight();

            int index = 1;

            while (bmpWidth > 32 && bmpHeight > 32) {

                if(InterruptLoading)
                {
                    textureDataDeque.clear();
                    Loaded = true;
                    InterruptLoading = false;
                    return;
                }

                //cut image size in half
                bmpWidth /= 2;
                bmpHeight /= 2;

                //blur current image
                Bitmap blurredFull = BlurBuilder.blur(context, bmp, (float)bmpWidth / originalWidth, (float)bmpHeight / originalHeight, 4.0f);
                TextureUploadData blurredTexData = new TextureUploadData(blurredFull, texName, index);
                textureDataDeque.add(blurredTexData);

                index++;
            }

        }
        Loaded = true;

    }

    private void StopLoading(){
    }

    public void InterruptLoad(){
        InterruptLoading = true;
    }

    ///Loads textures from memory into the GPU
    public void UploadTextures(){
        int currentSize = textureDataDeque.size();
        for(int i = 0; i < currentSize; i++){
            TextureUploadData data = textureDataDeque.pop();
            if(data == null){
                return;
            }
            UpdateTexture(data._bitmap, data._textureName, data._textureLevel);
        }


    }

    private void SetTexture(int bitmapID, int GLImageIndex, int textureName) {
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), bitmapID);

        GLES20.glActiveTexture(GLImageIndex);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureName);
        //clamp texture to edge of shape
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, bmp, 0 );
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

        GenerateBlurredTextures(bmp);
    }




    private void UpdateTexture(Bitmap bmp, int textureName, int textureLevel) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureName);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, textureLevel, GLES20.GL_RGBA, bmp, 0 );
    }



    private void GenerateBlurredTextures(Bitmap bmp) {

        int originalWidth, bmpWidth;
        originalWidth = bmpWidth = bmp.getWidth();
        int originalHeight, bmpHeight;
        originalHeight = bmpHeight = bmp.getHeight();

        int index = 1;

        while (bmpWidth > 32 && bmpHeight > 32) {
            //cut image size in half
            bmpWidth /= 2;
            bmpHeight /= 2;

            //blur current image
            Bitmap blurredFull = BlurBuilder.blur(context, bmp, (float)bmpWidth / originalWidth, (float)bmpHeight / originalHeight, 4.0f);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, index, blurredFull, 0);
            index++;

        }
    }
}


