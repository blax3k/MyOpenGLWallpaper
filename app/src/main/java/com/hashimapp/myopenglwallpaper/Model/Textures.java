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
import java.util.List;
import java.util.stream.Collectors;

public class Textures
{


    class TextureUploadData
    {
        Bitmap bitmap;
        int textureLevel;
        int textureName;

        public TextureUploadData(Bitmap bitmap, int bitmapId, int level)
        {
            this.bitmap = bitmap;
            textureName = bitmapId;
            textureLevel = level;
        }
    }
    public static final float BLUR_RADIUS = 3.0f;

    public boolean Loading;

    private Deque<TextureUploadData> textureUploadDataDeque;
    private List<Integer> texturesToRemove;

    public static final int[] GL_TEXTURE_IDS = new int[]{
            GLES20.GL_TEXTURE0, GLES20.GL_TEXTURE1, GLES20.GL_TEXTURE2, GLES20.GL_TEXTURE3,
            GLES20.GL_TEXTURE4, GLES20.GL_TEXTURE5, GLES20.GL_TEXTURE6, GLES20.GL_TEXTURE7,
            GLES20.GL_TEXTURE8, GLES20.GL_TEXTURE9, GLES20.GL_TEXTURE10, GLES20.GL_TEXTURE11,
            GLES20.GL_TEXTURE12, GLES20.GL_TEXTURE13, GLES20.GL_TEXTURE14, GLES20.GL_TEXTURE15,
            GLES20.GL_TEXTURE16, GLES20.GL_TEXTURE17, GLES20.GL_TEXTURE18, GLES20.GL_TEXTURE19,
            GLES20.GL_TEXTURE20, GLES20.GL_TEXTURE21, GLES20.GL_TEXTURE22, GLES20.GL_TEXTURE23,
            GLES20.GL_TEXTURE24, GLES20.GL_TEXTURE25, GLES20.GL_TEXTURE26, GLES20.GL_TEXTURE27,
            GLES20.GL_TEXTURE28, GLES20.GL_TEXTURE29, GLES20.GL_TEXTURE30, GLES20.GL_TEXTURE31,
    };

    private HashMap<Integer, TextureData> textureDataMap;
    private List<Integer> UsedTextureSlots;
    private List<Integer> OpenTextureSlots;


    Context context;

    Date startDate;

    public Textures(Context inContext)
    {
        startDate = new Date();

        context = inContext;

        textureNames = new int[32];
        GLES20.glGenTextures(32, textureNames, 0);
        textureUploadDataDeque = new ArrayDeque<>();
        texturesToRemove = new ArrayList<>();
        OpenTextureSlots = new ArrayList<>();
        UsedTextureSlots = new ArrayList<>();
        textureDataMap = new HashMap<>();

        for (int i = 0; i < GL_TEXTURE_IDS.length; i++)
        {
            OpenTextureSlots.add(i);
        }
    }



    public TextureData AddTexture(int bitmapID)
    {
        if (textureDataMap.containsKey(bitmapID))
        {
            return textureDataMap.get(bitmapID);
        }

        TextureData textureData = new TextureData(GL_TEXTURE_IDS[GetNextGLTextureIDIndex()], textureNames[textureNamesIndex], textureNamesIndex);
        textureNamesIndex++;

        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), bitmapID);

        GLES20.glActiveTexture(textureData.GLTextureIndex);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureData.textureName);
        //clamp texture to edge of shape
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, bmp, 0);
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

        GenerateBlurredTextures(bmp);

        textureDataMap.put(bitmapID, textureData);
        return textureData;
    }

    public TextureData getTextureData(int bitmapId){
        return textureDataMap.get(bitmapId);
    }

    private int GetNextGLTextureIDIndex()
    {
        int nextIndex = OpenTextureSlots.remove(0);
        UsedTextureSlots.add(nextIndex);

        return nextIndex;
    }

    public int textureNamesIndex = 0;
    public int[] textureNames;


    public boolean UploadComplete()
    {
        if (textureUploadDataDeque.size() > 0)
        {
            return false;
        }
        return true;
    }

    public void QueueTextures(List<Integer> bitmapIds)
    {
        for(Integer bitmapId : bitmapIds){
            Log.d("stuff", "Queue texture bitmapId: " + bitmapId);
            if(textureDataMap.containsKey(bitmapId) || bitmapId <= 0)
            {
                continue;
            }

            BitmapFactory.Options op = new BitmapFactory.Options();
            op.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), bitmapId, op);
            if(bmp != null){
                TextureUploadData uploadData = new TextureUploadData(bmp, bitmapId, 0);
                textureUploadDataDeque.add(uploadData);
            }else{
                Log.d("stuff", "bitmap " + bitmapId + " not found");
            }
        }

        texturesToRemove = textureDataMap.keySet().stream()
                .filter(bitmapId -> !bitmapIds.contains(bitmapId))
                .collect(Collectors.toList());
        Loading = true;

        Log.d("stuff", "textureUploadDataDeque.size(): " + textureUploadDataDeque.size());
    }


    ///Loads textures from memory into the GPU
    public void UploadTextures()
    {
        if(!Loading){
            return;
        }


        while (!textureUploadDataDeque.isEmpty())
        {
            TextureUploadData uploadData = textureUploadDataDeque.pop();
            if (uploadData == null)
            {
                return;
            }

            TextureData textureData;
            if(!texturesToRemove.isEmpty())
            {
                textureData = textureDataMap.get(texturesToRemove.get(0));
                textureDataMap.remove(texturesToRemove.get(0));
                texturesToRemove.remove(0);
            }else{
                textureData = new TextureData(GL_TEXTURE_IDS[GetNextGLTextureIDIndex()], textureNames[textureNamesIndex], textureNamesIndex);
                textureNamesIndex++;
            }


            GLES20.glActiveTexture(textureData.GLTextureIndex);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, uploadData.textureName);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, uploadData.textureLevel, GLES20.GL_RGBA, uploadData.bitmap, 0);
            GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
            GenerateBlurredTextures(uploadData.bitmap);

            textureDataMap.put(uploadData.textureName, textureData);
        }

        for(int bitmapId : texturesToRemove){
            textureDataMap.remove(bitmapId);
        };
        texturesToRemove.clear();

        for(int bitmapId : textureDataMap.keySet()){
            Log.d("stuff", "textureDataMap.values: " + bitmapId);
        }
    }

    private void UploadTextureThread(){

    }


    private void GenerateBlurredTextures(Bitmap bmp)
    {
        int originalWidth, bmpWidth;
        originalWidth = bmpWidth = bmp.getWidth();
        int originalHeight, bmpHeight;
        originalHeight = bmpHeight = bmp.getHeight();

        int index = 1;

        while (bmpWidth > 32 && bmpHeight > 32)
        {
            //cut image size in half
            bmpWidth /= 2;
            bmpHeight /= 2;

            //blur current image
            Bitmap blurredFull = BlurBuilder.blur(context, bmp, (float) bmpWidth / originalWidth, (float) bmpHeight / originalHeight, BLUR_RADIUS);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, index, blurredFull, 0);
            index++;

        }
    }
}


