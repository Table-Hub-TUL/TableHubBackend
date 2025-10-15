package com.tablehub.thbackend.service.implementations;
import com.tablehub.thbackend.model.SectionLayout;
import com.tablehub.thbackend.repo.SectionLayoutRepository;
import com.tablehub.thbackend.service.interfaces.SectionLayoutService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SectionLayoutServiceImpl implements SectionLayoutService {

    private final SectionLayoutRepository sectionLayoutRepository;

    public SectionLayoutServiceImpl(SectionLayoutRepository sectionLayoutRepository) {
        this.sectionLayoutRepository = sectionLayoutRepository;
    }

    @Override
    public List<SectionLayout> findAll() {
        return sectionLayoutRepository.findAll();
    }

    @Override
    public Optional<SectionLayout> findById(Integer id) {
        return sectionLayoutRepository.findById(id);
    }

    @Override
    public SectionLayout save(SectionLayout layout) {
        return sectionLayoutRepository.save(layout);
    }

    @Override
    public SectionLayout update(Integer id, SectionLayout newLayout) {
        return sectionLayoutRepository.findById(id)
                .map(existing -> {
                    existing.setViewportWidth(newLayout.getViewportWidth());
                    existing.setViewportHeight(newLayout.getViewportHeight());
                    existing.setShape(newLayout.getShape());
                    return sectionLayoutRepository.save(existing);
                })
                .orElseThrow(() -> new IllegalArgumentException("SectionLayout not found with id " + id));
    }

    @Override
    public void delete(Integer id) {
        if (!sectionLayoutRepository.existsById(id)) {
            throw new IllegalArgumentException("SectionLayout not found with id " + id);
        }
        sectionLayoutRepository.deleteById(id);
    }
}