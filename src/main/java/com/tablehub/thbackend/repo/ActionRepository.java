package com.tablehub.thbackend.repo;

import com.tablehub.thbackend.model.Action;
import com.tablehub.thbackend.model.ActionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActionRepository extends JpaRepository<Action, Long> {
    Optional<Action> findByName(ActionType name);
}