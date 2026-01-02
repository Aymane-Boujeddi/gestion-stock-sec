package com.gestion.stock.service;

import com.gestion.stock.dto.request.FournisseurCreateDTO;
import com.gestion.stock.entity.Fournisseur;

import java.util.List;

public interface FournisseurService {


    public Fournisseur createFournisseur(Fournisseur fournisseur);
    public Fournisseur getFournisseurById(Long Id);
    public List<Fournisseur> getAllFournisseur();
    public boolean emailExists(String email);
    public boolean telephoneExists(String telephone);
    public boolean iceExists(String ICE);
    public Fournisseur updateFournisseur(Fournisseur fournisseur,Long id);
    public void deleteFournisseur(Long id);
}
