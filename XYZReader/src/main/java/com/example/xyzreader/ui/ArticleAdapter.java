package com.example.xyzreader.ui;

import android.support.v7.widget.RecyclerView;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xyzreader.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by radsen on 6/14/17.
 */

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private List<Spanned> paragraphs;

    public ArticleAdapter(){
        paragraphs = new ArrayList<>();
    }

    @Override
    public ArticleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_paragraph, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArticleAdapter.ViewHolder holder, int position) {
        Spanned paragraph = paragraphs.get(position);
        holder.tvParagraph.setText(paragraph);
    }

    @Override
    public int getItemCount() {
        if(paragraphs == null){
            return 0;
        }

        return paragraphs.size();
    }

    public void swap(List<Spanned> list) {
        if(list != null){
            paragraphs = list;
        }

        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvParagraph;

        public ViewHolder(View itemView) {
            super(itemView);
            tvParagraph = (TextView) itemView.findViewById(R.id.article_body);
        }
    }
}
