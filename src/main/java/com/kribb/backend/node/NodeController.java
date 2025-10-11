// src/main/java/com/kribb/backend/node/NodeController.java
package com.kribb.backend.node;

import com.kribb.backend.node.dto.NodeCreateRequest;
import com.kribb.backend.node.dto.NodeResponse;
import com.kribb.backend.node.dto.NodeUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/nodes")
@RequiredArgsConstructor
public class NodeController {

    private final NodeService nodeService;

    // 프로젝트 내 노드 목록
    @GetMapping
    public List<NodeResponse> list(@PathVariable Long projectId) {
        return nodeService.list(projectId);
    }

    // 단일 조회
    @GetMapping("/{id}")
    public NodeResponse get(@PathVariable Long id) {
        return nodeService.get(id);
    }

    // 범용 노드 생성(JSON)
    @PostMapping
    public NodeResponse create(@RequestBody @Valid NodeCreateRequest req) {
        return nodeService.create(req);
    }

    // 업데이트
    @PatchMapping("/{id}")
    public NodeResponse update(@PathVariable Long id, @RequestBody @Valid NodeUpdateRequest req) {
        return nodeService.update(id, req);
    }

    // 삭제
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        nodeService.delete(id);
    }

    // ===== PDB 업로드 전용 =====
    // multipart/form-data: fields => name, x, y, file
    @PostMapping(path = "/pdb", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public NodeResponse uploadPdb(
            @PathVariable Long projectId,
            @RequestPart(required = false) String name,
            @RequestPart(required = false) Double x,
            @RequestPart(required = false) Double y,
            @RequestPart("file") MultipartFile file
    ) {
        return nodeService.uploadPdb(projectId, name, x, y, file);
    }
}
