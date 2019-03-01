import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PhénixChallenge {
	
	// déclaration du placement des colonnes des fichiers
	private final static int txId = 0;
	private final static int datetime = 1;
	private final static int magasin = 2;
	private final static int produitTransac = 3;
	private final static int qte = 4;
	private final static int produitRef = 0;
	private final static int prix  = 1;
	private final static String path = "C:/phenix/";
	
	public static void main(String[] args) throws IOException {
		// déclaration des variables
		HashMap<String, Vente> listeVente = new HashMap<String, Vente>();
		ArrayList<String> dates = new ArrayList<String>();
		int i =0;

		// Récupération des 7 derniers jours dans une arraylist
		Date dateJour = new Date();
		String pattern = "yyyyMMdd";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		dates.add(simpleDateFormat.format(dateJour));
		for(i = 1; i < 7; i++) {
			dateJour.setDate(dateJour.getDate() - 1);
			dates.add(simpleDateFormat.format(dateJour));
			System.out.println(dates.get(i));
		}
		
		// parcour des dates et récupérations des dernières données.
		for(String date : dates) {
			//Récupération des ventes totales
			listeVente = transactionReader(path + "transactions_" + date + ".data");
			
			if (listeVente != null) {
				// Récupération du chiffre d'affaire de chaque objet
				listeVente = prixReader(path, listeVente, date);
					
				// Tri décroissant du nombre de ventes
				List<Vente> VenteParQuantite = new ArrayList<>(listeVente.values());
				WriteHundredMost(VenteParQuantite, "top_100_ventes_GLOBAL_" + date + ".data");
				
				// Tri décroissant du prix total de ventes
				List<Vente> VenteParPrix = new ArrayList<>(listeVente.values());
				WriteHundredExpensive(VenteParPrix, "top_100_ca_GLOBAL_" + date + ".data");
				
				// Exécution unitaire pour chaque magasin
				List<String> magasins = new ArrayList<String>();
				
				// récupération d'une liste des magasins
				for (String mapKey : listeVente.keySet()) { 
					magasins.add(mapKey.split("\\|")[0]);
				}
				// suppression des doublons
				Set<String> listeMagasins = new HashSet<String>(magasins);
				
				// Lancement des calculs pour chacun des magasins
				List<Vente> ventes = new ArrayList();
				
				for (String magasin : listeMagasins) {
					ventes  = RécupérationMagasinUnique(VenteParQuantite, magasin);
					WriteHundredMost(ventes, "top_100_ventes_" + magasin + "_" + date + ".data");
					WriteHundredExpensive(ventes, "top_100_ca_" + magasin + "_" + date + ".data");
				}
			}
		}
        System.out.println("Fin du processus!");
    }
	
	/**
	 * Lecture des fichiers de transaction pour récupérer la quantité de chaque produit vendu dans les magasins
	 * @param path
	 * @return
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static HashMap<String, Vente> transactionReader(String path) throws NumberFormatException, IOException {
		HashMap<String, Vente> listeVente = new HashMap<String, Vente>();
		String[] decoupe;
		String key;
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			// Récupération des IDs des objets achetés
			while(br.readLine() != null) {
				decoupe = br.readLine().toString().split("\\|");
				key = decoupe[magasin] + "|" + decoupe[produitTransac];
				if(decoupe[magasin] != null && decoupe[produitTransac] != null && decoupe[qte] != null) {
					if(!listeVente.containsKey(key)) {
						listeVente.put(key, new Vente(decoupe[magasin], decoupe[produitTransac], Integer.parseInt(decoupe[qte]), (double) 0));
					} else {
						listeVente.put(key, new Vente(decoupe[magasin], decoupe[produitTransac], listeVente.get(key).getQuantite() + Integer.parseInt(decoupe[qte]),(double) 0));
					};
				}
			}
			br.close();
			return listeVente;
		} catch (FileNotFoundException e) {
		System.out.println("Pas de fichier à cet emplacement");
		}
		return null;
	}
	
	/**
	 * 
	 * @param path
	 * @param listeVente
	 * @return
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static HashMap<String, Vente> prixReader(String path, HashMap<String, Vente> listeVente, String date) throws NumberFormatException, IOException {
		String[] decoupe;
		String key;
		String ligne;
		
		// Calcul du prix total des ventes
		List<Vente> VenteParPrix = new ArrayList<>(listeVente.values());
		for (Vente v : VenteParPrix) {
			// Vérification de l'éxistence du fichier cible
			File f =  new File(path + "/reference_prod-" + v.getMagasin() + "_" + date +".data");
			if (f.exists()) {
				BufferedReader br = new BufferedReader(new FileReader(f.getPath()));
				while((ligne = br.readLine()) != null) {
					decoupe = ligne.toString().split("\\|");
					key = v.getMagasin() + "|" + v.getProduit();
					// Mise à jour du prix total en multipliant le prix par la quantité
					if(decoupe[produitRef] != null && decoupe[prix] != null)
					{
						if (v.getProduit().equals(decoupe[produitRef])) {
							listeVente.put(key, new Vente(v.getMagasin(), v.getProduit(), v.getQuantite(), v.getQuantite() * Double.parseDouble(decoupe[prix])));
						}
					}
				}
				br.close();
			} else {
				// Si le fichier contenant les prix n'existe pas à la date ou pour le magasin :
				System.out.println("Fichier de référence non trouvé");
			}
		}
		return listeVente;
	}
	
	/**
	 * écriture des 10 premières lignes de la collection donné
	 * @param Ventes
	 * @param filename
	 */
	public static void WriteHundredMost(List<Vente> Ventes, String filename)
	{
		PrintWriter writer;
		int i = 0;
		Vente v;
		
		// Tri de la liste des ventes
		Collections.sort(Ventes, Comparator.comparing(Vente::getQuantite));
		Collections.reverse(Ventes);
		
		// écriture des 100 premières lignes dans le fichier cible.
		try {
			writer = new PrintWriter(path + filename, "UTF-8");
			writer.println("Liste des meilleures ventes : ");
			while(i < 100) {
				v = Ventes.get(i);
				writer.println(v.getMagasin() + " : " + v.getProduit() + " : " + v.getQuantite());
				i ++;
			}
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// Erreur lors de l'écriture dans le fichier cible.
			System.out.println("Erreur lors de l'écriture dans le fichier");
		}
	}
	
	/**
	 * écriture des 10 premières lignes de la collection donné
	 * @param Ventes
	 * @param filename
	 */
	public static void WriteHundredExpensive(List<Vente> Ventes, String filename)
	{
		PrintWriter writer;
		int i = 0;
		Vente v;
		
		// Tri de la liste des ventes
		Collections.sort(Ventes, Comparator.comparingDouble(Vente::getPrixTotal));
		Collections.reverse(Ventes);
		
		// écriture des 100 premières lignes dans le fichier cible.
		try {
			writer = new PrintWriter(path + filename, "UTF-8");
			writer.println("Liste des meilleures ventes : ");
			while(i < 100) {
				v = Ventes.get(i);
				writer.println(v.getMagasin() + " : " + v.getProduit() + " : " + v.getPrixTotal());
				i ++;
			}
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// Erreur lors de l'écriture dans le fichier cible.
			System.out.println("Erreur lors de l'écriture dans le fichier");
		}
	}
	
	/**
	 * Retour d'une liste de vente pour un magasin
	 * @param Ventes
	 * @param magasin
	 * @return
	 */
	public static List<Vente> RécupérationMagasinUnique (List<Vente> Ventes, String magasin)
	{
		List<Vente> VenteMagasin = Ventes.stream()
		    .filter(p -> p.getMagasin().equals(magasin)).collect(Collectors.toList());
		return VenteMagasin;
	}
}
