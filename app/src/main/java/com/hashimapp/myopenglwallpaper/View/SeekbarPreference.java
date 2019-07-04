package com.hashimapp.myopenglwallpaper.View;

import android.content.Context;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hashimapp.myopenglwallpaper.R;

public class SeekbarPreference extends DialogPreference {


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

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        // view is your layout expanded and added to the dialog
        // find and hang on to your views here, add click listeners etc
        // basically things you would do in onCreate
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            // deal with persisting your values here
        }
    }

}
