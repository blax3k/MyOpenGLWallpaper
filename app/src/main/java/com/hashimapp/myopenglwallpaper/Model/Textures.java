package com.hashimapp.myopenglwallpaper.Model;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Textures
{
    class TextureUploadData
    {
        Bitmap Bitmap;
        int TextureIndex;
        String BitmapName;
        String DisposeBitmapName;
        ArrayList<Bitmap> MipMapList;

        public TextureUploadData(Bitmap bitmap, ArrayList<Bitmap> mipMaps, int textureIndex, String bitmapName, String removeBitmapName)
        {
            Bitmap = bitmap;
            MipMapList = mipMaps;
            TextureIndex = textureIndex;
            BitmapName = bitmapName;
            DisposeBitmapName = removeBitmapName;
        }
    }

    public static int GetBitmapSize(int widthHeight)
    {

        int bmpSize;
        if (widthHeight <= RESOLUTION_800)
        {
            bmpSize = IMAGE_SIZE_512;
        } else if (widthHeight > RESOLUTION_800 && widthHeight <= RESOLUTION_1400)
        {
            bmpSize = IMAGE_SIZE_1024;
        } else
        {//resolution >RESOLUTION_1400
            bmpSize = IMAGE_SIZE_2048;
        }

        return bmpSize;
    }

    private boolean InterruptLoading = false;

    private static final int MAX_OPEN_TEXTURE_SLOTS = 4;
    private static final int MAX_USABLE_TEXTURE_SLOTS = 28;

    private final static int RESOLUTION_800 = 512;
    private final static int RESOLUTION_1400 = 1280;
    private final static int RESOLUTION_1920 = 1920;

    public static final int IMAGE_SIZE_512 = 512;
    public static final int IMAGE_SIZE_1024 = 1024;
    public static final int IMAGE_SIZE_2048 = 2048;

    public static final float BLUR_RADIUS = 3.0f;

    private boolean Loaded;
    private int widthHeight;

    private Deque<TextureUploadData> textureUploadDataDeque;
    private Map<String, TextureData> BitmapTextureDataMap; //the bitmaps in memory
    public Deque<String> DisposableBitmpapIdQueue;
    public Deque<String> UploadBitmapIdQueue;



    public int[] textureNames;
    //indices of the GL_TEXTURE_ID and textureNames arrays
    private ArrayList<Integer> UsedTextureSlots;
    private ArrayList<Integer> OpenTextureSlots;

    Context context;
    Date startDate;
    private Resources resources;

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


    public Textures(Context inContext, int mWidthHeight)
    {
        startDate = new Date();

        context = inContext;
        widthHeight = mWidthHeight;

        textureNames = new int[32];
        GLES20.glGenTextures(32, textureNames, 0);
        OpenTextureSlots = new ArrayList<>();
        UsedTextureSlots = new ArrayList<>();

        BitmapTextureDataMap = new HashMap<>();
        textureUploadDataDeque = new ArrayDeque<>();
        DisposableBitmpapIdQueue = new ArrayDeque<>();
        UploadBitmapIdQueue = new ArrayDeque<>();

        resources = context.getResources();

        for (int i = 0; i < GL_TEXTURE_IDS.length; i++)
        {
            OpenTextureSlots.add(i);
        }
    }


//    public TextureData AddTexture(int bitmapID, int textureNameIndex)
//    {
//        int glTextureID = GL_TEXTURE_IDS[GetNextGLTextureIDIndex()];
//
//        TextureData textureData = new TextureData(glTextureID, textureNames[textureNameIndex], textureNameIndex, bitmapID);
//        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), bitmapID);
//
//        GLES20.glActiveTexture(textureData.GLTextureIndex);
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureData.textureName);
//        //clamp texture to edge of shape
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
//        // Set filtering
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
//
//        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, bmp, 0);
//
//        GenerateBlurredTextures(bmp);
//
//        return textureData;
//    }

    /*

     */
    public void QueueTextures(ArrayList<String> bitmapNameList)
    {
        Loaded = false;
        //figure out which textures need to be replaced/removed
        for(Map.Entry<String, TextureData> entry : BitmapTextureDataMap.entrySet())
        {
            if(!bitmapNameList.contains(entry.getKey()))
            {
                DisposableBitmpapIdQueue.add(entry.getKey());
            }
        }

        Integer index;
        String disposeBitmapName = "";
        //Queue the new textures into any available slots
        for(String bitmapName: bitmapNameList)
        {
            if(BitmapTextureDataMap.containsKey(bitmapName) || bitmapName.isEmpty())
            {
                continue; //bitmap is already loaded
            }

            disposeBitmapName = "";
            int disposeBitmapID = 0;
            if(DisposableBitmpapIdQueue.size() > 0)
            {   //load into disposable index
                disposeBitmapName = DisposableBitmpapIdQueue.removeFirst();
                index = BitmapTextureDataMap.get(disposeBitmapName).TextureIndex;
            }
            else
            {   //no more disposable slots. get a new one
                index = GetNextGLTextureIDIndex();
            }

            int bitmapID = resources.getIdentifier(bitmapName, "drawable", context.getPackageName());
            Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), bitmapID);
            ArrayList mipMaps = GetBlurredMipMaps(bmp);
            textureUploadDataDeque.add(new TextureUploadData(bmp,mipMaps, index, bitmapName, disposeBitmapName));
        }
        Loaded = true;
    }

    public void DequeTextures()
    {
        while(textureUploadDataDeque.size() > 0)
        {
            TextureUploadData uploadData = textureUploadDataDeque.pop();
            TextureData data = UploadTexture(uploadData); //upload bmp to GPU

            BitmapTextureDataMap.put(data.BitmapName, data);
            if(!uploadData.DisposeBitmapName.isEmpty())
            {
                BitmapTextureDataMap.remove(uploadData.DisposeBitmapName);
            }
        }
    }


    public TextureData GetTextureData(String bitmapName)
    {
        return BitmapTextureDataMap.get(bitmapName);
    }

    /*
    Upload bitmap to GPU
     */
    public TextureData UploadTexture(TextureUploadData data)
    {
        TextureData textureData = new TextureData(GL_TEXTURE_IDS[data.TextureIndex], textureNames[data.TextureIndex], data.TextureIndex, data.BitmapName);

        GLES20.glActiveTexture(textureData.GLTextureIndex);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureData.textureName);
        //clamp texture to edge of shape
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, data.Bitmap, 0);

        ApplyMipMaps(data.MipMapList);

        return textureData;
    }

    private int GetNextGLTextureIDIndex()
    {
        int nextIndex = OpenTextureSlots.remove(0);
        UsedTextureSlots.add(nextIndex);

        return nextIndex;
    }

    public boolean UploadComplete()
    {
        if (textureUploadDataDeque.size() > 0 || !Loaded)
        {
            return false;
        }
        return true;
    }



    private void StopLoading()
    {
    }

    public void InterruptLoad()
    {
        InterruptLoading = true;
    }


    /*
    Generate Blurred textures in mipmap for currently selected GL Texture
     */
    private void ApplyMipMaps(ArrayList<Bitmap> mipMaps)
    {
        int index = 1;

        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D); //generate mipmap for the current texture

        for(Bitmap bitmap : mipMaps)
        {
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, index, bitmap, 0);
            index++;
        }
    }


    /*
    Generate Blurred textures in mipmap for currently selected GL Texture
     */
    private ArrayList<Bitmap> GetBlurredMipMaps(Bitmap bmp)
    {
        int originalWidth, bmpWidth;
        originalWidth = bmpWidth = bmp.getWidth();
        int originalHeight, bmpHeight;
        originalHeight = bmpHeight = bmp.getHeight();

        ArrayList<Bitmap> mipMaps = new ArrayList<>();

        while (bmpWidth > 32 && bmpHeight > 32)
        {
            //cut image size in half
            bmpWidth /= 2;
            bmpHeight /= 2;

            //blur current image
            Bitmap blurredFull = BlurBuilder.blur(context, bmp, (float) bmpWidth / originalWidth, (float) bmpHeight / originalHeight, BLUR_RADIUS);
            mipMaps.add(blurredFull);
        }

        return mipMaps;
    }
}


