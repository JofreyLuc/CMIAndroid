package com.univ.lorraine.cmi.reader.listener;

import android.widget.Toast;

import com.skytree.epub.State;
import com.skytree.epub.StateListener;
import com.univ.lorraine.cmi.Utilities;
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
                reader.getProgressDialog().hide();
                Utilities.enableUserInput(reader);
                break;
            case BUSY:
                Utilities.disableUserInput(reader);
                reader.getProgressDialog().show();
                break;
            case LOADING:
                Utilities.disableUserInput(reader);
                reader.getProgressDialog().show();
                break;
            case ROTATING:
                Utilities.disableUserInput(reader);
                reader.getProgressDialog().show();
                break;
        }
    }
}
