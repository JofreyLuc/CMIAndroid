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

import com.skytree.epub.HighlightListener;
import com.skytree.epub.Highlights;
import com.skytree.epub.ReflowableControl;

import java.util.Observable;
import java.util.Observer;

import static com.skytree.epub.Setting.debug;

public class ReflowableActivity extends AppCompatActivity {

    // Insert these variables as member variables of Activity.

    ReflowableControl rv;       // ReflowableControl

    Button markButton;          // the button to mark selected text.

    RelativeLayout ePubView;    // Basic View of Activity.

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // call the routine for creation and arrangement of ReflowableControl.
        this.makeLayout();
    }

    // the function for creation and arrangement of ReflowableControl.
    public void makeLayout() {

        // Create Highlights Object.
        Highlights highlights = new Highlights();
        String fileName = new String();

        // Set the epub file name to open.
        fileName = "Alice.epub";

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float density = metrics.density;


        // Create ReflowableControl Object.
        rv = new ReflowableControl(this);

        // Read PagesStack and save it to Bitmap.
        Bitmap pagesStack = BitmapFactory.decodeResource(getResources(), R.drawable.pages_stack);

        // Read PagesCenter and save it to Bitmap.
        Bitmap pagesCenter = BitmapFactory.decodeResource(getResources(), R.drawable.pages_center);

        // Register pagesStack to ReflowableControl.
        rv.setPagesStackImage(pagesStack);

        // Register pagesCenter to ReflowableControl.
        rv.setPagesCenterImage(pagesCenter);

        // set base directory
        rv.setBaseDirectory(Utilities.getBookStoragePath(getApplicationContext()));

        unzipBook2("livre.epub");

        // set the epub file name.
        rv.setBookName("test");

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

        // set the Listener to detect the change of state
        //rv.setStateListener(new StateDelegate());
        // set the Content processing listener.
        ContentHandler contentListener = new ContentHandler();
        rv.setContentListener(contentListener);

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

    public void unzip

    public void unzipBook2(String fileName) {

        String targetDir = new String(Utilities.getBookStoragePath(getApplicationContext()) + "/test");

        String filePath = new String(Utilities.getBookStoragePath(getApplicationContext()) + "/2");
        Unzip unzip = new Unzip(fileName, filePath, targetDir);
        unzip.addObserver(new UnzipHandler2());
        unzip.unzip();
    }

    class UnzipHandler2 implements Observer {
        @Override
        public void update(Observable observable, Object data) {
            //Unzip completed
            (new Handler()).postDelayed(new Runnable() {
                public void run() {

                }
            },500);
        }
    }
}
