package com.gestion.stock.mapper;


import com.gestion.stock.dto.response.StockResponseDTO;
import com.gestion.stock.entity.Stock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StockMapper {

    @Mapping(target = "numeroLot",source = "numeroLot" )
    @Mapping(target = "dateEntre",source = "dateEntre" )
    @Mapping(target = "prixAchat",source = "prixAchat" )
    @Mapping(target = "quantiteActuel",source = "quantiteActuel" )
    @Mapping(target = "nomProduit" ,source = "produit.nom" )
    @Mapping(target = "commandeId",source = "commande.id" )
    StockResponseDTO toResponseDto(Stock stock);

}
