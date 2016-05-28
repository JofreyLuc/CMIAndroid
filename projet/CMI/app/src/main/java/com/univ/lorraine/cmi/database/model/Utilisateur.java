package com.univ.lorraine.cmi.database.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by alexis on 10/05/2016.
 */
public class Utilisateur {

    public static final String ID_UTILISATEUR_JSON_NAME = "idUtilisateur";

    public static final String FACEBOOK_ID_JSON_NAME = "facebookId";

    public static final String GOOGLE_ID_JSON_NAME = "googleId";

    public static final String EMAIL_JSON_NAME = "email";

    public static final String PASSWORD_JSON_NAME = "password";

    public static final String PSEUDO_JSON_NAME = "pseudo";

    public static final String NOM_JSON_NAME = "nom";

    public static final String PRENOM_JSON_NAME = "prenom";

    public static final String DATE_NAISSANCE_JSON_NAME = "dateNaissance";

    public static final String SEXE_JSON_NAME = "sexe";

    public static final String POSSIBILITE_SUIVI_JSON_NAME = "possibiliteSuivi";

    public static final String INSCRIPTION_VALIDEE_JSON_NAME = "inscriptionValidee";

    public static final String TOKEN_JSON_NAME_JSON = "token";

    @Expose
    @SerializedName(ID_UTILISATEUR_JSON_NAME)
    private Long idUtilisateur;

    @Expose
    @SerializedName(FACEBOOK_ID_JSON_NAME)
    private String facebookId;

    @Expose
    @SerializedName(GOOGLE_ID_JSON_NAME)
    private String googleId;

    @Expose
    @SerializedName(EMAIL_JSON_NAME)
    private String email;

    @Expose
    @SerializedName(PASSWORD_JSON_NAME)
    private String password;

    @Expose
    @SerializedName(PSEUDO_JSON_NAME)
    private String pseudo;

    @Expose
    @SerializedName(NOM_JSON_NAME)
    private String nom;

    @Expose
    @SerializedName(PRENOM_JSON_NAME)
    private String prenom;

    @Expose
    @SerializedName(DATE_NAISSANCE_JSON_NAME)
    private Date dateNaissance;

    @Expose
    @SerializedName(SEXE_JSON_NAME)
    private char sexe;

    @Expose
    @SerializedName(POSSIBILITE_SUIVI_JSON_NAME)
    private boolean possibiliteSuivi;

    @Expose
    @SerializedName(INSCRIPTION_VALIDEE_JSON_NAME)
    private boolean inscriptionValidee;

    @Expose
    @SerializedName(TOKEN_JSON_NAME_JSON)
    private String token;

    public Utilisateur() {}

    public Utilisateur(Long idU) {
        idUtilisateur = idU;
    }

    public Long getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(Long idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Date getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public char getSexe() {
        return sexe;
    }

    public void setSexe(char sexe) {
        this.sexe = sexe;
    }

    public String getToken() { return token; }

    public void setToken(String token) { this.token = token; }

    public boolean isPossibiliteSuivi() {
        return possibiliteSuivi;
    }

    public void setPossibiliteSuivi(boolean possibiliteSuivi) {
        this.possibiliteSuivi = possibiliteSuivi;
    }

    public boolean isInscriptionValidee() {
        return inscriptionValidee;
    }

    public void setInscriptionValidee(boolean inscriptionValidee) {
        this.inscriptionValidee = inscriptionValidee;
    }
}
