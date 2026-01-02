package com.gestion.stock.service;

import com.gestion.stock.dto.request.CommandeRequestDTO;
import com.gestion.stock.dto.request.CommandeUpdateRequestDTO;
import com.gestion.stock.dto.response.CommandeResponseDTO;
import com.gestion.stock.entity.Commande;
import com.gestion.stock.entity.Fournisseur;

import java.util.List;
import java.util.Map;

public interface CommandeService {

    public CommandeResponseDTO saveCommande(CommandeRequestDTO commandeRequestDTO);
    public List<CommandeResponseDTO> allCommandes();
    public CommandeResponseDTO commandeByID(Long id);
    public CommandeResponseDTO updateCommande(Long id, CommandeUpdateRequestDTO commandeUpdateRequestDTO);
    public String deleteCommande(Long id);
    public CommandeResponseDTO changeStatusToAnnulee(Long id);
    public Map<String , Object> changeStatusToLivree(Long id);
    public List<CommandeResponseDTO> commandesFournisseurById(Long id);
}
