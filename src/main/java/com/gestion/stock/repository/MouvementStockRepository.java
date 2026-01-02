package com.gestion.stock.repository;


import com.gestion.stock.entity.MouvementStock;
import com.gestion.stock.enums.TypeMouvement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MouvementStockRepository extends JpaRepository<MouvementStock,Long> {

    public List<MouvementStock> findMouvementStockByDateMouvementBetweenAndTypeMouvement(LocalDateTime dateDebut, LocalDateTime dateFin, TypeMouvement type);

    //@Query("select  ms.stock.produit.nom as nom , ms.typeMouvement as type , count(ms.typeMouvement) from MouvementStock ms   group by ms , type  ")
    @Query("select  ms.stock.produit.nom as nom , ms.typeMouvement as type , count(ms) from MouvementStock ms   group by ms.stock.produit.nom , type")
    List<Object[]> findProductWithCount();

}