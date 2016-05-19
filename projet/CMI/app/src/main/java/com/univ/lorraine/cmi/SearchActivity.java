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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


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

        Spinner spinnerLangue = (Spinner) findViewById(R.id.spinnerLangue);

        if (spinnerLangue != null) {
            spinnerLangue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedItemText = (String) parent.getItemAtPosition(position);
                    if(position > 0){
                        Toast.makeText(getApplicationContext(), "Selection : " + selectedItemText, Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_rechercher, menu);

        Utilities.checkNetworkAvailable(this);

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
        //mStatusView.setText(newText);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mStatusView.setText(String.format("%s%s", getResources().getString(R.string.result_recherche), query));
        return false;
    }

    protected boolean isAlwaysExpanded() {
        return false;
    }

}

