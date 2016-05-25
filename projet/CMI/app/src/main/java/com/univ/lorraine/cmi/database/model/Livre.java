package com.univ.lorraine.cmi.database.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
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

    public static final Long IDSERVEUR_NONE = Long.valueOf(-1);

    // json Strings

    public static final String ID_SERVEUR_JSON_NAME = "id";

    public static final String TITRE_JSON_NAME = "titre";

    public static final String AUTEUR_JSON_NAME = "auteur";

    public static final String LANGUE_JSON_NAME = "langue";

    public static final String GENRE_JSON_NAME = "genre";

    public static final String DATE_PARUTION_JSON_NAME = "dateParution";

    public static final String RESUME_JSON_NAME = "resume";

    public static final String NOTE_MOYENNE_JSON_NAME = "noteMoyenne";

    public static final String LIEN_DL_EPUB_JSON_NAME = "lienDLEpub";

    public static final String LIEN_COUVERTURE_JSON_NAME = "lienCouverture";

    // Database Strings

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

    /* Id local du livre
       Cet id n'est pas passé dans le json
     */
    @DatabaseField(columnName = ID_FIELD_NAME, generatedId = true)
    private Long idLivre;

    // Id serveur (Gutembert) du livre, si non-importé en local
    @Expose
    @SerializedName(ID_SERVEUR_JSON_NAME)
    @DatabaseField(columnName = ID_SERVEUR_FIELD_NAME)
    private Long idServeur;

    @Expose
    @SerializedName(TITRE_JSON_NAME)
    @DatabaseField(columnName = TITRE_FIELD_NAME)
    private String titre;

    @Expose
    @SerializedName(AUTEUR_JSON_NAME)
    @DatabaseField(columnName = AUTEUR_FIELD_NAME)
    private String auteur;

    @Expose
    @SerializedName(LANGUE_JSON_NAME)
    @DatabaseField(columnName = LANGUE_FIELD_NAME)
    private String langue;

    @Expose
    @SerializedName(GENRE_JSON_NAME)
    @DatabaseField(columnName = GENRE_FIELD_NAME)
    private String genre;

    @Expose
    @SerializedName(DATE_PARUTION_JSON_NAME)
    @DatabaseField(columnName = DATE_PARUTION_FIELD_NAME)
    private String dateParution;

    @Expose
    @SerializedName(RESUME_JSON_NAME)
    @DatabaseField(columnName = RESUME_FIELD_NAME)
    private String resume;

    @Expose
    @SerializedName(NOTE_MOYENNE_JSON_NAME)
    @DatabaseField(columnName = NOTE_MOYENNE_FIELD_NAME)
    private float noteMoyenne;

    @Expose
    @SerializedName(LIEN_DL_EPUB_JSON_NAME)
    @DatabaseField(columnName = LIEN_DL_EPUB_FIELD_NAME)
    private String lienDLEpub;

    @Expose
    @SerializedName(LIEN_COUVERTURE_JSON_NAME)
    // Pas dans la base de données locale
    private String lienCouverture;

    // Nécéssaire pour ORMlite
    public Livre() {}

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
        this.idServeur = IDSERVEUR_NONE;
        this.lienDLEpub = "";
        this.noteMoyenne = 2;
        //TEMPORAIRE
    }

    public boolean estImporteLocalement() {
        return idServeur == IDSERVEUR_NONE;
    }

    // GETTERS AND SETTERS

    public Long getIdLivre() {
        return idLivre;
    }

    public void setIdLivre(Long idLivre) {
        this.idLivre = idLivre;
    }

    public Long getIdServeur() {
        return idServeur;
    }

    public void setIdServeur(Long idServeur) {
        this.idServeur = idServeur;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getLangue() {
        return langue;
    }

    public void setLangue(String langue) {
        this.langue = langue;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDateParution() {
        return dateParution;
    }

    public void setDateParution(String dateParution) {
        this.dateParution = dateParution;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public float getNoteMoyenne() {
        return noteMoyenne;
    }

    public void setNoteMoyenne(float noteMoyenne) {
        this.noteMoyenne = noteMoyenne;
    }

    public String getLienDLEpub() {
        return lienDLEpub;
    }

    public void setLienDLEpub(String lienDLEpub) {
        this.lienDLEpub = lienDLEpub;
    }

    public String getLienCouverture() {
        return lienCouverture;
    }

    public void setLienCouverture(String lienCouverture) {
        this.lienCouverture = lienCouverture;
    }

    // FIN GETTERS AND SETTERS

    @Override
    public String toString() {
        return "Livre{" +
                "idLivre=" + idLivre +
                ", idServeur=" + idServeur +
                ", titre='" + titre + '\'' +
                ", auteur='" + auteur + '\'' +
                ", langue='" + langue + '\'' +
                ", genre='" + genre + '\'' +
                ", dateParution='" + dateParution + '\'' +
                ", resume='" + resume + '\'' +
                ", noteMoyenne=" + noteMoyenne +
                ", lienDLEpub='" + lienDLEpub + '\'' +
                ", lienCouverture='" + lienCouverture + '\'' +
                '}';
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
        idLivre = (Long) in.readValue(Long.class.getClassLoader());
        idServeur = (Long) in.readValue(Long.class.getClassLoader());
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
        dest.writeValue(idLivre);
        dest.writeValue(idServeur);
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
