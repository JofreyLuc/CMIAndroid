package com.univ.lorraine.cmi;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ListView;
import android.widget.TextView;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    
    private SearchView mSearchView;
    private TextView mStatusView;

    public int [] images = {R.mipmap.no_cover,R.mipmap.no_cover,R.mipmap.no_cover,R.mipmap.no_cover,R.mipmap.no_cover,R.mipmap.no_cover,R.mipmap.no_cover,R.mipmap.no_cover};
    public String [] list = {"Book1","Book2","Book3","Book4","Book5","Book6","Book7","Book8"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mStatusView = (TextView) findViewById(R.id.status_text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_rechercher, menu);

        if (!isNetworkAvailable())
            launchingConnection();

        ListView listResult = (ListView) findViewById(R.id.list_result);
        if (listResult != null) {
            listResult.setAdapter(new ListAdapter(this, list, images));
        }


        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        setupSearchView();

        return true;
    }


    private void setupSearchView() {

        if (isAlwaysExpanded())
            mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mStatusView.setText(newText);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mStatusView.setText("Résultat(s) pour : " + query);
        return false;
    }

    protected boolean isAlwaysExpanded() {
        return false;
    }

    // verifie si une connection internet est possible
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }


    // Alert Popup si pas de connexion internet
    public void launchingConnection() {
        // Alert dialog si pas de connexion internet
        // Choix 1 : rien
        // Choix 2 : Ouverture de la page des parametres pour activer internet
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder
                .setMessage(getString(R.string.connection_popup_message))
                .setCancelable(false)
                .setTitle(getString(R.string.connection_popup_name));

        // Choix 2
        alertDialogBuilder.setPositiveButton(getString(R.string.connection_popup_settings), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
            }
        });

        // Choix 1
        alertDialogBuilder.setNegativeButton(getString(R.string.connection_popup_quit), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialogBuilder.show();
    }
}

