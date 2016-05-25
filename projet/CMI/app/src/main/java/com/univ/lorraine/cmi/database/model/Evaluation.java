package com.univ.lorraine.cmi.database.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by alexis on 10/05/2016.
 */
public class Evaluation {

    // json Strings

    public static final String ID_SERVEUR_JSON_NAME = "id";

    public static final String UTILISATEUR_JSON_NAME = "idUtilisateur";

    public static final String LIVRE_JSON_NAME = "idLivre";

    public static final String COMMENTAIRE_JSON_NAME = "commentaire";

    public static final String NOTE_JSON_NAME = "note";

    public static final String DATE_MODIFICATION_JSON_NAME = "dateModification";

    @Expose
    @SerializedName(ID_SERVEUR_JSON_NAME)
    private Long idEvaluation;

    @Expose
    @SerializedName(UTILISATEUR_JSON_NAME)
    private Utilisateur utilisateur;

    @Expose
    @SerializedName(LIVRE_JSON_NAME)
    private Livre livre;

    @Expose
    @SerializedName(COMMENTAIRE_JSON_NAME)
    private String commentaire;

    @Expose
    @SerializedName(NOTE_JSON_NAME)
    private double note;

    @Expose
    @SerializedName(DATE_MODIFICATION_JSON_NAME)
    private Date dateModification;

    public Evaluation(){}

    public Long getIdEvaluation() {
        return idEvaluation;
    }

    public void setIdEvaluation(Long idEvaluation) {
        this.idEvaluation = idEvaluation;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Livre getLivre() {
        return livre;
    }

    public void setLivre(Livre livre) {
        this.livre = livre;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public double getNote() {
        return note;
    }

    public void setNote(double note) {
        this.note = note;
    }

    public Date getDateModification() {
        return dateModification;
    }

    public void setDateModification(Date dateModification) {
        this.dateModification = dateModification;
    }
}
