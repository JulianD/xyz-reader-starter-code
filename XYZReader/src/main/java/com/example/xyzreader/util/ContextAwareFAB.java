package com.example.xyzreader.util;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v13.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by radsen on 6/20/17.
 */

public class ContextAwareFAB extends CoordinatorLayout.Behavior {

    private static final int SCROLL_VERTICAL_THRESHOLD = 40;
    private boolean hasReachedBottom;

    public ContextAwareFAB(Context context, AttributeSet attrs){
        super();
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child,
                                       View directTargetChild, View target, int nestedScrollAxes) {
        // If scrolling vertically
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof RecyclerView;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, final View child, View dependency) {

        RecyclerView rv = (RecyclerView) dependency;

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                hasReachedBottom = recyclerView.computeVerticalScrollRange() -
                        SCROLL_VERTICAL_THRESHOLD <= recyclerView.computeVerticalScrollOffset() +
                                recyclerView.computeVerticalScrollExtent();

                if(hasReachedBottom && recyclerView.computeVerticalScrollOffset() > 0){
                    animateIn(child);
                } else {
                    animateOut(child);
                }
            }
        });

        return true;
    }

    private void animateOut(final View child) {
        child.animate().alpha(0).setDuration(500).withEndAction(new Runnable() {
            @Override
            public void run() {
                child.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void animateIn(final View child) {
        child.setVisibility(View.VISIBLE);
        child.animate().alpha(1).setDuration(500).withEndAction(new Runnable() {
            @Override
            public void run() {
                child.setVisibility(View.VISIBLE);
            }
        });
    }
}