package com.univ.lorraine.cmi;

import android.util.Log;
import android.widget.Toast;

import com.skytree.epub.Highlight;
import com.skytree.epub.PageInformation;
import com.skytree.epub.PageMovedListener;

import static com.skytree.epub.Setting.debug;

/**
 * Created by alexis on 13/05/2016.
 */
class PageMovedDelegate implements PageMovedListener {
    public void onPageMoved(PageInformation pi) {
        String msg = "chapterIndex: "+pi.chapterIndex
                +"\nnumberOfChaptersInBook: "+pi.numberOfChaptersInBook
                +"\nnumberOfPagesInBook"+pi.numberOfPagesInBook
                +"\npageIndex: "+pi.pageIndex
                +"\nnumberOfPagesInChapter: "+pi.numberOfPagesInChapter
                +"\nchapterTitle: "+pi.chapterTitle
                +"\npagePositionInChapter: "+pi.pagePositionInChapter
                +"\npagePositionInBook: "+pi.pagePositionInBook
                +"\npageDescription: "+pi.pageDescription
                +"\nstartIndex: "+pi.startIndex
                +"\nendIndex: "+pi.endIndex
                +"\nstartOffset: "+pi.startOffset
                +"\nendOffset: "+pi.endOffset;

        if (pi.highlightsInPage != null)
            for (int i=0; i<pi.highlightsInPage.getSize(); i++) {
                Highlight th = pi.highlightsInPage.getHighlight(i);
                msg+=String.format(" highlight si:%d so:%d ei:%d eo:%d",th.startIndex,th.startOffset,th.endIndex,th.endOffset);
            }
        msg+=pi.pageDescription;
        Log.e("SKY", msg);
    }

    @Override
    public void onChapterLoaded(int i) {

    }

    @Override
    public void onFailedToMove(boolean b) {

    }

}
