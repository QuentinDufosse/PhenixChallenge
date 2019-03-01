# PhenixChallenge

## Description 

L’application permet de traiter les fichiers de transaction et de référence de carrefour. Les Quantités vendues et le chiffre d’affaires obtenu sont triés et le top 100 est écrit dans un fichier. Cette action est effectuée globalement et pour chaque magasin unitairement.

## Utilisation du code

### Récupération et compilation

Pour tester le code, il y a deux solutions :

  - Récupérer le fichier .jar exécutable
  - Récupérer le code au sein d’un IDE pour le langage Java. Puis l’exporter en tant que .jar exécutable

Pour un bon déroulé du processus, il faut déposer le .jar avec un sous dossier « data » qui contiendra les fichiers de transaction. Puis il faut déposer les fichiers de transaction et les fichiers de références des 7 derniers jours dans ce dossier.

## Fonctionnement

### Algorithme principale

En premier lieu, le programme va récupérer dans une hashmap les clefs constituées du duo : nom de magasin et ID produit avec pour valeur la quantité de vente associé.
Dans un second temps, Les fichiers de références associés sont parcourus pour récupérer le prix de chaque objet multiplié par la quantité déjà en mémoire pour obtenir le chiffre d’affaires effectué.

Cette liste est ensuite triée pour avoir le top 100 des quantités et du chiffre d'affaires tous magasins confondus.

Ensuite, la liste des magasins est récupéré, puis permet de filtrer la Hashmap pour obtenir ces tops 100 pour chacun des magasins.

Les fichiers sont nommés tel que décrit dans le nom de l’énoncé.
Concernant les dates antérieures, le programme les écrits actuellement avec le même format que le fichier du jour.

date du jour : top_100_ca_GLOBAL_20190301.data
date de la veille : top_100_ca_GLOBAL_20190228.data
(A clarifier si -J7 correspond à la date avec 7 jours en moins ou s'il faut écrire -J7 dans le nom du fichier.)

### Classe

La classe vente a été créé pour avoir contenir tous les aspects que l’on doit retourner dans les fichiers :
  magasin : l’ID du magasin
	produit : l’ID du produit
	quantite : Le nombre total de vente d’un produit d’un magasin contenu dans le fichier transaction
	prixTotal : Le chiffre d’affaires total de vente d’un produit d'un magasin calculé à partir du prix des fichiers références

## Optimisation possible

### Performance

Actuellement, le fonctionnement avec une Hashmap ne permet pas de répondre à la contrainte de RAM. Un grand nombre d’enregistrement entraine donc aussi un quantité de donnée en mémoire importante. Il faudrait donc modifier le code pour effectuer le traitement en passant les actions depuis le fichier par tranche de 10 000 lignes.

### Logs

Actuellement, les logs ne sont pas déposé dans un fichier associé. Il faudrait soit passer par un système de log propre à java, ou écrire les logs dans un fichier à l'emplacement du fichier java.
  
## Compatibilité

jdk-11.0.2
