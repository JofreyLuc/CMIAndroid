package com.univ.lorraine.cmi.reader;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.skytree.epub.Book;
import com.skytree.epub.HighlightListener;
import com.skytree.epub.Highlights;
import com.skytree.epub.KeyListener;
import com.skytree.epub.PageTransition;
import com.skytree.epub.ReflowableControl;
import com.skytree.epub.SkyKeyManager;
import com.skytree.epub.SkyProvider;

import com.univ.lorraine.cmi.R;
import com.univ.lorraine.cmi.Utilities;
import com.univ.lorraine.cmi.database.model.Bibliotheque;
import com.univ.lorraine.cmi.database.model.Livre;
import com.univ.lorraine.cmi.reader.listener.ContentHandler;
import com.univ.lorraine.cmi.reader.listener.HighlightDelegate;
import com.univ.lorraine.cmi.reader.listener.PageMovedDelegate;
import com.univ.lorraine.cmi.reader.listener.ReflowableControlCustom;
import com.univ.lorraine.cmi.reader.listener.SearchDelegate;
import com.univ.lorraine.cmi.reader.listener.SelectionDelegate;
import com.univ.lorraine.cmi.reader.listener.StateDelegate;

/**
 * Activité principale du reader.
 * Utilise le SDK SkyEpub.
 */
public class ReaderActivity extends AppCompatActivity {

    /** ReflowableControl hérite de RelativeLayout.
     * Permet la lecture des fichiers epub.
     */
    private ReflowableControlCustom rv;

    /**
     * Bouton Highlight
     */
    private Button markButton;

    /**
     * Vue basique de l'activité
     */
    private RelativeLayout ePubView;

    /**
     * Permet de gérer les epubs encryptés.
     * Nécessaire au ContentProvider qui se charge de lire le fichier epub.
     */
    private SkyKeyManager keyManager;

    /**
     * Barre de chargement lors du chargement d'un epub.
     */
    private ProgressDialog progress;

    /**
     * Objet bibliothèque lié au livre à lire.
     */
    private Bibliotheque bibliotheque;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // On récupère l'objet bibliothèque lié au livre passé dans l'Intent
        Bundle b = getIntent().getBundleExtra("bundle");
        bibliotheque = b.getParcelable("bibliotheque");

        // ProgressDialog non annulable en cliquant sur l'écran
        // mais annulable avec la touche Back + fin de l'activité
        progress=new ProgressDialog(this);
        progress.setMessage("Chargement...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.setCancelable(true);
        progress.setCanceledOnTouchOutside(false);
        progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });

        this.keyManager = new SkyKeyManager("", "");
        this.makeLayout();
    }

    public ProgressDialog getProgressDialog() {
        return progress;
    }

    public ViewGroup getView() {
        return rv;
    }

    // Création et arrangement du ReflowableControl
    public void makeLayout() {

        Livre livre = bibliotheque.getLivre();
        // Chemin du fichier epub déduit de l'id du livre
        String bookFilePath = Utilities.getBookFilePath(getApplicationContext(), livre);
        // Position de lecture de départ
        float startPosition = bibliotheque.getPositionLecture();

        // Création des highlights (surlignage)
        Highlights highlights = new Highlights();

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float density = metrics.density;

        // Création de l'objet ReflowableControl
        rv = new ReflowableControlCustom(this);

        // Passe le répertoire de base.
        rv.setBaseDirectory(Utilities.getBookStoragePath(getApplicationContext()));

        // Passe le chemin de l'epub.
        rv.setBookPath(bookFilePath);

        rv.setStartPosition(startPosition);

        rv.setPageTransition(PageTransition.Curl);
        //rv.setCurlQuality(0.5f);

        // Read PagesStack and save it to Bitmap.
        Bitmap pagesStack = BitmapFactory.decodeResource(getResources(), R.drawable.pages_stack);

        // Read PagesCenter and save it to Bitmap.
        Bitmap pagesCenter = BitmapFactory.decodeResource(getResources(), R.drawable.pages_center);


        // Register pagesStack to ReflowableControl.
        rv.setPagesStackImage(pagesStack);

        // Register pagesCenter to ReflowableControl.
        rv.setPagesCenterImage(pagesCenter);

        // set two pages mode(double paged mode) when landscape view.
        rv.setDoublePagedForLandscape(true);

        // set default font
        rv.setFont("TimesRoman", 26);

        // set default line space.  ( unit is %)
        rv.setLineSpacing(135); // the value is supposed to be percent(%).

        // set the left and right margins. 15% of screen width.
        rv.setHorizontalGapRatio(0.15);
        // set the top and bottom margins. 10% of screen height.
        rv.setVerticalGapRatio(0.1);

        // set the Listener for Highlight processing.
        rv.setHighlightListener(new HighlightDelegate(highlights));

        // set the Listener for Page Moving.
        rv.setPageMovedListener(new PageMovedDelegate());

        // set the Listener for text processing
        rv.setSelectionListener(new SelectionDelegate());

        // set the Listener for Searching.
        rv.setSearchListener(new SearchDelegate());

        // set the Content processing listener.
        ContentHandler contentListener = new ContentHandler();
        rv.setContentListener(contentListener);

        // ContentProvider
        SkyProvider skyProvider = new SkyProvider();
        skyProvider.setKeyListener(new KeyDelegate());
        rv.setContentProvider(skyProvider);

        // set the Listener to detect the change of state
        rv.setStateListener(new StateDelegate(this));

        rv.setNavigationAreaWidthRatio(0.4f); // both left and right side.

        // Create Layout Params for ReflowableControl
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        params.width = RelativeLayout.LayoutParams.FILL_PARENT;
        params.height = RelativeLayout.LayoutParams.FILL_PARENT;
        rv.setLayoutParams(params);

        // Create RelativeLayout for ContentView.
        ePubView = new RelativeLayout(this);
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT,
                RelativeLayout.LayoutParams.FILL_PARENT);
        ePubView.setLayoutParams(rlp);
        // insert RelativeView into ContentView.
        ePubView.addView(rv);

        // Create the button to show up when text selected.
        RelativeLayout.LayoutParams markButtonParam = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        markButton = new Button(this);
        markButton.setText("Highlight");
        markButtonParam.leftMargin = (int) (240 * density);
        markButtonParam.topMargin = (int) (5 * density);
        markButtonParam.width = (int) (70 * density);
        markButtonParam.height = (int) (35 * density);
        markButton.setLayoutParams(markButtonParam);
        markButton.setId(8083);
        //markButton.setOnClickListener(onClickListener);
        markButton.setVisibility(View.VISIBLE);
        ePubView.addView(markButton);

        //  Specify ePubView including ReflowableView and mark Button as a ContentView of Activity.
        setContentView(ePubView);
    }

    class KeyDelegate implements KeyListener {
        @Override
        public String getKeyForEncryptedData(String uuidForContent, String contentName, String uuidForEpub) {
            String key = keyManager.getKey(uuidForContent,uuidForEpub);
            return key;
        }

        @Override
        public Book getBook() {
            return rv.getBook();
        }
    }
}
