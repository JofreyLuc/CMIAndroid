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
