package com.tablehub.thbackend.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ActionType {
    REPORT_NEW(10),
    VALIDATE(2);

    private final int defaultPoints;
}