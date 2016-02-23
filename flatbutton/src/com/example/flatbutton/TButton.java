package com.example.flatbutton;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;





/**
 * Created by hoang8f on 5/5/14.
 */

public class TButton extends Button implements View.OnTouchListener {

    //Custom values
    private boolean isShadowEnabled = true;
    private int mButtonColor;
    private int mShadowColor;
    private int mShadowHeight;
    private int mCornerRadius;//顏色
    //Native values
    private int mPaddingLeft;
    private int mPaddingRight;
    private int mPaddingTop;
    private int mPaddingBottom;
    //Background drawable
    private Drawable pressedDrawable;
    private Drawable unpressedDrawable;

    boolean isShadowColorDefined = false;

    public TButton(Context context) {
        super(context);
        init();
        this.setOnTouchListener(this);
    }

    public TButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        parseAttrs(context, attrs);
        this.setOnTouchListener(this);
    }

    public TButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        parseAttrs(context, attrs);
        this.setOnTouchListener(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //Update background color
        refresh();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                updateBackground(pressedDrawable);
                //左,上,右,下
                this.setPadding(mPaddingLeft, mPaddingTop + mShadowHeight, mPaddingRight, mPaddingBottom);
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                updateBackground(unpressedDrawable);
                this.setPadding(mPaddingLeft, mPaddingTop + mShadowHeight, mPaddingRight, mPaddingBottom + mShadowHeight);
                break;
        }
        return false;
    }

    private void init() {
        //Init default values
        isShadowEnabled = true;
        Resources resources = getResources();
        if (resources == null) return;
        mButtonColor = resources.getColor(R.color.fbutton_default_color);
        mShadowColor = resources.getColor(R.color.fbutton_default_shadow_color);
        mShadowHeight = resources.getDimensionPixelSize(R.dimen.fbutton_default_shadow_height);
      
    }

    private void parseAttrs(Context context, AttributeSet attrs) {
        //Load from custom attributes
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TButton);
        if (typedArray == null) return;
        for (int i = 0; i < typedArray.getIndexCount(); i++) {
            int attr = typedArray.getIndex(i);
            if (attr == R.styleable.TButton_shadowEnabled) {
                isShadowEnabled = typedArray.getBoolean(attr, true); //Default is true
            }  else if (attr == R.styleable.TButton_shadowHeight) {
                mShadowHeight = typedArray.getDimensionPixelSize(attr, R.dimen.fbutton_default_shadow_height);
            } 
        }
        typedArray.recycle();

        //Get paddingLeft, paddingRight
        int[] attrsArray = new int[]{
                android.R.attr.paddingLeft,  // 0
                android.R.attr.paddingRight, // 1
        };
        TypedArray ta = context.obtainStyledAttributes(attrs, attrsArray);
        if (ta == null) return;
        mPaddingLeft = ta.getDimensionPixelSize(0, 0);
        mPaddingRight = ta.getDimensionPixelSize(1, 0);
        ta.recycle();

        //Get paddingTop, paddingBottom
        int[] attrsArray2 = new int[]{
                android.R.attr.paddingTop,   // 0
                android.R.attr.paddingBottom,// 1
        };
        TypedArray ta1 = context.obtainStyledAttributes(attrs, attrsArray2);
        if (ta1 == null) return;
        mPaddingTop = ta1.getDimensionPixelSize(0, 0);
        mPaddingBottom = ta1.getDimensionPixelSize(1, 0);
        ta1.recycle();
    }

    public void refresh() {
        int alpha = Color.alpha(mButtonColor);
        float[] hsv = new float[3];
        Color.colorToHSV(mButtonColor, hsv);
        hsv[2] *= 0.8f; // value component
        //if shadow color was not defined, generate shadow color = 80% brightness
        if (!isShadowColorDefined) {
            mShadowColor = Color.HSVToColor(alpha, hsv);
        }
        //Create pressed background and unpressed background drawables

            if (isShadowEnabled) {
                pressedDrawable = createDrawable(mCornerRadius, Color.TRANSPARENT, mButtonColor);
                unpressedDrawable = createDrawable(mCornerRadius, mButtonColor, mShadowColor);
            } else {
                mShadowHeight = 0;
                pressedDrawable = createDrawable(mCornerRadius, mShadowColor, Color.TRANSPARENT);
                unpressedDrawable = createDrawable(mCornerRadius, mButtonColor, Color.TRANSPARENT);
            }

        updateBackground(unpressedDrawable);
        //Set padding
        this.setPadding(mPaddingLeft, mPaddingTop + mShadowHeight, mPaddingRight, mPaddingBottom + mShadowHeight);
    }

    private void updateBackground(Drawable background) {
        if (background == null) return;
        //Set button background
        if (Build.VERSION.SDK_INT >= 16) {
            this.setBackground(background);
        } else {
            this.setBackgroundDrawable(background);
        }
    }
    private LayerDrawable createDrawable(int radius, int topColor, int bottomColor) {

        float[] outerRadius = new float[]{radius, radius, radius, radius, radius, radius, radius, radius};

        //Top
        RoundRectShape topRoundRect = new RoundRectShape(outerRadius, null, null);
        ShapeDrawable topShapeDrawable = new ShapeDrawable(topRoundRect);
        topShapeDrawable.getPaint().setColor(topColor);
        //Bottom
        RoundRectShape roundRectShape = new RoundRectShape(outerRadius, null, null);
        ShapeDrawable bottomShapeDrawable = new ShapeDrawable(roundRectShape);
        bottomShapeDrawable.getPaint().setColor(bottomColor);
        //Create array
        Drawable[] drawArray = {bottomShapeDrawable, topShapeDrawable};
        LayerDrawable layerDrawable = new LayerDrawable(drawArray);

        //上方按下效果
        if (isShadowEnabled && topColor != Color.TRANSPARENT) {
            //unpressed drawable
            layerDrawable.setLayerInset(0, 0, 0, 0, 0);  /*index, left, top, right, bottom*/
        } else {
            //pressed drawable
            layerDrawable.setLayerInset(0, 0, mShadowHeight, 0, 0);  /*index, left, top, right, bottom*/
        }
        //下方陰影按下效果
        layerDrawable.setLayerInset(1, 0, 0, 0, mShadowHeight);  /*index, left, top, right, bottom*/

        return layerDrawable;
    }

    //Setter
    public void setShadowEnabled(boolean isShadowEnabled) {//開啟陰影
        this.isShadowEnabled = isShadowEnabled;
        setShadowHeight(0);
        refresh();
    }

    public void setShadowHeight(int shadowHeight) {//陰影高度
        this.mShadowHeight = shadowHeight;
        refresh();
    } 

}