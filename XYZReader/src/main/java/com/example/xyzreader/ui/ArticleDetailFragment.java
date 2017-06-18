package com.example.xyzreader.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.model.Article;

/**
 * A fragment representing a single Article detail screen. This fragment is
 * either contained in a {@link ArticleListActivity} in two-pane mode (on
 * tablets) or a {@link ArticleDetailActivity} on handsets.
 */
public class ArticleDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ArticleDetailFragment.class.getSimpleName();

    private Cursor mCursor;
    private View mRootView;
    private ImageView mPhotoView;

    private RecyclerView rvArticle;
    private ArticleAdapter adapter;
    private Article mArticle;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticleDetailFragment() {
    }

    public static ArticleDetailFragment newInstance(Article article) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(Article.KEY, article);
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            if (getArguments().containsKey(Article.KEY)) {
                mArticle = getArguments().getParcelable(Article.KEY);
            }
        }

        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");

        // In support library r8, calling initLoader for a fragment in a FragmentPagerAdapter in
        // the fragment's onCreate may cause the same LoaderManager to be dealt to multiple
        // fragments because their mIndex is -1 (haven't been added to the activity yet). Thus,
        // we do this in onActivityCreated.
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);

        mPhotoView = (ImageView) mRootView.findViewById(R.id.photo);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP  && mArticle != null) {
            mPhotoView.setTransitionName(String.valueOf(mArticle.getId()));
        }

        mRootView.findViewById(R.id.share_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText("Some sample text")
                        .getIntent(), getString(R.string.action_share)));
            }
        });

        return mRootView;
    }

    private void bindViews() {
        if (mRootView == null) {
            return;
        }

        rvArticle = (RecyclerView) mRootView.findViewById(R.id.rv_article);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rvArticle.setLayoutManager(mLayoutManager);
        adapter = new ArticleAdapter(mArticle);
        rvArticle.setAdapter(adapter);

        if (mCursor != null) {

            loadBody(mCursor.getString(ArticleLoader.Query.BODY));

            ImageLoaderHelper.getInstance(getActivity()).getImageLoader()
                    .get(mCursor.getString(ArticleLoader.Query.PHOTO_URL), new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                            Bitmap bitmap = imageContainer.getBitmap();
                            if (bitmap != null) {
                                mPhotoView.setImageBitmap(imageContainer.getBitmap());
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError volleyError) {

                        }
                    });
        }
    }

    @SuppressWarnings("deprecation")
    private void loadBody(final String body) {
        new AsyncTask<String, Void, List<Spanned>>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                bodyView.setText("Loading");
            }

            @Override
            protected List<Spanned> doInBackground(String... strings) {
                String body = strings[0];

                String[] paragraphs = body.split("(\r\n|\n)");
                List<Spanned> spannedList = new ArrayList<Spanned>();

                for (String paragraph : paragraphs){
                    Spanned spanned;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        spanned = Html.fromHtml(paragraph, Html.FROM_HTML_MODE_LEGACY);
                    } else {
                        spanned = Html.fromHtml(paragraph);
                    }
                    spannedList.add(spanned);
                }

                return spannedList;
            }

            @Override
            protected void onPostExecute(final List<Spanned> list) {
                super.onPostExecute(list);
                adapter.swap(list);
            }
        }.execute(body);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG, "onCreateLoader");
        if(mArticle != null){
            return ArticleLoader.newInstanceForItemId(getActivity(), mArticle.getId());
        } else {
            return ArticleLoader.newAllArticlesInstance(getActivity());
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.d(TAG, "onLoadFinished");
        if (!isAdded()) {
            if (cursor != null) {
                cursor.close();
            }
            return;
        }

        mCursor = cursor;
        if (mCursor != null && !mCursor.moveToFirst()) {
            Log.e(TAG, "Error reading item detail cursor");
            mCursor.close();
            mCursor = null;
        }

        mArticle = Article.create(cursor);
        bindViews();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
        mArticle = null;
        bindViews();
    }

    public void load(Article article) {
        Log.d(TAG, "load");
        mArticle = article;
        getLoaderManager().restartLoader(0, null, this);
    }
}