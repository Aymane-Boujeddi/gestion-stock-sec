package com.gestion.stock.service.impl;

import com.gestion.stock.dto.request.BonSortieRequestDto;
import com.gestion.stock.dto.request.BonSortieUpdateRequestDTO;
import com.gestion.stock.dto.response.BonSortieResponseDTO;
import com.gestion.stock.entity.*;
import com.gestion.stock.enums.*;
import com.gestion.stock.exception.InsuffissientStockException;
import com.gestion.stock.mapper.BonSortieItemMapper;
import com.gestion.stock.mapper.BonSortieMapper;
import com.gestion.stock.mapper.StockToMouvementMapper;
import com.gestion.stock.repository.BonSortieRepository;
import com.gestion.stock.repository.MouvementStockRepository;
import com.gestion.stock.repository.ProduitRepository;
import com.gestion.stock.repository.StockRepository;
import com.gestion.stock.service.BonSortieService;
import com.gestion.stock.service.StockService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


@Service
@Transactional
@RequiredArgsConstructor
public class BonSortieServiceImpl implements BonSortieService {

    private final BonSortieRepository bonSortieRepository;
    private final ProduitRepository produitRepository;
    private final MouvementStockRepository mouvementStockRepository;
    private final StockRepository stockRepository;
    private final BonSortieMapper mapper;
    private final StockToMouvementMapper stockToMouvementMapper;
    private final BonSortieItemMapper bonSortieItemMapper;


    @Override
    public BonSortieResponseDTO createBonSortie(BonSortieRequestDto bonSortieRequestDto) {
        BonSortie newBonSortie = mapper.toEntity(bonSortieRequestDto);
        newBonSortie.setDateSortie(LocalDateTime.now());
        newBonSortie.setStatut(StatutBonSortie.BROUILLON);
        newBonSortie.getItems().forEach(item -> item.setBonSortie(newBonSortie));

        return mapper.toResponseDTO(bonSortieRepository.save(newBonSortie));
    }
    @Override
    public List<BonSortieResponseDTO> getAllBonsSortie() {
        return bonSortieRepository.findAll().stream().map(mapper::toResponseDTO).toList();
    }
    @Override
    public BonSortieResponseDTO getBonSortieById(Long id) {
        BonSortie bonSortie = bonSortieRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Bon de sortie introuvable avec l'id : " + id));

        return mapper.toResponseDTO(bonSortie);
    }
    @Override
    public BonSortieResponseDTO updateBonSortie(Long id, BonSortieUpdateRequestDTO bonSortieUpdateRequestDto) {
        BonSortie existingBon = bonSortieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bon de sortie introuvable avec l'id : " + id));

        if (existingBon.getStatut() != StatutBonSortie.BROUILLON) {
            throw new IllegalStateException("Seuls les bons de sortie en BROUILLON peuvent être modifiés.");
        }

        if (bonSortieUpdateRequestDto.getAtelier() != null) {
            existingBon.setAtelier(bonSortieUpdateRequestDto.getAtelier());
        }
        if (bonSortieUpdateRequestDto.getMotif() != null) {
            existingBon.setMotif(MotifType.valueOf(bonSortieUpdateRequestDto.getMotif().toUpperCase()));
        }
        if (bonSortieUpdateRequestDto.getMotifDetails() != null) {
            existingBon.setMotifDetails(bonSortieUpdateRequestDto.getMotifDetails());
        }
        if (bonSortieUpdateRequestDto.getItems() != null) {
            existingBon.getItems().clear();
            existingBon.getItems().addAll(
                    bonSortieUpdateRequestDto.getItems().stream()
                            .map(bonSortieItemMapper::toEntity)
                            .peek(item -> item.setBonSortie(existingBon))
                            .toList()
            );
        }

        existingBon.setUpdatedAt(LocalDateTime.now());

        return mapper.toResponseDTO(bonSortieRepository.save(existingBon));
    }

    @Override
    public BonSortieResponseDTO updateBonSortieToAnnuler(Long id) {
        BonSortie updatingBonSortie = bonSortieRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("BonSortie not found id : "+ id));
        updatingBonSortie.setStatut(StatutBonSortie.ANNULE);
        return mapper.toResponseDTO(bonSortieRepository.save(updatingBonSortie));
    }

    @Override
    public Map<String, Object> updateBonSortieToValider(Long id) {


        BonSortie updatingBonSortie = bonSortieRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("BonSortie not found id : "+ id));

        if (updatingBonSortie.getStatut() != StatutBonSortie.BROUILLON) {
            throw new IllegalStateException("Only BROUILLON status can be validated");
        }

        List<BonSortieItem> sortieItems = updatingBonSortie.getItems();
        List<Long> itemProduitIdList = sortieItems.stream().map(item -> item.getProduit().getId()).toList();
        List<Stock> stockWithProduit = stockRepository.findAll().stream()
                .filter(stock -> itemProduitIdList.contains(stock.getProduit().getId()) && stock.getQuantiteActuel() > 0)
                .sorted(Comparator.comparing(Stock::getDateEntre))
                .toList();


        sortieItems.forEach(item -> {
            List<Stock> stockForThisProduit = stockWithProduit.stream()
                    .filter(stock -> stock.getProduit().getId().equals(item.getProduit().getId()) )
                    .toList();
            int totalAvailable = stockForThisProduit.stream()
                    .mapToInt(Stock::getQuantiteActuel)
                    .sum();
            int quantite = item.getQuantite();

            if (quantite > totalAvailable) {
                throw new InsuffissientStockException("Insufficient stock for product: " + item.getProduit().getId() + " current stock : " + totalAvailable);
            }


            for (Stock stock : stockForThisProduit) {
                if (quantite <= 0) break;

                if (quantite >= stock.getQuantiteActuel()) {
                    int stockQty = stock.getQuantiteActuel();
                    quantite -= stock.getQuantiteActuel();
                //    MouvementStock mouvement = stockToMouvementMapper.toMouvementSortie(stock);

                    stock.setQuantiteActuel(0);
                    updateStock(stock);
                    addMouvementSortie(stock,stockQty);
                 //   mouvementStockRepository.save(mouvement);
                    updateProduit(stock.getProduit().getId(),stockQty);

                } else {
                    addMouvementSortie(stock,quantite);
                    updateProduit(stock.getProduit().getId(), quantite);
                    stock.setQuantiteActuel(stock.getQuantiteActuel() - quantite);
                    updateStock(stock);

                    quantite = 0;
                }

            }
        });
        updatingBonSortie.setStatut(StatutBonSortie.VALIDE);
        bonSortieRepository.save(updatingBonSortie);


        return Map.of("status","VALIDE");
    }



    private Produit getProduitById(Long id)  {
        return produitRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Produit not found id : "+ id));
    }

    private Produit updateProduit(Long id,int quantite){
        Produit produit = getProduitById(id);
        produit.setStockActuel(produit.getStockActuel() - quantite);
        return produitRepository.save(produit);
    }
    private Stock updateStock(Stock stock) {
        return stockRepository.save(stock);
    }
    private void deleteStock(Stock stock){
        stockRepository.delete(stock);
    }
    private MouvementStock addMouvementSortie(Stock stock,int quantite){
        MouvementStock mouvementStockSortie = stockToMouvementMapper.toMouvementSortie(stock);
        mouvementStockSortie.setQuantite(quantite);

       return  mouvementStockRepository.save(mouvementStockSortie);
    }


}