package com.univ.lorraine.cmi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.univ.lorraine.cmi.database.CmidbaOpenDatabaseHelper;
import com.univ.lorraine.cmi.database.model.Livre;
import com.univ.lorraine.cmi.retrofit.CallMeIshmaelService;
import com.univ.lorraine.cmi.retrofit.CallMeIshmaelServiceProvider;

import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.epub.Main;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private SearchView searchView;
    private TextView resultText;
    ListView listResult;
    // Helper permettant d'interagir avec la database
    private CmidbaOpenDatabaseHelper dbhelper = null;

    private List<Livre> resultats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        resultText = (TextView) findViewById(R.id.status_text);

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

        resultats = new ArrayList<>();
    }

    /**
     * Overriden in order to close the database
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbhelper != null){
            OpenHelperManager.releaseHelper();
            dbhelper = null;
        }
    }

    /**
     * Retourne le databaseHelper (crée si il n'existe pas)
     * @return dbhelper
     */
    private CmidbaOpenDatabaseHelper getHelper(){
        if (dbhelper == null){
            dbhelper = OpenHelperManager.getHelper(this, CmidbaOpenDatabaseHelper.class);
        }
        return dbhelper;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_rechercher, menu);

        Utilities.checkNetworkAvailable(this);

        listResult = (ListView) findViewById(R.id.list_result);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        setupSearchView();

        return true;
    }


    private void setupSearchView() {

        if (isAlwaysExpanded())
            searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(final String query) {

        /*ProgressDialog progress = new ProgressDialog(getApplicationContext());
        progress.setMessage("Recherche...");
        progress.setIndeterminate(true);
        progress.setCanceledOnTouchOutside(false);
        progress.show();*/
        final Activity activity = this;
        CallMeIshmaelService cmiService = CallMeIshmaelServiceProvider.getService();
        Call<List<Livre>> call = cmiService.searchLivre(query, query, query, null, null);
        call.enqueue(new Callback<List<Livre>>() {
            @Override
            public void onResponse(Call<List<Livre>> call, Response<List<Livre>> response) {
                Log.e("RES", response.body().toString());
                resultats = response.body();
                listResult.setAdapter(new ListAdapter(activity, getHelper(), resultats));
            }

            @Override
            public void onFailure(Call<List<Livre>> call, Throwable t) {
                Log.e("FAIL",t.toString());
            }
        });

        //progress.dismiss();

        resultText.setText(String.format("%s%s", getResources().getString(R.string.result_recherche), query));
        return true;
    }

    protected boolean isAlwaysExpanded() {
        return false;
    }

}

