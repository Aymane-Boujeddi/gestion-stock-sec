package com.gestion.stock.mapper;


import com.gestion.stock.dto.request.FournisseurCreateDTO;
import com.gestion.stock.dto.response.FournisseurResponseDTO;
import com.gestion.stock.entity.Fournisseur;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface FournisseurMapper {


    Fournisseur toEntity(FournisseurCreateDTO dto);

    FournisseurResponseDTO toResponseDTO(Fournisseur fournisseur);


}
