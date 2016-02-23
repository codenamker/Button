package com.example.flatbutton;

import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity
        implements CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener {

    private TButton twitterBtn; 
    private TextView shadowHeight;
    private SeekBar shadowHeightBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        twitterBtn = (TButton) findViewById(R.id.f_twitter_button);       
        ToggleButton shadowSwitch = (ToggleButton) findViewById(R.id.enable_shadow_switch);
        shadowHeightBar = (SeekBar) findViewById(R.id.shadow_height_seekbar);
        shadowHeight = (TextView) findViewById(R.id.shadow_height_value);       
        shadowSwitch.setOnCheckedChangeListener(this);
        shadowHeightBar.setOnSeekBarChangeListener(this);

      
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        twitterBtn.setShadowEnabled(isChecked);
        updateShadowHeight(shadowHeightBar.getProgress());
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        shadowHeight.setText(progress + "dp");
        updateShadowHeight(progress);
    }

    private void updateShadowHeight(int height) {
        //Convert from dp to pixel
        int shadowHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, getResources().getDisplayMetrics());
        twitterBtn.setShadowHeight(shadowHeight);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
