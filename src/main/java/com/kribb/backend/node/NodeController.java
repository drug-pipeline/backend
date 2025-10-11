// NodeController.java
package com.kribb.backend.node;

import com.kribb.backend.node.dto.LinkCreateRequest;
import com.kribb.backend.node.dto.NodeCreateRequest;
import com.kribb.backend.node.dto.NodeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class NodeController {

    private final NodeService nodeService;
    private final NodeRepository nodeRepository;
    private final NodeFileRepository nodeFileRepository;

    // 1) 노드 생성
    @PostMapping("/projects/{projectId}/nodes")
    public NodeResponse createNode(@PathVariable Long projectId, @RequestBody NodeCreateRequest body) {
        // path의 projectId를 신뢰 소스로 사용
        NodeCreateRequest req = new NodeCreateRequest(
                projectId, body.type(), body.name(), body.status(), body.x(), body.y(), body.metaJson());
        return nodeService.create(req);
    }

    // 2) 노드에 파일 업로드
    @PostMapping(path = "/nodes/{nodeId}/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public NodeFileEntity upload(@PathVariable Long nodeId, @RequestPart("file") MultipartFile file) {
        return nodeService.upload(nodeId, file);
    }

    // (옵션) 노드 파일 목록
    @GetMapping("/nodes/{nodeId}/files")
    public List<NodeFileEntity> listFiles(@PathVariable Long nodeId) {
        return nodeFileRepository.findByNodeId(nodeId);
    }

    // 3) 노드 링크 생성
    @PostMapping("/projects/{projectId}/links")
    public NodeLinkEntity link(@PathVariable Long projectId, @RequestBody LinkCreateRequest body) {
        LinkCreateRequest req = new LinkCreateRequest(projectId, body.sourceNodeId(), body.targetNodeId());
        return nodeService.link(req);
    }

    // 4) 후행 노드에서 선행 노드 파일 참조 목록 얻기 (직전 노드 기준)
    @GetMapping("/projects/{projectId}/nodes/{nodeId}/inputs")
    public List<NodeFileEntity> upstreamInputs(@PathVariable Long projectId, @PathVariable Long nodeId) {
        return nodeService.upstreamFiles(projectId, nodeId);
    }
}
