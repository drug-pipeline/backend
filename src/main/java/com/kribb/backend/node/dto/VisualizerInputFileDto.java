package com.kribb.backend.node.dto;

import java.time.Instant;

public record VisualizerInputFileDto(
        Long id,
        Long nodeId,
        String originalName,
        String storedPath,
        String contentType,
        Long size,
        Instant createdAt
) {}
