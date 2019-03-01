# PhenixChallenge

## Description 

L'application permet de traiter les fichiers de transaction et de référence de carrefour. Les Quantités vendues et le chiffre d'affaires obtenu sont triés et le top 100 est écrit dans un fichier. Cette action est effectuée globalement et pour chaque magasin unitairement.

## Utilisation du code

### récupération et compilation

Pour tester le code, il y a deux solutions :

  - Récupérer le fichier .jar exécutable
  - Récupérer le code au sein d'un IDE pour le langage Java. Puis l'exporter en tant que .jar exécutable

Pour un bon déroulé du processus, il faut créer sur l'emplacement C: de la machine un dossier phenix. Puis il faut déposer les fichiers de transaction et les fichiers de références des 7 derniers jours dans ce dossier.

## fonctionnement

### algorithme principale

En premier lieu, le programme va récupérer dans une hashmap les clefs constituées du duo : nom de magasin et ID produit avec pour valeur la quantité de vente associé.
Dans un second temps, Les fichiers de références associés sont parcourus pour récupérer le prix de chaque objet multiplié par la quantité déjà en mémoire pour obtenir le chiffre d'affaires effectué.

Cette liste est ensuite triée pour avoir le top 100 des quantités et du chiffre d'affaires tous magasins confondus.

Ensuite, la liste des magasins est récupéré, puis permet de filtrer la Hashmap pour obtenir ces top 100 pour chacun des magasins.

### Classe

La classe vente a été créé pour avoir contenir tous les aspects que l'on doit retourner dans les fichiers :
  magasin : l'ID du magasin
	produit : l'ID du produit
	quantite : Le nombre total de vente d'un produit d'un magasin contenu dans le fichier transaction
	prixTotal : Le chiffre d'affaires total de vente d'un produit d'un magasin calculé à partir du prix des fichiers références
  
## Les difficultés rencontrées


  
## Compatibilité

jdk-11.0.2
