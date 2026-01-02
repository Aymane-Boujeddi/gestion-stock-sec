package com.gestion.stock.service.impl;

import com.gestion.stock.dto.response.MouvementStockResponseDTO;
import com.gestion.stock.dto.response.ProduitResponseDTO;
import com.gestion.stock.dto.response.StockResponseDTO;
import com.gestion.stock.entity.DetailsCommande;
import com.gestion.stock.entity.MouvementStock;
import com.gestion.stock.entity.Produit;
import com.gestion.stock.entity.Stock;
import com.gestion.stock.enums.TypeMouvement;
import com.gestion.stock.mapper.*;
import com.gestion.stock.repository.MouvementStockRepository;
import com.gestion.stock.repository.ProduitRepository;
import com.gestion.stock.repository.StockRepository;
import com.gestion.stock.service.StockService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final MouvementStockRepository mouvementStockRepository;
    private final ProduitRepository produitRepository;
    private final DetailsCommandetoStockMapper detailsToStockMapper;
    private final StockToMouvementMapper stockToMouvementMapper;
    private final MouvementStockMapper mouvementStockMapper;
    private final ProduitMapper produitMapper;
    private final StockMapper stockMapper;

    @Override
    public List<StockResponseDTO> createStockLotsAndMouvement(List<DetailsCommande> detailsCommandeList) {
       return detailsCommandeList.stream().map(detailsCommande -> {

            Produit produit = produitRepository.findById(detailsCommande.getProduit().getId()).orElseThrow(() -> new EntityNotFoundException("Produit not found id : " + detailsCommande.getProduit().getId()));
            produit.setStockActuel(produit.getStockActuel() + detailsCommande.getQuantite());
            Produit updatedProduit =  produitRepository.save(produit);
           Stock newStock =  detailsToStockMapper.detailsToStock(detailsCommande);
           newStock.setDateEntre(LocalDateTime.now());
           newStock.setProduit(updatedProduit);
            Stock  savedStock=stockRepository.save(newStock);
            MouvementStock mouvementEntree = stockToMouvementMapper.toMouvementEntre(savedStock);
            mouvementStockRepository.save(mouvementEntree);
           return stockMapper.toResponseDto(savedStock);

        }).toList();


    }

    @Override
    public List<StockResponseDTO> allStock() {

        return findAllStock().stream().map(stockMapper::toResponseDto).toList();
    }

    @Override
    public Map<String, List<StockResponseDTO>> stocksForProductSortedFifo(Long id) {
       String productName = produitRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Produit not found id : " + id)).getNom();

       List<StockResponseDTO> productStock = findAllStock().stream()
               .filter(stock -> stock.getProduit().getId().equals(id))
               .sorted(Comparator.comparing(Stock::getDateEntre))
               .map(stockMapper::toResponseDto).toList();

       Map<String , List<StockResponseDTO>> productWithStockList = new HashMap<>();
       productWithStockList.put(productName,productStock);

        return productWithStockList;
    }

    @Override
    public List<MouvementStockResponseDTO> historiqueMouvement() {

        return findAllMouvement().stream().map(mouvementStockMapper::toResponseDTO).toList();
    }

    @Override
    public List<MouvementStockResponseDTO> historiqueMouvementStockProduit(Long id) {
//        String productName = produitRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Produit not found id : " + id)).getNom();
//
//        return findAllMouvement().stream()
//                .filter(mouvement -> mouvement.getStock().getProduit().getId().equals(id))
//                .map(mouvementStockMapper::toResponseDTO)
//                .toList();
        List<MouvementStock> mouvementStocks = mouvementStockRepository.findMouvementStockByDateMouvementBetweenAndTypeMouvement(LocalDateTime.now().minusDays(1),LocalDateTime.now(), TypeMouvement.ENTREE);
        return mouvementStocks.stream().map(mouvementStockMapper::toResponseDTO).toList();
    }

    @Override
    public List<ProduitResponseDTO> produitUnderThreshold() {
        return produitRepository.findAll().stream()
                .filter(produit -> produit.getStockActuel() < produit.getPointCommande())
                .map(produitMapper::toResponseDto)
                .toList();
    }

    @Override
    public String valorisationStock() {
       double totalValeurStock =  findAllStock().stream()
               .mapToDouble(stock -> stock.getQuantiteActuel() * stock.getPrixAchat())
               .sum();
        return "La valorisation du Stock : " + totalValeurStock;
    }



    private List<Stock> findAllStock(){
        return stockRepository.findAll();
    }
    private List<MouvementStock> findAllMouvement(){return mouvementStockRepository.findAll();}
}
