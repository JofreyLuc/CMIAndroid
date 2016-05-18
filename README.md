# CMIAndroid

Projet PPIL, 2016

# Idées

- Commencer les id des bouquins à un nombre fixe (500 par ex) pour différencier les livres Gutembert des livres importés -> Du coup gérer la suppression des bouquins locaux (-> cascade pour la bibliothèque etc.)
- Ou alors ajouter un idGutembert dans Livre qui serait null en cas de livre local

Concernant la synchronisation serveur / device mobile :
- L'application mobile se synchronise avec le serveur après un événement X à définir (temps, autres...)
- L'application mobile utilise les données locales quand cela est possible et quand la synchro est ok
- L'application mobile demande les données au serveur quand cela est nécessaire (évaluations, profil utilisateur, suivi, etc.)
- Lors d'un changement concernant les données enregistrées localement (Livre, Bibliothèque, Annotation), l'application sauvegarde les données localement et tente d'envoyer les modifications au serveur
- Si l'envoi échoue (pas de connexion par exemple), on "met en cache" la modification et on attend un événement Y avant de l'envoyer (retour connexion internet par exemple)

Algo synchronisation device mobile < serveur :

(Déclencher par un événement device mobile : connexion internet par exemple)
(Authentification automatique si nécessaire)
On récupére donc l'idUtilisateur : ID_USER

Mobile -> serveur : users/{idUser = ID_USER}/library (pour récupèrer les bibliothèques de l'utilisateur)
POUR chaque bibliothèque récupérée (BIBLIO_SERVEUR)
    
    Mobile : Select biblio where idServeur = BIBLIO_SERVEUR.idBibliotheque -> BIBLIO_MOBILE (résultat requête)
    
    // Pour les nouvelles bibliothèques
    SI BIBLIO_MOBILE = null (pas de correspondance : la bibliothèque a été nouvellement ajoutée)
        /* Ajout de la bibliothèque */
          Mobile : create BIBLIO_SERVEUR  // On l'ajoute
          Mobile : téléchargement epub + gestion fichier
        /* Fin ajout de la bibliothèque */
    
    // Pour les bibliothèques déjà présentes
    SINON
        // Si la bibliothèque du serveur est plus à jour que celle sur mobile
        SI BIBLIO_SERVEUR.timestamp plus récente que BIBLIO_MOBILE.timestamp
            /* Maj de la bibliothèque */
            Mobile : update BIBLIO_SERVEUR
            /* Fin maj de la bibliothèque */
        FINSI
    FINSI
        
    /* Gestion des annotations de cette bibliothèque */
    Mobile -> serveur : users/{idUser = ID_USER}/library/{idLibrary = BIBLIO_SERVEUR.idBiblio} (pour récupèrer les annotations de cette bibliothèque)
    
    POUR chaque annotation récupérée (ANNOTATION_SERVEUR)
        (Basiquement même chose que pour les bibliothèques)
        
        Mobile : Select annotation where idServeur = ANNOTATION_SERVEUR.idAnnotation -> ANNOTATION_MOBILE (résultat requête)
        
        // Pour les nouvelles annotations
        SI ANNOTATION_MOBILE = null (pas de correspondance : l'annotation a été nouvellement ajoutée)
        /* Ajout de l'annotation */
          Mobile : create ANNOTATION_SERVEUR  // On l'ajoute
        /* Fin ajout de l'annotation */
    
        // Pour les annotations déjà présentes
        SINON
            // Si l'annotation du serveur est plus à jour que celle sur mobile
            SI ANNOTATION_SERVEUR.timestamp plus récente que ANNOTATION_MOBILE.timestamp
                /* Maj de l'annotation */
                Mobile : update ANNOTATION_SERVEUR
                /* Fin maj de l'annotation */
            FINSI
        FINSI
        
         // On enregistre les idServeur de toutes les annotations pour gérer les annotations supprimées ensuite
          Mobile : LISTE_ID_ANNOT_SERVEUR = new List<int>.add(ANNOTATION_SERVEUR.idAnnot)
        
    FINPOUR
    
    // Pour les annotations supprimées (toutes les annotations sauf celles présentes sur le serveur et celles uniquement locales (null))
    Mobile : Select Annotation where Annotation.principal not in(Select principal from Annotation where idServeur in (LISTE_ID_ANNOT_SERVEUR) and idServeur not null
    POUR chaque annotation récupérée (ANNOTATION_A_SUPP)
        Mobile : delete ANNOTATION_A_SUPP
    FINPOUR
    
    /* Fin gestion des annotations de cette bibliothèque */
    
    // On enregistre les idServeur de toutes les bibliothèques pour gérer les bibliothèques supprimées ensuite
    Mobile : LISTE_ID_BIBLIO_SERVEUR = new List<int>.add(BIBLIO_SERVEUR.idBiblio)

FINPOUR

// Pour les bibliothèques supprimées (toutes les bibliothèques sauf celles présentes sur le serveur et celles uniquement locales (null))
Mobile : Select Bibliothèque where Bibliothèque.principal not in(Select principal from Bibliothèque where idServeur in (LISTE_ID_BIBLIO_SERVEUR) and idServeur not null
POUR chaque bibliothèque récupérée (BIBLIOTHEQUE_A_SUPP)
    Mobile : delete BIBLIOTHEQUE_A_SUPP
    Mobile : suppression dossier epub
      
      // Suppression des annotations de cette bibliothèque
      (CASCADE) Mobile : Select Annotation where idBibliotheque = BIBLIOTHEQUE_A_SUPP.idBibliotheque
      POUR chaque annotation récupérée (ANNOT_BIB_A_SUPP)
          Mobile : delete ANNOT_BIB_A_SUPP
      FINPOUR
      
FINPOUR

      
