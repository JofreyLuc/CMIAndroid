package com.univ.lorraine.cmi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.skytree.epub.Book;
import com.skytree.epub.HighlightListener;
import com.skytree.epub.Highlights;
import com.skytree.epub.KeyListener;
import com.skytree.epub.ReflowableControl;
import com.skytree.epub.SkyKeyManager;
import com.skytree.epub.SkyProvider;

import java.util.Observable;
import java.util.Observer;

import static com.skytree.epub.Setting.debug;

public class ReflowableActivity extends AppCompatActivity {

    // Insert these variables as member variables of Activity.

    ReflowableControl rv;       // ReflowableControl

    Button markButton;          // the button to mark selected text.

    RelativeLayout ePubView;    // Basic View of Activity.

    SkyKeyManager keyManager;

    Long idLivre;               // Id du livre à lire (bdd)

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // call the routine for creation and arrangement of ReflowableControl.
        this.keyManager = new SkyKeyManager("A3UBZzJNCoXmXQlBWD4xNo", "zfZl40AQXu8xHTGKMRwG69");
        Bundle bundle = getIntent().getExtras();
        idLivre = (Long)bundle.get("idLivre");
        this.makeLayout();
    }

    // the function for creation and arrangement of ReflowableControl.
    public void makeLayout() {

        String bookFilePath = (Utilities.getBookStoragePath(getApplicationContext())
                + "/" + idLivre
                +"/livre.epub");

        // Create Highlights Object.
        Highlights highlights = new Highlights();

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float density = metrics.density;

        // Create ReflowableControl Object.
        rv = new ReflowableControl(this);

        // set base directory
        rv.setBaseDirectory(Utilities.getBookStoragePath(getApplicationContext()));

        // set the epub file path.
        rv.setBookPath(bookFilePath);

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
        //rv.setHighlightListener(new HighlightDelegate());

        // set the Listener for Page Moving.
        rv.setPageMovedListener(new PageMovedDelegate());

        // set the Listener for text processing
        //rv.setSelectionListener(new SelectionDelegate());

        // set the Listener for Searching.
        //rv.setSearchListener(new SearchDelegate());

        // ContentProvider
        SkyProvider skyProvider = new SkyProvider();
        skyProvider.setKeyListener(new KeyDelegate());
        rv.setContentProvider(skyProvider);

        // set the Listener to detect the change of state
        //rv.setStateListener(new StateDelegate());
        // set the Content processing listener.
        ContentHandler contentListener = new ContentHandler();
        //rv.setContentListener(contentListener);

        // set the start point to read.
        rv.setStartPositionInBook(0);
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
        // insert RelativeVie into ContentView.
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
        markButton.setVisibility(View.INVISIBLE);
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
