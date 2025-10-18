package com.kribb.backend.project;

import com.kribb.backend.node.NodeService;
import com.kribb.backend.node.dto.LinkResponse;
import com.kribb.backend.node.dto.NodeDetailResponse;
import com.kribb.backend.node.dto.NodeResponse;
import com.kribb.backend.project.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository repository;

    private final NodeService nodeService;

    public ProjectResponse create(ProjectCreateRequest req) {
        var saved = repository.save(ProjectEntity.builder().name(req.name()).build());
        return new ProjectResponse(saved.getId(), saved.getName(), saved.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> findAll() {
        return repository.findAll().stream()
                .map(p -> new ProjectResponse(p.getId(), p.getName(), p.getCreatedAt()))
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse findById(Long id) {
        var p = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + id));
        return new ProjectResponse(p.getId(), p.getName(), p.getCreatedAt());
    }

    public ProjectResponse update(Long id, ProjectUpdateRequest req) {
        var p = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + id));
        p = ProjectEntity.builder()
                .id(p.getId())
                .name(req.name())
                .createdAt(p.getCreatedAt())
                .build();
        var updated = repository.save(p);
        return new ProjectResponse(updated.getId(), updated.getName(), updated.getCreatedAt());
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public GraphResponse getGraph(Long projectId, String includeCsv) {
        var include = parseInclude(includeCsv);

        // 1) 프로젝트
        var p = repository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
        var projectDto = new ProjectResponse(p.getId(), p.getName(), p.getCreatedAt());

        // 2) 노드 (✅ listByProject 사용)
        List<NodeResponse> nodes = List.of();
        if (include.contains("nodes") || include.contains("details")) {
            nodes = nodeService.listByProject(projectId);
        }

        // 3) 링크 (✅ listLinks 사용)
        List<LinkResponse> links = List.of();
        if (include.contains("links")) {
            links = nodeService.listLinks(projectId);
        }

        // 4) 상세(노드별 파일 등) — 우선 단건 호출로 조립(필요 시 벌크화 가능)
        Map<Long, NodeDetailResponse> details = Map.of();
        if (include.contains("details") && !nodes.isEmpty()) {
            var nodeIds = nodes.stream().map(NodeResponse::id).toList();
            var map = new HashMap<Long, NodeDetailResponse>(nodeIds.size());
            for (Long nodeId : nodeIds) {
                var d = nodeService.getDetail(nodeId);
                if (d != null) map.put(nodeId, d);
            }
            details = map;
        }

        return new GraphResponse(projectDto, nodes, links, details);
    }

    private Set<String> parseInclude(String includeCsv) {
        if (includeCsv == null || includeCsv.isBlank()) {
            return Set.of("nodes", "links", "details");
        }
        return Arrays.stream(includeCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toUnmodifiableSet());
    }
}
