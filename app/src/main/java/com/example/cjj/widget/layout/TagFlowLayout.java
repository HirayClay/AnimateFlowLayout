package com.example.cjj.widget.layout;
/**
 * Copied by CJJ on 2016/8/12.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.cjj.widget.MainActivity;
import com.example.cjj.widget.R;
import com.example.cjj.widget.util.Utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class TagFlowLayout extends FlowLayout implements TagAdapter.OnDataChangedListener, View.OnLongClickListener {
    private TagAdapter mTagAdapter;
    private boolean mAutoSelectEffect = true;
    private int mSelectedMax = -1;//-1为不限制数量
    private static final String TAG = "TagFlowLayout";
    private MotionEvent mMotionEvent;
    private boolean trigger;
    private ImageView target;//触发长按事件的TagView
    private MotionEvent downMotion;
    private int pos;
    private WindowManager wm;
    private float dx;
    private float dy;
    private WindowManager.LayoutParams lp;
    private int statusBarHeight =0;
    private Utils utils;
    private boolean animating;
    private TagView temp;

    @IntDef({Idle,Moving,Settled})
    public @interface FingerState{

    }
    private static final int Idle = 0xfff001;
    private static final int Moving = 0xfff002;
    private static final int Settled = 0xfff003;
    private Set<Integer> mSelectedView = new HashSet<Integer>();

    public TagFlowLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        if (context instanceof AppCompatActivity)
            wm = ((AppCompatActivity) context).getWindowManager();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TagFlowLayout);
        mAutoSelectEffect = ta.getBoolean(R.styleable.TagFlowLayout_auto_select_effect, true);
        mSelectedMax = ta.getInt(R.styleable.TagFlowLayout_max_select, -1);
        ta.recycle();

        if (mAutoSelectEffect)
        {
            setClickable(true);
        }
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        utils = new Utils();
    }

    public TagFlowLayout(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public TagFlowLayout(Context context)
    {
        this(context, null);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int cCount = getChildCount();

        for (int i = 0; i < cCount; i++)
        {
            TagView tagView = (TagView) getChildAt(i);
            tagView.setDrawingCacheEnabled(true);
            if (tagView.getVisibility() == View.GONE) continue;
            if (tagView.getTagView().getVisibility() == View.GONE)
            {
                tagView.setVisibility(View.GONE);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public interface OnSelectListener
    {
        void onSelected(Set<Integer> selectPosSet);
    }

    private OnSelectListener mOnSelectListener;

    public void setOnSelectListener(OnSelectListener onSelectListener)
    {
        mOnSelectListener = onSelectListener;
        if (mOnSelectListener != null) setClickable(true);
    }

    public interface OnTagClickListener
    {
        boolean onTagClick(View view, int position, FlowLayout parent);
    }

    private OnTagClickListener mOnTagClickListener;

    public void setOnTagClickListener(OnTagClickListener onTagClickListener)
    {
        mOnTagClickListener = onTagClickListener;
        if (onTagClickListener != null) setClickable(true);
    }


    public void setAdapter(TagAdapter adapter)
    {
        mTagAdapter = adapter;
        mTagAdapter.setOnDataChangedListener(this);
        mSelectedView.clear();
        changeAdapter();

    }

    private void changeAdapter()
    {
        removeAllViews();
        TagAdapter adapter = mTagAdapter;
        TagView tagViewContainer;
        HashSet preCheckedList = mTagAdapter.getPreCheckedList();
        for (int i = 0; i < adapter.getCount(); i++)
        {
            View tagView = adapter.getView(this, i, adapter.getItem(i));

            tagViewContainer = new TagView(getContext());
//            ViewGroup.MarginLayoutParams clp = (ViewGroup.MarginLayoutParams) tagView.getLayoutParams();
//            ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(clp);
//            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
//            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//            lp.topMargin = clp.topMargin;
//            lp.bottomMargin = clp.bottomMargin;
//            lp.leftMargin = clp.leftMargin;
//            lp.rightMargin = clp.rightMargin;
            tagView.setDuplicateParentStateEnabled(true);
            if (tagView.getLayoutParams() != null)
            {
                tagViewContainer.setLayoutParams(tagView.getLayoutParams());
            } else
            {
                ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(dip2px(getContext(), 5),
                        dip2px(getContext(), 5),
                        dip2px(getContext(), 5),
                        dip2px(getContext(), 5));
                tagViewContainer.setLayoutParams(lp);
            }
            tagViewContainer.addView(tagView);
            addView(tagViewContainer);

            if (preCheckedList.contains(i))
            {
                tagViewContainer.setChecked(true);
            }

            if (mTagAdapter.setSelected(i, adapter.getItem(i)))
            {
                mSelectedView.add(i);
                tagViewContainer.setChecked(true);
            }
        }
        mSelectedView.addAll(preCheckedList);
        setOnLongClickListener(this);
    }

    @Override
    public boolean onLongClick(View v) {
        temp = findChild((int) downMotion.getX(), (int) downMotion.getY());
        target = new ImageView(getContext());
        target.setScaleType(ImageView.ScaleType.FIT_XY);
        if(temp == null)return false;
        if (temp != null) {
            target.setImageBitmap(temp.getDrawingCache());
        }
        if (temp != null)
        {
            pos = findPosByView(temp);
           int[] loc = new int[2];
            temp.getLocationInWindow(loc);
            dx = downMotion.getRawX()-loc[0];
            dy = downMotion.getRawY()-loc[1]+statusBarHeight;
            Log.i(TAG, "onLongClick: "+dx+"---"+dy);
        }
        temp.setVisibility(INVISIBLE);
        trigger = true;
        return false;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_UP)
        {
            mMotionEvent = MotionEvent.obtain(event);
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            downMotion = MotionEvent.obtain(event);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        Log.i(TAG, "dispatchTouchEvent: "+ev.getAction());
        int rx = (int) ev.getRawX();
        int ry = (int) ev.getRawY();
        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "dispatchTouchEvent: DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                if (trigger)
                moveTarget(rx,ry);
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "dispatchTouchEvent: UP");
                settle();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void moveTarget(int rx, int ry) {
        Log.i(TAG, "moveTarget: "+rx+"------"+ry);
        if (lp == null){
            lp = new WindowManager.LayoutParams();
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.format = PixelFormat.TRANSLUCENT;
            lp.gravity = Gravity.LEFT|Gravity.TOP;
            lp.width  = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height  = WindowManager.LayoutParams.WRAP_CONTENT;
        }
        int destX = (int) (rx - dx);
        int destY = (int) (ry - dy);

//        findDisorderView(destX,destY);

        lp.x = destX;
        lp.y = destY;

        if (target!= null&&target.getParent() == null)
        {
            wm.addView(target,lp);
        }else {
            wm.updateViewLayout(target,lp);
            //默认 当target 的中心点进入到某个View的内部触发位置交换动画
            findDisorderView(destX+target.getWidth()/2,destY+target.getHeight()/2);
            Log.i(TAG, "moveTarget: ");
        }
    }
    /**
     * 找出被拖动的View覆盖的TagView
     * @param destX ..
     * @param destY ..
     */
    private void findDisorderView(int destX, int destY) {
        final int i = indexChild(destX, destY);//
        if (i !=-1) {
            //开始动画
            if (!animating) {
                animating = true;
                utils.apply(this, pos, i).end(new Utils.AnimationListener() {
                    @Override
                    public void end() {
                       if (sortDataArray(pos,i))
                           animating = false;
                    }
                });

            }
        }
    }

    /**
     *
     * @param p 移动View的起始位置
     * @param i   移动View结束位置
     */
    private boolean sortDataArray(int p, int i) {
        List datas = getAdapter().getDatas();
        if (p != -1&&datas != null){
                Object o = datas.get(p);
                Object o1 = datas.get(i);//缓存
                datas.set(i, o);//modify the dest object
            if (p<i) {
                for (int j = p; j < i; j++) {
                    if (j == i - 1)
                        datas.set(j, o1);
                    else
                    {
                        if(j + 1 == i)
                            datas.set(j,o1);
                        else
                        datas.set(j, datas.get(j + 1));
                    }
                }
            }
            else{
                for (int k = p; k > i; k--) {//倒数
                    if (k == i-1)
                        datas.set(k,o1);
                    else
                    {
                        if (k - 1 == i)
                            datas.set(k,o1);
                        else
                        datas.set(k,datas.get(k-1));
                    }
                }
            }
        }
        pos = i;//更新移动View的索引
        return true;
    }

    private void settle() {
        if (trigger) {
            pos = -1;
            if (target.getParent() != null)
                wm.removeView(target);
            onChanged();
            trigger = false;
            target = null;
        }
    }

    @Override
    public boolean performClick()
    {
        if (mMotionEvent == null) return super.performClick();

        int x = (int) mMotionEvent.getX();
        int y = (int) mMotionEvent.getY();
        mMotionEvent = null;

        TagView child = findChild(x, y);
        int pos = findPosByView(child);
        if (child != null)
        {
            doSelect(child, pos);
            if (mOnTagClickListener != null)
            {
                return mOnTagClickListener.onTagClick(child.getTagView(), pos, this);
            }
        }
        return super.performClick();
    }


    /**
     *
     * @param count
     */
    public void setMaxSelectCount(int count)
    {
        if (mSelectedView.size() > count)
        {
            Log.w(TAG, "you has already select more than " + count + " views , so it will be clear .");
            mSelectedView.clear();
        }
        mSelectedMax = count;
    }

    public Set<Integer> getSelectedList()
    {
        return new HashSet<Integer>(mSelectedView);
    }

    private void doSelect(TagView child, int position)
    {
        if (mAutoSelectEffect)
        {
            if (!child.isChecked())
            {
                //处理max_select=1的情况
                if (mSelectedMax == 1 && mSelectedView.size() == 1)
                {
                    Iterator<Integer> iterator = mSelectedView.iterator();
                    Integer preIndex = iterator.next();
                    TagView pre = (TagView) getChildAt(preIndex);
                    pre.setChecked(false);
                    child.setChecked(true);
                    mSelectedView.remove(preIndex);
                    mSelectedView.add(position);
                } else
                {
                    if (mSelectedMax > 0 && mSelectedView.size() >= mSelectedMax)
                        return;
                    child.setChecked(true);
                    mSelectedView.add(position);
                }
            } else
            {
                child.setChecked(false);
                mSelectedView.remove(position);
            }
            if (mOnSelectListener != null)
            {
                mOnSelectListener.onSelected(new HashSet<Integer>(mSelectedView));
            }
        }
    }

    public TagAdapter getAdapter()
    {
        return mTagAdapter;
    }


    private static final String KEY_CHOOSE_POS = "key_choose_pos";
    private static final String KEY_DEFAULT = "key_default";


    @Override
    protected Parcelable onSaveInstanceState()
    {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_DEFAULT, super.onSaveInstanceState());

        String selectPos = "";
        if (mSelectedView.size() > 0)
        {
            for (int key : mSelectedView)
            {
                selectPos += key + "|";
            }
            selectPos = selectPos.substring(0, selectPos.length() - 1);
        }
        bundle.putString(KEY_CHOOSE_POS, selectPos);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state)
    {
        if (state instanceof Bundle)
        {
            Bundle bundle = (Bundle) state;
            String mSelectPos = bundle.getString(KEY_CHOOSE_POS);
            if (!TextUtils.isEmpty(mSelectPos))
            {
                String[] split = mSelectPos.split("\\|");
                for (String pos : split)
                {
                    int index = Integer.parseInt(pos);
                    mSelectedView.add(index);

                    TagView tagView = (TagView) getChildAt(index);
                    if (tagView != null)
                        tagView.setChecked(true);
                }

            }
            super.onRestoreInstanceState(bundle.getParcelable(KEY_DEFAULT));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    private int findPosByView(View child)
    {
        final int cCount = getChildCount();
        for (int i = 0; i < cCount; i++)
        {
            View v = getChildAt(i);
            if (v == child) return i;
        }
        return -1;
    }

    private TagView findChild(int x, int y)
    {
        final int cCount = getChildCount();
        for (int i = 0; i < cCount; i++)
        {
            TagView v = (TagView) getChildAt(i);
            if (v.getVisibility() == View.GONE) continue;
            Rect outRect = new Rect();
            v.getHitRect(outRect);
            if (outRect.contains(x, y))
            {
                return v;
            }
        }
        return null;
    }

    private int indexChild(int x, int y)
    {
        final int cCount = getChildCount();
        for (int i = 0; i < cCount; i++)
        {
            TagView v = (TagView) getChildAt(i);
            if (v.getVisibility() == View.GONE) continue;
            Rect outRect = new Rect();
            v.getGlobalVisibleRect(outRect);//此方法包含了状态栏高度，故此加上状态栏的高度 ，愚蠢的坐标系，fuck
            if (outRect.contains(x, y+statusBarHeight))
            {
                if (i !=pos)
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onChanged()
    {
        mSelectedView.clear();
        changeAdapter();
    }

    public static int dip2px(Context context, float dpValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
