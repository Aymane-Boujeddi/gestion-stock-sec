package com.gestion.stock.controller;
import com.gestion.stock.dto.request.BonSortieRequestDto;
import com.gestion.stock.dto.request.BonSortieUpdateRequestDTO;
import com.gestion.stock.dto.response.BonSortieResponseDTO;
import com.gestion.stock.dto.response.CommandeResponseDTO;
import com.gestion.stock.service.BonSortieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/bonsSortie")
@RequiredArgsConstructor
public class BonSortieController {
    private final BonSortieService bonSortieService;

    @PostMapping
    @PreAuthorize("hasAuthority('BON_SORTIE_CREATE')")
    public ResponseEntity<BonSortieResponseDTO> createBonSortie(@Valid @RequestBody BonSortieRequestDto bonSortieRequestDto){
        return ResponseEntity.ok(bonSortieService.createBonSortie(bonSortieRequestDto));
    }
    @GetMapping
    @PreAuthorize("hasAuthority('BON_SORTIE_READ')")
    public ResponseEntity<List<BonSortieResponseDTO>> getAllBonsSortie() {
        return ResponseEntity.ok(bonSortieService.getAllBonsSortie());
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('BON_SORTIE_READ')")
    public ResponseEntity<BonSortieResponseDTO> getBonSortieById(@PathVariable Long id) {
        return ResponseEntity.ok(bonSortieService.getBonSortieById(id));
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('BON_SORTIE_CREATE')")
    public ResponseEntity<BonSortieResponseDTO> updateBonSortie(@PathVariable Long id, @Valid @RequestBody BonSortieUpdateRequestDTO bonSortieUpdateRequestDto) {

        return ResponseEntity.ok(bonSortieService.updateBonSortie(id, bonSortieUpdateRequestDto));
    }
    @PutMapping("/{id}/annuler")
    @PreAuthorize("hasAuthority('BON_SORTIE_CANCEL')")
    public ResponseEntity<BonSortieResponseDTO> updateBonSortieToAnnuler(@PathVariable Long id){
        return ResponseEntity.ok(bonSortieService.updateBonSortieToAnnuler(id));
    }

    @PutMapping("/{id}/valider")
    @PreAuthorize("hasAuthority('BON_SORTIE_VALIDATE')")
    public ResponseEntity<Map<String , Object>> updateBonSortieToValider(@PathVariable Long id){
        return ResponseEntity.ok(bonSortieService.updateBonSortieToValider(id));
    }

}
