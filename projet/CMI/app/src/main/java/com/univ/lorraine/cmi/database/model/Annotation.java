package com.univ.lorraine.cmi.database.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by jyeil on 10/05/16.
 */

@DatabaseTable(tableName = "annotation")
public class Annotation {

    public static final String TABLE_NAME = "annotation";

    public static final String ID_FIELD_NAME = "idAnnotation";

    public static final String ID_SERVEUR_FIELD_NAME = "idServeur";

    public static final String BIBLIOTHEQUE_FIELD_NAME = "idBibliotheque";

    public static final String NUMERO_PAGE_FIELD_NAME = "numeroPage";

    public static final String NUMERO_FIELD_NAME = "numero";

    public static final String TEXTE_FIELD_NAME = "texte";

    public static final String DATE_MODIFICATION_FIELD_NAME = "dateModification";

    @DatabaseField(columnName = ID_FIELD_NAME, generatedId = true)
    private Long idAnnotation;

    @DatabaseField(columnName = ID_SERVEUR_FIELD_NAME)
    private Long idServeur;

    @DatabaseField(columnName = BIBLIOTHEQUE_FIELD_NAME, foreign = true)
    private Bibliotheque bibliotheque;

    @DatabaseField(columnName = NUMERO_PAGE_FIELD_NAME)
    private int numeroPage;

    @DatabaseField(columnName = NUMERO_FIELD_NAME)
    private int numero;

    @DatabaseField(columnName = TEXTE_FIELD_NAME)
    private String texte;

    @DatabaseField(columnName = DATE_MODIFICATION_FIELD_NAME)
    private Date dateModification;

    // Needed by ORMlite
    Annotation() {}

    public void setBibliotheque(Bibliotheque b){
        bibliotheque = b;
    }
}
