// src/main/java/com/kribb/backend/node/dto/NodeUpdateRequest.java
package com.kribb.backend.node.dto;

import com.kribb.backend.node.NodeStatus;

public record NodeUpdateRequest(
        String name,
        NodeStatus status,
        Double x,
        Double y,
        String metaJson
) {}
