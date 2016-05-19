package com.univ.lorraine.cmi;




import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * Created by julienhans on 17/05/2016.
 */
public class ListAdapter extends BaseAdapter {


    String [] result;
    Context context;
    int [] imageResult;

    public ListAdapter(Context c, String[] prgmNameList, int[] prgmImages) {
        result = prgmNameList;
        context = c;
        imageResult = prgmImages;
    }
    @Override
    public int getCount() {
        return result.length;
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
        TextView tv=(TextView) rowView.findViewById(R.id.textViewListResult);
        ImageView img=(ImageView) rowView.findViewById(R.id.imageViewListResult);
        tv.setText(result[position]);
        img.setImageResource(imageResult[position]);
        rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Selection "+result[position], Toast.LENGTH_LONG).show();
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
                    Toast.makeText(context, "voir détails "+result[position], Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (lire != null) {
            lire.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ouvrir le reader
                    Toast.makeText(context, "Lire "+result[position], Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (add != null) {
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ajouter le livre à la bibliothèque
                    Toast.makeText(context, "Ajouter "+result[position], Toast.LENGTH_SHORT).show();
                }
            });
        }
        return rowView;
    }

}

