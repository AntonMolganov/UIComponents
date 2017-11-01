package com.example.uicomponents;

import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;


public class MultistateSwitch extends RelativeLayout implements View.OnClickListener {
    private static final int ANIMATION_DURATION = 150;

    private static final int DIRECTION_RIGHT = 0;
    private static final int DIRECTION_LEFT = 1;
    private static final int DIRECTION_BOUNCE = 2;

    private static final float ENABLED_ALPHA_VALUE = 1f;
    private static final float DISABLED_ALPHA_VALUE = 0.6f;

    private static final int DEFAULT_STATES_QTY = 2;
    private static final int DEFAULT_STATE = 0;
    private static final boolean DEFAULT_ENABLED = true;
    private static final int DEFAULT_DIRECTION = DIRECTION_RIGHT;
    private static final float DEFAULT_TOGGLE_SCALE = 1f;
    private static final int DEFAULT_BAR_COLOR = 0xff4d4d4d;
    private static final int DEFAULT_TOGGLE_COLOR = 0xffb9b9b9;
    private static final float DEFAULT_TOGGLE_RELATIVE_PADDING = 0.1f;



    private static final String BUNDLE_KEY_STATE = "CurrentState";
    private static final String BUNDLE_KEY_STATES_QTY = "StateQty";
    private static final String BUNDLE_KEY_DIRECTION = "Direction";
    private static final String BUNDLE_KEY_ENABLED = "Enabled";
    private static final String BUNDLE_KEY_BAR_COLOR = "BarColor";
    private static final String BUNDLE_KEY_TOGGLE_COLOR = "ToggleColor";
    private static final String BUNDLE_BAR_COLORS = "BarColors";
    private static final String BUNDLE_TOGGLE_COLORS = "ToggleColors";



    private LayoutTransition mLayoutTransition;
    private RelativeLayout mParentLayout;
    private ToggleImageView mToggle;
    private ImageView mBar;

    private int mCurrentState = DEFAULT_STATE;
    private int mStatesQty = DEFAULT_STATES_QTY;
    private boolean mEnabled;
    private int mDirection;
    private int mCurrentDirection;

    private int mSpecifiedDivisionLength;
    private int mDivisionLength;
    private float mToggleScale;
    private float mToggleRelativePadding;

    private int mBarColor;
    private int[] mBarColors;

    private int mToggleColor;
    private int[] mToggleColors;

    private Drawable mToggleDrawable;
    private Drawable[] mToggleDrawables;

    private TextView mLabelTextView;
    private CharSequence[] mLabelStrings;


    private final Collection<StateChangedListener> mListeners = Collections.synchronizedCollection(new LinkedList<StateChangedListener>());
    private int mWidth;
    private int mHeight;


    public MultistateSwitch(Context context) {
        this(context, null);
    }

    public MultistateSwitch(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultistateSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        removeAllViews();
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.multistateswitch, this, true);
        mToggle = (ToggleImageView) findViewById(R.id.toggle);
        mBar = (ImageView) findViewById(R.id.bar);
        mParentLayout = (RelativeLayout) findViewById(R.id.parent);
        if (mLayoutTransition == null) {
            mLayoutTransition = new LayoutTransition();
            mLayoutTransition.setDuration(ANIMATION_DURATION);
            mLayoutTransition.enableTransitionType(LayoutTransition.CHANGING);
            mLayoutTransition.setInterpolator(LayoutTransition.CHANGING, new FastOutLinearInInterpolator());
        }

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MultistateSwitch, defStyleAttr, 0);
        try {
            mCurrentState = typedArray.getInt(R.styleable.MultistateSwitch_current_state, DEFAULT_STATE);
            mStatesQty = typedArray.getInt(R.styleable.MultistateSwitch_states_qty, DEFAULT_STATES_QTY);
            if (mStatesQty < 2) mStatesQty = 2;
            if (mCurrentState > mStatesQty -1) mCurrentState = mStatesQty -1;
            if (mCurrentState < 0) mCurrentState = 0;
            mEnabled = typedArray.getBoolean(R.styleable.MultistateSwitch_enabled, true);
            mDirection = typedArray.getInt(R.styleable.MultistateSwitch_direction, DIRECTION_RIGHT);
            if (mCurrentState < mStatesQty-1 ) {
                mCurrentDirection = DIRECTION_RIGHT;
            }else{
                mCurrentDirection = DIRECTION_LEFT;
            }

            mBarColor = typedArray.getColor(R.styleable.MultistateSwitch_barColor, DEFAULT_BAR_COLOR);
            mToggleColor = typedArray.getColor(R.styleable.MultistateSwitch_toggleColor, DEFAULT_TOGGLE_COLOR);
            mToggleScale = typedArray.getFloat(R.styleable.MultistateSwitch_toggleScale, DEFAULT_TOGGLE_SCALE);
            mToggleRelativePadding = typedArray.getFloat(R.styleable.MultistateSwitch_toggleRelativePadding, DEFAULT_TOGGLE_RELATIVE_PADDING);

            mSpecifiedDivisionLength = getResources().getDimensionPixelSize(R.dimen.multistateswitch_default_division_length);
            int toggle_drawable_id = typedArray.getResourceId(R.styleable.MultistateSwitch_toggleDrawable, 0);
            if (toggle_drawable_id == 0) {
                mToggleDrawable = null;
            } else {
                mToggleDrawable = ContextCompat.getDrawable(getContext(), toggle_drawable_id);
            }
        } finally {
            typedArray.recycle();
        }


        setState(mCurrentState, false);

        setOnClickListener(this);
    }

    public int getState(){
        return mCurrentState;
    }

    public void setState(int state, boolean animated) {
        if (animated){
            mParentLayout.setLayoutTransition(mLayoutTransition);
            setLayoutTransition(mLayoutTransition);
        }else{
            mParentLayout.setLayoutTransition(null);
            setLayoutTransition(null);
        }
        mCurrentState = state;
        if (mDirection == DIRECTION_BOUNCE) {
            if (mCurrentState == mStatesQty -1) mCurrentDirection = DIRECTION_LEFT;
            if (mCurrentState == 0) mCurrentDirection = DIRECTION_RIGHT;
        }

        setPositions();
        setDrawables();
        setLabel();
        notifyListeners();
    }

    private int getNextState() {
        if (mDirection == DIRECTION_RIGHT) {
            mCurrentState++;
            if (mCurrentState > mStatesQty -1) mCurrentState = 0;
        }else if (mDirection == DIRECTION_LEFT){
            mCurrentState--;
            if (mCurrentState < 0) mCurrentState = mStatesQty -1;
        }else if (mDirection == DIRECTION_BOUNCE) {
            if (mCurrentDirection == DIRECTION_RIGHT){
                if (mCurrentState < mStatesQty -1) mCurrentState++;
                if (mCurrentState == mStatesQty -1){
                    mCurrentDirection = DIRECTION_LEFT;
                }
            }else{
                if (mCurrentState > 0) mCurrentState--;
                if (mCurrentState == 0) {
                    mCurrentDirection = DIRECTION_RIGHT;
                }
            }
        }
        return mCurrentState;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);


        int default_height = getResources().getDimensionPixelSize(R.dimen.multistateswitch_default_height);

        if (widthMode != MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY){
            mHeight = default_height;
            if ((heightMode == MeasureSpec.UNSPECIFIED) || (heightMode == MeasureSpec.AT_MOST && mHeight < MeasureSpec.getSize(heightMeasureSpec))) heightMeasureSpec = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY);
            mWidth = mSpecifiedDivisionLength * (mStatesQty - 1) + mHeight;
            if ((widthMode == MeasureSpec.UNSPECIFIED) || (widthMode == MeasureSpec.AT_MOST && mWidth < MeasureSpec.getSize(widthMeasureSpec))) widthMeasureSpec = MeasureSpec.makeMeasureSpec(mWidth, MeasureSpec.EXACTLY);
        }else if (widthMode != MeasureSpec.EXACTLY){
            mHeight = MeasureSpec.getSize(heightMeasureSpec);
            mWidth = mSpecifiedDivisionLength * (mStatesQty - 1) + mHeight;
            if ((widthMode == MeasureSpec.UNSPECIFIED) || (widthMode == MeasureSpec.AT_MOST && mWidth < MeasureSpec.getSize(widthMeasureSpec))) widthMeasureSpec = MeasureSpec.makeMeasureSpec(mWidth, MeasureSpec.EXACTLY);
        }else if (heightMode != MeasureSpec.EXACTLY){
            mWidth = MeasureSpec.getSize(widthMeasureSpec);
            if (mWidth < default_height) {
                mHeight = mWidth;
            }else{
                mHeight = default_height;
            }
            if ((heightMode == MeasureSpec.UNSPECIFIED) || (heightMode == MeasureSpec.AT_MOST && mHeight < MeasureSpec.getSize(heightMeasureSpec))) heightMeasureSpec = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY);
        }
        if (MeasureSpec.getSize(widthMeasureSpec) < MeasureSpec.getSize(heightMeasureSpec)) heightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getMode(heightMeasureSpec));

        mDivisionLength = Math.round(((float)MeasureSpec.getSize(widthMeasureSpec) - MeasureSpec.getSize(heightMeasureSpec)) / (mStatesQty - 1));

        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        setPositions();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void setPositions() {
        //set bar
        if (mToggleScale > 1){
            int barHeight = Math.round(mHeight / mToggleScale);
            int barMargin = Math.round(((float)mHeight - barHeight)/2);
            MarginLayoutParams barParams = ((MarginLayoutParams) mBar.getLayoutParams());
            barParams.setMargins(barMargin,
                    barMargin,
                    barMargin,
                    barMargin);
            mBar.setLayoutParams(barParams);
        }

        //set toggle
        MarginLayoutParams toggleParams = ((MarginLayoutParams) mToggle.getLayoutParams());
        if (mToggleScale < 1){


            int toggleSize = Math.round(mHeight * mToggleScale);
            int toggleVerticalMargin = Math.round((mHeight - toggleSize) / 2);
            toggleSize = mHeight - toggleVerticalMargin * 2;

            int toggleLeftMargin = (mCurrentState != mStatesQty - 1) ? (toggleVerticalMargin + mDivisionLength * mCurrentState) : (mWidth - toggleSize - toggleVerticalMargin);
            toggleParams.setMargins(toggleLeftMargin,
                    toggleVerticalMargin,
                    toggleParams.rightMargin,
                    toggleVerticalMargin);
        }else{
            int toggleSize = mHeight;
            int toggleLeftMargin = (mCurrentState != mStatesQty - 1) ? (mDivisionLength * mCurrentState) : (mWidth - toggleSize);
            toggleParams.setMargins(toggleLeftMargin,
                    toggleParams.topMargin,
                    toggleParams.rightMargin,
                    toggleParams.bottomMargin);
        }
        mToggle.setLayoutParams(toggleParams);
        int padding = Math.round(mToggle.getWidth() * mToggleRelativePadding);
        mToggle.setPadding(padding, padding, padding, padding);
    }

    private void setDrawables() {
        Drawable nextBarDrawable = ContextCompat.getDrawable(getContext(), R.drawable.multistateswitch);
        ((GradientDrawable) nextBarDrawable).setColor(getBarColor(mCurrentState));
        if (mBar.getDrawable() == null) {
            mBar.setImageDrawable(nextBarDrawable);
        }else{
            Drawable currentBarDrawable = mBar.getDrawable() instanceof TransitionDrawable ? ((TransitionDrawable) mBar.getDrawable()).getDrawable(1) : mBar.getDrawable();
            TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{ currentBarDrawable, nextBarDrawable});
            mBar.setImageDrawable(transitionDrawable);
            transitionDrawable.startTransition(ANIMATION_DURATION);
        }

        Drawable nextToggleBackgroundDrawable = ContextCompat.getDrawable(getContext(), R.drawable.multistateswitch);
        ((GradientDrawable) nextToggleBackgroundDrawable).setColor(getToggleColor(mCurrentState));
        if (mToggle.getBackground() == null) {
            mToggle.setBackground(nextToggleBackgroundDrawable);
        }else{
            Drawable currentToggleBackgroundDrawable = mToggle.getBackground() instanceof TransitionDrawable ? ((TransitionDrawable) mToggle.getBackground()).getDrawable(1) : mToggle.getBackground();
            TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{currentToggleBackgroundDrawable, nextToggleBackgroundDrawable});
            mToggle.setBackground(transitionDrawable);

            transitionDrawable.startTransition(ANIMATION_DURATION);
        }

        Drawable nextToggleDrawable = getToggleDrawable(mCurrentState);
        if (mToggle.getDrawable() == null) {
            mToggle.setImageDrawable(nextToggleDrawable);
        }else{
            Drawable currentToggleDrawable = mToggle.getDrawable() instanceof TransitionDrawable ? ((TransitionDrawable) mToggle.getDrawable()).getDrawable(1) : mToggle.getDrawable();
            TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{ currentToggleDrawable, nextToggleDrawable});
            mToggle.setImageDrawable(transitionDrawable);
            transitionDrawable.setCrossFadeEnabled(true);
            transitionDrawable.startTransition(ANIMATION_DURATION);
        }

        setAlpha(mEnabled ? ENABLED_ALPHA_VALUE : DISABLED_ALPHA_VALUE);
    }

    private int getBarColor(int state){
        if (mBarColors == null || mBarColors.length == 0) {
            return mBarColor;
        }else{
            if (mCurrentState > mBarColors.length - 1) return mBarColors[mBarColors.length - 1];
            if (mCurrentState < 0) return mBarColors[0];
            return mBarColors[state];
        }
    }

    public void setBarColors(int[] colors){
        mBarColors = colors;
        setState(mCurrentState, false);
    }

    private int getToggleColor(int state){
        if (mToggleColors == null || mToggleColors.length == 0) {
            return mToggleColor;
        }else{
            if (state > mToggleColors.length - 1) return mToggleColors[mToggleColors.length - 1];
            if (state < 0) return mToggleColors[0];
            return mToggleColors[state];
        }
    }

    public void setToggleColors(int[] colors){
        mToggleColors = colors;
        setState(mCurrentState, false);
    }

    private Drawable getToggleDrawable(int state){
        if (mToggleDrawables == null || mToggleDrawables.length == 0) {
            return mToggleDrawable;
        }else{
            if (state > mToggleDrawables.length - 1) return mToggleDrawables[mToggleDrawables.length - 1];
            if (state < 0) return mToggleDrawables[0];
            return mToggleDrawables[state];
        }
    }

    public void setToggleDrawables(Drawable[] drawables){
        mToggleDrawables = drawables;
        setState(mCurrentState, false);
    }

    public void attachTextView(TextView textview, CharSequence[] strings){
        this.mLabelTextView = textview;
        this.mLabelStrings = strings;
    }

    private void setLabel(){
        if (mLabelTextView != null && mLabelStrings != null && mLabelStrings.length > 0 ) {
            CharSequence text;
            if (mCurrentState > mLabelStrings.length - 1) {
                text = mLabelStrings[mLabelStrings.length - 1];
            }else if (mCurrentState < 0) {
                text = mLabelStrings[0];
            }else{
                text = mLabelStrings[mCurrentState];
            }
            mLabelTextView.setText(text);
        }
    }

    @Override
    public void onClick(View view) {
        if (mEnabled) {
            setState(getNextState(), true);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mEnabled = enabled;
        setState(mCurrentState, false);
    }

    public void addStateChangedListener(StateChangedListener listener) {
        if (listener == null) return;
        mListeners.add(listener);
    }

    public void removeStateChangedListener(StateChangedListener listener) {
        if (listener != null && mListeners != null) mListeners.removeAll(Collections.singletonList(listener));
    }

    public void removeAllStateChangedListeners() {
        if (mListeners != null) mListeners.clear();
    }

    private void notifyListeners() {
        if (mListeners != null) {
            synchronized (mListeners){
                for (StateChangedListener l : mListeners){
                    l.onStateChange(this, mCurrentState);
                }
            }
        }
    }

    public interface StateChangedListener {
        void onStateChange(MultistateSwitch s, int state);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putInt(BUNDLE_KEY_STATE, mCurrentState);
        bundle.putInt(BUNDLE_KEY_STATES_QTY, mStatesQty);
        bundle.putBoolean(BUNDLE_KEY_ENABLED, mEnabled);
        bundle.putInt(BUNDLE_KEY_DIRECTION, mDirection);
        bundle.putInt(BUNDLE_KEY_BAR_COLOR, mBarColor);
        bundle.putInt(BUNDLE_KEY_TOGGLE_COLOR, mToggleColor);
        bundle.putIntArray(BUNDLE_BAR_COLORS, mBarColors);
        bundle.putIntArray(BUNDLE_TOGGLE_COLORS, mToggleColors);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        if (state instanceof Bundle) // implicit null check
        {
            Bundle prevState = (Bundle) state;
            mCurrentState = prevState.getInt(BUNDLE_KEY_STATE, DEFAULT_STATE);
            mStatesQty = prevState.getInt(BUNDLE_KEY_STATES_QTY, DEFAULT_STATES_QTY);
            mEnabled = prevState.getBoolean(BUNDLE_KEY_ENABLED, DEFAULT_ENABLED);
            mDirection = prevState.getInt(BUNDLE_KEY_DIRECTION, DEFAULT_DIRECTION);
            mDirection = prevState.getInt(BUNDLE_KEY_DIRECTION, DEFAULT_DIRECTION);
            mBarColor = prevState.getInt(BUNDLE_KEY_BAR_COLOR, DEFAULT_BAR_COLOR);
            mToggleColor = prevState.getInt(BUNDLE_KEY_TOGGLE_COLOR, DEFAULT_TOGGLE_COLOR);
            mBarColors = prevState.getIntArray(BUNDLE_BAR_COLORS);
            mToggleColors = prevState.getIntArray(BUNDLE_TOGGLE_COLORS);
            setState(mCurrentState, false);
            notifyListeners();
        }
        super.onRestoreInstanceState(state);
    }

    public static class ToggleImageView extends ImageView {

        public ToggleImageView(Context context) {
            super(context);
        }

        public ToggleImageView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public ToggleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public ToggleImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(heightMeasureSpec, heightMeasureSpec);
        }
    }


}
