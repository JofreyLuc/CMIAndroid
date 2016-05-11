package com.univ.lorraine.cmi;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.univ.lorraine.cmi.database.CmidbaOpenDatabaseHelper;
import com.univ.lorraine.cmi.database.model.Livre;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private static final int FILEPICKER_CODE = 0;

    Integer[] imageIDs = {
            R.mipmap.book,
            R.mipmap.book,
            R.mipmap.book,
            R.mipmap.book,
            R.mipmap.book,
            R.mipmap.book,
            R.mipmap.book,
            R.mipmap.book,
            R.mipmap.book,
            R.mipmap.book,
            R.mipmap.book,
            R.mipmap.book,
            R.mipmap.book,
            R.mipmap.book,

    };

    String[] titles = {
            "book1",
            "book2",
            "book3",
            "book4",
            "book5",
            "book6",
            "book7",
            "book8",
            "book9",
            "book10",
            "book11",
            "book12",
            "book13",
            "book14",
            "book15",


    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GridView gridView = (GridView) findViewById(R.id.grid);
        gridView.setAdapter(new ImageAdapter(this));
        try {
            testDatabase();
        } catch (SQLException e){
            Toast.makeText(getApplicationContext(), "bugbug", Toast.LENGTH_LONG).show();
        }
        // Création du dossier interne de l'app
        getApplicationContext().getDir("CallMeIshmael", Context.MODE_PRIVATE);

       testFilePicker();
    }

    // fonction qui initialise un menu de lActionBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_biblio_perso, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // fonction qui gere les actions des items des menus de lActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                // DO SOMETHING
                //Toast.makeText(getApplicationContext(), "action_new", Toast.LENGTH_LONG).show();
                testFilePicker();
                return true;
            case R.id.overflow1:
                // DO SOMETHING
                Toast.makeText(getApplicationContext(), "overflow1", Toast.LENGTH_LONG).show();
                return true;
            case R.id.overflow2:
                // DO SOMETHING
                Toast.makeText(getApplicationContext(), "overflow2", Toast.LENGTH_LONG).show();
                return true;
            default:
                return false;
        }
    }

    // fonction qui gere les actions des items du menu de chaque livre
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_supp:
                // DO SOMETHING
                Toast.makeText(getApplicationContext(), "action_supp", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_details:
                // DO SOMETHING
                Toast.makeText(getApplicationContext(), "action_details", Toast.LENGTH_LONG).show();
                return true;
            default:
                return false;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            // Choix d'un livre (fichier epub) via le Filepicker
            case FILEPICKER_CODE :
                // Résultat OK
                if (resultCode == Activity.RESULT_OK) {
                    // Sélection multiple de fichier
                    if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                        ClipData clip = data.getClipData();
                        if (clip != null)
                            for (int i = 0; i < clip.getItemCount(); i++) {
                                Uri uri = clip.getItemAt(i).getUri();
                                Toast.makeText(getApplicationContext(), uri.toString(), Toast.LENGTH_LONG).show();
                                // import livre local
                            }
                    }
                    // Sélection unique de fichier
                    else {
                        Uri uri = data.getData();
                        Toast.makeText(getApplicationContext(), uri.toString(), Toast.LENGTH_LONG).show();
                        // import livre local
                    }
                }
                break;
        }
    }

    private void testFilePicker() {
        Intent i = new Intent(getApplicationContext(), MyFilePickerActivity.class);

        // Options
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);

        // Configure initial directory by specifying a String.
        // You could specify a String like "/storage/emulated/0/", but that can
        // dangerous. Always use Android's API calls to get paths to the SD-card or
        // internal memory.
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
        startActivityForResult(i, FILEPICKER_CODE);
    }

    private void testDatabase() throws SQLException{
        CmidbaOpenDatabaseHelper dbhelper = OpenHelperManager.getHelper(this, CmidbaOpenDatabaseHelper.class);

        Dao<Livre, Long> daolivre = dbhelper.getLivreDao();
        Date currentTime = new Date(System.currentTimeMillis());

        daolivre.create(new Livre(currentTime));

        List<Livre> ll = daolivre.queryForAll();
        Toast.makeText(getApplicationContext(), ll.get(0).toString(), Toast.LENGTH_LONG).show();
    }

    // inflate le menu de gestion des livres (suppression, details)
    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_livre);
        popup.show();
    }


    /**********************
     * classe qui customise les items de la gridview
     */
    public class ImageAdapter extends BaseAdapter {
        private Context context;

        public ImageAdapter(Context c)
        {
            context = c;
        }


        @Override
        public int getCount() {
            return imageIDs.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            ImageView icon;
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row=inflater.inflate(R.layout.grid_item, parent, false);
            TextView label=(TextView)row.findViewById(R.id.icon_text);
            label.setText(titles[position]);
            icon=(ImageView)row.findViewById(R.id.icon_image);
            icon.setImageResource(imageIDs[position]);
            return row;
        }
    }
}

