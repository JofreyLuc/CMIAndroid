package com.univ.lorraine.cmi.database.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;

/**
 * Classe représentant un livre, importé sur l'appareil de l'utilisateur ou présent dans la base de données Gutembert+.
 * Contient toutes les métadonnées du livre et un id unique autogénéré.
 * Correspond à une table sqlite "livre" dans la base de données locale. Parcelable.
 */

@DatabaseTable(tableName = "livre")
public class Livre implements Parcelable {

    public static final String TABLE_NAME = "livre";

    public static final String ID_FIELD_NAME = "idLivre";

    public static final String ID_SERVEUR_FIELD_NAME = "idServeur";

    public static final String TITRE_FIELD_NAME = "titre";

    public static final String AUTEUR_FIELD_NAME = "auteur";

    public static final String LANGUE_FIELD_NAME = "langue";

    public static final String GENRE_FIELD_NAME = "genre";

    public static final String DATE_PARUTION_FIELD_NAME = "dateParution";

    public static final String RESUME_FIELD_NAME = "resume";

    public static final String NOTE_MOYENNE_FIELD_NAME = "noteMoyenne";

    public static final String LIEN_DL_EPUB_FIELD_NAME = "lienDLEpub";

    // Id local du livre
    @DatabaseField(columnName = ID_FIELD_NAME, generatedId = true)
    private Long idLivre;

    // Id serveur (Gutembert) du livre, si non-importé en local
    @DatabaseField(columnName = ID_SERVEUR_FIELD_NAME)
    private Long idServeur;

    @DatabaseField(columnName = TITRE_FIELD_NAME)
    private String titre;

    @DatabaseField(columnName = AUTEUR_FIELD_NAME)
    private String auteur;

    @DatabaseField(columnName = LANGUE_FIELD_NAME)
    private String langue;

    @DatabaseField(columnName = GENRE_FIELD_NAME)
    private String genre;

    @DatabaseField(columnName = DATE_PARUTION_FIELD_NAME)
    private String dateParution;

    @DatabaseField(columnName = RESUME_FIELD_NAME)
    private String resume;

    @DatabaseField(columnName = NOTE_MOYENNE_FIELD_NAME)
    private float noteMoyenne;

    @DatabaseField(columnName = LIEN_DL_EPUB_FIELD_NAME)
    private String lienDLEpub;

    // Nécéssaire pour ORMlite
    Livre() {}

    /**
     * Crée un livre à partir de données directes (titre, auteur...).
     */
    public Livre(String t, String a, String l, String g, String d, String r, float noteM, String lienDL) {
        titre = t;
        auteur = a;
        langue = l;
        genre = g;
        dateParution = d;
        resume = r;
        noteMoyenne = noteM;
        lienDLEpub = lienDL;
    }

    /**
     * Crée un livre à partir d'un objet book en extrayant ses metadatas.
     *
     * @param book Objet book.
     */
    public Livre(Book book) {
        Metadata meta = book.getMetadata();

        String delim = "";

        // Titres
        StringBuilder titres = new StringBuilder(delim);
        for (String t : meta.getTitles()) {
            titres.append(delim);
            titres.append(t);
            delim = ", ";
        }
        this.titre = titres.toString();

        // Auteurs
        delim = "";
        StringBuilder auteurs = new StringBuilder(delim);
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
        StringBuilder types = new StringBuilder(delim);
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
        StringBuilder resumes = new StringBuilder(delim);
        for (String r : meta.getDescriptions()) {
            titres.append(delim);
            resumes.append(r);
            delim = ", ";
        }
        this.resume = resumes.toString();

        // Langue
        if (meta.getLanguage() == null) this.langue = "";
        else this.langue = meta.getLanguage();

        //TEMPORAIRE
        this.idServeur = Long.valueOf(0);
        this.lienDLEpub = "";
        this.noteMoyenne = 2;
        //TEMPORAIRE
    }

    public boolean estImporteLocalement() {
        return idServeur == null || idServeur == 0;
    }

    public Long getIdLivre() {
        return idLivre;
    }

    public Long getIdServeur() {
        return idServeur;
    }

    public String getLangue() {
        return langue;
    }

    public String getGenre() {
        return genre;
    }

    public String getDateParution() {
        return dateParution;
    }

    public String getResume() {
        return resume;
    }

    public float getNoteMoyenne() {
        return noteMoyenne;
    }

    public String getLienDLEpub() {
        return lienDLEpub;
    }

    public String getTitre(){
        return titre;
    }

    public String getAuteur(){
        return auteur;
    }

    @Override
    public String toString() {
        return titre +" " +  auteur + " " + dateParution + " id  : " + idLivre;
    }

    /* Parcelable */

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Permet de recréer un Livre à partir d'une Parcel in.
     */
    private Livre(Parcel in){
        idLivre = in.readLong();
        idServeur = in.readLong();
        titre = in.readString();
        auteur = in.readString();
        genre = in.readString();
        resume = in.readString();
        langue = in.readString();
        dateParution = in.readString();
        lienDLEpub = in.readString();
        noteMoyenne = in.readFloat();
    }

    /**
     * Ecrit les données du Livre dans une Parcel dest.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(idLivre);
        // idServeur à -1 si null pour éviter une NullPointerException
        if (idServeur == null)
            idServeur = Long.valueOf(-1);
        dest.writeLong(idServeur);
        dest.writeString(titre);
        dest.writeString(auteur);
        dest.writeString(genre);
        dest.writeString(resume);
        dest.writeString(langue);
        dest.writeString(dateParution);
        dest.writeString(lienDLEpub);
        dest.writeFloat(noteMoyenne);
    }

    public static final Parcelable.Creator<Livre> CREATOR = new Parcelable.Creator<Livre>(){
        public Livre createFromParcel(Parcel in){
            return new Livre(in);
        }

        public Livre[] newArray(int size) {
            return new Livre[size];
        }
    };
}
