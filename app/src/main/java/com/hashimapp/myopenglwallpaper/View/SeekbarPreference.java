package com.hashimapp.myopenglwallpaper.View;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hashimapp.myopenglwallpaper.R;

public class SeekbarPreference extends Preference {


    public SeekbarPreference(Context context) {
        super(context);
    }

    public SeekbarPreference(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public SeekbarPreference(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        super.onCreateView(parent);
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return li.inflate(R.layout.seekbar_preference, parent, false);
    }
}
