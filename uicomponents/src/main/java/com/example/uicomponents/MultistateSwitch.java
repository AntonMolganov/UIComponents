package com.example.uicomponents;

import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;


public class MultistateSwitch extends RelativeLayout implements View.OnClickListener, View.OnLayoutChangeListener {
    private static final int ANIMATION_DURATION = 150;
    private static final int DEFAULT_STATES_QTY = 2;
    private static final int DEFAULT_STATE = 0;
    private static final boolean DEFAULT_ENABLED = true;
    private static final int DEFAULT_DIRECTION = DIRECTION_RIGHT;

    private static final int DIRECTION_RIGHT = 0;
    private static final int DIRECTION_LEFT = 1;
    private static final int DIRECTION_BOUNCE = 2;

    private static final float ENABLED_ALPHA_VALUE = 1.0f;
    private static final float DISABLED_ALPHA_VALUE = 0.6f;

    private static final int DEFAULT_BAR_COLOR = 0xFF7E7E7E;
    private static final int DEFAULT_TOGGLE_COLOR = 0xFF616161;
    
    private static final String BUNDLE_KEY_STATE = "CurrentState";
    private static final String BUNDLE_KEY_STATES_QTY = "StateQty";
    private static final String BUNDLE_KEY_DIRECTION = "Direction";
    private static final String BUNDLE_KEY_ENABLED = "Enabled";
    private static final String BUNDLE_KEY_BAR_COLOR = "BarColor";
    private static final String BUNDLE_KEY_TOGGLE_COLOR = "ToggleColor";
    private static final String BUNDLE_KEY_TOGGLE_DRAWABLE = "ToggleDrawable";
    private static final String BUNDLE_BAR_COLORS = "BarColors";
    private static final String BUNDLE_TOGGLE_COLORS = "ToggleColors";
    private static final String BUNDLE_TOGGLE_DRAWABLES = "ToggleDrawables";



    private LayoutTransition mLayoutTransition;
    private ToggleImageView mToggle;
    private ImageView mBar;
    private float mRealDivisionSize;

    private int mCurrentState = DEFAULT_STATE;
    private int mStatesQty = DEFAULT_STATES_QTY;
    private boolean mEnabled;
    private int mDirection;
    private int mCurrentDirection;

    private int mBarColor;
    private int[] mBarColors;

    private int mToggleColor;
    private int[] mToggleColors;

    private Drawable mToggleDrawable;
    private Drawable[] mToggleDrawables;



    private Collection<StateChangedListener> mListeners;

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
        RelativeLayout mParentLayout = (RelativeLayout) findViewById(R.id.parent);
        if (mLayoutTransition == null) {
            mLayoutTransition = new LayoutTransition();
            mLayoutTransition.setDuration(ANIMATION_DURATION);
            mLayoutTransition.enableTransitionType(LayoutTransition.CHANGING);
            mLayoutTransition.setInterpolator(LayoutTransition.CHANGING, new FastOutLinearInInterpolator());
        }
        setLayoutTransition(mLayoutTransition);
        mParentLayout.setLayoutTransition(mLayoutTransition);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MultistateSwitch, defStyleAttr, 0);
        try {
            mCurrentState = typedArray.getInt(R.styleable.MultistateSwitch_current_state, DEFAULT_STATE);
            mStatesQty = typedArray.getInt(R.styleable.MultistateSwitch_states_qty, DEFAULT_STATES_QTY);
            if (mStatesQty < 2) mStatesQty = 2;
            if (mCurrentState > mStatesQty -1) mCurrentState = mStatesQty -1;
            mEnabled = typedArray.getBoolean(R.styleable.MultistateSwitch_enabled, true);
            mDirection = typedArray.getInt(R.styleable.MultistateSwitch_direction, DIRECTION_RIGHT);

            mCurrentDirection = DIRECTION_RIGHT;
            mBarColor = typedArray.getColor(R.styleable.MultistateSwitch_barColor, DEFAULT_BAR_COLOR);
            mToggleColor = typedArray.getColor(R.styleable.MultistateSwitch_toggleColor, DEFAULT_TOGGLE_COLOR);

            int toggle_drawable_id = typedArray.getResourceId(R.styleable.MultistateSwitch_toggleDrawable, 0);
            if (toggle_drawable_id == 0) {
                mToggleDrawable = null;
            } else {
                mToggleDrawable = ContextCompat.getDrawable(getContext(), toggle_drawable_id);
            }
        } finally {
            typedArray.recycle();
        }

        setState(mCurrentState);
        addOnLayoutChangeListener(this);
        setOnClickListener(this);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = (Bundle) super.onSaveInstanceState();
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
        super.onRestoreInstanceState(state);
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
        setState(mCurrentState);
        notifyListeners();
    }


    @Override
    public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
        removeOnLayoutChangeListener(this);
        measure(getMeasuredWidth(), getMeasuredHeight());
    }

    public int getState(){
        return mCurrentState;
    }

    public void setState(int state) {
        mCurrentState = state;
        setAppearance();
        setTogglePosition();
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
                else if (mCurrentState == mStatesQty -1) {
                    mCurrentState--;
                    mCurrentDirection = DIRECTION_LEFT;
                }
            }else{
                if (mCurrentState > 0) mCurrentState--;
                else if (mCurrentState == 0) {
                    mCurrentState++;
                    mCurrentDirection = DIRECTION_RIGHT;
                }
            }
        }
        return mCurrentState;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);


        int defaultWidth = getResources().getDimensionPixelSize(R.dimen.multistateswitch_default_interval)*(mStatesQty -1)+mToggle.getWidth();

        if (widthMode != MeasureSpec.EXACTLY) {
            if ((widthMode == MeasureSpec.UNSPECIFIED) || (widthMode == MeasureSpec.AT_MOST && defaultWidth < MeasureSpec.getSize(widthMeasureSpec))) widthMeasureSpec = MeasureSpec.makeMeasureSpec(defaultWidth, MeasureSpec.EXACTLY);
        }

        int defaultHeight = getResources().getDimensionPixelSize(R.dimen.multistateswitch_default_height);
        if (heightMode != MeasureSpec.EXACTLY) {
            if ((heightMode == MeasureSpec.UNSPECIFIED) || (heightMode == MeasureSpec.AT_MOST && defaultHeight < MeasureSpec.getSize(heightMeasureSpec))) heightMeasureSpec = MeasureSpec.makeMeasureSpec(defaultHeight, MeasureSpec.EXACTLY);
        }

        if (MeasureSpec.getSize(widthMeasureSpec) < MeasureSpec.getSize(heightMeasureSpec)) heightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getMode(heightMeasureSpec));


        int barHeight = mToggle.getHeight() * 2 / 3;
        int margin = (mToggle.getHeight() - barHeight ) / 2;
        LayoutParams params = (LayoutParams) mBar.getLayoutParams();
        params.setMargins(margin, margin, margin, margin);
        mBar.setLayoutParams(params);

        mRealDivisionSize = ((float)mBar.getWidth() - barHeight) / (mStatesQty - 1);
        setTogglePosition();

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void setTogglePosition() {
        MarginLayoutParams toggleParams = ((MarginLayoutParams) mToggle.getLayoutParams());
        int margin = (mCurrentState != mStatesQty - 1) ? (int) (mRealDivisionSize * mCurrentState) : getWidth() - mToggle.getWidth();
        toggleParams.setMargins(margin,
                toggleParams.topMargin,
                toggleParams.rightMargin,
                toggleParams.bottomMargin);
        mToggle.setLayoutParams(toggleParams);
    }

    private void setAppearance() {

        Drawable nextBarDrawable = ContextCompat.getDrawable(getContext(), R.drawable.multistateswitch);
        ((GradientDrawable) nextBarDrawable).setColor(getBarColor(mCurrentState));
        if (mBar.getDrawable() == null) {
            mBar.setImageDrawable(nextBarDrawable);
        }else{
            Drawable currentBarDrawable = mBar.getDrawable() instanceof TransitionDrawable ? ((TransitionDrawable) mBar.getDrawable()).getDrawable(1) : mBar.getDrawable();
            TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{ currentBarDrawable, nextBarDrawable});
            transitionDrawable.setCrossFadeEnabled(true);
            mBar.setImageDrawable(transitionDrawable);
            transitionDrawable.startTransition(ANIMATION_DURATION);
        }

        Drawable nextToggleBackgroundDrawable = ContextCompat.getDrawable(getContext(), R.drawable.multistateswitch);
        ((GradientDrawable) nextToggleBackgroundDrawable).setColor(getToggleColor(mCurrentState));
        if (mToggle.getBackground() == null) {
            mToggle.setBackground(nextToggleBackgroundDrawable);
        }else{
            Drawable currentToggleBackgroundDrawable = mToggle.getBackground() instanceof TransitionDrawable ? ((TransitionDrawable) mToggle.getBackground()).getDrawable(1) : mToggle.getBackground();
            TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{ currentToggleBackgroundDrawable, nextToggleBackgroundDrawable});
            transitionDrawable.setCrossFadeEnabled(true);
            mToggle.setBackground(transitionDrawable);
            transitionDrawable.startTransition(ANIMATION_DURATION);
        }

        Drawable nextToggleDrawable = getToggleDrawable(mCurrentState);
        if (mToggle.getDrawable() == null) {
            mToggle.setImageDrawable(nextToggleDrawable);
        }else{
            Drawable currentToggleDrawable = mToggle.getDrawable() instanceof TransitionDrawable ? ((TransitionDrawable) mToggle.getDrawable()).getDrawable(1) : mToggle.getDrawable();
            TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{ currentToggleDrawable, nextToggleDrawable});
            transitionDrawable.setCrossFadeEnabled(true);
            mToggle.setImageDrawable(transitionDrawable);
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
        setState(mCurrentState);
    }

    private int getToggleColor(int state){
        if (mToggleColors == null || mToggleColors.length == 0) {
            return mToggleColor;
        }else{
            if (mCurrentState > mToggleColors.length - 1) return mToggleColors[mToggleColors.length - 1];
            if (mCurrentState < 0) return mToggleColors[0];
            return mToggleColors[state];
        }
    }

    public void setToggleColors(int[] colors){
        mToggleColors = colors;
        setState(mCurrentState);
    }

    private Drawable getToggleDrawable(int state){
        if (mToggleDrawables == null || mToggleDrawables.length == 0) {
            return mToggleDrawable;
        }else{
            if (mCurrentState > mToggleDrawables.length - 1) return mToggleDrawables[mToggleDrawables.length - 1];
            if (mCurrentState < 0) return mToggleDrawables[0];
            return mToggleDrawables[state];
        }
    }

    public void setToggleDrawables(Drawable[] drawables){
        mToggleDrawables = drawables;
        setState(mCurrentState);
    }

    @Override
    public void onClick(View view) {
        if (mEnabled) {
            setState(getNextState());
            notifyListeners();
        }
    }

    public void addStateChangedListener(StateChangedListener listener) {
        if (listener == null) return;
        if (mListeners == null) mListeners = Collections.synchronizedCollection(new LinkedList<StateChangedListener>());
        mListeners.add(listener);
    }

    public void removeStateChangedListener(StateChangedListener listener) {
        if (listener != null && mListeners != null) mListeners.removeAll(Collections.singletonList(listener));
    }

    public void removeAllListeners() {
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
