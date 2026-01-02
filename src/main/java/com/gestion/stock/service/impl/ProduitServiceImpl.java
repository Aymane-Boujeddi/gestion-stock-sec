package com.gestion.stock.service.impl;

import com.gestion.stock.entity.Produit;
import com.gestion.stock.repository.ProduitRepository;
import com.gestion.stock.service.ProduitService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProduitServiceImpl implements ProduitService {

    private final ProduitRepository produitRepository;

    @Override
    public Produit saveProduit(Produit produit) {

        return  produitRepository.save(produit);
    }

    @Override
    public List<Produit> allProduits() {
        return produitRepository.findAll();
    }

    @Override
    public Produit getProduitById(Long id) {
        return produitRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Produit Not Found with ID " + id));
    }

    @Override
    public Produit updateProduit(Produit produit, Long id) {
        Produit currentProduit = getProduitById(id);

        produit.setId(currentProduit.getId());
        return  produitRepository.save(produit);


    }

    @Override
    public void deleteProduit(Long id) {
        Produit produit = getProduitById(id);

        produitRepository.delete(produit);
    }
}
