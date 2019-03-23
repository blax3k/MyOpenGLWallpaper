package com.hashimapp.myopenglwallpaper.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

/**
 * Created by Blake on 12/31/2015.
 */
public class BlurBuilder
{

    public static Bitmap blur(Context context, Bitmap image, float blurRadius, int width, int height) {

        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        if(blurRadius != 0)
        {
            RenderScript rs = RenderScript.create(context);
            ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
            Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
            theIntrinsic.setRadius(blurRadius);
            theIntrinsic.setInput(tmpIn);
            theIntrinsic.forEach(tmpOut);
            tmpOut.copyTo(outputBitmap);
        }
        outputBitmap = Bitmap.createScaledBitmap(outputBitmap, image.getWidth(), image.getHeight(), false);

        return outputBitmap.copy(Bitmap.Config.ARGB_4444, true);
    }
}