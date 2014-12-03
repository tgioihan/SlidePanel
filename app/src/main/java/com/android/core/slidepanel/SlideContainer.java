package com.android.core.slidepanel;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by tuannx on 12/1/2014.
 */
public class SlideContainer extends ViewGroup {
    private View content;
    private View bottomTopHeader;
    private BottomView viewBottom;

    public int getBottomTopOffset() {
        return bottomTopOffset;
    }

    private int bottomTopOffset;
    private int bottomOffset;

    public ISlideChange getSlideChangeListener() {
        return slideChangeListener;
    }

    public void setSlideChangeListener(ISlideChange slideChangeListener) {
        this.slideChangeListener = slideChangeListener;
    }

    private ISlideChange slideChangeListener;

    public SlideContainer(Context context) {
        super(context);
    }

    public SlideContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int width = r - l;
        final int height = b - t;
        content.layout(0, 0, width, height - bottomOffset);

        // botomview layout

        if (bottomTopHeader != null) {
            bottomTopHeader.layout(0, -bottomTopOffset, width, 0);
        }
        viewBottom.layout(0, height - bottomOffset, width, height
                - bottomOffset + viewBottom.getMeasuredHeight());

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);

        final int contentWidth = getChildMeasureSpec(widthMeasureSpec, 0, width);
        final int contentHeight = getChildMeasureSpec(heightMeasureSpec, 0,
                height - bottomOffset);
        content.measure(contentWidth, contentHeight);

        measureChild(bottomTopHeader, width, height);
        bottomTopOffset = +bottomTopHeader.getMeasuredHeight();
        Log.d("", "bottomTopOffset " + bottomTopOffset);
        viewBottom.setTopOffset(bottomTopOffset);

        final int bottomWidth = getChildMeasureSpec(widthMeasureSpec, 0, width);
        final int bottomHeight = MeasureSpec.makeMeasureSpec(height - bottomTopOffset, MeasureSpec.EXACTLY);
        viewBottom.measure(bottomWidth, bottomHeight);

        setMeasuredDimension(width, height);
    }

    public void setContent(int resId) {
        View view = LayoutInflater.from(getContext()).inflate(resId, null);
        setContent(view);
    }

    public void setContent(View v) {
        if (content != null)
            removeView(content);
        content = v;
        addView(content);
    }

    /**
     * must use inflate with parent to avoid measure problem
     *
     * @param resId
     */
    public void setBottomTopHeader(int resId) {
        View view = LayoutInflater.from(getContext()).inflate(resId, this, false);
        setBottomTopHeader(view);
    }

    public void setBottomTopHeader(View v) {
        if (bottomTopHeader != null)
            removeView(bottomTopHeader);
        bottomTopHeader = v;
        addView(bottomTopHeader);
    }

    public void setBottomView(int resId) {
        try {
            BottomView view = (BottomView) LayoutInflater.from(getContext()).inflate(resId, this, false);
            setBottomView(view);
        } catch (ClassCastException e) {
            try {
                throw new Exception("bottom view must be instance of BottomView");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public void setBottomView(BottomView v) {
        if (viewBottom != null)
            removeView(viewBottom);
        viewBottom = v;
        viewBottom.setSlideListener(new ISlide() {
            @Override
            public void onStartSlide() {
                if(slideChangeListener!=null){
                    boolean bottomIn = isBottomIn();
                    slideChangeListener.onStartSlide(bottomIn);
                }
            }

            @Override
            public void onSlide(int offset, int maxDistance) {
                Log.d("", "onSlide bottomtopheader  " + offset + " " + maxDistance + " " + bottomTopHeader.getHeight());
                if (bottomTopHeader != null) {
                    Log.d("", "onSlide height  " + bottomTopHeader.getHeight() + " ");
                    int top = -(offset * bottomTopHeader.getHeight() / maxDistance);
                    Log.d("", "onSlide top  " + top + " ");
                    bottomTopHeader.layout(bottomTopHeader.getLeft(), top, bottomTopHeader.getRight(), top + bottomTopHeader.getHeight());
                }
                if(slideChangeListener!=null){
                    boolean bottomIn = isBottomIn();
                    slideChangeListener.onSlide(offset, bottomIn);
                }
            }

            @Override
            public void onSlideFinish() {
                if(slideChangeListener!=null){
                    boolean bottomIn = isBottomIn();
                    slideChangeListener.onSlideFinish(bottomIn);
                }
            }
        });
        addView(viewBottom);
    }

    public boolean isBottomIn() {
        return !(viewBottom.getTop()>bottomOffset);
    }

    public void setBotomIn(boolean bottomIn){
        if(bottomIn){
            if(!isBottomIn()){
                viewBottom.slideToY(bottomTopOffset);
            }
        }else{
            if(isBottomIn()){
                viewBottom.slideToY(getHeight()  - bottomOffset);
            }
        }
    }

    public void setBottomOffset(int bottomOffset) {
        this.bottomOffset = bottomOffset;
        if (viewBottom != null) {
            viewBottom.setBottomOffset(bottomOffset);
        }
//        invalidate();
    }

    public void setBottomTopOffset(int bottomTopOffset) {
        this.bottomTopOffset = bottomTopOffset;
        if (viewBottom != null) {
            viewBottom.setTopOffset(bottomTopOffset);
        }
    }

    public interface ISlideChange {

        void onStartSlide(boolean bottomIn);

        void onSlide(int offset, boolean bottomIn);

        void onSlideFinish(boolean bottomIn);
    }
}
