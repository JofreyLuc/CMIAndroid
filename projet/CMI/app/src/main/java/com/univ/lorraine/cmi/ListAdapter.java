package com.univ.lorraine.cmi;




import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.univ.lorraine.cmi.database.model.Livre;
import com.univ.lorraine.cmi.retrofit.CallMeIshmaelService;
import com.univ.lorraine.cmi.retrofit.CallMeIshmaelServiceProvider;

import java.util.List;

import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by julienhans on 17/05/2016.
 */
public class ListAdapter extends BaseAdapter {

    List<Livre> result;
    Context context;
    TextView data;
    ImageView cover;

    public ListAdapter(Context c, List<Livre> livres) {
        result = livres;
        context = c;
    }
    @Override
    public int getCount() {
        return result.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_result_item, parent, false);
        data = (TextView) rowView.findViewById(R.id.textViewListResult);
        cover = (ImageView) rowView.findViewById(R.id.imageViewListResult);

        data.setText(String.format("%s\n\n%s", result.get(position).getTitre(), result.get(position).getAuteur()));

        Utilities.loadLinkedCoverInto(context, result.get(position), cover);

        rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });



        ImageButton add = (ImageButton) rowView.findViewById(R.id.imageButtonAdd);
        Button lire = (Button) rowView.findViewById(R.id.buttonRead);
        Button details = (Button) rowView.findViewById(R.id.buttonDetails);

        if (details != null) {
            details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ouvrir les détails du livre
                }
            });
        }

        if (lire != null) {
            lire.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ouvrir le reader
                }
            });
        }

        if (add != null) {
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ajouter le livre à la bibliothèque
                }
            });
        }
        return rowView;
    }

}

