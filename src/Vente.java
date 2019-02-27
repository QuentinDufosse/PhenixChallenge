public class Vente {
	
	private String magasin;
	private String produit;
	private Integer quantite;
	private Double prixTotal;
	
	public Vente(String magasin, String produit, Integer quantite, Double prixTotal) {
		super();
		this.magasin = magasin;
		this.produit = produit;
		this.quantite = quantite;
		this.setPrixTotal(prixTotal);
	}

	public String getMagasin() {
		return magasin;
	}
	public void setMagasin(String magasin) {
		this.magasin = magasin;
	}
	public String getProduit() {
		return produit;
	}
	public void setProduit(String produit) {
		this.produit = produit;
	}
	public Integer getQuantite() {
		return quantite;
	}
	public void setQuantite(Integer quantite) {
		this.quantite = quantite;
	}
	public Double getPrixTotal() {
		return prixTotal;
	}

	public void setPrixTotal(Double prixTotal) {
		this.prixTotal = prixTotal;
	}
}
