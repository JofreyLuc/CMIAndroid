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

    @Override
    public void onHighlightDeleted(Highlight highlight) {

    }

    @Override
    public void onHighlightInserted(Highlight highlight) {

    }

    @Override
    public void onHighlightUpdated(Highlight highlight) {

    }

    @Override
    public void onHighlightHit(Highlight highlight, int i, int i1, Rect rect, Rect rect1) {

    }

    @Override
    public Highlights getHighlightsForChapter(int i) {
        return null;
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
