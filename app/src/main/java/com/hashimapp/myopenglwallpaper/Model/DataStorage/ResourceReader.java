package com.hashimapp.myopenglwallpaper.Model.DataStorage;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.hashimapp.myopenglwallpaper.Model.SpriteData;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ResourceReader
{
    private Context _context;
    Gson _gson;
    JsonObject object;

    private final String SPRITE_DATA_STORAGE_FOLDER = "Sprite Data Storage/";
    private final String SCENE_DATA_FOLDER = "Scene Data Storage/";


    public ResourceReader(Context context)
    {
        _context = context;
        _gson = new Gson();
        object = new JsonObject();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public SceneData GetSceneData(String sceneDataKey)
    {
        try
        {
            Reader fileReader = GetFileReader(SCENE_DATA_FOLDER, sceneDataKey);
            SceneData sceneData = _gson.fromJson(fileReader, SceneData.class);
            return sceneData;
        } catch (Exception e)
        {
            Log.d("json", "couldn't read file");
            return new SceneData();
        }
    }

    public ArrayList<String> GetSceneNames()
    {
        String filePath = GetFilePath() + "/" + SCENE_DATA_FOLDER + "/";
        File directory = new File(filePath);
        File[] files = directory.listFiles();

        ArrayList<String> fileNames = new ArrayList<>();
        for(int i = 0; i < files.length; i++)
        {

            String name = files[i].getName();
            name = name.substring(0, name.lastIndexOf('.'));
            fileNames.add(name);
        }

        return fileNames;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<SpriteData> GetSpriteDataList(String SceneDataKey)
    {
        try
        {
            Reader fileReader = GetFileReader(SCENE_DATA_FOLDER, SceneDataKey);
            SceneData sceneData = _gson.fromJson(fileReader, SceneData.class);

            String previousFile = "";
            SpriteDataStorage spriteDataStorage = null;
            ArrayList<SpriteDataParameters> spriteDataParameters = sceneData.SpriteDataList;
            Collections.sort(spriteDataParameters, (p1, p2) -> p1.SpriteFileName.compareTo(p2.SpriteFileName)); //group like sprite file names

            ArrayList<SpriteData> spriteDataList = new ArrayList();

            for(SpriteDataParameters spriteParm : sceneData.SpriteDataList)
            {
                if(!previousFile.equalsIgnoreCase(spriteParm.SpriteFileName) && !spriteParm.SpriteFileName.isEmpty())
                {
                    fileReader = GetFileReader(SPRITE_DATA_STORAGE_FOLDER, spriteParm.SpriteFileName);
                    previousFile = spriteParm.SpriteFileName;
                    spriteDataStorage = _gson.fromJson(fileReader, SpriteDataStorage.class);
                }

                if(spriteDataStorage != null)
                {
                    SpriteData spriteData = spriteDataStorage.GetSpriteData(spriteParm);
                    spriteDataList.add(spriteData);
                }
            }

            return spriteDataList;
        } catch (Exception e)
        {
            Log.d("json", "couldn't read file");
            return new ArrayList<>();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void SaveSceneData(SceneData sceneData)
    {
        try
        {
            Writer writer = GetFileWriter(SCENE_DATA_FOLDER, sceneData.SceneKey);
            Gson gson = new GsonBuilder().create();
            gson.toJson(sceneData, writer);
            writer.close();
        } catch (Exception e)
        {
            Log.d("json", "couldn't create new file" + e.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void SaveSpriteDataStorage(SpriteDataStorage spriteDataStorage)
    {
        try
        {
            Writer writer = GetFileWriter(SPRITE_DATA_STORAGE_FOLDER, spriteDataStorage.Name);
            Gson gson = new GsonBuilder().create();
            gson.toJson(spriteDataStorage, writer);
            writer.close();
        } catch (Exception e)
        {
            Log.d("json", "couldn't create new file" + e.getMessage());
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private Reader GetFileReader(String folder, String name) throws IOException
    {
        String fileName = name + ".json";
        String filePath = GetFilePath() + "/" + folder + "/";
        File file = new File(filePath, fileName);
        Reader fileReader = Files.newBufferedReader(Paths.get(file.getPath()));

        return fileReader;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Writer GetFileWriter(String folder, String name) throws IOException
    {
        String fileName = name + ".json";
        String filePath = GetFilePath() + "/" + folder + "/";

        File fileDirectory = new File(filePath);
        fileDirectory.mkdirs();

        File file = new File(filePath, fileName);
        Writer fileWriter = Files.newBufferedWriter(Paths.get(file.getPath()));

        return fileWriter;
    }


    private String GetFilePath()
    {
        checkExternalMedia();
//        String filePath = _context.getExternalFilesDir(null).getPath();
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();

        return filePath;
    }


    private void checkExternalMedia()
    {

        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state))
        {
            // Can read and write the media
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
        {
            // Can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else
        {
            // Can't read or write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
    }

}
