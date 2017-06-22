package com.example.xyzreader.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.example.xyzreader.R;
import com.example.xyzreader.data.UpdaterService;
import com.example.xyzreader.model.Article;

/**
 * An activity representing a list of Articles. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link ArticleDetailActivity} representing item details. On tablets, the
 * activity presents a grid of items as cards.
 */
public class ArticleListActivity extends AppCompatActivity implements RecyclerViewListener {

    private static final String TAG = ArticleListActivity.class.toString();

    private ArticleDetailFragment fragmentArticleDetail;
    private ArticleListFragment fragmentArticleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        if (savedInstanceState == null) {
            refresh();
        }

        fragmentArticleList = (ArticleListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.master);

        fragmentArticleDetail = (ArticleDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.detail);
    }

    public void refresh() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mRefreshingReceiver,
                new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mRefreshingReceiver);
    }

    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
                boolean isRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
                fragmentArticleList.updateRefreshingUI(isRefreshing);
            }
        }
    };

    @Override
    public void onItemClicked(Article article, long itemId) {
        Log.d(TAG, "I'm here clicking this position: " + itemId);
        fragmentArticleDetail.load(article);
    }
}