package com.gestion.stock.dto.response;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class FournisseurResponseDTO {
    private String nom;
    private String adresse;
    private String personneContact;
    private String email;
    private String telephone;
    private String ville;
    private String raisonSociale;
    private String ICE;
    private LocalDateTime createdAt;

}
