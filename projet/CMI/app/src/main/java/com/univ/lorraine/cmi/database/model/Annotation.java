package com.univ.lorraine.cmi.database.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by jyeil on 10/05/16.
 */

@DatabaseTable(tableName = "annotation")
public class Annotation {

    // json Strings

    public static final String ID_SERVEUR_JSON_NAME = "idAnnotation";

    public static final String BIBLIOTHEQUE_JSON_NAME = "bibliotheque";

    public static final String POSITION_JSON_NAME = "position";

    public static final String TEXTE_JSON_NAME = "texte";

    public static final String DATE_MODIFICATION_JSON_NAME = "dateModification";

    // Database Strings

    public static final String TABLE_NAME = "annotation";

    public static final String ID_FIELD_NAME = "idAnnotation";

    public static final String ID_SERVEUR_FIELD_NAME = "idServeur";

    public static final String BIBLIOTHEQUE_FIELD_NAME = "idBibliotheque";

    public static final String POSITION_FIELD_NAME = "position";

    public static final String TEXTE_FIELD_NAME = "texte";

    public static final String DATE_MODIFICATION_FIELD_NAME = "dateModification";

    // Cet id n'est pas passé dans le json
    @DatabaseField(columnName = ID_FIELD_NAME, generatedId = true)
    private Long idAnnotation;

    @Expose
    @SerializedName(ID_SERVEUR_JSON_NAME)
    @DatabaseField(columnName = ID_SERVEUR_FIELD_NAME)
    private Long idServeur;

    @Expose
    @SerializedName(BIBLIOTHEQUE_JSON_NAME)
    @DatabaseField(columnName = BIBLIOTHEQUE_FIELD_NAME, foreign = true)
    private Bibliotheque bibliotheque;

    @Expose
    @SerializedName(POSITION_JSON_NAME)
    @DatabaseField(columnName = POSITION_FIELD_NAME)
    private double position;

    @Expose
    @SerializedName(TEXTE_JSON_NAME)
    @DatabaseField(columnName = TEXTE_FIELD_NAME)
    private String texte;

    @Expose
    @SerializedName(DATE_MODIFICATION_JSON_NAME)
    @DatabaseField(columnName = DATE_MODIFICATION_FIELD_NAME)
    private Date dateModification;

    // Needed by ORMlite
    Annotation() {}

    // GETTERS AND SETTERS

    public Long getIdAnnotation() {
        return idAnnotation;
    }

    public void setIdAnnotation(Long idAnnotation) {
        this.idAnnotation = idAnnotation;
    }

    public Long getIdServeur() {
        return idServeur;
    }

    public void setIdServeur(Long idServeur) {
        this.idServeur = idServeur;
    }

    public Bibliotheque getBibliotheque() {
        return bibliotheque;
    }

    public void setBibliotheque(Bibliotheque bibliotheque) {
        this.bibliotheque = bibliotheque;
    }

    public double getPosition() {
        return position;
    }

    public void setPosition(double position) {
        this.position = position;
    }

    public String getTexte() {
        return texte;
    }

    public void setTexte(String texte) {
        this.texte = texte;
    }

    public Date getDateModification() {
        return dateModification;
    }

    public void setDateModification(Date dateModification) {
        this.dateModification = dateModification;
    }

    // FIN GETTERS AND SETTERS

}
