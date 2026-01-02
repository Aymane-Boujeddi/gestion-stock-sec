package com.gestion.stock.entity;


import com.gestion.stock.enums.TypeMouvement;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "mouvement_stocks")
@Data
public class MouvementStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private int quantite;

    @Column(name = "type_mouvement")
    @Enumerated(EnumType.STRING)
    private TypeMouvement typeMouvement;

    @Column(name = "date_mouvement")
    private LocalDateTime dateMouvement;

    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;

}
