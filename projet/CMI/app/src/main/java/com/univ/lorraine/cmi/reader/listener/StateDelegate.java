package com.univ.lorraine.cmi.reader.listener;

import android.view.View;

import com.skytree.epub.State;
import com.skytree.epub.StateListener;
import com.univ.lorraine.cmi.reader.ReaderActivity;

/**
 * Created by alexis on 13/05/2016.
 */
public class StateDelegate implements StateListener {

    private ReaderActivity reader;

    public StateDelegate(ReaderActivity r) {
        reader = r;
    }

    @Override
    public void onStateChanged(State state) {
        switch (state) {
            case NORMAL:
                reader.getLoadingBar().setVisibility(View.GONE);
                break;
            case BUSY:
                break;
            case LOADING:
                reader.getLoadingBar().setVisibility(View.VISIBLE);
                break;
            case ROTATING:
                break;
        }
    }
}
