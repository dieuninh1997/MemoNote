package com.ttdn.memonote.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by ttdn1 on 9/29/2017.
 */

public class MyRecyclerItemListener
        implements RecyclerView.OnItemTouchListener{


    private OnClickItemInterface clickListener;
    private GestureDetector gestureDetector;


    public interface OnClickItemInterface{
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View child=rv.findChildViewUnder(e.getX(),e.getY());
        if(child!=null && clickListener!=null && gestureDetector.onTouchEvent(e))
        {
            clickListener.onClick(child,rv.getChildAdapterPosition(child));
            return true;
        }

        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public MyRecyclerItemListener(Context context,
                                  final RecyclerView recyclerView,
                                  final OnClickItemInterface listener)
    {
        this.clickListener=listener;
        gestureDetector=new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {
                        View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                        if (child != null && clickListener != null) {
                            clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                        }
                    }
                });

    }
}