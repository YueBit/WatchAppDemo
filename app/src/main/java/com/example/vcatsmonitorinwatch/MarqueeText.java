package com.example.vcatsmonitorinwatch;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Class for show MarqueeText for multiple TextViews at the same time
 */
public class MarqueeText extends androidx.appcompat.widget.AppCompatTextView{

    public MarqueeText(Context context){
        super(context);
    }

    public MarqueeText(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    public MarqueeText(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    public boolean isFocused(){
        return true;
    }
}