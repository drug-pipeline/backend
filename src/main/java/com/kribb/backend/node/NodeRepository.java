// NodeRepository.java
package com.kribb.backend.node;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NodeRepository extends JpaRepository<NodeEntity, Long> {
    List<NodeEntity> findByProjectId(Long projectId);
}
