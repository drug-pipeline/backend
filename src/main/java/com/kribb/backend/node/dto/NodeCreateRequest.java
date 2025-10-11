// src/main/java/com/kribb/backend/node/dto/NodeCreateRequest.java
package com.kribb.backend.node.dto;

import com.kribb.backend.node.NodeStatus;
import com.kribb.backend.node.NodeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record NodeCreateRequest(
        @NotNull Long projectId,
        @NotNull NodeType type,
        @NotBlank String name,
        @NotNull NodeStatus status,
        @NotNull Double x,
        @NotNull Double y,
        Map<String, Object> meta
) {}
