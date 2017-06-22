package com.example.xyzreader.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.UpdaterService;
import com.example.xyzreader.model.Article;

/**
 * Created by radsen on 6/16/17.
 */

public class ArticleListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ArticleListFragment.class.getSimpleName();

    private static final String SCROLLED_POSITION = "com.udacity.xyz.scroll.position";
    private static final String SELECTED_POSITION = "com.udacity.xyz.selected.position";

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private boolean mIsRefreshing = false;
    private RecyclerViewListener listener;
    private int mScrollPosition;
    private int mSelPos;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof RecyclerViewListener){
            listener = (RecyclerViewListener) context;
        } else {
            throw new ClassCastException("The activity does not implements RecyclerViewListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_article_list, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null){
            mScrollPosition = savedInstanceState.getInt(SCROLLED_POSITION, 0);
            mSelPos = savedInstanceState.getInt(SELECTED_POSITION, Adapter.NO_SELECTION);
        } else {
            mSelPos = Adapter.NO_SELECTION;
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getActivity().startService(new Intent(getContext(), UpdaterService.class));
            }
        });

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(getResources().getBoolean(R.bool.multipane)){
            mScrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
            outState.putInt(SCROLLED_POSITION, mScrollPosition);
            outState.putInt(SELECTED_POSITION, mSelPos);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(getContext());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Adapter adapter = new Adapter(cursor);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = null;
        if(getResources().getBoolean(R.bool.multipane)){
            layoutManager = new LinearLayoutManager(getContext());
        } else {
            int columnCount = getResources().getInteger(R.integer.list_column_count);
            layoutManager = new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        }

        mRecyclerView.setLayoutManager(layoutManager);
        int selPos = (mSelPos != Adapter.NO_SELECTION) ? mSelPos : Adapter.NO_SELECTION;
        adapter.setSelectedItem(selPos);
        mRecyclerView.scrollToPosition(mScrollPosition);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    public void updateRefreshingUI(boolean mIsRefreshing) {
        mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {

        public static final int NO_SELECTION = -1;

        private Cursor mCursor;
        private int selectedItem;

        public Adapter(Cursor cursor) {
            mCursor = cursor;
        }

        @Override
        public long getItemId(int position) {
            mCursor.moveToPosition(position);
            return mCursor.getLong(ArticleLoader.Query._ID);
        }

        @Override
        public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.list_item_article,
                    parent, false);
            final ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            mCursor.moveToPosition(position);
            final Article article = Article.create(mCursor);
            article.setPosition(position);

            if(getResources().getBoolean(R.bool.multipane)){
                if(position == selectedItem){
                    holder.itemView.setSelected(true);
                } else {
                    holder.itemView.setSelected(false);
                }
            }

            holder.titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
            holder.subtitleView.setText(article.getByLine());
            holder.thumbnailView.setImageUrl(
                    mCursor.getString(ArticleLoader.Query.THUMB_URL),
                    ImageLoaderHelper.getInstance(getActivity()).getImageLoader());
            holder.thumbnailView.setAspectRatio(mCursor.getFloat(ArticleLoader.Query.ASPECT_RATIO));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!getResources().getBoolean(R.bool.multipane)){
                        String transitionName = String.valueOf(getItemId(holder.getAdapterPosition()));
                        Bundle bundle = ActivityOptionsCompat
                                .makeSceneTransitionAnimation(getActivity(),
                                        holder.thumbnailView, transitionName)
                                .toBundle();
                        Intent intent = new Intent(getActivity(), ArticleDetailActivity.class);
                        intent.putExtra(Article.KEY, article);
                        startActivity(intent, bundle);
                    } else {
                        listener.onItemClicked(article, getItemId(holder.getAdapterPosition()));
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mCursor.getCount();
        }

        public void setSelectedItem(int selectedItem) {
            this.selectedItem = selectedItem;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public DynamicHeightNetworkImageView thumbnailView;
        public TextView titleView;
        public TextView subtitleView;

        public ViewHolder(View view) {
            super(view);
            thumbnailView = (DynamicHeightNetworkImageView) view.findViewById(R.id.photo);
            titleView = (TextView) view.findViewById(R.id.article_title);
            subtitleView = (TextView) view.findViewById(R.id.article_subtitle);
        }
    }
}
