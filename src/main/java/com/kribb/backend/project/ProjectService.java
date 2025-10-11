package com.kribb.backend.project;

import com.kribb.backend.project.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository repository;

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
}
