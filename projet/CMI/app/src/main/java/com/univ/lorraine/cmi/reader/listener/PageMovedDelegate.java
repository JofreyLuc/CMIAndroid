package com.univ.lorraine.cmi.reader.listener;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.skytree.epub.Highlight;
import com.skytree.epub.PageInformation;
import com.skytree.epub.PageMovedListener;
import com.univ.lorraine.cmi.database.model.Bibliotheque;
import com.univ.lorraine.cmi.reader.ReaderActivity;

import java.sql.SQLException;
import java.util.Locale;

/**
 * Listener appelé lors d'un changement de page.
 */
public class PageMovedDelegate implements PageMovedListener {

    ReaderActivity reader;

    Dao<Bibliotheque, Long> daobibliotheque;

    public PageMovedDelegate(ReaderActivity r) {
        reader = r;
    }

    /**
     * Changement de page.
     *
     * @param pi Informations sur la page et le livre.
     */
    public void onPageMoved(PageInformation pi) {
        String msg = "chapterIndex: "+pi.chapterIndex
                +"\nnumberOfChaptersInBook: "+pi.numberOfChaptersInBook
                +"\nnumberOfPagesInBook: "+pi.numberOfPagesInBook
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
        Log.e("SKY", msg);

        // TEST
        Bibliotheque bibliotheque = reader.getBibliotheque();
        bibliotheque.setPositionLecture(pi.pagePositionInBook);

        try {
            Dao<Bibliotheque, Long> daobibliotheque = reader.getHelper().getBibliothequeDao();
            daobibliotheque.update(bibliotheque);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Changement de chapitre.
     *
     * @param i Numéro du chapitre.
     */
    @Override
    public void onChapterLoaded(int i) {

    }

    /**
     * Tentative de changement de page au début ou à la fin d'un livre.
     *
     * @param b
     */
    @Override
    public void onFailedToMove(boolean b) {

    }

}
