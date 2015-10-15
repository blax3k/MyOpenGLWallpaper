package com.hashimapp.myopenglwallpaper;

/**
 * Created by Blake on 8/19/2015.
 */
public class DataHolder
{
    public DataHolder()
    {

    }

    private float opacity = 0.0f;

    public float getOpacity()
    {
        return opacity;
    }

    public void setOpacity(float newOpacity)
    {
        if(newOpacity > 1)
            opacity = 1;
        else if(opacity < 0)
            opacity = 0;
        else
            opacity = newOpacity;
        updateGirlColor();
    }

    public void updateGirlColor()
    {
       float[] newNormalColor = new float[]
            {1f, 1f, 1f, opacity,
                    1f, 1f, 1f, opacity,
                    1f, 1f, 1f, opacity,
                    1f, 1f, 1f, opacity};
        normalColorGirl = newNormalColor;
        float[] newNightcolor = new float[]
                {0.17f, 0.27f, 0.27f, opacity,
                        0.17f, 0.27f, 0.63921568627f, opacity,
                        0.17f, 0.27f, 0.63921568627f, opacity,
                        0.17f, 0.27f, 0.63921568627f, opacity};
        nightColorGirl = newNightcolor;
    }


    //Sprite posisitions
    public float[] vertices1 = new float[]{
            -1.55f,  1.55f, -2.0f,   // top left
            -1.55f, -1.55f, -2.0f,   // bottom left
            1.55f, -1.55f, -2.0f,   // bottom right
            1.55f,  1.55f, -2.0f }; // top right

    public float[] grassVertices = new float[]{
            -1.5f,  -0.8f, -2.0f,   // top left
            -1.5f, -1.3f, -2.0f,   // bottom left
            1.5f, -1.3f, -2.0f,   // bottom right
            1.5f,  -0.8f, -2.0f }; // top right

//    public float[] vertices2 = new float[]{
//            -2.9f,  2.9f, 1.98f,   // top left
//            -2.9f, -2.9f, 1.98f,   // bottom left
//            2.9f, -2.9f, 1.98f,   // bottom right
//            2.9f,  2.9f, 1.98f }; // top right

    public float[] vertices2 = new float[]{
            -1.0f,  1.0f, 1.98f,   // top left
            -1.0f, -1.0f, 1.98f,   // bottom left
            1.0f, -1.0f, 1.98f,   // bottom right
            1.0f,  1.0f, 1.98f }; // top right

    public float[] vertices3 = new float[]{
            -1.8f,  1.8f, 1.99f,   // top left
            -1.8f, -1.8f, 1.99f,   // bottom left
            1.8f, -1.8f, 1.99f,   // bottom right
            1.8f,  1.8f, 1.99f }; // top right

//    public float[] vertices4 = new float[]{
//            -1.9f,  2.4f, 2.0f,   // top left
//            -1.9f, -2.4f, 2.0f,   // bottom left
//            1.9f, -2.4f, 2.0f,   // bottom right
//            1.9f,  2.4f, 2.0f }; // top right

    public float[] vertices4 = new float[]{
            -2.0f,  2.5f, 2.0f,   // top left
            -2.0f, -0.0f, 2.0f,   // bottom left
            2.0f, -0.0f, 2.0f,   // bottom right
            2.0f,  2.5f, 2.0f }; // top right

    public float[] fieldVertices = new float[]{
            -4.0f,  2.5f, 2.0f,   // top left
            -4.0f, -2.5f, 2.0f,   // bottom left
            4.0f, -2.5f, 2.0f,   // bottom right
            4.0f,  2.5f, 2.0f }; // top right

    public float[] vertices5 = new float[]{
            -0.87f,  1.44f, 2.0f,   // top left
            -0.87f, -1.44f, 2.0f,   // bottom left
            0.87f, -1.44f, 2.0f,   // bottom right
            0.87f,  1.44f, 2.0f }; // top right

    public float[] girlVertices = new float[]{
            -0.435f,  0.2f, 0.0f,   // top left
            -0.435f, -1.235f, 0.0f,   // bottom left
            0.435f, -1.235f, 0.0f,   // bottom right
            0.435f,  0.2f, 0.0f }; // top right

    //Texture colors
    public float[] normalColor = new float[]
            {1f, 1f, 1f, 1f,
                    1f, 1f, 1f, 1f,
                    1f, 1f, 1f, 1f,
                    1f, 1f, 1f, 1f};

    public float[] normalColorGirl = new float[]
            {1f, 1f, 1f, opacity,
                    1f, 1f, 1f, opacity,
                    1f, 1f, 1f, opacity,
                    1f, 1f, 1f, opacity};

    public float[] nightColorGirl = new float[]
            {0.17f, 0.27f, 0.27f, opacity,
                    0.17f, 0.27f, 0.63921568627f, opacity,
                    0.17f, 0.27f, 0.63921568627f, opacity,
                    0.17f, 0.27f, 0.63921568627f, opacity};

    public float[] sunsetColor = new float[]
            {1.0f, 0.68235294117f, 0.63921568627f, 1.0f,
                    1.0f, 0.68235294117f, 0.63921568627f, 1.0f,
                    1.0f, 0.68235294117f, 0.63921568627f, 1.0f,
                    1.0f, 0.68235294117f, 0.63921568627f, 1.0f};
    public float[] nightColor = new float[]
                                    {0.17f, 0.27f, 0.27f, 1.0f,
                                    0.17f, 0.27f, 0.63921568627f, 1.0f,
                                    0.17f, 0.27f, 0.63921568627f, 1.0f,
                                    0.17f, 0.27f, 0.63921568627f, 1.0f};
    public float[] transparentColor = new float[]
            {1f, 1f, 1f, 0.3f,
                    1f, 1f, 1f, 0.3f,
                    1f, 1f, 1f, 0.3f,
                    1f, 1f, 1f, 0.3f
            };

}
