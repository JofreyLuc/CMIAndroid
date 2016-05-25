package com.univ.lorraine.cmi;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.univ.lorraine.cmi.database.model.Evaluation;

import java.util.List;

/**
 * Created by Jyeil on 25/05/2016.
 */
public class EvalRecyclerAdapter extends RecyclerView.Adapter<EvalRecyclerAdapter.EvalViewHolder> {

    private List<Evaluation> evals;
    private Context context;

    public EvalRecyclerAdapter(Context c, List<Evaluation> le){
        evals = le;
        context = c;
    }

    @Override
    public EvalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View evalView = inflater.inflate(R.layout.eval_item, parent, false);

        // Return a new holder instance
        return new EvalViewHolder(evalView);
    }

    @Override
    public void onBindViewHolder(EvalViewHolder holder, int position) {
        Evaluation e = evals.get(position);

        RatingBar rate = holder.rate;
        TextView rater = holder.rater;
        TextView eval = holder.eval;

        rate.setRating((float) e.getNote());
        rater.setText(e.getUtilisateur().getPseudo());
        eval.setText(e.getCommentaire());
    }

    @Override
    public int getItemCount() {
        return evals.size();
    }

    public static class EvalViewHolder extends RecyclerView.ViewHolder {
        public RatingBar rate;
        public TextView rater;
        public TextView eval;

        public EvalViewHolder(View itemView) {
            super(itemView);
            rate = (RatingBar) itemView.findViewById(R.id.eval_rating_bar);
            rater = (TextView) itemView.findViewById(R.id.eval_rater);
            eval = (TextView) itemView.findViewById(R.id.eval_eval);
        }
    }
}
