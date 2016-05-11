package com.univ.lorraine.cmi.database.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

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

    @DatabaseField
    private String cheminFichier;

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

    @Override
    public String toString() {
        return titre +" " +  auteur + " " + dateParution + " id  : " + idLivre;
    }
}
