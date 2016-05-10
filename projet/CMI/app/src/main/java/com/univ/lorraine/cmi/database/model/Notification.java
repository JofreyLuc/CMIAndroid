package com.univ.lorraine.cmi.database.model;

import java.util.Date;

/**
 * Created by alexis on 10/05/2016.
 */
public class Notification {

    private Long idNotification;

    private String type;

    private Utilisateur utilisateur;

    private Livre livre;

    private Evaluation evaluation;

    private Date dateCreation;

    private boolean vue;
}
