package com.gestion.stock.service.impl;

import com.gestion.stock.entity.Fournisseur;
import com.gestion.stock.repository.FournisseurRepository;
import com.gestion.stock.service.FournisseurService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class FournisseurServiceImpl implements FournisseurService {

    private final FournisseurRepository fournisseurRepository;


    @Override
    public  Fournisseur createFournisseur(Fournisseur fournisseur) {
        return fournisseurRepository.save(fournisseur);
    }

    @Override
    public Fournisseur getFournisseurById(Long Id)   {
        return fournisseurRepository.findById(Id).orElseThrow(()-> new IllegalArgumentException("There is no Fournisseur with this Id"));
    }

    @Override
    public List<Fournisseur> getAllFournisseur() {
        List<Fournisseur> fournisseurs =  fournisseurRepository.findAll();

        if(fournisseurs.isEmpty()){
            throw new IllegalArgumentException("There is no Fournisseur");
        }
        return fournisseurs;
    }

    @Override
    public boolean emailExists(String email) {
        return fournisseurRepository.existsByEmail(email);
    }

    @Override
    public boolean telephoneExists(String telephone) {
        return fournisseurRepository.existsByTelephone(telephone);
    }

    @Override
    public boolean iceExists(String ICE) {
        return fournisseurRepository.existsByICE(ICE);
    }

    @Override
    public Fournisseur updateFournisseur(Fournisseur fournisseur, Long id) {
        Fournisseur currentFournisseur = getFournisseurById(id);
        fournisseur.setId(id);
        fournisseur.setCreatedAt(currentFournisseur.getCreatedAt());
        return fournisseurRepository.save(fournisseur);
    }

    @Override
    public void deleteFournisseur(Long id) {
        Fournisseur fournisseur = getFournisseurById(id);
        fournisseurRepository.delete(fournisseur);
    }


}
