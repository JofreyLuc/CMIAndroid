package com.univ.lorraine.cmi.database.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by alexis on 10/05/2016.
 */
public class Utilisateur {

    private static final String ID_UTILISATEUR_JSON_NAME = "idUtilisateur";

    private static final String FACEBOOK_ID_JSON_NAME = "facebookId";

    private static final String GOOGLE_ID_JSON_NAME = "googleId";

    private static final String EMAIL_JSON_NAME = "email";

    private static final String PASSWORD_JSON_NAME = "password";

    private static final String PSEUDO_JSON_NAME = "pseudo";

    private static final String NOM_JSON_NAME = "nom";

    private static final String PRENOM_JSON_NAME = "prenom";

    private static final String DATE_NAISSANCE_JSON_NAME = "dateNaissance";

    private static final String SEXE_JSON_NAME = "sexe";

    private static final String POSSIBILITE_SUIVI_JSON_NAME = "possibiliteSuivi";

    private static final String INSCRIPTION_VALIDEE_JSON_NAME = "inscriptionValidee";

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
}
