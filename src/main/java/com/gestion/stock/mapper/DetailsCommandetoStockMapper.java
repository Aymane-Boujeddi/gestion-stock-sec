package com.gestion.stock.mapper;

import com.gestion.stock.entity.DetailsCommande;
import com.gestion.stock.entity.Stock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface DetailsCommandetoStockMapper {



    @Mapping(target = "id",ignore = true)
    @Mapping(target = "numeroLot",expression = "java(createNumeroLot())")
    @Mapping(target = "dateEntre",ignore = true)
    @Mapping(target = "prixAchat",source = "prix")
    @Mapping(target = "quantiteActuel",source = "quantite")
    @Mapping(target = "produit",source = "produit")
    @Mapping(target = "commande",source = "commande")
    public abstract Stock detailsToStock(DetailsCommande detailsCommande);

    default String createNumeroLot(){
        return "LOT-" +  UUID.randomUUID().toString().substring(0,8);
    }
}
