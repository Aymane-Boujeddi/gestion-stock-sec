package com.gestion.stock.mapper;


import com.gestion.stock.dto.response.MouvementStockResponseDTO;
import com.gestion.stock.entity.MouvementStock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MouvementStockMapper {


    @Mapping(target = "quantite",source = "quantite" )
    @Mapping(target = "dateMouvement",source = "dateMouvement" )
    @Mapping(target = "typeMouvement",source = "typeMouvement" )
    @Mapping(target = "stockId",source = "stock.id" )
    @Mapping(target = "produitNom",source = "stock.produit.nom")
    @Mapping(target = "commandeId",source = "stock.commande.id")
    @Mapping(target = "fournisseurNom",source = "stock.commande.fournisseur.nom")
    MouvementStockResponseDTO toResponseDTO(MouvementStock mouvementStock);


}
