package com.univ.lorraine.cmi.synchronize;

import android.os.AsyncTask;

import com.univ.lorraine.cmi.synchronize.callContainer.CallContainer;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by alexis on 22/05/2016.
 */
public abstract class AbstractCallContainerQueue {

    private LinkedList<CallContainer> queue = new LinkedList<CallContainer>();

    public final void enqueue(CallContainer item) {
        final CallContainer fitem = item;
        new AsyncTask<Void, Integer, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                beforeEnqueue(fitem);
                queue.addLast(fitem);
                afterEnqueue(fitem);
                return null;
            }
        }.execute();
    }

    protected final CallContainer dequeue() {
        return queue.poll();
    }

    public final boolean isEmpty() {
        return queue.isEmpty();
    }

    public final Iterator<CallContainer> iterator() {
        return queue.iterator();
    }

    public final int size() {
        return queue.size();
    }

    /**
     * Exécute tous les call présents dans la file un par un dans l'ordre dans une AsyncTask.
     */
    public void execute() {
        if (!isEmpty()) {
            new AsyncTask<Void, Integer, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    while (!isEmpty()) {
                        CallContainer callContainer = dequeue();
                        callContainer.execute();
                    }
                    return null;
                }
            }.execute();
        }
    }

    protected abstract void beforeEnqueue(CallContainer callContainer);

    protected abstract void afterEnqueue(CallContainer callContainer);

    public final String toString() {
        return queue.toString();
    }

}
