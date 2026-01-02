package com.gestion.stock.mapper;


import com.gestion.stock.entity.MouvementStock;
import com.gestion.stock.entity.Stock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StockToMouvementMapper {

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "quantite",source = "quantiteActuel")
    @Mapping(target = "stock",source = "stock")
    @Mapping(target = "dateMouvement", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "typeMouvement",expression = "java(com.gestion.stock.enums.TypeMouvement.ENTREE)")
    MouvementStock toMouvementEntre(Stock stock);

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "quantite",source = "quantiteActuel")
    @Mapping(target = "stock",source = "stock")
    @Mapping(target = "dateMouvement", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "typeMouvement",expression = "java(com.gestion.stock.enums.TypeMouvement.SORTIE)")
    MouvementStock toMouvementSortie(Stock stock);


}
