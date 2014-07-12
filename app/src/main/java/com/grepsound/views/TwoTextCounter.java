package com.grepsound.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.grepsound.R;

public class TwoTextCounter extends RelativeLayout {
    LayoutInflater inflater = null;
    TextView counter, label;

    public TwoTextCounter(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews();
    }

    public TwoTextCounter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public TwoTextCounter(Context context) {
        super(context);
        initViews();
    }

    void initViews() {
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.counter_with_label, this, true);
        counter = (TextView) findViewById(R.id.counter);
        label = (TextView) findViewById(R.id.label);
    }

    public void setCounter(int value) {
        counter.setText(""+value);
    }

    public void setLabel(String value) {
        label.setText(value);
    }

    /**
     * Animate the counter value from 0 to the given @param value
     * @param value final value
     * @param duration duration of animation in ms
     */
    public void setCounterWithAnimation(int value, int duration) {
        counter.setText("0");
    }
}
