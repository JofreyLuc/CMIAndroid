package com.univ.lorraine.cmi.reader.listener;

import android.util.Log;

import com.skytree.epub.Highlight;
import com.skytree.epub.PageInformation;
import com.skytree.epub.PageMovedListener;
import com.univ.lorraine.cmi.BookUtilities;
import com.univ.lorraine.cmi.database.model.Bibliotheque;
import com.univ.lorraine.cmi.reader.ReaderActivity;

/**
 * Listener appelé lors d'un changement de page.
 */
public class PageMovedDelegate implements PageMovedListener {

    ReaderActivity reader;

    PageInformation currentPage;

    /**
     * Booléen permettant d'éviter plusieurs appels à afterLastPage.
     */
    boolean afterLastPageAlreadyCalled;

    /**
     * Booléen permettant d'éviter plusieurs appels à beforeFirstPage.
     */
    boolean beforeFirstPageAlreadyCalled;

    public PageMovedDelegate(ReaderActivity r) {
        reader = r;
        currentPage = new PageInformation();
        afterLastPageAlreadyCalled = false;
        beforeFirstPageAlreadyCalled = false;
    }

    public void resetAfterLastPageAlreadyCalled() {
        afterLastPageAlreadyCalled = false;
    }

    public void resetBeforeFirstPageAlreadyCalled() {
        beforeFirstPageAlreadyCalled = false;
    }

    /**
     * Changement de page.
     *
     * @param pi Informations sur la page et le livre.
     */
    public void onPageMoved(PageInformation pi) {
        currentPage = pi;

        String msg =
                "firstCharacterOffsetInPage: "+currentPage.firstCharacterOffsetInPage
                +"\ntextLengthInPage: "+currentPage.textLengthInPage
                +"\nlastCharacterOffsetInPage: "+(currentPage.textLengthInPage + currentPage.firstCharacterOffsetInPage);

        if (currentPage.highlightsInPage != null)
            for (int i=0; i<currentPage.highlightsInPage.getSize(); i++) {
                Highlight th = currentPage.highlightsInPage.getHighlight(i);
                msg+=String.format(" highlight si:%d so:%d ei:%d eo:%d",th.startIndex,th.startOffset,th.endIndex,th.endOffset);
            }
        Log.e("SKY", msg);

        // On enregistre la position de ce livre avec l'objet bibliothèque
        double positionLecture;
        Bibliotheque bibliotheque = reader.getBibliotheque();

        if (isAtLastPage())
            positionLecture = 1.;
        else
            positionLecture = currentPage.pagePositionInBook;

        // Si la position de lecture n'a pas changé, on ne fait rien
        if (positionLecture == bibliotheque.getPositionLecture())
            return;

        bibliotheque.setPositionLecture(positionLecture);
        BookUtilities.updateBibliotheque(bibliotheque, reader.getHelper());
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
            afterLastPage();
        else
            beforeFirstPage();
    }

    /**
     * Appelé lors d'une tentative de changement de page avant la 1ère page.
     */
    private void beforeFirstPage() {
        // Permet d'éviter un double appel lors d'un swipe
        if (!beforeFirstPageAlreadyCalled) {
            beforeFirstPageAlreadyCalled = true;
        }
    }

    /**
     * Appelé lors d'une tentative de changement de page après la dernière page.
     */
    private void afterLastPage() {
        // Permet d'éviter un double appel lors d'un swipe
        if (!afterLastPageAlreadyCalled) {
            afterLastPageAlreadyCalled = true;
            Log.d("TEST", "apres last page");
            reader.goToEndOfBookPage();
        }
    }

    private boolean isAtLastPage() {
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
