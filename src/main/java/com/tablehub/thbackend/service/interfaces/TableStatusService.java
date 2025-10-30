package com.tablehub.thbackend.service.interfaces;

import com.tablehub.thbackend.dto.request.TableUpdateRequest;

public interface TableStatusService {
    void updateTableStatus(TableUpdateRequest request);
}