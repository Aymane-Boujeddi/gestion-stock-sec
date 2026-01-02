package com.gestion.stock.repository;

import com.gestion.stock.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository  extends JpaRepository<Stock,Long> {


}
