// dto/NodeFileDto.java
package com.kribb.backend.node.dto;

import java.time.Instant;

public record NodeFileDto(
        Long id,
        String originalName,
        String contentType,
        Long size,
        Instant createdAt
) {}
