// NodeLinkRepository.java
package com.kribb.backend.node;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NodeLinkRepository extends JpaRepository<NodeLinkEntity, Long> {
    List<NodeLinkEntity> findByProjectIdAndTargetNodeId(Long projectId, Long targetNodeId);
    List<NodeLinkEntity> findByProjectIdAndSourceNodeId(Long projectId, Long sourceNodeId);
}
