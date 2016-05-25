package com.univ.lorraine.cmi;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.univ.lorraine.cmi.database.model.Livre;

import java.io.File;
import java.util.List;

/**
 * Created by Jyeil on 23/05/2016.
 */
public class TopRecyclerAdapter extends RecyclerView.Adapter<TopRecyclerAdapter.TopViewHolder> {

    private List<Livre> items;
    private Context context;

    public TopRecyclerAdapter(List<Livre> i, Context c){
        items = i;
        context = c;
    }

    @Override
    public TopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View topView = inflater.inflate(R.layout.top_item, parent, false);

        // Return a new holder instance
        return new TopViewHolder(topView);
    }

    @Override
    public void onBindViewHolder(TopViewHolder holder, int position) {
        // Get the data model based on position
        Livre livre = items.get(position);

        // Set item views based on the data model
        ImageView cover = holder.cover;
        Utilities.loadLinkedCoverInto(context, livre, cover);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class TopViewHolder extends RecyclerView.ViewHolder {
        public ImageView cover;

        public TopViewHolder(View itemView) {
            super(itemView);
            cover = (ImageView) itemView.findViewById(R.id.top_view);
        }
    }


}

