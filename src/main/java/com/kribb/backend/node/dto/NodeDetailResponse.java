// dto/NodeDetailResponse.java
package com.kribb.backend.node.dto;

import java.util.List;

public record NodeDetailResponse(
        NodeResponse node,
        List<NodeFileDto> files
) {}
