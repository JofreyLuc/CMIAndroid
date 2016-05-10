package com.univ.lorraine.cmi.database.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by jyeil on 10/05/16.
 */

@DatabaseTable(tableName = "cmidba")
public class Bibliotheque {

    @DatabaseField(generatedId = true)
    private Long idBibliotheque;

    @DatabaseField(foreign = true)
    private Livre livre;

    @DatabaseField
    private int numeroPage;

    @DatabaseField
    private Date dateModification;
}
