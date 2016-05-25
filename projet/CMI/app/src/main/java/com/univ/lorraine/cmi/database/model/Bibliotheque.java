package com.univ.lorraine.cmi.database.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by jyeil on 10/05/16.
 */

@DatabaseTable(tableName = "bibliotheque")
public class Bibliotheque implements Parcelable {

    // json Strings

    public static final String ID_SERVEUR_JSON_NAME = "idBibliotheque";

    public static final String LIVRE_JSON_NAME = "idLivre";

    public static final String POSITION_LECTURE_JSON_NAME = "positionLecture";

    public static final String DATE_MODIFICATION_JSON_NAME = "dateModification";

    // Database Strings

    public static final String TABLE_NAME = "bibliotheque";

    public static final String ID_FIELD_NAME = "idBibliotheque";

    public static final String ID_SERVEUR_FIELD_NAME = "idServeur";

    public static final String LIVRE_FIELD_NAME = "idLivre";

    public static final String POSITION_LECTURE_FIELD_NAME = "positionLecture";

    public static final String DATE_MODIFICATION_FIELD_NAME = "dateModification";

    // Cet id n'est pas passé dans le json
    @DatabaseField(columnName = ID_FIELD_NAME, generatedId = true)
    private Long idBibliotheque;

    @Expose
    @SerializedName(ID_SERVEUR_JSON_NAME)
    @DatabaseField(columnName = ID_SERVEUR_FIELD_NAME)
    private Long idServeur;

    @Expose
    @SerializedName(LIVRE_JSON_NAME)
    @DatabaseField(columnName = LIVRE_FIELD_NAME, foreign = true, foreignAutoRefresh = true)
    private Livre livre;

    @Expose
    @SerializedName(POSITION_LECTURE_JSON_NAME)
    @DatabaseField(columnName = POSITION_LECTURE_FIELD_NAME)
    private double positionLecture;

    @Expose
    @SerializedName(DATE_MODIFICATION_JSON_NAME)
    @DatabaseField(columnName = DATE_MODIFICATION_FIELD_NAME)
    private Date dateModification;

    // Needed by ORMlite
    public Bibliotheque() {}

    /**
     * Crée un objet Bibliothèque à partir d'un livre.
     *
     * @param l Livre.
     */
    public Bibliotheque(Livre l) {
        livre = l;
        positionLecture = 0d;
        dateModification = new Date();
    }

    // GETTERS AND SETTERS

    public Long getIdBibliotheque() {
        return idBibliotheque;
    }

    public void setIdBibliotheque(Long idBibliotheque) {
        this.idBibliotheque = idBibliotheque;
    }

    public Long getIdServeur() {
        return idServeur;
    }

    public void setIdServeur(Long idServeur) {
        this.idServeur = idServeur;
    }

    public Livre getLivre() {
        return livre;
    }

    public void setLivre(Livre livre) {
        this.livre = livre;
    }

    public double getPositionLecture() {
        return positionLecture;
    }

    public void setPositionLecture(double positionLecture) {
        if (positionLecture <= 1.)
        this.positionLecture = positionLecture;
    }

    public Date getDateModification() {
        return dateModification;
    }

    public void setDateModification(Date dateModification) {
        this.dateModification = dateModification;
    }

    public String toString(){
        return "Bibliothèque : " + idBibliotheque;
    }

    // FIN GETTERS AND SETTERS

    /* Parcelable */

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Permet de recréer un Bibliothèque à partir d'une Parcel in.
     */
    public Bibliotheque(Parcel in) {
        idBibliotheque = (Long) in.readValue(Long.class.getClassLoader());
        idServeur = (Long) in.readValue(Long.class.getClassLoader());
        livre = in.readParcelable(getClass().getClassLoader());
        positionLecture = in.readDouble();
        dateModification = new Date(in.readLong());
    }

    /**
     * Ecrit les données du Livre dans une Parcel dest.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(idBibliotheque);
        dest.writeValue(idServeur);
        dest.writeParcelable(livre, flags);
        dest.writeDouble(positionLecture);
        dest.writeLong(dateModification.getTime());
    }

    public static final Parcelable.Creator<Bibliotheque> CREATOR = new Parcelable.Creator<Bibliotheque>(){
        public Bibliotheque createFromParcel(Parcel in){
            return new Bibliotheque(in);
        }

        public Bibliotheque[] newArray(int size) {
            return new Bibliotheque[size];
        }
    };

}
