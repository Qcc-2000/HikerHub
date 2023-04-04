package edu.northeastern.hikerhub;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class SlideUpLayout extends LinearLayout {
    private View bar;
    private View content;
    private Scroller scroller;
    private int downY;

    public SlideUpLayout(Context context) {
        this(context, null);
    }

    public SlideUpLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        scroller = new Scroller(getContext());
        bar = getChildAt(0);
        content = getChildAt(1);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //bar置底
        bar.layout(0, getMeasuredHeight() - bar.getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight());
        //content隐藏
        content.layout(0, getMeasuredHeight(), getMeasuredWidth(), bar.getBottom() + content.getMeasuredHeight());
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int offsetY = (int) event.getY() - downY;
                int toScroll = getScrollY() - offsetY;
                if (toScroll < 0) {
                    toScroll = 0;
                } else if (toScroll > content.getMeasuredHeight()) {
                    toScroll = content.getMeasuredHeight();
                }
                scrollTo(0, toScroll);
                downY = (int) event.getY();
                break;
            case MotionEvent.ACTION_UP:
                int offsetScroll = getScrollY();
                if (offsetScroll > content.getMeasuredHeight() / 2) {
                    scroller.startScroll(getScrollX(), getScrollY(), 0, content.getMeasuredHeight() - offsetScroll, 500);
                } else {
                    scroller.startScroll(getScrollX(), getScrollY(), 0, -offsetScroll, 500);
                }
                invalidate();
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            invalidate();
        }
    }
}
