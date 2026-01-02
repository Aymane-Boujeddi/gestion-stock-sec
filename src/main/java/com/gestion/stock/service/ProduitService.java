package com.gestion.stock.service;

import com.gestion.stock.entity.Produit;

import java.util.List;

public interface ProduitService {
    public Produit saveProduit(Produit produit);
    public List<Produit> allProduits();
    public Produit getProduitById(Long id);
    public Produit updateProduit(Produit produit,Long id);
    public void deleteProduit(Long id);
}
