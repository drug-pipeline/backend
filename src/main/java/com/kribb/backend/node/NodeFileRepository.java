// NodeFileRepository.java
package com.kribb.backend.node;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;
import java.util.List;

public interface NodeFileRepository extends JpaRepository<NodeFileEntity, Long> {
    List<NodeFileEntity> findByNodeId(Long nodeId);

    // ▼ 신규: 여러 nodeId의 파일을 한 번에 가져오기 (N+1 방지)
    List<NodeFileEntity> findByNodeIdIn(Collection<Long> nodeIds);
}
