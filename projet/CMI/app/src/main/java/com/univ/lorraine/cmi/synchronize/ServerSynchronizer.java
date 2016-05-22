package com.univ.lorraine.cmi.synchronize;

import android.os.AsyncTask;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.squareup.picasso.Downloader;
import com.univ.lorraine.cmi.database.CmidbaOpenDatabaseHelper;
import com.univ.lorraine.cmi.database.model.Annotation;
import com.univ.lorraine.cmi.database.model.Bibliotheque;
import com.univ.lorraine.cmi.retrofit.CallMeIshmaelService;
import com.univ.lorraine.cmi.retrofit.CallMeIshmaelServiceProvider;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * Created by alexis on 20/05/2016.
 */
public class ServerSynchronizer extends AsyncTask<Void, Integer, Void> {

    CmidbaOpenDatabaseHelper dbhelper;

    Long idUser;

    public ServerSynchronizer(CmidbaOpenDatabaseHelper dbh) {
        super();
        dbhelper = dbh;
        // TEMPORAIRE
        idUser = Long.valueOf(5);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        synchronize();
        return null;
    }

    private void synchronize() {
        if (authenticate() && idUser != null) {
            CallMeIshmaelService service = CallMeIshmaelServiceProvider.getService();
            try {
                // On récupère les bibliothèques de l'utilisateur sur le serveur
                Response<List<Bibliotheque>> biblioResponse = service.getBibliotheques(idUser).execute();
                List<Bibliotheque> bibliothequesServeur = biblioResponse.body();

                Dao<Bibliotheque, Long> bibliothequeDao = dbhelper.getBibliothequeDao();
                List<Long> listeIdBibliothequesServeur = new ArrayList<Long> ();
                // Pour chaque bibliothèque
                for(Bibliotheque biblioServeur : bibliothequesServeur) {
                    // On cherche une correspondance avec une bibliothèque de la base locale
                    List<Bibliotheque> bibliothequesMobile = bibliothequeDao.queryForEq(Bibliotheque.ID_SERVEUR_FIELD_NAME, biblioServeur.getIdServeur());

                    // Nouvelle bibliotèque
                    if (bibliothequesMobile.isEmpty()) {
                        // Ajout de la bibliothèque
                        // Ajout du livre
                        // Téléchargement de l'epub et création du dossier
                    }
                    // Bibliothèque déjà présente
                    else {
                        Bibliotheque biblioMobile = bibliothequesMobile.get(0);
                        // Si la bibliothèque du serveur est plus à jour que celle sur mobile
                        if (biblioServeur.getDateModification().after(biblioMobile.getDateModification())) {
                            // Mise à jour de la bibliothèque
                            biblioServeur.setIdBibliotheque(biblioMobile.getIdBibliotheque());
                            bibliothequeDao.update(biblioServeur);
                        }
                    }

                    /* Gestion des annotations de cette bibliothèque */

                    // On récupère les annotations de l'utilisateur sur le serveur
                    Response<List<Annotation>> annotResponse = service.getAnnotations(idUser, biblioServeur.getIdServeur()).execute();
                    List<Annotation> annotationsServeur = annotResponse.body();

                    Dao<Annotation, Long> annotationDao = dbhelper.getAnnotationDao();
                    List<Long> listeIdAnnotationsServeur = new ArrayList<Long> ();
                    // Pour chaque annotation
                    for (Annotation annotServeur : annotationsServeur) {
                        // On cherche une correspondance avec une annotation de la base locale
                        List<Annotation> annotationsMobile = annotationDao.queryForEq(Annotation.ID_SERVEUR_FIELD_NAME, annotServeur.getIdServeur());

                        // Nouvelle annotation
                        if (annotationsMobile.isEmpty()) {
                            // Ajout de l'annotation
                        }
                        // Annotation déjà présente
                        else {
                            Annotation annotMobile = annotationsMobile.get(0);
                            // Si l'annotation du serveur est plus à jour que celle sur mobile
                            if (annotServeur.getDateModification().after(annotMobile.getDateModification())) {
                                // Mise à jour de l'annotation
                                annotServeur.setIdAnnotation(annotMobile.getIdAnnotation());
                                annotationDao.update(annotServeur);
                            }
                        }

                        // On enregistre les idServeur de toutes les annotations pour gérer les annotations supprimées ensuite
                        listeIdAnnotationsServeur.add(annotServeur.getIdServeur());
                    }

                    // Pour les annotations supprimées (toutes les annotations sauf celles présentes sur le serveur et celles uniquement locales (null))
                    QueryBuilder<Annotation, Long> queryBuilder = annotationDao.queryBuilder();
                    Where<Annotation, Long> where = queryBuilder.where();
                    where.isNotNull(Annotation.ID_SERVEUR_FIELD_NAME);                          // WHERE idServeur NOT NULL
                    where.notIn(Annotation.ID_SERVEUR_FIELD_NAME, listeIdAnnotationsServeur);   // AND NOT IN (...)
                    List<Annotation> annotationsASupprimer = queryBuilder.query();
                    annotationDao.delete(annotationsASupprimer);                                // Delete résultats
                    /* Fin gestion de annotations de cette bibliothèque */

                    // On enregistre les idServeur de toutes les bibliothèques pour gérer les bibliothèques supprimées ensuite
                    listeIdBibliothequesServeur.add(biblioServeur.getIdServeur());
                }

                // Pour les bibliothèques supprimées (toutes les bibliothèques sauf celles présentes sur le serveur et celles uniquement locales (null))
                QueryBuilder<Bibliotheque, Long> queryBuilder = bibliothequeDao.queryBuilder();
                Where<Bibliotheque, Long> where = queryBuilder.where();
                where.isNotNull(Bibliotheque.ID_SERVEUR_FIELD_NAME);                          // WHERE idServeur NOT NULL
                where.notIn(Bibliotheque.ID_SERVEUR_FIELD_NAME, listeIdBibliothequesServeur); // AND NOT IN (...)
                List<Bibliotheque> bibliothequesASupprimer = queryBuilder.query();
                bibliothequeDao.delete(bibliothequesASupprimer);                              // Delete résultats
                // + Cascade sur les annotations de ces bibliothèque
                for (Bibliotheque biblioASupprimer : bibliothequesASupprimer) {
                    // Suppression dossier epub
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean authenticate() {
        // TEMPORAIRE
        return true;
    }

}
