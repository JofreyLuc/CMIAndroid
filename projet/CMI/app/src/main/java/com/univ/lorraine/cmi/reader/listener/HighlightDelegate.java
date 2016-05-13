package com.univ.lorraine.cmi.reader.listener;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.skytree.epub.Caret;
import com.skytree.epub.Highlight;
import com.skytree.epub.HighlightListener;
import com.skytree.epub.Highlights;

/**
 * Created by alexis on 13/05/2016.
 */
public class HighlightDelegate implements HighlightListener {

    // <RAJOUTE
    Highlights highlights;

    public HighlightDelegate(Highlights h){
        highlights = h;
    }
    // RAJOUTE>

    public void onHighlightDeleted(Highlight highlight) {
        for (int index = 0; index < highlights.getSize(); index++) {
            Highlight temp = highlights.getHighlight(index);
            if (temp.chapterIndex == highlight.chapterIndex
                    && temp.startIndex == highlight.startIndex
                    && temp.endIndex == highlight.endIndex
                    && temp.startOffset == highlight.startOffset
                    && temp.endOffset == highlight.endOffset) {
                highlights.removeHighlight(index);
            }
        }
    }

    public void onHighlightInserted(Highlight highlight) {
        highlights.addHighlight(highlight);
    }

    @Override
    public void onHighlightUpdated(Highlight highlight) {

    }

    @Override
    public void onHighlightHit(Highlight highlight, int i, int i1, Rect rect, Rect rect1) {

    }

    public Highlights getHighlightsForChapter(int chapterIndex) {
        Highlights results = new Highlights();
        for (int index = 0; index < highlights.getSize(); index++) {
            Highlight highlight = highlights.getHighlight(index);
            if (highlight.chapterIndex == chapterIndex) {
                results.addHighlight(highlight);
            }
        }
        return results;
    }

    @Override
    public Bitmap getNoteIconBitmapForColor(int i, int i1) {
        return null;
    }

    @Override
    public void onNoteIconHit(Highlight highlight) {

    }

    @Override
    public Rect getNoteIconRect(int i, int i1) {
        return null;
    }

    @Override
    public void onDrawHighlightRect(Canvas canvas, Highlight highlight, Rect rect) {

    }

    @Override
    public void onDrawCaret(Canvas canvas, Caret caret) {

    }
}
