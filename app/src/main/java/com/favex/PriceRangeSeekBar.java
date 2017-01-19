package com.favex;

import android.content.Context;
import android.util.AttributeSet;
import org.florescu.android.rangeseekbar.RangeSeekBar;

/**
 * Created by Tavish on 19-Jan-17.
 */

public class PriceRangeSeekBar extends RangeSeekBar{
    public PriceRangeSeekBar(Context context) {
        super(context);
    }
    public PriceRangeSeekBar(Context context, AttributeSet attrs) {
        super(context,attrs);
    }
    public PriceRangeSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected String valueToString(Number value) {
        String s = String.valueOf(value.intValue());
        if (value.intValue() == 500)
            return (s + "+");
        return s;
    }
}
