package com.hashimapp.myopenglwallpaper.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.FloatRange;
import android.util.Log;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Blake on 12/31/2015.
 */
public class BlurBuilder
{
    private static final AtomicReference<RenderScript> sRenderscript = new AtomicReference<>();

    public static Bitmap blur(Context context, Bitmap bitmapOriginal, @FloatRange(from = 0.0f, to = 25.0f) float radius, boolean overrideOriginal, boolean recycleOriginal)
    {
        if (bitmapOriginal == null || bitmapOriginal.isRecycled())
            return null;
        RenderScript rs = sRenderscript.get();
        if (rs == null)
            if (!sRenderscript.compareAndSet(null, rs = RenderScript.create(context)) && rs != null)
                rs.destroy();
            else
                rs = sRenderscript.get();
        final Bitmap inputBitmap = bitmapOriginal.getConfig() == Bitmap.Config.ARGB_8888 ? bitmapOriginal : bitmapOriginal.copy(Bitmap.Config.ARGB_8888, true);
        final Bitmap outputBitmap = overrideOriginal ? bitmapOriginal : Bitmap.createScaledBitmap(bitmapOriginal, bitmapOriginal.getWidth()/128, bitmapOriginal.getHeight()/128, true);
        final Allocation input = Allocation.createFromBitmap(rs, inputBitmap);
        final Allocation output = Allocation.createTyped(rs, input.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(radius);
        script.setInput(input);
        script.forEach(output);
        if (recycleOriginal && !overrideOriginal)
            bitmapOriginal.recycle();
        output.copyTo(outputBitmap);
        return outputBitmap;
    }


    public static Bitmap blur(Context context, Bitmap image, float bitmapWidthScale, float bitmapHeightScale, @FloatRange(from = 0.0f, to = 25.0f) float blurRadius)
    {
        int width = Math.round(image.getWidth() * bitmapWidthScale);
        int height = Math.round(image.getHeight() * bitmapHeightScale);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript rs = RenderScript.create(context);

        ScriptIntrinsicBlur intrinsicBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);

        intrinsicBlur.setRadius(blurRadius);
        intrinsicBlur.setInput(tmpIn);
        intrinsicBlur.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);

        return outputBitmap;
    }


}