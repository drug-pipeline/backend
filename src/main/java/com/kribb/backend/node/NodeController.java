// NodeController.java
package com.kribb.backend.node;

import com.kribb.backend.node.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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

    @PutMapping("/projects/{projectId}/nodes/{nodeId}")
    public NodeResponse updateNode(
            @PathVariable Long projectId,
            @PathVariable Long nodeId,
            @RequestBody NodeUpdateRequest body
    ) {
        return nodeService.updateNode(projectId, nodeId, body);
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

    @GetMapping("/projects/{projectId}/nodes")
    public List<NodeResponse> listNodes(@PathVariable Long projectId) {
        return nodeService.listByProject(projectId);
    }

    @GetMapping("/nodes/{nodeId}")
    public NodeResponse getNode(@PathVariable Long nodeId) {
        return nodeService.getOne(nodeId);
    }

    @GetMapping("/nodes/{nodeId}/detail")
    public NodeDetailResponse getNodeDetail(@PathVariable Long nodeId) {
        return nodeService.getDetail(nodeId);
    }

    @GetMapping(path = "/projects/{projectId}/links", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<LinkResponse> listProjectLinks(@PathVariable Long projectId) {
        return nodeService.listLinks(projectId);
    }

    @GetMapping(path = "/projects/{projectId}/nodes/{visualizerId}/inputs", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<VisualizerInputFileDto> listVisualizerInputs(
            @PathVariable Long projectId,
            @PathVariable Long visualizerId
    ) {
        return nodeService.listVisualizerInputs(projectId, visualizerId);
    }

    /** 파일 바이너리 다운로드 (원본 MIME 유지 + inline 표시) */
    @GetMapping("/nodes/{fileId}/content")
    public ResponseEntity<Resource> getFileContent(@PathVariable Long fileId) throws Exception {
        NodeFileEntity f = nodeFileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("file not found: " + fileId));

        Path path = Path.of(f.getStoredPath());
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("stored file missing: " + path);
        }

        // MIME 설정 (없으면 octet-stream)
        String mime = (f.getContentType() != null && !f.getContentType().isBlank())
                ? f.getContentType()
                : "application/octet-stream";
        MediaType mediaType;
        try {
            mediaType = MediaType.parseMediaType(mime);
        } catch (Exception ignore) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        // Content-Disposition: inline; filename="..."; filename*=UTF-8''...
        ContentDisposition cd = ContentDisposition.inline()
                .filename(f.getOriginalName(), StandardCharsets.UTF_8)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setContentDisposition(cd);
        headers.setContentLength(Files.size(path));

        Resource body = new FileSystemResource(path);
        return ResponseEntity.ok()
                .headers(headers)
                .body(body);
    }
}
