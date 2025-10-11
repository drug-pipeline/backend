// dto/LinkCreateRequest.java  (새로 추가)
package com.kribb.backend.node.dto;

public record LinkCreateRequest(
        Long projectId,
        Long sourceNodeId,
        Long targetNodeId
) {}
