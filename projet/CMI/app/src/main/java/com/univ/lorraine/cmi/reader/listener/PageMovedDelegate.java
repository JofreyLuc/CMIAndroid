package com.univ.lorraine.cmi.reader.listener;

import android.util.Log;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.skytree.epub.Highlight;
import com.skytree.epub.PageInformation;
import com.skytree.epub.PageMovedListener;
import com.univ.lorraine.cmi.database.model.Bibliotheque;
import com.univ.lorraine.cmi.reader.ReaderActivity;

import java.sql.SQLException;

/**
 * Listener appelé lors d'un changement de page.
 */
public class PageMovedDelegate implements PageMovedListener {

    ReaderActivity reader;

    Dao<Bibliotheque, Long> daobibliotheque;

    PageInformation currentPage;

    public PageMovedDelegate(ReaderActivity r) {
        reader = r;
        currentPage = new PageInformation();
    }

    /**
     * Changement de page.
     *
     * @param pi Informations sur la page et le livre.
     */
    public void onPageMoved(PageInformation pi) {
        currentPage = pi;

        String msg = "chapterIndex: "+currentPage.chapterIndex
                +"\nnumberOfChaptersInBook: "+currentPage.numberOfChaptersInBook
                +"\nnumberOfPagesInBook: "+currentPage.numberOfPagesInBook
                +"\npageIndex: "+currentPage.pageIndex
                +"\npageIndexInBook: "+currentPage.pageIndexInBook
                +"\nnumberOfPagesInChapter: "+currentPage.numberOfPagesInChapter
                +"\nchapterTitle: "+currentPage.chapterTitle
                +"\npagePositionInChapter: "+currentPage.pagePositionInChapter
                +"\npagePositionInBook: "+currentPage.pagePositionInBook
                +"\npageDescription: "+currentPage.pageDescription
                +"\nstartIndex: "+currentPage.startIndex
                +"\nendIndex: "+currentPage.endIndex
                +"\nstartOffset: "+currentPage.startOffset
                +"\nendOffset: "+currentPage.endOffset;

        if (currentPage.highlightsInPage != null)
            for (int i=0; i<currentPage.highlightsInPage.getSize(); i++) {
                Highlight th = currentPage.highlightsInPage.getHighlight(i);
                msg+=String.format(" highlight si:%d so:%d ei:%d eo:%d",th.startIndex,th.startOffset,th.endIndex,th.endOffset);
            }
        Log.e("SKY", msg);

        // On enregistre la position de ce livre avec l'objet bibliothèque

        double positionLecture;
        if (isAtLastPage())
            positionLecture = 1.;
        else
            positionLecture = currentPage.pagePositionInBook;

        Bibliotheque bibliotheque = reader.getBibliotheque();
        bibliotheque.setPositionLecture(positionLecture);

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
     * @param chapterIndex Numéro du chapitre.
     */
    @Override
    public void onChapterLoaded(int chapterIndex) {

    }

    /**
     * Tentative de changement de page au début ou à la fin d'un livre.
     *
     * @param marchePas Ne marche pas. Sert à rien.
     */
    @Override
    public void onFailedToMove(boolean marchePas) {
        Log.d("TEST", "failed move");
        if (isAtLastPage())
            onLastPage();
        else
            onFirstPage();
    }

    private void onFirstPage() {

    }

    private void onLastPage() {
        // ajouter un intent vers une nouvelle activité éventuellement
        Log.d("TEST", "apres last page");
    }

    private boolean isAtLastPage() {
        // Si on est à la dernière page
        boolean lastPage = false;
        // Pagination globale
        if (reader.getReflowableControl().isGlobalPagination())
            lastPage = currentPage.numberOfPagesInBook == currentPage.pageIndexInBook + 1;
            // Pagination par chapitre
        else
            lastPage = currentPage.numberOfChaptersInBook == currentPage.chapterIndex + 1
                    && currentPage.numberOfPagesInChapter == currentPage.pageIndex + 1;
        return lastPage;
    }
}
