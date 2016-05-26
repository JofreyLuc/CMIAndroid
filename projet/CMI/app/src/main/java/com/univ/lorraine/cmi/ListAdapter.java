package com.univ.lorraine.cmi;




import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.skytree.epub.Book;
import com.univ.lorraine.cmi.database.CmidbaOpenDatabaseHelper;
import com.univ.lorraine.cmi.database.model.Livre;

import java.util.List;

/**
 * Created by julienhans on 17/05/2016.
 */
public class ListAdapter extends BaseAdapter {

    List<Livre> result;
    Activity activity;
    TextView data;
    ImageView cover;
    CmidbaOpenDatabaseHelper dbHelper;

    public ListAdapter(Activity a, CmidbaOpenDatabaseHelper dbH, List<Livre> livres) {
        result = livres;
        activity = a;
        dbHelper = dbH;
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
        LayoutInflater inflater = ( LayoutInflater ) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Livre livre = result.get(position);
        View rowView = inflater.inflate(R.layout.list_result_item, parent, false);
        data = (TextView) rowView.findViewById(R.id.textViewListResult);
        cover = (ImageView) rowView.findViewById(R.id.imageViewListResult);

        data.setText(String.format("%s\n\n%s", livre.getTitre(), result.get(position).getAuteur()));

        Utilities.loadLinkedCoverInto(activity, livre, cover);

        // Si le livre est déjà présent dans la bibliothèque
        if (BookUtilities.isInBdd(livre, dbHelper)) {
            rowView.findViewById(R.id.imageButtonAdd).setVisibility(ImageButton.INVISIBLE);
            rowView.findViewById(R.id.buttonRead).setVisibility(Button.INVISIBLE);
            rowView.findViewById(R.id.buttonDetails).setVisibility(Button.INVISIBLE);

        }
        else
            rowView.findViewById(R.id.deja_ajoute).setVisibility(View.GONE);

        rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });



        ImageButton add = (ImageButton) rowView.findViewById(R.id.imageButtonAdd);
        final Button lire = (Button) rowView.findViewById(R.id.buttonRead);
        Button details = (Button) rowView.findViewById(R.id.buttonDetails);

        if (details != null) {
            details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ouvrir les détails du livre
                    Livre livre = result.get(position);
                    Bundle b = new Bundle();
                    b.putParcelable("livre", livre);
                    Intent i = new Intent(activity, BookDetailsActivity.class);
                    i.putExtra("bundle", b);
                    activity.startActivity(i);
                }
            });
        }

        if (lire != null) {
            lire.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ouvrir le reader
                    Livre livre = result.get(position);
                    // Si le livre n'est pas déjà dans la base locale, on l'ajoute et on le lit
                    if (!BookUtilities.isInBdd(livre, dbHelper))
                        BookUtilities.ajouterLivreBibliothequeEtLire(activity, livre, dbHelper);
                    else
                        BookUtilities.lancerLecture(activity, livre, dbHelper);
                }
            });
        }

        if (add != null) {
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ajouter le livre à la bibliothèque
                    Livre livre = result.get(position);
                    BookUtilities.ajouterLivreBibliotheque(activity, livre, dbHelper);
                }
            });
        }
        return rowView;
    }

}

