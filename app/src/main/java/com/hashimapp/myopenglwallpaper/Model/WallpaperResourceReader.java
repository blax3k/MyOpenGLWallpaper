package com.hashimapp.myopenglwallpaper.Model;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.internal.bind.util.ISO8601Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WallpaperResourceReader
{
    private Context _context;
    Gson _gson;
    JsonObject object;


    public WallpaperResourceReader(Context context)
    {
        _context = context;
        _gson = new Gson();
        object = new JsonObject();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public SceneData LoadSceneData(String fileKey)
    {
        String fileName = fileKey + ".json";
        File file = new File(GetFilePath(), fileName);
        try
        {
            Reader fileReader = Files.newBufferedReader(Paths.get(file.getPath()));
            SceneData sceneData = _gson.fromJson(fileReader, SceneData.class);
            return sceneData;
        }catch(Exception e){
            Log.d("json", "couldn't read file");
            return new SceneData();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void SaveSceneData(SceneData sceneData)
    {
        String fileName = sceneData.SceneKey + ".json";
        File file = new File(GetFilePath(), fileName);
        try
        {
            Writer writer = Files.newBufferedWriter(Paths.get(file.getPath()));
//            _gson.toJson(sceneData, writer);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(sceneData, writer);
            writer.close();
//
//            FileOutputStream f = new FileOutputStream(file);
//            PrintWriter pw = new PrintWriter(f);
//            pw.println("this is a test line");
//            pw.flush();
//            pw.close();
//            f.close();

//            _gson.toJson(sceneData, new FileWriter(file.getPath()));
        }
        catch (Exception e)
        {
            Log.d("json", "couldn't create new file" + e.getMessage());
        }
    }

    private String GetFilePath()
    {
        checkExternalMedia();
        String filePath = _context.getExternalFilesDir(null).getPath();
        return filePath;
    }


    private void checkExternalMedia(){
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // Can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // Can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Can't read or write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
    }

}
