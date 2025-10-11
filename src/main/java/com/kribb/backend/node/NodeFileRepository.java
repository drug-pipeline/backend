// NodeFileRepository.java
package com.kribb.backend.node;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NodeFileRepository extends JpaRepository<NodeFileEntity, Long> {
    List<NodeFileEntity> findByNodeId(Long nodeId);
}
