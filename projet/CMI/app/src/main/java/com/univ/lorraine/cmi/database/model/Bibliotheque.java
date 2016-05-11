package com.univ.lorraine.cmi.database.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by jyeil on 10/05/16.
 */

@DatabaseTable(tableName = "bibliotheque")
public class Bibliotheque {

    @DatabaseField(generatedId = true)
    private Long idBibliotheque;

    @DatabaseField
    private Long idServeur;

    @DatabaseField(foreign = true, columnName = "idLivre")
    private Livre livre;

    @DatabaseField
    private int numeroPage;

    @DatabaseField
    private Date dateModification;

    // Needed by ORMlite
    Bibliotheque() {}

    public void setLivre(Livre l){
        livre = l;
    }
}
