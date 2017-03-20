package com.example.uicomponents;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class AdvancedSearchBar extends FrameLayout {
    private static final int DEFAULT_WIDTH_DP = 300;
    private static final int DEFAULT_HEIGHT_DP = 50;

    private final static int DEFAULT_BG_COLOR = 0xffffffff;

    private final static int DEFAULT_TEXT_COLOR = 0xff000000;
    private final static float DEFAULT_TEXT_SIZE_SP = 8f;
    private final static int DEFAULT_HINT_COLOR = 0xff808080;

    private final static int DEFAULT_MENU_DRAWABLE_ID = R.drawable.ic_menu_black_24dp;
    private final static int DEFAULT_SEARCH_DRAWABLE_ID = R.drawable.ic_keyboard_voice_black_24dp;
    private final static int DEFAULT_CLEAR_DRAWABLE_ID = R.drawable.ic_clear_black_24dp;

    private final static float DEFAULT_RADIUS_DP = 2.0f;

    private GradientDrawable gdr_all;
    private GradientDrawable gdr_top;
    private GradientDrawable gdr_bot;

    private Context mContext;

    private RelativeLayout mRLInput;
    private ImageView mIVMenu;
    private ImageView mIVSearch;
    private ImageView mIVClear;
    private RelativeLayout mRLLogo;
    private EditText mETSearchString;
    private ListView mLVResults;

    private RelativeLayout mDialog_RLInput;
    private ImageView mDialog_IVMenu;
    private ImageView mDialog_IVSearch;
    private ImageView mDialog_IVClear;
    private EditText mDialog_ETSearchString;
    private RelativeLayout mDialog_RLLogo;
    private ListView mDialog_LVResults;



    private float mRadius = DEFAULT_RADIUS_DP;
    private int mBgColor = DEFAULT_BG_COLOR;
    private int mTextColor = DEFAULT_TEXT_COLOR;
    private float mTextSize = DEFAULT_TEXT_SIZE_SP;
    private String mHint = null;
    private int mHintColor = DEFAULT_HINT_COLOR;
    private int mMenuDrawableId = -1;
    private int mSearchDrawableId = -1;
    private int mClearDrawableId = -1;

    private OnClickListener mOnMenuClickListener = null;
    private OnClickListener mOnSearchClickListener = null;

    private AdvancedSearchBarDialog mDialog;

    public AdvancedSearchBar(Context context) {
        super(context);
        setup(context, null);
    }

    public AdvancedSearchBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs);
    }

    public AdvancedSearchBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context, attrs);
    }

    public AdvancedSearchBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        setup(context, attrs);
    }

    private void setup(Context context, AttributeSet attrs){
        mContext = context;
        inflate(mContext, R.layout.advancedsearchbar, this);

        mRLInput = (RelativeLayout) findViewById(R.id.rl_input);
        mIVMenu = (ImageView) findViewById(R.id.img_menu);
        mIVSearch = (ImageView) findViewById(R.id.img_search);
        mIVClear = (ImageView) findViewById(R.id.img_clear);
        mRLLogo = (RelativeLayout) findViewById(R.id.rl_logo);
        mETSearchString = (EditText) findViewById(R.id.et_searchstring);
        mLVResults = (ListView) findViewById(R.id.lv_results);

        mIVClear.setVisibility(View.INVISIBLE);
        mRLLogo.setVisibility(View.VISIBLE);
        mETSearchString.setVisibility(View.INVISIBLE);
        mLVResults.setVisibility(View.GONE);


        if (attrs != null){
            TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.SearchBar);
            try{
                mBgColor = a.getColor(R.styleable.SearchBar_backgroundColor,DEFAULT_BG_COLOR);
                mRadius = a.getDimension(R.styleable.SearchBar_radius,convertDpToPixels(DEFAULT_RADIUS_DP,mContext));

                mMenuDrawableId = a.getResourceId(R.styleable.SearchBar_menuDrawable, DEFAULT_MENU_DRAWABLE_ID);
                mSearchDrawableId = a.getResourceId(R.styleable.SearchBar_searchDrawable, DEFAULT_SEARCH_DRAWABLE_ID);
                mClearDrawableId = a.getResourceId(R.styleable.SearchBar_clearDrawable, DEFAULT_CLEAR_DRAWABLE_ID);

                mTextColor = a.getColor(R.styleable.SearchBar_textColor,DEFAULT_TEXT_COLOR);
                mTextSize = a.getDimension(R.styleable.SearchBar_textSize,convertSpToPixels(DEFAULT_TEXT_SIZE_SP,mContext));
                mHint = a.getString(R.styleable.SearchBar_hint);
                mHintColor = a.getColor(R.styleable.SearchBar_hintColor,DEFAULT_HINT_COLOR);
            }finally {
                a.recycle();
            }
        }

        gdr_all = new GradientDrawable();
        gdr_all.setShape(GradientDrawable.RECTANGLE);
        gdr_all.setColor(mBgColor);
        gdr_all.setCornerRadius(mRadius);

        gdr_top = new GradientDrawable();
        gdr_top.setShape(GradientDrawable.RECTANGLE);
        gdr_top.setColor(mBgColor);
        float[] radii_top = new float[8];
        radii_top[0] = radii_top[1] = radii_top[2] = radii_top[3] = mRadius;
        radii_top[4] = radii_top[5] = radii_top[6] = radii_top[7] = 0;
        gdr_top.setCornerRadii(radii_top);

        gdr_bot = new GradientDrawable();
        gdr_bot.setShape(GradientDrawable.RECTANGLE);
        gdr_bot.setColor(mBgColor);
        float[] radii_bot = new float[8];
        radii_bot[0] = radii_bot[1] = radii_bot[2] = radii_bot[3] = 0;
        radii_bot[4] = radii_bot[5] = radii_bot[6] = radii_bot[7] = mRadius;
        gdr_bot.setCornerRadii(radii_bot);

        mRLInput.setBackground(gdr_all);




        mIVMenu.setImageDrawable(mContext.getResources().getDrawable(mMenuDrawableId));
        mIVSearch.setImageDrawable(mContext.getResources().getDrawable(mSearchDrawableId));
        mIVClear.setImageDrawable(mContext.getResources().getDrawable(mSearchDrawableId));

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
        mRLLogo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        setLogoView(inflate(mContext, R.layout.default_logo, null));


        mDialog = new AdvancedSearchBarDialog(mContext);
        addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                mDialog.dismiss();
            }
        });


    }

    private void showDialog(){
        applyCurrentLocation(mDialog);
        mDialog.show();
    }

    public void closeInput(){
        mDialog.dismiss();
    }

    public void setOnMenuClickListener(OnClickListener l) {
        mOnMenuClickListener = l;
    }

    public void setOnSearchClickListener(OnClickListener l) {
        mOnSearchClickListener = l;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener oicl){
        mDialog_LVResults.setOnItemClickListener(oicl);
    }

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener oilcl){
        mDialog_LVResults.setOnItemLongClickListener(oilcl);
    }

    public void setSearchAdapter(SearchAdapter adapter) {
        ListView l = mDialog_LVResults;
        if (l != null) l.setAdapter(adapter);
    }

    public void setLogoView(View logo){
        mRLLogo.removeAllViews();
        mRLLogo.addView(logo);
    }



    private void applyCurrentLocation(AdvancedSearchBarDialog d){
        int[] location = new int[2];
        getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        int w = getWidth();
        int h = getHeight();
        d.setLocation(x,y,w,h);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int desiredWidth = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_WIDTH_DP, getResources().getDisplayMetrics()));
        int desiredHeight = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_HEIGHT_DP, getResources().getDisplayMetrics()));

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            child.measure(MeasureSpec.makeMeasureSpec(width, widthMode), MeasureSpec.makeMeasureSpec(height, heightMode));
        }
        setMeasuredDimension(width, height);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed){
            AdvancedSearchBarDialog d = mDialog;
            if (d.isShowing()) applyCurrentLocation(d);
        }
    }




    private class AdvancedSearchBarDialog extends Dialog{
        private int origin_x,origin_y,origin_w,origin_h;

        private AdvancedSearchBarDialog(Context context) {
            super(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            setup();
        }

        private void setup(){
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            layoutParams.dimAmount = .4f;
            getWindow().setAttributes(layoutParams);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            setContentView(R.layout.advancedsearchbar);
            setCanceledOnTouchOutside(true);
            mDialog_RLInput = (RelativeLayout) findViewById(R.id.rl_input);
            mDialog_IVMenu = (ImageView) findViewById(R.id.img_menu);
            mDialog_IVSearch = (ImageView) findViewById(R.id.img_search);
            mDialog_IVClear = (ImageView) findViewById(R.id.img_clear);
            mDialog_RLLogo = (RelativeLayout) findViewById(R.id.rl_logo);
            mDialog_ETSearchString = (EditText) findViewById(R.id.et_searchstring);
            mDialog_LVResults = (ListView) findViewById(R.id.lv_results);

            mIVClear.setVisibility(View.INVISIBLE);
            mDialog_RLLogo.setVisibility(View.INVISIBLE);
            mDialog_ETSearchString.setVisibility(View.VISIBLE);


            mDialog_RLInput.setBackground(gdr_all);

            mDialog_IVMenu.setImageDrawable(mContext.getResources().getDrawable(mMenuDrawableId));
            mDialog_IVSearch.setImageDrawable(mContext.getResources().getDrawable(mSearchDrawableId));
            mDialog_IVClear.setImageDrawable(mContext.getResources().getDrawable(mClearDrawableId));

            mDialog_IVMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View.OnClickListener l = mOnMenuClickListener;
                    if (l != null){
                        l.onClick(v);
                    }
                }
            });

            mDialog_IVSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View.OnClickListener l = mOnSearchClickListener;
                    if (l != null){
                        l.onClick(v);
                    }
                }
            });

            mDialog_IVClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog_ETSearchString.getText().clear();
                }
            });


            mDialog_ETSearchString.setHintTextColor(mHintColor);
            mDialog_ETSearchString.setHint(mHint);
            mDialog_ETSearchString.setTextSize(mTextSize);
            mDialog_ETSearchString.setTextColor(mTextColor);
            mDialog_ETSearchString.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if (s.length()>0){
                        mDialog_IVSearch.setVisibility(View.INVISIBLE);
                        mDialog_IVClear.setVisibility(View.VISIBLE);
                    }else{
                        mDialog_IVSearch.setVisibility(View.VISIBLE);
                        mDialog_IVClear.setVisibility(View.INVISIBLE);
                    }
                    SearchAdapter sa = (SearchAdapter) mDialog_LVResults.getAdapter();
                    if (sa != null) sa.setSearchString(s);
                    mDialog.refreshView();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            mDialog_LVResults.setDivider(null);
            mDialog_LVResults.setDividerHeight(0);

        }

        private void setLocation(int x, int y, int w, int h){
            origin_x = x;
            origin_y = y;
            origin_w = w;
            origin_h = h;
            refreshView();
        }

        private void refreshView(){
            int h = origin_h;
            SearchAdapter a = (SearchAdapter) mDialog_LVResults.getAdapter();

            //calculate dialog height
            if (a == null || a.getCount() == 0){
                mDialog_LVResults.setVisibility(View.GONE);
                mDialog_RLInput.setBackground(gdr_all);
            }else{
                mDialog_LVResults.setVisibility(View.VISIBLE);
                mDialog_RLInput.setBackground(gdr_top);
                mDialog_LVResults.setBackground(gdr_bot);

                //determine display height
                WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int display_height = size.y;

                //calculate listview height
                ViewGroup.MarginLayoutParams mlp = (MarginLayoutParams) mDialog_LVResults.getLayoutParams();
                int totalHeight = mlp.topMargin + mlp.bottomMargin + mDialog_LVResults.getPaddingTop() + mDialog_LVResults.getPaddingBottom();
                int desiredWidth = MeasureSpec.makeMeasureSpec(mDialog_LVResults.getWidth(), MeasureSpec.AT_MOST);
                for (int i = 0; i < a.getCount(); i++) {
                    View listItem = a.getView(i, null, mDialog_LVResults);
                    if(listItem != null){
                        listItem.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                        listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
                        totalHeight += listItem.getMeasuredHeight();

                    }
                }
                h += totalHeight;

                if (origin_y + origin_h + totalHeight > display_height) {
                    h = display_height - origin_y;
                }
            }


            //set dialog size
            WindowManager.LayoutParams wmlp = getWindow().getAttributes();
            wmlp.gravity = Gravity.TOP | Gravity.LEFT;
            wmlp.x = origin_x;
            wmlp.y = origin_y;
            wmlp.width = origin_w;
            wmlp.height = h;
            getWindow().setAttributes(wmlp);

            //override input bar size
            mDialog_RLInput.getLayoutParams().width = origin_w;
            mDialog_RLInput.getLayoutParams().height = origin_h;
        }

        @Override
        public void show() {
            super.show();
            mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE  | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            mDialog.getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            mDialog_ETSearchString.requestFocus();
            refreshView();
        }

        @Override
        public void dismiss() {
            super.dismiss();
        }
    }

    public abstract static class SearchAdapter extends BaseAdapter {

        public abstract void setSearchString(CharSequence searchString);

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }

    private static class SavedState extends BaseSavedState {
        private CharSequence mSearchString = null;
        private boolean mFocus = false;

        private SavedState(Parcelable superState, CharSequence searchString, boolean focus) {
            super(superState);
            this.mSearchString = searchString;
            mFocus = focus;
        }

        private SavedState(Parcel in) {
            super(in);
            mSearchString = in.readString();
            mFocus = (in.readByte() == 1);
        }

        public CharSequence getSearchString() {
            return mSearchString;
        }

        public boolean getFocus() {
            return mFocus;
        }


        @Override
        public void writeToParcel(Parcel destination, int flags) {
            super.writeToParcel(destination, flags);
            destination.writeString(mSearchString.toString());
            destination.writeByte((byte)(mFocus ? 1 : 0));
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, mDialog_ETSearchString.getText(), mDialog.isShowing());
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        mDialog_ETSearchString.setText(savedState.getSearchString());
        if (savedState.getFocus()) {
            mDialog.show();
        }else{
            mDialog.dismiss();
        }
    }

    public static int convertDpToPixels(float dp, Context context) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
        return px;
    }

    public static int convertSpToPixels(float sp, Context context) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
        return px;
    }

}
