package com.kribb.backend.project.dto;

import com.kribb.backend.node.dto.NodeResponse;
import com.kribb.backend.node.dto.NodeDetailResponse;
import com.kribb.backend.node.dto.LinkResponse;

import java.util.List;
import java.util.Map;

public record GraphResponse(
        ProjectResponse project,
        List<NodeResponse> nodes,
        List<LinkResponse> links,
        Map<Long, NodeDetailResponse> details
) {}
