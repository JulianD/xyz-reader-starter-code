package com.example.xyzreader.ui;

import com.example.xyzreader.model.Article;

/**
 * Created by radsen on 6/16/17.
 */

interface RecyclerViewListener {
    void onItemClicked(Article article, long position);
}
