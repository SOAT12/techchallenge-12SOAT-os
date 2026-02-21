package com.fiap.soat12.os.cleanarch.domain.repository;

import com.fiap.soat12.os.cleanarch.domain.model.ToolCategory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ToolCategoryRepository {

    Optional<ToolCategory> findById(UUID toolCategoryId);

    Optional<ToolCategory> findByToolCategoryName(String toolCategoryName);

    List<ToolCategory> findAllActive();

    List<ToolCategory> findAll();

    ToolCategory save(ToolCategory newToolCategory);
}
