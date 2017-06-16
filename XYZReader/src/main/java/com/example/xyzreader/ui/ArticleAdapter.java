package com.example.xyzreader.ui;

import android.support.v7.widget.RecyclerView;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.model.Article;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by radsen on 6/14/17.
 */

public class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_CONTENT = 1;

    private Article mArticle;

    private List<Spanned> paragraphs;

    public ArticleAdapter(){
        paragraphs = new ArrayList<>();
    }

    public ArticleAdapter(Article article) {
        mArticle = article;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
        RecyclerView.ViewHolder vh = null;

        switch (viewType){
            case TYPE_HEADER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_header, parent, false);
                vh = new VHHeader(view);
                break;
            case TYPE_CONTENT:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_paragraph, parent, false);
                vh = new VHContent(view);
                break;
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof VHHeader){
            VHHeader header = (VHHeader) holder;
            header.tvTitle.setText(mArticle.getTitle());
            header.tvByLine.setText(mArticle.getByLine());
        } else if (holder instanceof VHContent){
            VHContent content = (VHContent) holder;
            Spanned paragraph = paragraphs.get(position - 1);
            content.tvParagraph.setText(paragraph);
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;

        if(mArticle != null){
            count++;
        }

        if(paragraphs != null){
            return count + paragraphs.size();
        }

        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return TYPE_HEADER;
        }

        return TYPE_CONTENT;
    }

    public void swap(List<Spanned> list) {
        if(list != null){
            paragraphs = list;
        }

        notifyDataSetChanged();
    }

    public class VHHeader extends RecyclerView.ViewHolder {
        private TextView tvByLine;
        private TextView tvTitle;

        public VHHeader(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.article_title);
            tvByLine = (TextView) itemView.findViewById(R.id.article_byline);
        }
    }

    public class VHContent extends RecyclerView.ViewHolder {
        private TextView tvParagraph;

        public VHContent(View itemView) {
            super(itemView);
            tvParagraph = (TextView) itemView.findViewById(R.id.article_body);
        }
    }
}
