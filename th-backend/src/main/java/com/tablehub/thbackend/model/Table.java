package com.tablehub.thbackend.model;

import com.tablehub.thbackend.dto.TableStatusEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
@jakarta.persistence.Table(name = "tables")
public class Table {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tableId;

    private TableStatusEnum status;

    public Table() {
    }

    public Table(Long tableId, TableStatusEnum status) {
        this.tableId = tableId;
        this.status = status;
    }

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public TableStatusEnum getStatus() {
        return status;
    }

    public void setStatus(TableStatusEnum status) {
        this.status = status;
    }

    // TODO: finish this entity model (DAJCIE MI BAZE DANYCH)
}
