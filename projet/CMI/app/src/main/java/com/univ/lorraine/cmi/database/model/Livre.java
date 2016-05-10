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
    private String titre;

    @DatabaseField
    private String auteur;

    @DatabaseField
    private String langue;

    @DatabaseField
    private String genre;

    @DatabaseField
    private Date dateParution;

    @DatabaseField
    private String resume;

    @DatabaseField
    private int nombrePages;

    @DatabaseField
    private float noteMoyenne;

    @DatabaseField
    private String lienDLEpub;

    @DatabaseField
    private String lienCouverture;

    // Needed by ORMlite
    Livre() {}
}
