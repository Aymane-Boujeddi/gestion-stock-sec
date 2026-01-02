package com.gestion.stock.service.impl;

import com.gestion.stock.dto.request.CommandeRequestDTO;
import com.gestion.stock.dto.request.CommandeUpdateRequestDTO;
import com.gestion.stock.dto.request.DetailsCommandeUpdateRequestDTO;
import com.gestion.stock.dto.response.CommandeResponseDTO;
import com.gestion.stock.dto.response.StockResponseDTO;
import com.gestion.stock.entity.Commande;
import com.gestion.stock.entity.DetailsCommande;
import com.gestion.stock.enums.StatutCommande;
import com.gestion.stock.mapper.CommandeMapper;
import com.gestion.stock.mapper.DetailsCommandeMapper;
import com.gestion.stock.repository.CommandeRepository;
import com.gestion.stock.repository.FournisseurRepository;
import com.gestion.stock.service.CommandeService;
import com.gestion.stock.service.StockService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CommandeServiceImpl implements CommandeService {

    private final CommandeRepository commandeRepository;

    private final FournisseurRepository fournisseurRepository;

    private final StockService stockService;

    private final CommandeMapper mapper;

    private final DetailsCommandeMapper detailsMapper;

    @Override
    public CommandeResponseDTO saveCommande(CommandeRequestDTO commandeRequestDTO) {
        Commande newCommande = mapper.toEntity(commandeRequestDTO);
        newCommande.setStatutCommande(StatutCommande.EN_ATTENTE);
        newCommande.setDateCommande(LocalDateTime.now());
        Double montant = newCommande.getDetailsCommandes().stream().mapToDouble(commande -> commande.getPrix() * commande.getQuantite()).sum();
        newCommande.getDetailsCommandes().forEach(detailsCommande -> detailsCommande.setCommande(newCommande));
        newCommande.setMontantTotale(montant);
        return mapper.toResponseDto(commandeRepository.save(newCommande));
    }

    @Override
    public List<CommandeResponseDTO> allCommandes() {
        List<Commande> commandes = commandeRepository.findAll();
        return commandes.stream().map(mapper::toResponseDto).toList();

    }

    @Override
    public CommandeResponseDTO commandeByID(Long id) {
        Commande searchedCommande = commandeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Commande not found : " + id));
        return mapper.toResponseDto(searchedCommande);
    }

    @Override
    public CommandeResponseDTO updateCommande(Long id, CommandeUpdateRequestDTO commandeUpdateRequestDTO) {
        Commande existingCommande = getByID(id);
        mapper.updateEntity(commandeUpdateRequestDTO,existingCommande);

        if(commandeUpdateRequestDTO.getDetailsCommande() != null && !commandeUpdateRequestDTO.getDetailsCommande().isEmpty()){
            updateDetails(existingCommande,commandeUpdateRequestDTO.getDetailsCommande());
        }

        totalRecalcule(existingCommande);

        return mapper.toResponseDto(commandeRepository.save(existingCommande));
    }

    @Override
    public String deleteCommande(Long id) {
        Commande deletingCommande = getByID(id);
        commandeRepository.delete(deletingCommande);
        return "Commande deleted successfully";
    }

    @Override
    public CommandeResponseDTO changeStatusToAnnulee(Long id) {
        Commande commande = getByID(id);
        commande.setStatutCommande(StatutCommande.ANNULEE);
        return mapper.toResponseDto(commandeRepository.save(commande));
    }

    @Override
    public Map<String ,Object> changeStatusToLivree(Long id) {
        Commande commande = getByID(id);
        if(commande.getStatutCommande().equals(StatutCommande.LIVREE)){
            throw new IllegalArgumentException("This commande is already delivered");
        }
        commande.setStatutCommande(StatutCommande.LIVREE);
        List<StockResponseDTO> stockResponseDTOList =  stockService.createStockLotsAndMouvement(commande.getDetailsCommandes());
        Commande savedCommande = commandeRepository.save(commande);
        Map<String , Object> responseDtoMap = new HashMap<>();
        responseDtoMap.put("Stock list",stockResponseDTOList);
        responseDtoMap.put("Commande updated",mapper.toResponseDto(savedCommande));

        return responseDtoMap;
    }

    @Override
    public List<CommandeResponseDTO> commandesFournisseurById(Long id) {
        fournisseurRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Fournisseur not found id : " + id));
        List<Commande> commandeList = commandeRepository.findAllByFournisseurId(id);

        return commandeList.stream().map(mapper::toResponseDto).toList();
    }


    private Commande getByID(Long id){
        return commandeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Commande not found id : " + id));
    }

    private void updateDetails(Commande commande, List<DetailsCommandeUpdateRequestDTO> updateList ){
        Map<Long,DetailsCommande> existingCommandeDetails = commande.getDetailsCommandes().stream().collect(Collectors.toMap(DetailsCommande::getId,detailsCommande -> detailsCommande));

        updateList.forEach(detailsDto -> {
            DetailsCommande detailsCommande = existingCommandeDetails.get(detailsDto.getId());
            if(detailsCommande == null){
                throw new EntityNotFoundException("Detail not found id :" + detailsDto.getId());
            }

            detailsMapper.updateEntity(detailsDto,detailsCommande);
            detailsCommande.setCommande(commande);
        } );
    }

    private void totalRecalcule(Commande commande){
        if(commande.getDetailsCommandes() != null && !commande.getDetailsCommandes().isEmpty()){
            double total = commande.getDetailsCommandes().stream().mapToDouble(details ->details.getPrix() * details.getQuantite()).sum();
            commande.setMontantTotale(total);
        }
    }




}


