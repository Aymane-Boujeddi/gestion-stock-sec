package com.gestion.stock.mapper;


import com.gestion.stock.dto.request.CommandeRequestDTO;
import com.gestion.stock.dto.request.DetailsCommandeRequestDTO;
import com.gestion.stock.dto.request.DetailsCommandeUpdateRequestDTO;
import com.gestion.stock.dto.response.CommandeResponseDTO;
import com.gestion.stock.dto.response.DetailsCommandeResponseDTO;
import com.gestion.stock.repository.ProduitRepository;
import com.gestion.stock.entity.DetailsCommande;
import com.gestion.stock.entity.Produit;
import jakarta.persistence.EntityNotFoundException;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class DetailsCommandeMapper {

    @Autowired
    protected ProduitRepository produitRepository;


    @Mapping(target = "id",ignore = true)
    @Mapping(target = "commande" , ignore = true)
    @Mapping(target = "produit",source = "produitId",qualifiedByName = "idToProduit")
    public abstract DetailsCommande toEntity(CommandeRequestDTO.DetailsCommandeRequestDTO detailsCommandeDTO);


    @Mapping(target = "produitNom" , source =  "produit.nom")
    public abstract CommandeResponseDTO.DetailsCommandeResponseDTO toResponseDTO(DetailsCommande detailsCommande);


    @Mapping(target = "id" ,ignore = true)
    @Mapping(target = "commande", ignore = true)
    @Mapping(target = "produit", source = "produitId", qualifiedByName = "idToProduit",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateEntity(DetailsCommandeUpdateRequestDTO updateDTO, @MappingTarget DetailsCommande detailsCommande);


    @Named("idToProduit")
    protected Produit idToProduit(Long produitId){
        return produitRepository.findById(produitId).orElseThrow(
                () -> new EntityNotFoundException("Produit not found : " + produitId));
    }
}
