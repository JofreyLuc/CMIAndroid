package com.univ.lorraine.cmi.database.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by jyeil on 10/05/16.
 */

@DatabaseTable(tableName = "annotation")
public class Annotation {

    @DatabaseField(generatedId = true)
    private Long idAnnotation;

    @DatabaseField
    private Long idServeur;

    @DatabaseField(foreign = true, columnName = "idBibliotheque")
    private Bibliotheque bibliotheque;

    @DatabaseField
    private int numeroPage;

    @DatabaseField
    private int numero;

    @DatabaseField
    private String texte;

    @DatabaseField
    private Date dateModification;

    // Needed by ORMlite
    Annotation() {}

    public void setBibliotheque(Bibliotheque b){
        bibliotheque = b;
    }
}
