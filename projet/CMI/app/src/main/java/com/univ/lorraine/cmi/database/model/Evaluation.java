package com.univ.lorraine.cmi.database.model;

import java.util.Date;

/**
 * Created by alexis on 10/05/2016.
 */
public class Evaluation {

    private Long idEvaluation;

    private Utilisateur utilisateur;

    private Livre livre;

    private String commentaire;

    private int note;

    private Date dateModification;

}
