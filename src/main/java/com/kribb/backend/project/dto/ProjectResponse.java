package com.kribb.backend.project.dto;

import java.time.LocalDateTime;

public record ProjectResponse(Long id, String name, LocalDateTime createdAt) {
}
