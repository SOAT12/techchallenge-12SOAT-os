package com.fiap.soat12.os.dto.serviceorder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOrderFullCreationResponseDTO {

    private Long serviceOrderIdentifier;

}
