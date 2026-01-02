package com.gestion.stock.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BonSortieUpdateRequestDTO {

    private String atelier;

    private String motif;

    private String motifDetails;

    private List<BonSortieItemRequestDTO> items;
}
