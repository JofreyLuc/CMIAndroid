package com.univ.lorraine.cmi.database.model;

import java.util.Date;

/**
 * Created by alexis on 10/05/2016.
 */
public class Utilisateur {

    private Long idUtilisateur;

    private String facebookId;

    private String googleId;

    private String email;

    private String password;

    private String nom;

    private String prenom;

    private Date dateNaissance;

    private char sexe;

    private boolean possibiliteSuivi;

    private boolean inscriptionValidee;
}
