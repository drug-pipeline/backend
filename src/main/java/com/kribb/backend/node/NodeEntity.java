// src/main/java/com/kribb/backend/node/NodeEntity.java
package com.kribb.backend.node;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "node")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NodeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NodeType type;

    @Column(nullable = false, length = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NodeStatus status;

    @Column(nullable = false)
    private double x;

    @Column(nullable = false)
    private double y;

    @Column(name = "file_path", length = 1000)
    private String filePath;           // PDB 등 파일 경로

    @Column(columnDefinition = "JSON")
    private String meta;               // 타입별 확장(JSON string)

    @Column(name = "created_at", nullable = false, updatable = false)
    @ColumnDefault("CURRENT_TIMESTAMP")
    private Instant createdAt = Instant.now();

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
