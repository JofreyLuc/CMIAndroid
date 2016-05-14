package com.univ.lorraine.cmi.database.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by jyeil on 10/05/16.
 */

@DatabaseTable(tableName = "bibliotheque")
public class Bibliotheque implements Parcelable {

    @DatabaseField(generatedId = true)
    private Long idBibliotheque;

    @DatabaseField
    private Long idServeur;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "idLivre")
    private Livre livre;

    @DatabaseField
    private double positionLecture;

    @DatabaseField
    private Date dateModification;

    // Needed by ORMlite
    Bibliotheque() {}

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

    public double getPositionLecture() {
        return positionLecture;
    }

    public void setPositionLecture(double pos) {
        if (pos <= 1d)
            positionLecture = pos;
    }

    public Livre getLivre() {
        return livre;
    }

    public void setLivre(Livre l){
        livre = l;
    }

    /* Parcelable */

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Permet de recréer un Bibliothèque à partir d'une Parcel in.
     */
    public Bibliotheque(Parcel in) {
        idBibliotheque = in.readLong();
        idServeur = in.readLong();
        livre = in.readParcelable(getClass().getClassLoader());
        positionLecture = in.readDouble();
        dateModification = new Date(in.readLong());
    }

    /**
     * Ecrit les données du Livre dans une Parcel dest.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // idBibliotheque à -1 si null pour éviter une NullPointerException
        if (idBibliotheque == null)
            idBibliotheque = Long.valueOf(-1);
        dest.writeLong(idBibliotheque);
        // idServeur à -1 si null pour éviter une NullPointerException
        if (idServeur == null)
            idServeur = Long.valueOf(-1);
        dest.writeLong(idServeur);
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
