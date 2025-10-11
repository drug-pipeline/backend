// dto/NodeCreateRequest.java
package com.kribb.backend.node.dto;

import com.kribb.backend.node.NodeStatus;
import com.kribb.backend.node.NodeType;

public record NodeCreateRequest(
        Long projectId,
        NodeType type,
        String name,
        NodeStatus status,
        Double x,
        Double y,
        String metaJson
) {}
