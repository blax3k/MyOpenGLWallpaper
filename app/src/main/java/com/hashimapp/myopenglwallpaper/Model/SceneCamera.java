package com.hashimapp.myopenglwallpaper.Model;

public class SceneCamera
{
    public class SensorData
    {
        public float xOffset, yOffset;
        boolean Inverted;

        public SensorData()
        {
            xOffset = 0;
            yOffset = 0;
            Inverted = false;
        }

    }

    public boolean SensorUpdated;
    private SensorData sensorData;

    public SceneCamera(){
        sensorData = new SensorData();
    }

    public void SetSensorData(float x, float y, boolean invert)
    {
        sensorData.xOffset = x;
        sensorData.yOffset = y;
        sensorData.Inverted = invert;

        SensorUpdated = true;
    }

    public void SetSensorRead()
    {
        SensorUpdated = false;
    }

    public SensorData GetSensorData()
    {
        return sensorData;
    }

}
