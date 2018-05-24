package com.shiminion.cookybooky.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shiminion.cookybooky.R;
import com.shiminion.cookybooky.activity.MainActivity;
import com.shiminion.cookybooky.activity.YoutubeActivity;
import com.shiminion.cookybooky.firebasemodelclass.RecipeModelClass;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LandingPageRowAdapter extends RecyclerView.Adapter<LandingPageRowAdapter.MyViewHolder> {

    private List<RecipeModelClass> myRecipeList;
    private Context context;

    public LandingPageRowAdapter(List<RecipeModelClass> myRecipeList, Context context) {
        this.myRecipeList = myRecipeList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_rowadapter, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Picasso.with(context).load("http://img.youtube.com/vi/" + myRecipeList.get(position).getUrl() + "/0.jpg").into(holder.preview_image);
        holder.title.setText(myRecipeList.get(position).getName());
        holder.preview_image.setTag(position);
        holder.preview_image.setOnClickListener(rowOnClickListener);
    }

    @Override
    public int getItemCount() {
        return myRecipeList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView preview_image;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            preview_image = (ImageView) view.findViewById(R.id.preview_image);
        }
    }

    public View.OnClickListener rowOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int index=0;
            switch (view.getId()) {
                case R.id.preview_image:
                    Intent intent = new Intent(context, YoutubeActivity.class);
                    intent.putExtra("key",myRecipeList.get(index).getUrl());
                    context.startActivity(intent);
                    break;
            }
        }
    };
}
