package com.gestion.stock.mapper;


import com.gestion.stock.dto.request.CommandeRequestDTO;
import com.gestion.stock.dto.request.CommandeUpdateRequestDTO;
import com.gestion.stock.dto.request.DetailsCommandeUpdateRequestDTO;
import com.gestion.stock.dto.response.CommandeResponseDTO;
import com.gestion.stock.entity.Commande;
import com.gestion.stock.entity.Fournisseur;
import com.gestion.stock.repository.FournisseurRepository;
import jakarta.persistence.EntityNotFoundException;
import jdk.jfr.Name;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring",uses = {DetailsCommandeMapper.class})
public abstract class  CommandeMapper {

    @Autowired
    protected FournisseurRepository fournisseurRepository;


    @Mapping(target = "fournisseur",source = "fournisseurId",qualifiedByName = "idToFournisseur")
    @Mapping(target = "detailsCommandes",source = "detailsCommande")
    @Mapping(target = "montantTotale",ignore = true)
    @Mapping(target = "statutCommande",ignore = true)
    public abstract  Commande toEntity(CommandeRequestDTO commandeRequestDTO);

    @Mapping(target = "dateCommande" , source = "dateCommande")
    @Mapping(target = "montantTotale" , source = "montantTotale")
    @Mapping(target = "fournisseurNom" , source = "fournisseur.nom")
    public abstract CommandeResponseDTO toResponseDto(Commande commande);

    @Mapping(target = "dateCommande" , ignore = true)
    @Mapping(target = "montantTotale" , ignore = true)
    @Mapping(target = "detailsCommandes" ,source = "detailsCommande" , nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT )
    @Mapping(target = "statutCommande" , ignore = true )
    @Mapping(target = "fournisseur" , source = "fournisseurId" , qualifiedByName = "idToFournisseur",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateEntity(CommandeUpdateRequestDTO updateDto, @MappingTarget Commande existingCommande);


    @Named("idToFournisseur")
    protected Fournisseur mapToFournisseur(Long fournisseurId){
        return fournisseurRepository.findById(fournisseurId).orElseThrow(() -> new EntityNotFoundException("Fournisseur not found : " + fournisseurId));
    }

//    @Named("preventNullList")
//    protected List<DetailsCommandeUpdateRequestDTO> preventNullList(List<DetailsCommandeUpdateRequestDTO> list){
//        if(list == null){
//            return Collections.emptyList();
//        }
//        return list;
//    }

}
