package com.tablehub.thbackend.service.interfaces;

import com.tablehub.thbackend.model.SectionLayout;
import java.util.List;
import java.util.Optional;

public interface SectionLayoutService {
    List<SectionLayout> findAll();
    Optional<SectionLayout> findById(Integer id);
    SectionLayout save(SectionLayout layout);
    SectionLayout update(Integer id, SectionLayout layout);
    void delete(Integer id);
}
