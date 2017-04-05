package com.ghizzoniandrea.photoviewer;

import android.view.View;

/**
 * Created by ghizzoniandrea on 2017/3/15.
 */
public interface OnRecyclerViewItemClickListener {

    void onItemClickListener(View view, int position);

    void onItemLongClickListener(View view, int position);
}
