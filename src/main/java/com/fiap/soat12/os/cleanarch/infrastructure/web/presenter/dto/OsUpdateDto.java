package com.fiap.soat12.os.cleanarch.infrastructure.web.presenter.dto;


import com.fiap.soat12.os.cleanarch.util.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OsUpdateDto {

    private Long osId;
    private Status newStatus;
}