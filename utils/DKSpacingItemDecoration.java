package com.liudukun.dkchat.utils;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DKSpacingItemDecoration  extends RecyclerView.ItemDecoration {

    private int space;
    private int spanCount;

    public DKSpacingItemDecoration(int spanCount,int space) {
        super();
        this.space = space;
        this.spanCount = spanCount;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        outRect.left = space;
        outRect.bottom = space;
        if (position % spanCount == 0){
            outRect.left = 0;
        }

    }
}
