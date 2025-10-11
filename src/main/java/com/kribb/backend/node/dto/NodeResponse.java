// dto/NodeResponse.java
package com.kribb.backend.node.dto;

import com.kribb.backend.node.NodeStatus;
import com.kribb.backend.node.NodeType;

public record NodeResponse(
        Long id,
        Long projectId,
        NodeType type,
        String name,
        NodeStatus status,
        Double x,
        Double y
) {}
