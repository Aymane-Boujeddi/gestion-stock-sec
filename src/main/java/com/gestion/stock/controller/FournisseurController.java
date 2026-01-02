package com.gestion.stock.controller;


import com.gestion.stock.dto.request.FournisseurCreateDTO;
import com.gestion.stock.dto.response.FournisseurResponseDTO;
import com.gestion.stock.entity.Fournisseur;
import com.gestion.stock.exception.DuplicateResourceException;
import com.gestion.stock.mapper.FournisseurMapper;
import com.gestion.stock.service.FournisseurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/fournisseurs")
@RequiredArgsConstructor
public class FournisseurController {


    private final FournisseurService fournisseurService;

    private final FournisseurMapper mapper;




    @PostMapping
    @PreAuthorize("hasAuthority('FOURNISSEUR_CREATE')")
    public ResponseEntity<FournisseurResponseDTO> createFournisseur(@Valid @RequestBody FournisseurCreateDTO fournisseurCreateDTO) throws DuplicateResourceException {
        Fournisseur  newFournisseur = mapper.toEntity(fournisseurCreateDTO);
        if(fournisseurService.emailExists(fournisseurCreateDTO.getEmail())){
            throw new DuplicateResourceException("email");
        }
        if(fournisseurService.telephoneExists(fournisseurCreateDTO.getTelephone())){
            throw new DuplicateResourceException("telephone");
        }

        if(fournisseurService.iceExists(fournisseurCreateDTO.getICE())){
            throw new DuplicateResourceException("ICE");
        }

        Fournisseur newFournisseurSaved = fournisseurService.createFournisseur(newFournisseur);

        return ResponseEntity.ok(mapper.toResponseDTO(newFournisseurSaved));

    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FOURNISSEUR_READ')")
    public ResponseEntity<FournisseurResponseDTO> getFournisseurById(@PathVariable Long id){
        Fournisseur fournisseur = fournisseurService.getFournisseurById(id);
        FournisseurResponseDTO fournisseurResponseDTO = mapper.toResponseDTO(fournisseur);
        return ResponseEntity.ok(fournisseurResponseDTO);
    }


    @GetMapping
    @PreAuthorize("hasAuthority('FOURNISSEUR_READ')")
    public ResponseEntity<List<FournisseurResponseDTO>> getAllFournisseurs(){
        List<Fournisseur> fournisseurs = fournisseurService.getAllFournisseur();
        List<FournisseurResponseDTO> fournisseursDto = fournisseurs.stream().map(fournisseur -> mapper.toResponseDTO(fournisseur)).toList();

        return ResponseEntity.ok(fournisseursDto);

    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('FOURNISSEUR_UPDATE')")
    public ResponseEntity<FournisseurResponseDTO> updateFournisseurs(@PathVariable Long id,@Valid @RequestBody FournisseurCreateDTO fournisseurUpdateDto){
        Fournisseur fournisseur = mapper.toEntity(fournisseurUpdateDto);
        Fournisseur updatedFournisseur = fournisseurService.updateFournisseur(fournisseur,id);
        return ResponseEntity.ok(mapper.toResponseDTO(updatedFournisseur));

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FOURNISSEUR_DELETE')")
    public ResponseEntity<String> deleteFournisseur(@PathVariable Long id){
        fournisseurService.deleteFournisseur(id);
        return ResponseEntity.ok("Fournisseur with the id " + id + " is deleted successfully");
    }












}
