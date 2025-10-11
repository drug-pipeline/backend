// NodeService.java
package com.kribb.backend.node;

import com.kribb.backend.node.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NodeService {
    private final NodeRepository nodeRepository;
    private final NodeFileRepository nodeFileRepository;
    private final NodeLinkRepository nodeLinkRepository;

    private final Path root = Path.of("/data/nodes"); // 로컬 디스크 저장(최소구성)

    public NodeResponse create(NodeCreateRequest req) {
        NodeEntity saved = nodeRepository.save(NodeEntity.builder()
                .projectId(req.projectId())
                .type(req.type())
                .name(req.name())
                .status(req.status())
                .x(req.x())
                .y(req.y())
                .metaJson(req.metaJson())
                .createdAt(Instant.now())
                .build());
        return new NodeResponse(saved.getId(), saved.getProjectId(), saved.getType(),
                saved.getName(), saved.getStatus(), saved.getX(), saved.getY());
    }

    public NodeFileEntity upload(Long nodeId, MultipartFile file) {
        NodeEntity node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new IllegalArgumentException("node not found: " + nodeId));
        try {
            Files.createDirectories(root.resolve(String.valueOf(nodeId)));
            String stored = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path dest = root.resolve(String.valueOf(nodeId)).resolve(stored);
            file.transferTo(dest);

            NodeFileEntity rec = NodeFileEntity.builder()
                    .nodeId(node.getId())
                    .originalName(file.getOriginalFilename())
                    .storedPath(dest.toString())
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .build();
            return nodeFileRepository.save(rec);
        } catch (Exception e) {
            throw new RuntimeException("file upload failed", e);
        }
    }

    public NodeLinkEntity link(LinkCreateRequest req) {
        // 같은 프로젝트 내 링크만 허용
        return nodeLinkRepository.save(NodeLinkEntity.builder()
                .projectId(req.projectId())
                .sourceNodeId(req.sourceNodeId())
                .targetNodeId(req.targetNodeId())
                .build());
    }

    /** 4) 후행 노드에서 선행 노드 파일 참조 목록 얻기 (직전 노드들만; 재귀 없이 최소) */
    @Transactional(readOnly = true)
    public List<NodeFileEntity> upstreamFiles(Long projectId, Long targetNodeId) {
        List<NodeLinkEntity> incoming = nodeLinkRepository
                .findByProjectIdAndTargetNodeId(projectId, targetNodeId);
        List<NodeFileEntity> out = new ArrayList<>();
        for (NodeLinkEntity l : incoming) {
            out.addAll(nodeFileRepository.findByNodeId(l.getSourceNodeId()));
        }
        return out;
    }

    @Transactional(readOnly = true)
    public List<NodeResponse> listByProject(Long projectId) {
        return nodeRepository.findByProjectId(projectId).stream()
                .map(n -> new NodeResponse(n.getId(), n.getProjectId(), n.getType(),
                        n.getName(), n.getStatus(), n.getX(), n.getY()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public NodeResponse getOne(Long nodeId) {
        NodeEntity n = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new IllegalArgumentException("node not found: " + nodeId));
        return new NodeResponse(n.getId(), n.getProjectId(), n.getType(),
                n.getName(), n.getStatus(), n.getX(), n.getY());
    }

    @Transactional(readOnly = true)
    public NodeDetailResponse getDetail(Long nodeId) {
        NodeEntity n = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new IllegalArgumentException("node not found: " + nodeId));
        List<NodeFileDto> files = nodeFileRepository.findByNodeId(nodeId).stream()
                        .map(f -> new NodeFileDto(f.getId(), f.getOriginalName(), f.getContentType(),
                        f.getSize(), f.getCreatedAt()))
                .collect(Collectors.toList());
        NodeResponse node = new NodeResponse(n.getId(), n.getProjectId(), n.getType(),
                n.getName(), n.getStatus(), n.getX(), n.getY());
        return new NodeDetailResponse(node, files);
    }

    @Transactional(readOnly = true)
    public List<LinkResponse> listLinks(Long projectId) {
        return nodeLinkRepository.findByProjectId(projectId)
                .stream()
                .map(l -> new LinkResponse(
                        l.getId(),
                        l.getProjectId(),
                        l.getSourceNodeId(),
                        l.getTargetNodeId(),
                        l.getCreatedAt()
                ))
                .toList();
    }

    // NodeService.java - updateNode 수정본
    public NodeResponse updateNode(Long projectId, Long nodeId, NodeUpdateRequest body) {
        NodeEntity node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new IllegalArgumentException("Node not found: " + nodeId));

        if (!node.getProjectId().equals(projectId)) {
            throw new IllegalArgumentException("ProjectId mismatch");
        }

        if (body.name() != null) node.setName(body.name());
        if (body.status() != null) node.setStatus(body.status());
        if (body.x() != null) node.setX(body.x());
        if (body.y() != null) node.setY(body.y());
        if (body.metaJson() != null) node.setMetaJson(body.metaJson());

        NodeEntity updated = nodeRepository.save(node);

        // ✅ 정적 팩토리 메서드 대신 직접 생성
        return new NodeResponse(
                updated.getId(),
                updated.getProjectId(),
                updated.getType(),
                updated.getName(),
                updated.getStatus(),
                updated.getX(),
                updated.getY()
        );
    }

    /**
     * Visualizer 상류 노드(PDB/SDF/SMILES 등) 중 SUCCESS 상태의 파일들을 전부 모아 반환
     */
    @Transactional(readOnly = true)
    public List<VisualizerInputFileDto> listVisualizerInputs(Long projectId, Long visualizerId) {
        var incomingLinks = nodeLinkRepository.findByProjectIdAndTargetNodeId(projectId, visualizerId);

        var upstreamNodeIds = incomingLinks.stream()
                .map(NodeLinkEntity::getSourceNodeId)
                .distinct()
                .toList();
        if (upstreamNodeIds.isEmpty()) return List.of();

        Set<NodeType> ALLOWED_TYPES = EnumSet.of(NodeType.PDB, NodeType.SDF, NodeType.SMILES);

        var filteredUpstreamIds = nodeRepository.findAllById(upstreamNodeIds).stream()
                .filter(n -> ALLOWED_TYPES.contains(n.getType()))
                .filter(n -> n.getStatus() == NodeStatus.SUCCESS)
                .map(NodeEntity::getId)
                .toList();
        if (filteredUpstreamIds.isEmpty()) return List.of();

        return nodeFileRepository.findByNodeIdIn(filteredUpstreamIds).stream()
                .map(f -> new VisualizerInputFileDto(
                        f.getId(),
                        f.getNodeId(),
                        f.getOriginalName(),
                        f.getStoredPath(),
                        f.getContentType(),
                        f.getSize(),
                        f.getCreatedAt()
                ))
                .toList();
    }


}
