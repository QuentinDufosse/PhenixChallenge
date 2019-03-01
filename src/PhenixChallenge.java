import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PhenixChallenge {
	
	// Colonne value in the files
	private final static int txId = 0;
	private final static int datetime = 1;
	private final static int magasin = 2;
	private final static int produitTransac = 3;
	private final static int qte = 4;
	private final static int produitRef = 0;
	private final static int prix  = 1;
	// path to files
	private final static String path = ".\\data\\";
	private final static String logPath = ".\\phenix.log";
	
	public static void main(String[] args) throws IOException {
		// Variables
		HashMap<String, Vente> listeVente = new HashMap<String, Vente>();
		ArrayList<String> dates = new ArrayList<String>();
		int i =0;

		// get 7 last day as an arraylist
		Date dateJour = new Date();
		String pattern = "yyyyMMdd";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		dates.add(simpleDateFormat.format(dateJour));
		for(i = 1; i < 7; i++) {
			dateJour.setDate(dateJour.getDate() - 1);
			dates.add(simpleDateFormat.format(dateJour));
		}
		
		// for each date, we get the data
		for(String date : dates) {
			// Get the sales number
			listeVente = transactionReader(path + "transactions_" + date + ".data");
			
			if (listeVente != null) {
				// Get the sales total price
				listeVente = prixReader(path, listeVente, date);
					
				// Sorting the sales quantity and writing the top hundred in a file
				List<Vente> VenteParQuantite = new ArrayList<>(listeVente.values());
				WriteHundredMost(VenteParQuantite, "top_100_ventes_GLOBAL_" + date + ".data");
				
				// Sorting the sales total price and writing the top hundred in a file
				List<Vente> VenteParPrix = new ArrayList<>(listeVente.values());
				WriteHundredExpensive(VenteParPrix, "top_100_ca_GLOBAL_" + date + ".data");
				
				// Get those value for each shop
				List<String> magasins = new ArrayList<String>();
				
				// List of shop
				for (String mapKey : listeVente.keySet()) { 
					magasins.add(mapKey.split("\\|")[0]);
				}
				// unique value of them
				Set<String> listeMagasins = new HashSet<String>(magasins);
				
				// Get the top hundred of sales total price and quantity for each
				List<Vente> ventes = new ArrayList();
				
				for (String magasin : listeMagasins) {
					ventes  = GetUniqueShop(VenteParQuantite, magasin);
					WriteHundredMost(ventes, "top_100_ventes_" + magasin + "_" + date + ".data");
					WriteHundredExpensive(ventes, "top_100_ca_" + magasin + "_" + date + ".data");
				}
			}
		}
		FileWriter fw = new FileWriter(logPath, true);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.newLine();
        bw.write("Fin du processus!");
        bw.close();
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
		String ligne;
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			// Get the object IDs
			while((ligne = br.readLine()) != null) {
				decoupe = ligne.split("\\|");
				// check if nothing is null, and put values in the hashmap
				if(decoupe[magasin] != null && decoupe[produitTransac] != null && decoupe[qte] != null) {
					key = decoupe[magasin] + "|" + decoupe[produitTransac];
					if(!listeVente.containsKey(key)) {
						listeVente.put(key, new Vente(decoupe[magasin], decoupe[produitTransac], 
								Integer.parseInt(decoupe[qte]), (double) 0));
					} else {
						listeVente.put(key, new Vente(decoupe[magasin], decoupe[produitTransac], 
								listeVente.get(key).getQuantite() + Integer.parseInt(decoupe[qte]),(double) 0));
					};
				}
			}
			br.close();
			return listeVente;
		} catch (FileNotFoundException e) {
			FileWriter fw = new FileWriter(logPath, true);
			BufferedWriter bw = new BufferedWriter(fw);
	        bw.write(e.getMessage());
	        bw.newLine();
	        bw.close();
		}
		return null;
	}
	
	/**
	 * Calcul for eache selling in the hashmap the total price
	 * @param path
	 * @param listeVente
	 * @return
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static HashMap<String, Vente> prixReader(String path, HashMap<String, Vente> listeVente, String date) 
			throws NumberFormatException, IOException {
		String[] decoupe;
		String key;
		String ligne;
		
		List<Vente> VenteParPrix = new ArrayList<>(listeVente.values());
		for (Vente v : VenteParPrix) {
			// Look if the file exist
			File f =  new File(path + "/reference_prod-" + v.getMagasin() + "_" + date +".data");
			if (f.exists()) {
				BufferedReader br = new BufferedReader(new FileReader(f.getPath()));
				while((ligne = br.readLine()) != null) {
					decoupe = ligne.toString().split("\\|");
					key = v.getMagasin() + "|" + v.getProduit();
					// Calcul total price based on what is read in the price file
					if(decoupe[produitRef] != null && decoupe[prix] != null)
					{
						if (v.getProduit().equals(decoupe[produitRef])) {
							listeVente.put(key, new Vente(v.getMagasin(), 
									v.getProduit(), v.getQuantite(), v.getQuantite() * Double.parseDouble(decoupe[prix])));
						}
					}
				}
			} else {
				// If the file doesn't exist for the date and shop
		        FileWriter fw = new FileWriter(logPath, true);
				BufferedWriter bw = new BufferedWriter(fw);
		        bw.write("Fichier de référence non trouvé : " + f.getName());
		        bw.newLine();
		        bw.close();
			}
		}
		return listeVente;
	}
	
	/**
	 * write 100 First line of a list sorted by quantity
	 * @param Ventes
	 * @param filename
	 * @throws IOException 
	 */
	public static void WriteHundredMost(List<Vente> Ventes, String filename) throws IOException
	{
		PrintWriter writer;
		int i = 0, top = 100;
		Vente v;
		
		// Sort of the List
		Collections.sort(Ventes, Comparator.comparing(Vente::getQuantite));
		Collections.reverse(Ventes);
		
		// Write 100 first in the designed file
		try {
			writer = new PrintWriter(path + filename, "UTF-8");
			if ( Ventes.size() < 100) {
				top = Ventes.size();
			}
			while(i < top) {
				v = Ventes.get(i);
				writer.println(v.getMagasin() + " : " + v.getProduit() + " : " + v.getQuantite());
				i ++;
			}
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// If it's not possible to write in the file :
			FileWriter fw = new FileWriter(logPath, true);
			BufferedWriter bw = new BufferedWriter(fw);
	        bw.write(e.getMessage());
	        bw.newLine();
	        bw.close();
		}
	}
	
	/**
	 * write 100 First line of a list sorted by Total price
	 * @param Ventes
	 * @param filename
	 * @throws IOException 
	 */
	public static void WriteHundredExpensive(List<Vente> Ventes, String filename) throws IOException
	{
		PrintWriter writer;
		int i = 0, top = 100;;
		Vente v;
		
		// Tri de la liste des ventes
		Collections.sort(Ventes, Comparator.comparingDouble(Vente::getPrixTotal));
		Collections.reverse(Ventes);
		
		// écriture des 100 premières lignes dans le fichier cible.
		try {
			writer = new PrintWriter(path + filename, "UTF-8");
			if ( Ventes.size() < 100) {
				top = Ventes.size();
			}
			while(i < top) {
				v = Ventes.get(i);
				writer.println(v.getMagasin() + " : " + v.getProduit() + " : " + v.getPrixTotal());
				i ++;
			}
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// Erreur lors de l'écriture dans le fichier cible.
			FileWriter fw = new FileWriter(logPath, true);
			BufferedWriter bw = new BufferedWriter(fw);
	        bw.write(e.getMessage());
	        bw.newLine();
	        bw.close();
		}
	}
	
	/**
	 * Get a sale hashmap with only the sales of one shop
	 * @param Ventes
	 * @param magasin
	 * @return
	 */
	public static List<Vente> GetUniqueShop (List<Vente> Ventes, String magasin)
	{
		List<Vente> VenteMagasin = Ventes.stream()
		    .filter(p -> p.getMagasin().equals(magasin)).collect(Collectors.toList());
		return VenteMagasin;
	}
}
