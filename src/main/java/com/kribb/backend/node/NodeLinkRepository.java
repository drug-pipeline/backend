// src/main/java/com/kribb/backend/node/NodeLinkRepository.java
package com.kribb.backend.node;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NodeLinkRepository extends JpaRepository<NodeLinkEntity, Long> {

    List<NodeLinkEntity> findByProjectIdAndTargetNodeId(Long projectId, Long targetNodeId);
    List<NodeLinkEntity> findByProjectIdAndSourceNodeId(Long projectId, Long sourceNodeId);

    // 추가: 프로젝트의 모든 링크 조회
    List<NodeLinkEntity> findByProjectId(Long projectId);

    // 정렬이 필요하면 아래처럼 별도 메서드를 만들어 사용해도 됩니다.
    // List<NodeLinkEntity> findByProjectIdOrderByCreatedAtAsc(Long projectId);
}
