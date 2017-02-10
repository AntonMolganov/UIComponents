package com.example.uicomponents;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SearchBar extends FrameLayout {
    private static final int DESIRED_WIDTH_DP = 300;
    private static final int DESIRED_HEIGHT_DP = 50;


    private final static int DEFAULT_BG_COLOR = 0xffffffff;
    private final static int DEFAULT_TEXT_COLOR = 0xff000000;
    private final static float DEFAULT_TEXT_SIZE = 10f;
    private final static int DEFAULT_HINT_COLOR = 0xff808080;

    private final static float DEFAULT_RADIUS = 0f;

    private Context mContext;
    private RelativeLayout mRL;
    private ImageView mIVMenu;
    private ImageView mIVSearch;
    private EditText mEditText;

    private float mRadius = DEFAULT_RADIUS;
    private int mBgColor = DEFAULT_BG_COLOR;
    private int mTextColor = DEFAULT_TEXT_COLOR;
    private float mTextSize = DEFAULT_TEXT_SIZE;
    private String mHint = null;
    private int mHintColor = DEFAULT_HINT_COLOR;

    private int mMenuDrawableId = -1;
    private boolean mHasMenuDrawable = false;
    private int mSearchDrawableId = -1;
    private boolean mHasSearchDrawable = false;
    private int mClearDrawableId = -1;
    private boolean mHasClearDrawable = false;

    private OnClickListener mOnMenuClickListener = null;
    private OnClickListener mOnSearchClickListener = null;
    private TextWatcher mTextWatcher = null;
    private final OnClickListener mOnClearClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mEditText.getText().clear();
        }
    };

    public SearchBar(Context context) {
        super(context);
        setup(context, null);
    }

    public SearchBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs);
    }

    public SearchBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context, attrs);
    }

    public SearchBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
        super(context, attrs, defStyleAttr);
        setup(context, attrs);
    }


    private void setup(Context context, AttributeSet attrs){
        inflate(context, R.layout.searchbar, this);

        mContext = context;
        mRL = (RelativeLayout) findViewById(R.id.rl);
        mIVMenu = (ImageView) findViewById(R.id.img_menu);
        mIVSearch = (ImageView) findViewById(R.id.img_search);
        mEditText = (EditText) findViewById(R.id.edit);


        if (attrs != null){
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SearchBar);
            try{
                mBgColor = a.getColor(R.styleable.SearchBar_backgroundColor,DEFAULT_BG_COLOR);
                mRadius = a.getDimension(R.styleable.SearchBar_radius,DEFAULT_RADIUS);

                mMenuDrawableId = a.getResourceId(R.styleable.SearchBar_menuDrawable, -1);
                mHasMenuDrawable = (mMenuDrawableId != -1);

                mSearchDrawableId = a.getResourceId(R.styleable.SearchBar_searchDrawable, -1);
                mHasSearchDrawable = (mSearchDrawableId != -1);

                mClearDrawableId = a.getResourceId(R.styleable.SearchBar_clearDrawable, -1);
                mHasClearDrawable = (mClearDrawableId != -1);

                mTextColor = a.getColor(R.styleable.SearchBar_textColor,DEFAULT_TEXT_COLOR);
                mTextSize = a.getDimension(R.styleable.SearchBar_textSize,DEFAULT_TEXT_SIZE);
                mHint = a.getString(R.styleable.SearchBar_hint);
                mHintColor = a.getColor(R.styleable.SearchBar_hintColor,DEFAULT_HINT_COLOR);

            }finally {
                a.recycle();
            }
        }


        GradientDrawable gdr = new GradientDrawable();
        gdr.setShape(GradientDrawable.RECTANGLE);
        gdr.setColor(mBgColor);
        gdr.setCornerRadius(mRadius);
        mRL.setBackground(gdr);

        mIVMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                OnClickListener l = mOnMenuClickListener;
                if (l != null){
                    l.onClick(v);
                }
            }
        });
        mIVSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                OnClickListener l = mOnSearchClickListener;
                if (l != null){
                    l.onClick(v);
                }
            }
        });

        if (mHasMenuDrawable) {
            mIVMenu.setImageDrawable(context.getResources().getDrawable(mMenuDrawableId));
        }
        if (mHasSearchDrawable){
            mIVSearch.setImageDrawable(context.getResources().getDrawable(mSearchDrawableId));
        }


        mEditText.setTextColor(mTextColor);
        mEditText.setTextSize(mTextSize);
        mEditText.setHint(mHint);
        mEditText.setHintTextColor(mHintColor);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                TextWatcher t = mTextWatcher;
                if (t!=null) t.beforeTextChanged(s,start,count,after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()>0){
                    if (mHasClearDrawable){
                        mIVSearch.setImageDrawable(mContext.getResources().getDrawable(mClearDrawableId));

                    }else{
                        mIVSearch.setImageDrawable(null);
                    }
                    mIVSearch.setOnClickListener(mOnClearClickListener);
                }else{
                    if (mHasSearchDrawable){
                        mIVSearch.setImageDrawable(mContext.getResources().getDrawable(mSearchDrawableId));
                    }else{
                        mIVSearch.setImageDrawable(null);
                    }
                    mIVSearch.setOnClickListener(mOnSearchClickListener);
                }
                TextWatcher t = mTextWatcher;
                if (t!=null) t.onTextChanged(s,start,before,count);
            }

            @Override
            public void afterTextChanged(Editable s) {
                TextWatcher t = mTextWatcher;
                if (t!=null) t.afterTextChanged(s);
            }
        });
    }

    public void setOnMenuClickListener(OnClickListener l) {
        mOnMenuClickListener = l;
    }

    public void setOnSearchClickListener(OnClickListener l) {
        mOnSearchClickListener = l;
    }

    public void addTextChangedListener(TextWatcher tv){
        mTextWatcher = tv;
    }

    public void setOnEditorActionListener(TextView.OnEditorActionListener l){
        mEditText.setOnEditorActionListener(l);
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int desiredWidth = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DESIRED_WIDTH_DP, getResources().getDisplayMetrics()));
        int desiredHeight = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DESIRED_HEIGHT_DP, getResources().getDisplayMetrics()));


        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }



        final int count = getChildCount();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            child.measure(MeasureSpec.makeMeasureSpec(width, widthMode), MeasureSpec.makeMeasureSpec(height, heightMode));
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }
}
