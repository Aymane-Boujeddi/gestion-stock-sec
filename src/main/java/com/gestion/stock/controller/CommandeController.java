package com.gestion.stock.controller;


import com.gestion.stock.dto.request.CommandeRequestDTO;
import com.gestion.stock.dto.request.CommandeUpdateRequestDTO;
import com.gestion.stock.dto.request.DetailsCommandeUpdateRequestDTO;
import com.gestion.stock.dto.response.CommandeResponseDTO;
import com.gestion.stock.entity.Commande;
import com.gestion.stock.mapper.CommandeMapper;
import com.gestion.stock.repository.CommandeRepository;
import com.gestion.stock.service.CommandeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/commandes")
@RequiredArgsConstructor
public class CommandeController {

    private final CommandeService commandeService;

    private final CommandeMapper mapper;




    @PostMapping
    @PreAuthorize("hasAuthority('COMMANDE_CREATE')")
    public ResponseEntity<CommandeResponseDTO> createCommande(@Valid @RequestBody CommandeRequestDTO commandeRequestDTO){
       return ResponseEntity.ok(commandeService.saveCommande(commandeRequestDTO));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('COMMANDE_READ')")
    public ResponseEntity<List<CommandeResponseDTO>> getAllCommandes(){
        return ResponseEntity.ok(commandeService.allCommandes());
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('COMMANDE_READ')")
    public ResponseEntity<CommandeResponseDTO> getCommandeByID(@PathVariable Long id){
        return  ResponseEntity.ok(commandeService.commandeByID(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('COMMANDE_UPDATE')")
    public ResponseEntity<CommandeResponseDTO> updateCommandeById(@PathVariable Long id, @Valid @RequestBody CommandeUpdateRequestDTO commandeRequestDTO){


        return ResponseEntity.ok(commandeService.updateCommande(id,commandeRequestDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('COMMANDE_UPDATE')")
    public ResponseEntity<String> deleteCommandeById(@PathVariable Long id){
        return ResponseEntity.ok(commandeService.deleteCommande(id));
    }

    @PutMapping("/{id}/annulee")
    @PreAuthorize("hasAuthority('COMMANDE_CANCEL')")
    public ResponseEntity<CommandeResponseDTO> changeCommandeStatusAnnulee(@PathVariable Long id){
        return ResponseEntity.ok(commandeService.changeStatusToAnnulee(id));
    }

    @PutMapping("/{id}/livree")
    @PreAuthorize("hasAuthority('COMMANDE_RECEPTION')")
    public ResponseEntity<Map<String,Object>> changeCommandeStatusLivree(@PathVariable Long id){
        return ResponseEntity.ok(commandeService.changeStatusToLivree(id));
    }

    @GetMapping("/{id}/fournisseur")
    @PreAuthorize("hasAuthority('COMMANDE_READ')")
    public ResponseEntity<List<CommandeResponseDTO>> commandeFournisseurById(@PathVariable Long id){
        return ResponseEntity.ok(commandeService.commandesFournisseurById(id));
    }




}
