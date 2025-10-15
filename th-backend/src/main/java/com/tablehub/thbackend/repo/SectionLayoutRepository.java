package com.tablehub.thbackend.repo;

import com.tablehub.thbackend.model.SectionLayout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SectionLayoutRepository extends JpaRepository<SectionLayout, Integer> {
}
