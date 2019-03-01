# PhenixChallenge

## Description 

L'application permet de traiter les fichiers de transaction et de référence de carrefour. Les Quantités vendu et le chiffre d'affaire obtenu sont trié et le top 100 est écrit dans un fichier. Cette action est effectué globalement et pour chaque magasin unitairement.

## Utilisation du code

### récupération et compilation

Pour tester le code, il y a deux solutions :

  - Récupérer le fichier .jar éxécutable
  - Récupérer le code au sein d'un IDE pour le langage Java. Puis l'exporter en tant que .jar éxécutable

Pour un bon déroulé du processus, il faut créer sur l'emplacement C: de la machine un dossier phenix. Puis il faut déposer les fichiers de transaction et les fichiers de references des 7 derniers jours dans ce dossier.

## fonctionnement

### algorithme principale

En premier lieu, le programme va récupérer dans une hashmap les clefs constitués du duo : nom de magasin et ID produit avec pour valeur la quantité de vente associé.
Dans un second temps, Les fichiers de références associés sont parcourus pour récupérer le prix de chaque objet multiplié par la quantité déjà en mémoire pour obtenir le chiffre d'affaire effectué.

Cette liste est ensuite trié pour avoir le top 100 des quantités et du chiffre d'affaire tous magasins confondus.

Ensuite, la liste des magasins est récupéré, puis permet de filtrer la Hashmap pour obtenir ces top 100 pour chacun des magasins.

### Classe

La classe vente à été créé pour avoir contenir tout les aspects que l'ont doit retourner dans les fichiers :
  magasin : l'ID du magasin
	produit : l'ID du produit
	quantite : Le nombre total de vente d'un produit d'un magasin contenu dans le fichier transaction
	prixTotal : Le chiffre d'affaire total de vente d'un produit d'un magasin calculé à partir du prix des fichiers références
  
## Compatibilité

jdk-11.0.2
