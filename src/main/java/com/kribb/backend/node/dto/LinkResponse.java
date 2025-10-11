// src/main/java/com/kribb/backend/node/dto/LinkResponse.java
package com.kribb.backend.node.dto;

import java.time.Instant;

public record LinkResponse(
        Long id,
        Long projectId,
        Long sourceNodeId,
        Long targetNodeId,
        Instant createdAt
) {}
