// src/main/java/com/kribb/backend/node/dto/NodeUpdateRequest.java
package com.kribb.backend.node.dto;

import com.kribb.backend.node.NodeStatus;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record NodeUpdateRequest(
        String name,
        NodeStatus status,
        Double x,
        Double y,
        Map<String, Object> meta  // JSON string
) {}
