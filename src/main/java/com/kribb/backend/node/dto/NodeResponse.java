// src/main/java/com/kribb/backend/node/dto/NodeResponse.java
package com.kribb.backend.node.dto;

import com.kribb.backend.node.NodeEntity;
import com.kribb.backend.node.NodeStatus;
import com.kribb.backend.node.NodeType;

public record NodeResponse(
        Long id,
        Long projectId,
        NodeType type,
        String name,
        NodeStatus status,
        double x,
        double y,
        String filePath,
        String meta
) {
    public static NodeResponse from(NodeEntity e) {
        return new NodeResponse(
                e.getId(), e.getProjectId(), e.getType(), e.getName(),
                e.getStatus(), e.getX(), e.getY(), e.getFilePath(), e.getMeta()
        );
    }
}
