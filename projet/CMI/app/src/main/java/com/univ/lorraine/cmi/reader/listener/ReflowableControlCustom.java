package com.univ.lorraine.cmi.reader.listener;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.skytree.epub.ReflowableControl;

/**
 * Created by alexis on 13/05/2016.
 */
public class ReflowableControlCustom extends ReflowableControl {

    public ReflowableControlCustom(Context context) {
        super(context);
    }

    public ReflowableControlCustom(Context context, int i) {
        super(context, i);
    }

    @Override
    public boolean isRTL() {
        try {
            return super.isRTL();
        } catch (NullPointerException e) {
            // Sleep requis
            return false;
            //return isRTL();
        }
    }

}
