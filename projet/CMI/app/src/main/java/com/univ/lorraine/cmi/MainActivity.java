package com.univ.lorraine.cmi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.univ.lorraine.cmi.database.CmidbaOpenDatabaseHelper;
import com.univ.lorraine.cmi.database.model.Livre;
import com.univ.lorraine.cmi.reader.EpubManipulator;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private CmidbaOpenDatabaseHelper dbhelper = null;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            // Choix d'un livre (fichier epub) via le Filepicker
            case FILEPICKER_CODE :
                // Résultat OK
                if (resultCode == Activity.RESULT_OK) {
                    // Tableau contenant le/les uris
                    Uri[] uriArray;
                    // Sélection multiple de fichier
                    if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                        ClipData clip = data.getClipData();
                        uriArray = new Uri[clip.getItemCount()];
                        if (clip != null)
                            for (int i = 0; i < clip.getItemCount(); i++)
                                uriArray[i] = clip.getItemAt(i).getUri();
                    }
                    // Sélection unique de fichier
                    else {
                        uriArray = new Uri[1];
                        uriArray[0] = data.getData();
                    }
                    // On importe le/les epub(s)
                    importEpubs(uriArray);
                }
                break;
        }
    }

    private void importEpubs(Uri[] epubUris) {
        String path = "";
        String fileName = "";
        for (int i = 0; i < epubUris.length; i++) {
            //ProgressDialog.show(this, "Import", "Import epub").setCancelable(false);
            // import livre local
            path = epubUris[i].getPath();
            fileName = epubUris[i].getLastPathSegment();
            try {
                EpubManipulator epm = new EpubManipulator(path, fileName.substring(0, fileName.length() - 5), getApplicationContext());
                Toast.makeText(getApplicationContext(),"Epub importé", Toast.LENGTH_SHORT).show();
            } catch (Exception e){
                Log.e("EXC", e.getMessage());
            }
        }
    }

    private void testFilePicker() {
        Intent i = new Intent(this, MyFilePickerActivity.class);

        // Options
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, true);
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


    /**
     * Returns the database helper (created if null)
     * @return
     */
    private CmidbaOpenDatabaseHelper getHelper(){
        if (dbhelper == null){
            dbhelper = OpenHelperManager.getHelper(this, CmidbaOpenDatabaseHelper.class);
        }
        return dbhelper;
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

