package com.univ.lorraine.cmi.reader.listener;

import android.util.Log;

import com.skytree.epub.PagingInformation;
import com.skytree.epub.PagingListener;

/**
 * Created by alexis on 15/05/2016.
 */
public class PagingDelegate implements PagingListener {

    private int nombrePages;

    public PagingDelegate() {
        nombrePages = 0;
    }

    // called when global pagination for all chapters is started
    @Override
    public void onPagingStarted(int i) {
        Log.e("SKY", "nbPages = "+nombrePages);
    }

    // called when paginating one chapter is over.
    @Override
    public void onPaged(PagingInformation pagingInformation) {
        /*int ci = pagingInformation.chapterIndex;
        int cn = rv.getNumberOfChapters();
        int value = (int)((float)ci*100/(float)cn);
        changePagingView(value);
        sd.insertPagingInformation(pagingInformation);*/
        nombrePages += pagingInformation.numberOfPagesInChapter;
    }

    // called when global pagination for all chapters is finished
    @Override
    public void onPagingFinished(int i) {
        Log.e("SKY", "nbPages = "+nombrePages);
    }

    // if there's no information for given pagingInformation, you should return 0
    @Override
    public int getNumberOfPagesForPagingInformation(PagingInformation pagingInformation) {
        return 0;
    }
}
