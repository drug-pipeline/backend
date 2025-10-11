// src/main/java/com/kribb/backend/node/NodeService.java
package com.kribb.backend.node;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kribb.backend.node.dto.NodeCreateRequest;
import com.kribb.backend.node.dto.NodeResponse;
import com.kribb.backend.node.dto.NodeUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NodeService {

    private final NodeRepository nodeRepository;
    private final ObjectMapper objectMapper; // ← 스프링이 자동 주입

    @Value("${app.storage.base-dir:uploads}")
    private String baseDir;

    @Value("${app.storage.pdb-dir:pdb}")
    private String pdbDir;

    public List<NodeResponse> list(Long projectId) {
        return nodeRepository.findByProjectIdOrderByIdAsc(projectId)
                .stream().map(NodeResponse::from).toList();
    }

    public NodeResponse create(NodeCreateRequest req) {
        String metaJson = toJsonOrNull(req.meta());

        NodeEntity e = NodeEntity.builder()
                .projectId(req.projectId())
                .type(req.type())
                .name(req.name())
                .status(req.status())
                .x(req.x())
                .y(req.y())
                .meta(metaJson) // ← 직렬화된 JSON 문자열 저장
                .build();

        return NodeResponse.from(nodeRepository.save(e));
    }

    public NodeResponse get(Long id) {
        return NodeResponse.from(find(id));
    }

    public NodeResponse update(Long id, NodeUpdateRequest req) {
        NodeEntity e = find(id);
        if (req.name() != null)   e.setName(req.name());
        if (req.status() != null) e.setStatus(req.status());
        if (req.x() != null)      e.setX(req.x());
        if (req.y() != null)      e.setY(req.y());
        if (req.meta() != null)   e.setMeta(toJsonOrNull(req.meta())); // ← 직렬화
        return NodeResponse.from(e);
    }

    public void delete(Long id) {
        nodeRepository.deleteById(id);
    }

    /** PDB 파일 업로드 + 노드 생성 */
    public NodeResponse uploadPdb(Long projectId, String name, Double x, Double y, MultipartFile file) {
        Path dir = Paths.get(baseDir, pdbDir, String.valueOf(projectId));
        try {
            Files.createDirectories(dir);
            String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
            String safeName = (name == null || name.isBlank()) ? "pdb" : name.trim();
            String filename = safeName.replaceAll("[^a-zA-Z0-9._-]", "_") + "_" + ts + ".pdb";
            Path target = dir.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            NodeEntity e = NodeEntity.builder()
                    .projectId(projectId)
                    .type(NodeType.PDB)
                    .name(safeName)
                    .status(NodeStatus.SUCCESS) // 파일 저장 성공 시 SUCCESS
                    .x(x != null ? x : 0d)
                    .y(y != null ? y : 0d)
                    .filePath(target.toString())
                    .build();

            return NodeResponse.from(nodeRepository.save(e));
        } catch (Exception ex) {
            throw new RuntimeException("PDB 파일 저장 실패", ex);
        }
    }

    private NodeEntity find(Long id) {
        return nodeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Node not found: " + id));
    }

    /** Map -> JSON 문자열 (null 허용) */
    private String toJsonOrNull(Object meta) {
        if (meta == null) return null;      // meta 제거
        try {
            return objectMapper.writeValueAsString(meta);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("meta 직렬화 실패: " + e.getMessage(), e);
        }
    }
}
