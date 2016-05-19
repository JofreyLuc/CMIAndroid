package com.univ.lorraine.cmi.reader;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.skytree.epub.Book;
import com.skytree.epub.Highlights;
import com.skytree.epub.KeyListener;
import com.skytree.epub.PageTransition;
import com.skytree.epub.ReflowableControl;
import com.skytree.epub.SkyKeyManager;
import com.skytree.epub.SkyProvider;

import com.squareup.picasso.Picasso;
import com.univ.lorraine.cmi.R;
import com.univ.lorraine.cmi.Utilities;
import com.univ.lorraine.cmi.database.CmidbaOpenDatabaseHelper;
import com.univ.lorraine.cmi.database.model.Bibliotheque;
import com.univ.lorraine.cmi.database.model.Livre;
import com.univ.lorraine.cmi.reader.listener.ContentHandler;
import com.univ.lorraine.cmi.reader.listener.HighlightDelegate;
import com.univ.lorraine.cmi.reader.listener.PageMovedDelegate;
import com.univ.lorraine.cmi.reader.listener.PagingDelegate;
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

    private CmidbaOpenDatabaseHelper dbhelper = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // On récupère l'objet bibliothèque lié au livre passé dans l'Intent
        bibliotheque = getIntent().getBundleExtra("bundle").getParcelable("bibliotheque");

        // ProgressDialog non annulable en cliquant sur l'écran
        // mais annulable avec la touche Back + fin de l'activité
        progress = new ProgressDialog(this);
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
    public CmidbaOpenDatabaseHelper getHelper(){
        if (dbhelper == null){
            dbhelper = OpenHelperManager.getHelper(this, CmidbaOpenDatabaseHelper.class);
        }
        return dbhelper;
    }

    public Bibliotheque getBibliotheque() {
        return bibliotheque;
    }

    public ProgressDialog getProgressDialog() {
        return progress;
    }

    public ReflowableControl getReflowableControl() {
        return rv;
    }

    // Création et arrangement du ReflowableControl
    public void makeLayout() {

        Livre livre = bibliotheque.getLivre();
        // Chemin du fichier epub déduit de l'id du livre
        String bookFilePath = Utilities.getBookFilePath(getApplicationContext(), livre);
        // Position de lecture de départ
        float startPosition = (float)bibliotheque.getPositionLecture();

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

        /*rv.setGlobalPagination(true);
        rv.setPagingListener(new PagingDelegate());*/


        // delay times for proper operations.
        // !! DO NOT SET these values if there's no issue on your epub reader. !!
        // !! if delayTime is decresed, performance will be increase
        // !! if delayTime is set to too low value, a lot of problem can be occurred.
        // bringDelayTime(default 500 ms) is for curlView and mainView transition - if the value is too short, blink may happen.
        //rv.setBringDelayTime(500);
        // reloadDelayTime(default 100) is used for delay before reload (eg. changeFont, loadChapter or etc)
        //rv.setReloadDelayTime(100);
        // reloadDelayTimeForRotation(default 1000) is used for delay before rotation
        //rv.setReloadDelayTimeForRotation(1000);
        // retotaionDelayTime(default 1500) is used for delay after rotation.
        //rv.setRotationDelayTime(1500);
        // finalDelayTime(default 500) is used for the delay after loading chapter.
        //rv.setFinalDelayTime(500);
        // rotationFactor affects the delayTime before Rotation. default value 1.0f
        //rv.setRotationFactor(1.0f);
        // If recalcDelayTime is too short, setContentBackground function failed to work properly.
        //rv.setRecalcDelayTime(2500);

        // Read PagesStack and save it to Bitmap.
        Bitmap pagesStack = BitmapFactory.decodeResource(getResources(), R.drawable.pages_stack);

        // Read PagesCenter and save it to Bitmap.
        Bitmap pagesCenter = BitmapFactory.decodeResource(getResources(), R.drawable.pages_center);

        // Register pagesStack to ReflowableControl.
        rv.setPagesStackImage(pagesStack);

        // Register pagesCenter to ReflowableControl.
        rv.setPagesCenterImage(pagesCenter);

        rv.setMaxSizeForBackground(1024);

        // set two pages mode(double paged mode) when landscape view.
        boolean doublePaged = true;
        rv.setDoublePagedForLandscape(doublePaged);

        Bitmap portraitBackground = BitmapFactory.decodeResource(getResources(), R.drawable.phone_portrait_white);

        Bitmap landscapeBackground;
        if (doublePaged)
            landscapeBackground = BitmapFactory.decodeResource(getResources(),R.drawable.phone_landscape_double_white);
        else
            landscapeBackground = BitmapFactory.decodeResource(getResources(), R.drawable.phone_landscape_white);

        rv.setForegroundColor(Color.BLACK);

        rv.setBackgroundForPortrait(portraitBackground, new Rect(0, 0, 2004, 1506), new Rect(32, 0, 2004 - 32, 1506));            // Android Rect - left,top,right,bottom

        rv.setBackgroundForLandscape(landscapeBackground, new Rect(0, 0, 1002, 1506), new Rect(0, 0, 1002 - 32, 1506)); 			// Android Rect - left,top,right,bottom

        // set default font
        rv.setFont("TimesRoman", 20);

        // set default line space.  ( unit is %)
        rv.setLineSpacing(135); // the value is supposed to be percent(%).

        // set the left and right margins. 15% of screen width.
        rv.setHorizontalGapRatio(0.30);
        // set the top and bottom margins. 10% of screen height.
        rv.setVerticalGapRatio(0.22);

        // set the Listener for Highlight processing.
        rv.setHighlightListener(new HighlightDelegate(highlights));

        // set the Listener for Page Moving.
        rv.setPageMovedListener(new PageMovedDelegate(this));

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
        markButton.setId(markButton.generateViewId());
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
