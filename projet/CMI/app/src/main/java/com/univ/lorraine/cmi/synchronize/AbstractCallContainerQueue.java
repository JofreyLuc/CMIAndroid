package com.univ.lorraine.cmi.synchronize;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.univ.lorraine.cmi.retrofit.CallMeIshmaelServiceProvider;
import com.univ.lorraine.cmi.synchronize.callContainer.CallContainer;
import com.univ.lorraine.cmi.synchronize.callContainer.bibliotheque.AbstractBibliothequeCall;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import retrofit2.Call;

/**
 * Created by alexis on 22/05/2016.
 */
public abstract class AbstractCallContainerQueue {

    private AsyncTask<Void, Integer, Void> task;

    @Expose
    private List<CallContainer> queue = new CopyOnWriteArrayList<CallContainer>();

    public AbstractCallContainerQueue() {

    }

    public List<CallContainer> getQueue() {
        return queue;
    }

    public void setQueue(List<CallContainer> queue) {
        this.queue = queue;
    }

    public synchronized final void enqueue(CallContainer item) {
        final CallContainer fitem = item;
        new AsyncTask<Void, Integer, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                beforeEnqueue(fitem);
                queue.add(fitem);
                afterEnqueue(fitem);
                return null;
            }
        }.execute();
    }

    protected final void requeue(CallContainer item) {
        queue.add(0, item);
    }

    protected final CallContainer dequeue() {
        return queue.remove(0);
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
    public synchronized void execute() {
        if (!isEmpty()) {
            task = new AsyncTask<Void, Integer, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    // Tant que la file n'est pas vide
                    // et que l'asynctask n'est pas annulé
                    while (!isEmpty() && !isCancelled()) {
                        CallContainer callContainer = dequeue();
                        callContainer.execute(CallMeIshmaelServiceProvider.getService());
                        Log.e("QUEUE", "Exécution : "+callContainer);
                        if (callContainer.hasFailed()) {    // Si le call a fail
                            requeue(callContainer);         // On le remet dans la file
                            onCallFailed(callContainer);
                        }
                    }
                    Log.e("QUEUE", "Fin exécution");
                    return null;
                }
            };
            task.execute();
        }
    }

    /**
     * Annule et stoppe l'asynctask d'execute(), arrêtant l'exécution des calls de la file.
     */
    protected void cancel() {
        task.cancel(true);
    }

    protected abstract void beforeEnqueue(CallContainer callContainer);

    protected abstract void afterEnqueue(CallContainer callContainer);

    protected abstract void onCallFailed(CallContainer callContainer);

    public final void clear() {
        queue.clear();
    }

    public final String toString() {
        return queue.toString();
    }

}
