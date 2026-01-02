package com.gestion.stock.service;


import com.gestion.stock.dto.request.BonSortieRequestDto;
import com.gestion.stock.dto.request.BonSortieUpdateRequestDTO;
import com.gestion.stock.dto.response.BonSortieResponseDTO;

import java.util.List;
import java.util.Map;

public interface BonSortieService {
    public BonSortieResponseDTO createBonSortie(BonSortieRequestDto bonSortieRequestDto);
    public List<BonSortieResponseDTO> getAllBonsSortie();
    public BonSortieResponseDTO getBonSortieById(Long id);
    public BonSortieResponseDTO updateBonSortie(Long id, BonSortieUpdateRequestDTO bonSortieUpdateRequestDto);
    public BonSortieResponseDTO updateBonSortieToAnnuler(Long id);
    public Map<String , Object> updateBonSortieToValider(Long id);
}
