package com.gestion.stock.mapper;


import com.gestion.stock.dto.request.BonSortieRequestDto;
import com.gestion.stock.dto.response.BonSortieResponseDTO;
import com.gestion.stock.entity.BonSortie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring" ,uses = {BonSortieItemMapper.class})
public interface BonSortieMapper {


    @Mapping(target = "numeroBon",expression = "java(createNumeroBonSortie())")
    @Mapping(target = "dateSortie",ignore = true)
    @Mapping(target = "motif",source = "motif")
    @Mapping(target = "motifDetails",source = "motifDetails")
    @Mapping(target = "atelier",source = "atelier")
    @Mapping(target = "items",source = "items")
    @Mapping(target = "statut", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    BonSortie toEntity(BonSortieRequestDto bonSortieRequestDto);

    @Mapping(target = "motif",source = "motif")
    @Mapping(target = "motifDetails",source = "motifDetails")
    @Mapping(target = "atelierDestinataire",source = "atelier")
    @Mapping(target = "items",source = "items")
    @Mapping(target = "dateSortie",source = "dateSortie")
    @Mapping(target = "statutBonSortie",source = "statut")
    BonSortieResponseDTO toResponseDTO(BonSortie bonSortie);

    @Named(value = "createNumBon")
    default String createNumeroBonSortie(){
        return "LOT-" +  UUID.randomUUID().toString().substring(0,8);
    }
}
