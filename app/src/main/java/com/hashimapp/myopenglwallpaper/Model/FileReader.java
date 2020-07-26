package com.hashimapp.myopenglwallpaper.Model;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

public class FileReader
{
    private Context _context;
    Gson _gson;
    JsonObject object;


    public FileReader(Context context)
    {
        _context = context;
        _gson = new Gson();
        object = new JsonObject();
    }

    public void LoadSceneData(String fileKey){
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put("name", 4);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        String userstring = jsonObject.toString();

        File file = new File(_context.getFilesDir(), "scene1");

    }

    public void SaveSceneData(SceneData sceneData)
    {
        String fileName = sceneData.SceneKey + ".json";
        File file = new File(GetFilePath(), fileName);
        try
        {
            file.createNewFile();
        }
        catch (Exception e)
        {
            Log.d("json", "couldn't create new file" + e.getMessage());
        }
        try(Writer writer = new FileWriter(file))
        {
            Gson gson = new GsonBuilder().create();
            gson.toJson(sceneData, writer);
        }
        catch(Exception e){
            Log.d("json", "couldn't create json file" + e.getMessage());
        }
    }

    private String GetFilePath()
    {
        String filePath = _context.getExternalFilesDir(null).getPath();
        return filePath;
    }


}
