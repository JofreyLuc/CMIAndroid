package com.univ.lorraine.cmi.database.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;

/**
 * Created by jyeil on 10/05/16.
 */

@DatabaseTable(tableName = "livre")
public class Livre {

    @DatabaseField(generatedId = true)
    private Long idLivre;

    @DatabaseField
    private Long idServeur;

    @DatabaseField
    private String titre;

    @DatabaseField
    private String auteur;

    @DatabaseField
    private String langue;

    @DatabaseField
    private String genre;

    @DatabaseField
    private String dateParution;

    @DatabaseField
    private String resume;

    @DatabaseField
    private float noteMoyenne;

    @DatabaseField
    private String lienDLEpub;

    @DatabaseField
    private String lienCouverture;

    // Needed by ORMlite
    Livre() {}

    public Livre(String t, String a, String l, String g, String d, String r, float noteM, String lienDL, String lienCou){
        titre = t;
        auteur = a;
        langue = l;
        genre = g;
        dateParution = d;
        resume = r;
        noteMoyenne = noteM;
        lienDLEpub = lienDL;
        lienCouverture = lienCou;
    }

    /**
     * Crée un livre à partir d'un objet book en extrayant ses metadatas.
     *
     * @param book Objet book.
     */
    public Livre(Book book) {
        Metadata meta = book.getMetadata();

        // Titres
        StringBuilder titres = new StringBuilder();
        String delim = "";
        for (String t : meta.getTitles()) {
            titres.append(delim);
            titres.append(t);
            delim = ", ";
        }
        this.titre = titres.toString();

        // Auteurs
        delim = "";
        StringBuilder auteurs = new StringBuilder();
        for (Author a : meta.getAuthors()) {
            titres.append(delim);
            if (a.getFirstname() != null) {
                auteurs.append(a.getFirstname() + " ");
            }
            if (a.getLastname() != null) {
                auteurs.append(a.getLastname());
            }
            delim = ", ";
        }
        this.auteur = auteurs.toString();

        // Genre
        delim = "";
        StringBuilder types = new StringBuilder();
        for (String g : meta.getTypes()) {
            titres.append(delim);
            types.append(g);
            delim = ", ";
        }
        this.genre = types.toString();

        // Dates
        nl.siegmann.epublib.domain.Date date = null;
        String dateString;
        for (nl.siegmann.epublib.domain.Date d : meta.getDates()) {
            if (d.getEvent() == nl.siegmann.epublib.domain.Date.Event.PUBLICATION) date = d;
        }
        if (date == null) dateString = "";
        else dateString = date.toString();
        this.dateParution = dateString;

        // Résumé
        delim = "";
        StringBuilder resumes = new StringBuilder();
        for (String r : meta.getDescriptions()) {
            titres.append(delim);
            resumes.append(r);
            delim = ", ";
        }
        this.resume = resumes.toString();
    }

    public Long getIdLivre() {
        return idLivre;
    }

    public boolean estImporteLocalement() {
        return idServeur == null;
    }

    @Override
    public String toString() {
        return titre +" " +  auteur + " " + dateParution + " id  : " + idLivre;
    }
}
