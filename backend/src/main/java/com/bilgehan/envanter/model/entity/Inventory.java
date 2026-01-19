package com.bilgehan.envanter.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Where;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Builder
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inventory")
@Where(clause = "is_deleted=false")
public class Inventory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InventoryItem> inventoryItems;

    @ManyToOne
    @JoinColumn(name="warehouse_id", nullable=false)
    private Warehouse warehouse;

    @CreationTimestamp
    private Timestamp updatedAt;

    @Column(columnDefinition = "boolean default false")
    private boolean isDeleted;
}
